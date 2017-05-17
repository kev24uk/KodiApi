package com.kl.kodiapi.devicecontroller;

import com.github.psamsotha.jersey.properties.Prop;
import com.kl.kodiapi.utils.denon.TelNetConnection;
import org.glassfish.jersey.filter.LoggingFilter;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("/devicecontroller")
public class AVRController implements DeviceController {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());
    public String AVR_IP_ADDRESS;
    TelNetConnection con;

    public AVRController(@Prop("AVR.IP") String ipAddress) {
        this.AVR_IP_ADDRESS = ipAddress;
        con = con.getInstance(AVR_IP_ADDRESS);
    }


    @GET
    @Path("AVR/turnOn")
    @Produces(MediaType.TEXT_PLAIN)
    public String powerOn() {
        con.sendCommand("PWON");
        return "Turned AVR On";
    }

    @GET
    @Path("AVR/turnOff")
    @Produces(MediaType.TEXT_PLAIN)
    public String powerOff() {
        con.sendCommand("PWSTANDBY");
        return "Turned AVR Off";
    }
}