package de.luuuuuis.privateserver.bungee.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageReceive implements Listener {

    /**
     * access and execute every privateserver command on bungee. No need to check anything on spigot cause bungee does :3
     *
     * @param e event
     */
    @EventHandler
    public void onReceive(PluginMessageEvent e) {
        if (!e.getTag().equals("pv:cmd")) return;

        @SuppressWarnings("UnstableApiUsage") ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String utf_in = in.readUTF();
        if (!(e.getReceiver() instanceof ProxiedPlayer)) return; // ah shit, i guess,,,

        ProxiedPlayer player = (ProxiedPlayer) e.getReceiver();

        ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "privateserver " + utf_in);
    }

}
