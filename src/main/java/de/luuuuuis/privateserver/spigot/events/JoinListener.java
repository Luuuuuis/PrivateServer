package de.luuuuuis.privateserver.spigot.events;

import de.luuuuuis.privateserver.spigot.PrivateServer;
import de.luuuuuis.privateserver.spigot.util.Owner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinListener implements Listener {

    /**
     * Sets permission for LoginEvent and asynchronously sets the permissions with an one second delay to override CloudNet again to make it work with every event afterwards.
     *
     * @param e Event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        Owner owner = PrivateServer.getInstance().getOwner();
        if (p.getUniqueId().equals(owner.getUuid())) {
            owner.setPlayer(p);
            owner.setPermissions();
            owner.setPermissionsWithDelay();
        }
    }


}
