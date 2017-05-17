package com.kl.kodiapi.utils.denon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TelNetConnection {
    private static TelNetConnection instance = null;

    private Socket s = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String ipAddress;

    private TelNetConnection(String ipAddress) {
        this.ipAddress = ipAddress;
        setupConnection();
    }

    private void setupConnection() {
        try {
            s = new Socket(ipAddress, 23);
            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String cmd){
        if (s.isClosed()) setupConnection();
        out.print(cmd + '\r');
        out.flush();

        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static TelNetConnection getInstance(String ipAddress){
        if(instance == null) {
            instance = new TelNetConnection(ipAddress);
        }
        return instance;
    }

}