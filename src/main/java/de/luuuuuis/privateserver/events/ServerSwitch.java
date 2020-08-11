package de.luuuuuis.privateserver.events;

import de.luuuuuis.privateserver.util.Config;
import de.luuuuuis.privateserver.util.Owner;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitch implements Listener {

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        Owner owner = Owner.getOwner(p);
        if (owner == null)
            return;

        if (p.hasPermission("privateserver.premium"))
            return;

        owner.getServers().stream().filter(cloudServer -> cloudServer.getName().equals(e.getFrom().getName())).findFirst().ifPresent(cloudServer -> {
            owner.sendMessage(Config.getInstance().getPrefix() + "§cYour server was shutdown due to performance saving.");
            cloudServer.stop();
        });
    }

}
