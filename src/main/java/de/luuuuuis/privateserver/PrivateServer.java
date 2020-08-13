package de.luuuuuis.privateserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.luuuuuis.privateserver.commands.PrivateServerCmd;
import de.luuuuuis.privateserver.events.DisconnectListener;
import de.luuuuuis.privateserver.events.ServerSwitch;
import de.luuuuuis.privateserver.events.TabComplete;
import de.luuuuuis.privateserver.util.CloudServer;
import de.luuuuuis.privateserver.util.Config;
import de.luuuuuis.privateserver.util.Metrics;
import de.luuuuuis.privateserver.util.Updater;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class PrivateServer extends Plugin {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PrivateServer instance;

    public static PrivateServer getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        /* stop all running private servers.
         * Sometimes only works when the BungeeCord is stopped with /end
         * kinda weird of CloudNet idk
         */
        CloudServer.getCloudServers().forEach(CloudServer::stop);
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

        //config
        Config.init(getDataFolder());

        //updater
        new Updater();

        //commands
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new PrivateServerCmd());
        pluginManager.registerListener(this, new TabComplete());
        pluginManager.registerListener(this, new ServerSwitch());
        pluginManager.registerListener(this, new DisconnectListener());

        /*
            bStats Metrics https://github.com/Bastian/bStats-Metrics/blob/master/bstats-bungeecord/src/examples/java/ExamplePlugin.java
            to disable these metrics change the bStats config and copy it into you template folder but please don't :C
         */
        System.out.println("UUID: " + Metrics.getUUID());
        Metrics metrics = new Metrics(this, 8521);
        metrics.addCustomChart(new Metrics.SingleLineChart("private_servers_running", () -> CloudServer.getCloudServers().size()));
    }
}
