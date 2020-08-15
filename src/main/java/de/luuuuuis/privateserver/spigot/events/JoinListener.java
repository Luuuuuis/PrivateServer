package de.luuuuuis.privateserver.spigot.events;

import de.luuuuuis.privateserver.spigot.PrivateServer;
import de.luuuuuis.privateserver.spigot.util.Owner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        Owner owner = PrivateServer.getInstance().getOwner();
        if (p.getUniqueId().equals(owner.getUuid())) {
            owner.setPlayer(p);
            owner.setPermissions();
        }

    }

}
