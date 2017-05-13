package com.kl.kodiapi.devicecontroller.denon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
    private static Connection instance = null;

    private Socket s = null;
    private PrintWriter out = null;
    private BufferedReader in = null;

    private Connection() {

        try
        {
            s = new Socket("192.168.0.14", 23);
            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void sendCommand(String cmd){
        out.print(cmd + '\r');
        out.flush();
        try {
            System.out.println(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getInstance(){
        if(instance == null) {
            instance = new Connection();
        }
        return instance;
    }

}