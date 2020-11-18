package de.luuuuuis.privateserver.bungee.util;

import de.luuuuuis.privateserver.bungee.PrivateServer;
import lombok.Getter;
import org.hjson.JsonValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Getter
public class Config {

    private static Config instance;

    private final String prefix, template;
    private final HashMap<String, Object> messages;
    private final ArrayList<String> groups;
    private final int maxServersRunning, maxServersPerUser, memory;

    public Config(HashMap<String, Object> messages, String prefix, String template, ArrayList<String> groups, int maxServersRunning, int maxServersPerUser, int memory) {
        this.messages = messages;
        this.prefix = prefix;
        this.template = template;
        this.groups = groups;
        this.maxServersRunning = maxServersRunning;
        this.maxServersPerUser = maxServersPerUser;
        this.memory = memory;
    }

    public static Config getInstance() {
        return instance;
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
            try (InputStream in = PrivateServer.class.getClassLoader().getResourceAsStream("config-bungee.json")) {
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