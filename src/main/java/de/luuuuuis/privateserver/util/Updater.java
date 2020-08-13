package de.luuuuuis.privateserver.util;

import com.google.gson.JsonObject;
import de.luuuuuis.privateserver.PrivateServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private String tag_name;
    //private String browser_download_url;

    public Updater() {
        try {
            JsonObject jsonObject = PrivateServer.GSON.fromJson(readJSONFromURL(), JsonObject.class);
            this.tag_name = jsonObject.get("tag_name").getAsString();
            //browser_download_url = jsonObject.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();

            if (isNew())
                report();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void report() {
        System.err.println(Config.getInstance().getPrefix() + "A newer version of PrivateServer is available. Please consider downloading the new version.\n" +
                Config.getInstance().getPrefix() + "More information & download link at https://github.com/Luuuuuis/PrivateServer/releases/latest.");
    }

    private String readJSONFromURL() throws IOException {
        URL url = new URL("https://api.github.com/repos/Luuuuuis/PrivateServer/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            connection.disconnect();

            return content.toString();
        }
    }

//    private void download() {
//        try (InputStream in = new URL(browser_download_url).openStream()) {
//            Files.copy(in, Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException | URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

    private boolean isNew() {
        return !tag_name.equals(PrivateServer.getInstance().getDescription().getVersion());
    }
}
