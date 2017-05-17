package com.kl.kodiapi.devicecontroller;

import javax.ws.rs.Path;

/**
 * Created by Kevin.Lane2 on 07/05/2017.
 */
@Path("/devicecontroller")
public interface DeviceController {

    public String powerOn();
    public String powerOff();


}
