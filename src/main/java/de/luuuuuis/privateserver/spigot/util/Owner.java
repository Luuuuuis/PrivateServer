package de.luuuuuis.privateserver.spigot.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Owner {

    private final UUID uuid;
    private Player player;

    public Owner() {
        this.uuid = CloudAPI.getInstance().getServerInfo(CloudAPI.getInstance().getServerId()).getServerConfig().getProperties().getObject("uniqueId", UUID.class);
    }

    public void setPermissions() {
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uuid);
        PermissionEntity permissionEntity = cloudPlayer.getPermissionEntity();
        cloudPlayer.setPermissionEntity(new Permission(uuid, permissionEntity.getPermissions(), permissionEntity.getPrefix(), permissionEntity.getSuffix(), permissionEntity.getGroups()));
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }
}
