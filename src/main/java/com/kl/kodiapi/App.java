package com.kl.kodiapi;

import com.github.psamsotha.jersey.properties.JerseyPropertiesFeature;
import com.kl.kodiapi.devicecontroller.TVController;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;


import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * App class.
 *
 */
public class App {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:9555/";

    public static HttpServer startServer() throws IOException {
        final ResourceConfig rc = new ResourceConfig().packages("com.kl.kodiapi");

        rc.register(LoggingFilter.class);
        rc.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "/templates/");
        rc.register(MustacheMvcFeature.class);
        rc.register(JerseyPropertiesFeature.class);
        rc.property(JerseyPropertiesFeature.RESOURCE_PATH, "config.properties");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * App method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl", BASE_URI));
    }
}

