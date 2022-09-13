package noppes.npcs.shared.client.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import noppes.npcs.CustomNpcs;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * https://developers.google.com/analytics/devguides/collection/protocol/ga4/sending-events?client_type=gtag#required_parameters
 */
public class AnalyticsTracking {

    public static void sendData(final UUID uuid, final String eventName, final String data) {
        new Thread(() -> {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("client_id", uuid.toString());
                JsonArray events = new JsonArray();
                JsonObject event = new JsonObject();
                event.addProperty("name", CustomNpcs.MODID + "_" + CustomNpcs.VERSION);
                JsonObject eventParams = new JsonObject();
                eventParams.addProperty("type", data);
                event.add("params", eventParams);
                events.add(event);
                body.add("events", events);

                String analyticsPostData = body.toString();

                URL url = new URL("https://www.google-analytics.com/mp/collect?measurement_id=G-VYV9D53HFS&api_secret=BQOVck8WTRG8yaCF_OhPdQ");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(analyticsPostData.getBytes(StandardCharsets.UTF_8).length));
                OutputStream dataOutput = connection.getOutputStream();
                dataOutput.write(analyticsPostData.getBytes(StandardCharsets.UTF_8));
                dataOutput.flush();
                dataOutput.close();
//                BufferedReader br = null;
//                if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
//                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                } else {
//                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
//                }
//                StringBuilder sb = new StringBuilder();
//                String output;
//                while ((output = br.readLine()) != null) {
//                    sb.append(output);
//                }
//                String response = sb.toString();
//                int code = connection.getResponseCode();
                connection.getInputStream().close();
                connection.disconnect();
            } catch (Exception ignored) {
            }
        }).start();
    }
}
