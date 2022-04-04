package ru.kpfu.itis.Makhsotova;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RequestHandler {
    private static URLConnection con;
    private static URL url;

    public  void openConnection() {
        try {
            con = url.openConnection();
        } catch (IOException ex) {
            throw  new IllegalArgumentException(ex);
        }
    }

    public  InputStream getInputStream() {
        try {
            return con.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public  void setURL(String tempURL) {
        try {
            url = new URL(tempURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

 }
