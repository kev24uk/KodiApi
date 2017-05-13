package com.kl.kodiapi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.mvc.Template;
import org.json.*;

@Path("/kodimethods")
public class KodiMethods {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    public static final String KODI_BASE_URL = "http://192.168.0.16:8080/jsonrpc?request=";
    public static final String GET_NEXT_EPISODE_URL = "{\"jsonrpc\":\"2.0\",\"method\":\"VideoLibrary.GetEpisodes\",\"id\":1,\"params\":{\"filter\":{\"field\":\"playcount\", \"operator\":\"is\",\"value\":\"0\"},\"properties\":[\"season\",\"episode\",\"runtime\",\"resume\",\"playcount\",\"tvshowid\",\"lastplayed\",\"file\"],\"tvshowid\":%s,\"limits\":{\"end\":1},\"sort\":{\"order\":\"ascending\",\"method\":\"episode\"}}}";
    public static final String PLAY_EPISODE_ID = "{\"jsonrpc\":\"2.0\",\"method\":\"Player.Open\",\"id\":1,\"params\":{\"item\":{\"episodeid\":%s}}}";
    public static final String GET_TV_SHOW_ID = "{\"method\":\"VideoLibrary.GetTVShows\",\"id\":1,\"jsonrpc\":\"2.0\",\"params\":{\"filter\":{\"field\":\"title\",\"value\":\"%%%s%%\",\"operator\":\"is\"},\"sort\":{\"method\":\"lastplayed\",\"order\":\"descending\"},\"properties\":[\"lastplayed\",\"playcount\"]}}";

    @GET
    @Path("tv/{TVShowName}")
    @Template(name = "/tvshowname.mustache")
    public Context tvEpisode(@PathParam("TVShowName") String tvShowName, @QueryParam("action") String action) throws Exception {
        JSONObject jsonUrlResponse = getJsonUrlResponse(KODI_BASE_URL + String.format(GET_TV_SHOW_ID,tvShowName));
        Integer numFound = (Integer)jsonUrlResponse.getJSONObject("result").getJSONObject("limits").get("total");

        while (numFound == 0) {
            LOGGER.warning("TV Series '" + tvShowName + "' not found. Trying '" + tvShowName.substring(0,tvShowName.lastIndexOf(" ")) + "'");
            tvShowName = tvShowName.substring(0,tvShowName.lastIndexOf(" "));
            jsonUrlResponse = getJsonUrlResponse(KODI_BASE_URL + String.format(GET_TV_SHOW_ID,tvShowName));
            numFound = (Integer)jsonUrlResponse.getJSONObject("result").getJSONObject("limits").get("total");
        }

        LOGGER.info("Found TV Show. Server response: " + jsonUrlResponse.toString());
        String tvshowid = getTvshowid(jsonUrlResponse);
        JSONObject jsonUrlResponse2 = getJsonUrlResponse(KODI_BASE_URL + getNextEpisodeUrl(tvshowid));
        JSONObject nextEpisodeDetails = getNextEpisodeDetails(jsonUrlResponse2);
        if (action == null) action="default";
        switch (action) {
            case "play":
                getJsonUrlResponse(KODI_BASE_URL + String.format(PLAY_EPISODE_ID,nextEpisodeDetails.get("episodeid")));
                LOGGER.info("Playing Next Episode. Server Response: " + jsonUrlResponse2.toString());
                return new Context(String.format("Playing %s Season %s Episode %s / Episode ID: %s",
                        tvShowName, nextEpisodeDetails.get("season"), nextEpisodeDetails.get("episode"), nextEpisodeDetails.get("episodeid")),jsonUrlResponse2.toString());
            default:
                LOGGER.info("No action. Server Response: " + jsonUrlResponse2.toString());
                return new Context(String.format("Next Episode of %s is Season %s Episode %s / Episode ID: %s",
                        tvShowName, nextEpisodeDetails.get("season"), nextEpisodeDetails.get("episode"), nextEpisodeDetails.get("episodeid")),jsonUrlResponse2.toString());
        }
    }

    @GET
    @Path("actions/{action}")
    @Produces(MediaType.TEXT_PLAIN)
    public String performKodiAction(@PathParam("action") String action) throws Exception {
        switch (action) {
            case "play":
            case "pause":
                return getJsonUrlResponse(KODI_BASE_URL + "{\"jsonrpc\":\"2.0\",\"method\":\"Player.PlayPause\",\"params\":{\"playerid\":1},\"id\": 1}").toString();
            default:
                return "No Action Recognised";
        }

    }

    private String getTvshowid(JSONObject object) {
        return object.getJSONObject("result").getJSONArray("tvshows").getJSONObject(0).get("tvshowid").toString();
    }

    private JSONObject getNextEpisodeDetails(JSONObject object) {
        return object.getJSONObject("result").getJSONArray("episodes").getJSONObject(0);
    }

    private JSONObject getJsonUrlResponse(String urlString) throws Exception {
        URL url = new URL(encodeKodiUrl(urlString));
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        return new JSONObject(responseStrBuilder.toString());
    }

    private String getNextEpisodeUrl(String tvshowid) {
        return String.format(GET_NEXT_EPISODE_URL, tvshowid);
    }

    private String encodeKodiUrl(String url) {
        return url.replaceAll("\"", "%22").replaceAll(" ", "%20");
    }

    public static class Context {
        public String detail;
        public String jsonResponse;

        public Context(final String detail, final String jsonResponse) {
            this.detail = detail;
            this.jsonResponse = jsonResponse;
        }
    }
}
