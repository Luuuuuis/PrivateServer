package de.luuuuuis.privateserver.spigot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.luuuuuis.privateserver.spigot.events.JoinListener;
import de.luuuuuis.privateserver.spigot.util.Config;
import de.luuuuuis.privateserver.spigot.util.Owner;
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

        // init Config
        Config.init(getDataFolder());

        // init Owner
        owner = new Owner();

        PluginManager pluginManager = getServer().getPluginManager();
        if (owner.getUuid() != null) {
            // This is not a private server
            pluginManager.disablePlugin(this);
        }
        pluginManager.registerEvents(new JoinListener(), this);
    }

    public Owner getOwner() {
        return owner;
    }
}
