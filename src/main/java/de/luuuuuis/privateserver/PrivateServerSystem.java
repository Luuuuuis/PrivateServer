package de.luuuuuis.privateserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.luuuuuis.privateserver.commands.PrivateServerCmd;
import de.luuuuuis.privateserver.events.TabComplete;
import de.luuuuuis.privateserver.util.Config;
import de.luuuuuis.privateserver.util.PrivateServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class PrivateServerSystem extends Plugin {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<PrivateServer> servers = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        //config
        Config.init(getDataFolder());

        //commands
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new PrivateServerCmd());
        pluginManager.registerListener(this, new TabComplete());
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // stop all private servers
        servers.forEach(PrivateServer::stop);
    }
}
