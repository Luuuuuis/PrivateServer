package de.luuuuuis.privateserver.spigot.util;

import de.luuuuuis.privateserver.spigot.PrivateServer;
import lombok.Getter;
import org.hjson.JsonValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Config {

    @Getter
    private static Config instance;
    @Getter
    private final ArrayList<String> permissions;

    public Config(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public synchronized static void init(File dataFolder) {
        String config = dataFolder.getPath() + "/config.json";
        if (Files.notExists(Paths.get(config))) {

            // create DataFolder
            if (!dataFolder.exists())
                if (!dataFolder.mkdir()) {
                    System.err.println("COULD NOT CREATE PLUGIN FOLDER. Please check permissions and/or try again.");
                    return;
                }

            // copy config
            try (InputStream in = PrivateServer.class.getClassLoader().getResourceAsStream("config-spigot.json")) {
                Files.copy(Objects.requireNonNull(in), Paths.get(config));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        read(config);
    }

    private static void read(String path) {
        try (FileReader fileReader = new FileReader(path)) {
            instance = PrivateServer.GSON.fromJson(JsonValue.readHjson(fileReader).toString(), Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}