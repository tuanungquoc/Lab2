package com.example.t0u000c.lab2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by VimmiRao on 10/23/2017.
 */

public class Connection {

    static String stream = null;
    public Connection(){

    }

    public String getUrlData(String urlLink){
        try {
            URL url = new URL(urlLink);
            HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
            if(urlc.getResponseCode() == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line ;
                while((line= br.readLine())!= null){
                    sb.append(line);
                }
                stream = sb.toString();
                urlc.disconnect();
            }


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return stream;
    }


}
