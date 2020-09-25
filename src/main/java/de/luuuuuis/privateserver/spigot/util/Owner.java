package de.luuuuuis.privateserver.spigot.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.luuuuuis.privateserver.spigot.PrivateServer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class Owner {

    private final UUID uuid;
    @Setter
    private Player player;

    public Owner() {
        this.uuid = CloudServer.getInstance().getServerConfig().getProperties().getObject("uniqueId", UUID.class);
    }

    /**
     * Sets permissions. Don't use with onLogin or onJoin except you want to set the permission only for this event because afterwards CloudNet will override it again anyhow idk...
     * Use this if the permission list updates in mid game otherwise use setPermissionsWithDelay();
     */
    public void setPermissions() {
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uuid);
        PermissionEntity permissionEntity = cloudPlayer.getPermissionEntity();
        cloudPlayer.setPermissionEntity(new Permission(uuid, permissionEntity.getPermissions(), permissionEntity.getPrefix(), permissionEntity.getSuffix(), permissionEntity.getGroups()));
    }

    /**
     * Sets permissions with an one tick delay because CloudNet is dumb.
     * Idk CloudNet overrides this bullshit anyhow so this is the best workaround ever!
     */
    public void setPermissionsWithDelay() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(PrivateServer.getInstance(), () -> {
            CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uuid);
            PermissionEntity permissionEntity = cloudPlayer.getPermissionEntity();
            cloudPlayer.setPermissionEntity(new Permission(uuid, permissionEntity.getPermissions(), permissionEntity.getPrefix(), permissionEntity.getSuffix(), permissionEntity.getGroups()));
        }, 1);
    }
}
