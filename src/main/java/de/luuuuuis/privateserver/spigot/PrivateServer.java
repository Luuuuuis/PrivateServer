package de.luuuuuis.privateserver.spigot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.luuuuuis.privateserver.spigot.commands.PrivateServerCmd;
import de.luuuuuis.privateserver.spigot.events.JoinListener;
import de.luuuuuis.privateserver.spigot.util.Config;
import de.luuuuuis.privateserver.spigot.util.Owner;
import lombok.Getter;
import net.jitse.npclib.NPCLib;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateServer extends JavaPlugin {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static PrivateServer instance;
    @Getter
    private Owner owner;
    @Getter
    private NPCLib npcLib;


    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        npcLib = new NPCLib(this);

        // init Config
        Config.init(getDataFolder());

        // init Owner
        owner = new Owner();


        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "pv:cmd");
        getCommand("privateserver").setExecutor(new PrivateServerCmd());

        // Listener for NPCs
        //pluginManager.registerEvents(new NPCListener(this), this);

        // prevents the server from starting new servers when changing state to ingame-mode
        CloudServer.getInstance().setAllowAutoStart(false);
    }

}