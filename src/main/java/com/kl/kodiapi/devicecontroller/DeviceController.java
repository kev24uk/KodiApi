package com.kl.kodiapi.devicecontroller;

import com.kl.kodiapi.devicecontroller.denon.Controller;
import com.kl.wakeonlan.WakeOnLan;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 * Created by Kevin.Lane2 on 07/05/2017.
 */
@Path("/devicecontroller")
public class DeviceController {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    public static final String TV_IP_ADDRESS = "192.168.0.21";
    public static final String TV_MAC_ADDRESS = "5c-49-7d-09-dd-52";

    @GET
    @Path("TV/turnOn")
    @Produces(MediaType.TEXT_PLAIN)
    public String turnOnTV() {
        WakeOnLan device = new WakeOnLan(TV_IP_ADDRESS, TV_MAC_ADDRESS);
        device.wakeDevice();
        LOGGER.info("Turned On TV");
        return "Turned On TV";
    }

    @GET
    @Path("AVR/{action}")
    @Produces(MediaType.TEXT_PLAIN)
    public String performAVRAction(@PathParam("action") String action) throws Exception {
        Controller avrController = new Controller();
        switch(action) {
            case "ON":
                avrController.handlePowerButtonAction();
                return "Turned On";
            default:
                return "Do Nothing";
        }
    }
}
