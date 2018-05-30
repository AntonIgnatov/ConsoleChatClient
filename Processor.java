package com.company;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Processor {
    private String text;
    private String login;
    private Gson gson = new GsonBuilder().create();

    public Processor(String login, String text) {
        this.login = login;
        this.text = text;
    }

    public void work() throws IOException {
        if (privateTo()) {
            return;
        } else if (toRoom()) {
            return;
        } else if (enterRoom()) {
            return;
        } else if (exitRoom()) {
            return;
        } else if (logOut()) {
            return;
        } else if (getStatus()) {
            return;
        } else if (help()) {
            return;
        } else {
            Message m = new Message(login, text);
            int res = m.send(Utils.getURL() + "/add");
        }
    }

    public boolean privateTo() throws IOException {
        if (this.text.startsWith("/PRIVATE ")) {
            String to = this.text.substring(9, this.text.indexOf(' ', 9));
            this.text = this.text.substring(8 + to.length() + 1);

            Message m = new Message(login, text);
            m.setTo(to);
            int res = m.send(Utils.getURL() + "/add");
            return true;
        }
        return false;
    }

    public boolean toRoom() throws IOException {
        if (this.text.startsWith("/ROOM ")) {
            String room = this.text.substring(6, this.text.indexOf(' ', 6));
            this.text = this.text.substring(6 + room.length() + 1);

            Message m = new Message(login, text);
            m.setRoom(room);
            int res = m.send(Utils.getURL() + "/add");
            return true;
        }
        return false;
    }

    public boolean enterRoom() throws IOException {
        if (this.text.startsWith("/ENTER ")) {
            String room = this.text.substring(7);
            String res = getData(Utils.getURL() + "/rooms?name=" + room + "&login=" + this.login);
            System.out.println("You enter room " + room);
            return true;
        }
        return false;
    }

    public boolean exitRoom() throws IOException {
        if (this.text.startsWith("/EXIT ")) {
            String room = this.text.substring(6);
            String res = getData(Utils.getURL() + "/rooms?name=" + room + "&login=" + this.login + "&exit=true");
            System.out.println("You leave room " + room);
            return true;
        }
        return false;
    }

    public boolean logOut() throws IOException {
        if (this.text.startsWith("/LEAVE")) {
            String res = getData(Utils.getURL() + "/authoris?login=" + login + "&exit=true");
            System.out.println("You leave chat(((");
            return true;
        }
        return false;
    }

    public boolean getStatus() {
        if (this.text.startsWith("/INFO")) {
            String url = Utils.getURL() + "/userstatus";
            if (this.text.length() > 5){
                url = url+"?login="+this.text.substring(6);
            }
                try {
                    String[] result = gson.fromJson(getData(url), String[].class);
                    for (String item : result) {
                        System.out.println(item);

                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();

                }
        }
        return false;

    }


    public static String getData(String sUrl) throws IOException {
        String data = "";
        URL url = new URL(sUrl);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();

        if (connect.getContentLengthLong() != 0) {

            try (InputStream is = connect.getInputStream(); InputStreamReader isr = new InputStreamReader(is, "utf-8")) {
                StringBuilder sb = new StringBuilder();

                BufferedReader br = new BufferedReader(isr);
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
            }
        }

        return data;
    }

    public boolean help() {
        if (this.text.startsWith("/HELP")) {
            System.out.println("/PRIVATE username text -> send private message to username");
            System.out.println("/ENTER roomname -> create chatroom or enter in existing");
            System.out.println("/ROOM roomname text -> send message to chatroom roomname");
            System.out.println("/EXIT roomname -> exit from chatroom");
            System.out.println("/INFO -> get all users status");
            System.out.println("/INFO username -> get username status");
            System.out.println("/LEAVE -> disconnect from chat");
            return true;
        }
        return false;
    }
}

