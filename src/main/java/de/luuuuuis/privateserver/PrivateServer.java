package de.luuuuuis.privateserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.luuuuuis.privateserver.commands.PrivateServerCmd;
import de.luuuuuis.privateserver.events.ServerSwitch;
import de.luuuuuis.privateserver.events.TabComplete;
import de.luuuuuis.privateserver.util.CloudServer;
import de.luuuuuis.privateserver.util.Config;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class PrivateServer extends Plugin {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static List<CloudServer> servers = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        System.out.println("You are using\n" +
                "  ____       _            _       ____                           \n" +
                " |  _ \\ _ __(_)_   ____ _| |_ ___/ ___|  ___ _ ____   _____ _ __ \n" +
                " | |_) | '__| \\ \\ / / _` | __/ _ \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|\n" +
                " |  __/| |  | |\\ V / (_| | |_  __/___) |  __/ |   \\ V /  __/ |   \n" +
                " |_|   |_|  |_| \\_/ \\__,_|\\__\\___|____/ \\___|_|    \\_/ \\___|_|   \n" +
                getDescription().getVersion() + "   by Luuuuuis (@realluuuuuis)\n\n" +
                "Support: https://discord.gg/2aSSGcz\n" +
                "GitHub: https://github.com/Luuuuuis/PrivateServer\n");

        //config
        Config.init(getDataFolder());

        //commands
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new PrivateServerCmd());
        pluginManager.registerListener(this, new TabComplete());
        pluginManager.registerListener(this, new ServerSwitch());
    }

    @Override
    public void onDisable() {
        super.onDisable();

        /* stop all running private servers.
         * Only works when the BungeeCord is stopped with /end
         */
        servers.forEach(CloudServer::stop);
    }
}
