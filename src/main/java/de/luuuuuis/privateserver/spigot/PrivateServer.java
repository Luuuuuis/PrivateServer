package de.luuuuuis.privateserver.spigot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.luuuuuis.privateserver.spigot.events.JoinListener;
import de.luuuuuis.privateserver.spigot.events.NPCListener;
import de.luuuuuis.privateserver.spigot.util.Config;
import de.luuuuuis.privateserver.spigot.util.Owner;
import net.jitse.npclib.NPCLib;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateServer extends JavaPlugin {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PrivateServer instance;
    private Owner owner;
    private NPCLib npcLib;

    public static PrivateServer getInstance() {
        return instance;
    }

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
        pluginManager.registerEvents(new NPCListener(this), this);
    }

    public Owner getOwner() {
        return owner;
    }

    public NPCLib getNpcLib() {
        return npcLib;
    }
}
