package de.luuuuuis.privateserver.bungee.events;

import de.luuuuuis.privateserver.bungee.util.CloudServer;
import de.luuuuuis.privateserver.bungee.util.Invitee;
import de.luuuuuis.privateserver.bungee.util.Owner;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DisconnectListener implements Listener {

    @EventHandler
    public void stopServers(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        Owner owner = Owner.getOwner(p);
        if (owner == null)
            return;

        //Avoiding ConcurrentModificationException
        new ArrayList<>(owner.getServers())
                .forEach(CloudServer::stop);
    }

    @EventHandler
    public void removeInvitations(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        Invitee.getInvitees().stream()
                .filter(invitee -> invitee.getPlayer().equals(p))
                .collect(Collectors.toList())
                .forEach(Invitee::revoke);
    }
}
