package de.luuuuuis.privateserver.util;

import de.luuuuuis.privateserver.PrivateServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
            try (InputStream in = PrivateServer.class.getClassLoader().getResourceAsStream("config.json")) {
                Files.copy(Objects.requireNonNull(in), Paths.get(config));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        read(config);
    }

    private static void read(String path) {
        try (FileReader fileReader = new FileReader(path)) {
            instance = PrivateServer.GSON.fromJson(fileReader, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTemplate() {
        return template;
    }

    public HashMap<String, Object> getMessages() {
        return messages;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public int getMaxServersRunning() {
        return maxServersRunning;
    }

    public int getMaxServersPerUser() {
        return maxServersPerUser;
    }

    public int getMemory() {
        return memory;
    }
}