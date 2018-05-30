package com.company;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String login = "";
        String password = "";
        try {

            boolean f = false;
            while (!f) {
                System.out.println("Enter your login: ");
                login = scanner.nextLine();
                System.out.println("Enter your password: ");
                password = scanner.nextLine();
                String resp = authorisation(login, password);
                if (resp.startsWith("You connected as ")) {
                    f = true;
                }
                System.out.println(resp);
            }

            Thread th = new Thread(new GetThread(login, password));
            th.setDaemon(true);
            th.start();


            while (true) {

                String text = scanner.nextLine();
                if (text.isEmpty()) {
                    String res = Processor.getData(Utils.getURL() + "/authoris?login=" + login + "&exit=true");
                    System.out.println("You leave chat(((");
                    break;
                }
                Processor processor = new Processor(login, text);
                processor.work();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public static String authorisation(String login, String password) throws IOException {
        URL url = new URL(Utils.getURL() + "/authoris?login=" + login + "&password=" + password);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        InputStream is = http.getInputStream();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);


        buf = bos.toByteArray();
        return new String(buf, StandardCharsets.UTF_8);

    }
}
