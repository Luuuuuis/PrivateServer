package de.luuuuuis.privateserver.bungee.events;

import de.dytanic.cloudnet.bridge.event.proxied.ProxiedServerRemoveEvent;
import de.luuuuuis.privateserver.bungee.util.CloudServer;
import de.luuuuuis.privateserver.bungee.util.Owner;
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

        owner.getServers().stream().filter(cloudServer -> cloudServer.getName().equals(e.getFrom().getName())).findFirst().ifPresent(CloudServer::remove);
    }

    @EventHandler
    public void onShutdown(ProxiedServerRemoveEvent e) {
        CloudServer.getCloudServers().stream().filter(cloudServer -> cloudServer.getName().equals(e.getServerInfo().getServiceId().getServerId())).findFirst().ifPresent(CloudServer::remove);
    }

}
