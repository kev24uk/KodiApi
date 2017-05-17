package com.kl.kodiapi.devicecontroller;

import com.github.psamsotha.jersey.properties.Prop;
import com.kl.kodiapi.utils.WebsocketClientEndpoint;
import com.kl.kodiapi.utils.wakeonlan.WakeOnLan;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

@Path("/devicecontroller")
public class TVController implements DeviceController {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    public static final String MESSAGE_FORMAT = "{\"method\": \"ms.remote.control\",\"params\": {\"Cmd\": \"Click\",\"DataOfCmd\": \"%s\",\"Option\": \"false\",\"TypeOfRemote\": \"SendRemoteKey\"}}";
    public String TV_IP_ADDRESS;
    public String TV_MAC_ADDRESS;
    public String URL_FORMAT= "ws://%s:%s/api/v2/channels/samsung.remote.control?name=%s";
    private WebsocketClientEndpoint clientEndPoint = null;

    public TVController(@Prop("tvIP") String tvIP, @Prop("tvMAC") String tvMAC) {
        this.TV_IP_ADDRESS = tvIP;
        this.TV_MAC_ADDRESS = tvMAC;
        try {
            //open new websocket
            clientEndPoint = new WebsocketClientEndpoint(new URI(String.format(URL_FORMAT,TV_IP_ADDRESS,8001,"kodiapi")));
            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }

    @GET
    @Path("TV/turnOn")
    @Produces(MediaType.TEXT_PLAIN)
    public String powerOn() {
        WakeOnLan device = new WakeOnLan(TV_IP_ADDRESS, TV_MAC_ADDRESS);
        device.wakeDevice();
        LOGGER.info("Turned On TV");
        return "Turned On TV";
    }

    @GET
    @Path("TV/turnOff")
    @Produces(MediaType.TEXT_PLAIN)
    public String powerOff() {
        // send message to websocket
        clientEndPoint.sendMessage(String.format(MESSAGE_FORMAT,"KEY_POWER"));
        return "Turned Off TV";
    }


}
