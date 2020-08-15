package de.luuuuuis.privateserver.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.luuuuuis.privateserver.spigot.events.JoinListener;
import de.luuuuuis.privateserver.spigot.util.Config;
import de.luuuuuis.privateserver.spigot.util.Owner;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateServer extends JavaPlugin {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PrivateServer instance;
    private Owner owner;

    public static PrivateServer getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        System.out.println("You are using\n" +
                "  ____       _            _       ____                           \n" +
                " |  _ \\ _ __(_)_   ____ _| |_ ___/ ___|  ___ _ ____   _____ _ __ \n" +
                " | |_) | '__| \\ \\ / / _` | __/ _ \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|\n" +
                " |  __/| |  | |\\ V / (_| | |_  __/___) |  __/ |   \\ V /  __/ |   \n" +
                " |_|   |_|  |_| \\_/ \\__,_|\\__\\___|____/ \\___|_|    \\_/ \\___|_|   \n" +
                getDescription().getVersion() + "   by Luuuuuis (@realluuuuuis)\n\n" +
                "Support: https://discord.gg/2aSSGcz\n" +
                "GitHub: https://github.com/Luuuuuis/PrivateServer\n");

        // init Config
        Config.init(getDataFolder());

        // init Owner
        owner = new Owner();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getServer().getOnlinePlayers().forEach(this::sendToFallback);
    }

    public void sendToFallback(Player player) {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "cloudnet:main");
        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Fallback"); //Connect to the fallback server in the iteration
        player.sendPluginMessage(this, "cloudnet:main", byteArrayDataOutput.toByteArray());
    }

    public Owner getOwner() {
        return owner;
    }
}
