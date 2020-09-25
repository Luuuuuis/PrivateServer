package de.luuuuuis.privateserver.spigot.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.luuuuuis.privateserver.spigot.PrivateServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateServerCmd implements CommandExecutor {

    /**
     * Send a message via "BungeeCord Plugin Messaging Channel" to the BungeeCord server to execute a privateserver
     * command. This could be useful if you want to have an armorstand in your lobby to click and execute a command
     * on the BungeeCord server
     *
     * @param sender  sender (Player !Server is not able to send plugin messages!)
     * @param command command
     * @param s       label
     * @param strings args
     * @return boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) return false;
        if (strings.length < 1) return false;

        Player p = (Player) sender;

        @SuppressWarnings("UnstableApiUsage") ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(String.join(" ", strings));

        p.sendPluginMessage(PrivateServer.getInstance(), "pv:cmd", output.toByteArray());

        return false;
    }
}
