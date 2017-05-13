package com.kl.kodiapi.devicecontroller.denon;

public class Controller {
    Connection con;

    public Controller() {
        con = con.getInstance();
    }

    public void handlePowerButtonAction() {
        con.sendCommand("PWON");
    }

    public void handleAuxButtonAction() {
    }

    public void handleTVButtonAction() {
    }

    public void handleVolDownAction() {
        con.sendCommand("MVDOWN");
    }

    public void handleVolUpAction() {
        con.sendCommand("MVUP");
    }
}
