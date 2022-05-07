package it.unipi.dii.inattentivedrivers.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetworkActivity {

    private static final String url = "http://apir.viamichelin.com/apir/1/geocode4f.xml?country=fra&city=nice&address=10 promenade des anglais&authkey=RESTGP20220505093104355520302385";
    String flag = "false";

    public String sendGet(String param) throws Exception {

        StringBuffer response = null;

        try {
            URL obj = new URL(url);
            URLConnection con = obj.openConnection();
            con.setConnectTimeout(2000);
            HttpURLConnection httpConnection = (HttpURLConnection) con;
            int responseHttp = httpConnection.getResponseCode();
/*
            if (responseHttp == HttpURLConnection.HTTP_OK) {
                flag = "true";
            } else {
                flag = "false";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Thong bao loi: "+e.toString());
        }
        return flag;*/


            httpConnection.setRequestMethod("GET");

            int responseCode = httpConnection.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Thong bao loi: " + e.toString());
        }

        return response.toString(); //here is your response which is in string type, but remember that the format is json.
    }
}