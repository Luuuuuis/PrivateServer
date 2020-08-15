package de.luuuuuis.privateserver.spigot.util;

import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionEntity;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;

import java.util.*;

public class Permission extends PermissionEntity {

    private final List<String> permissions = new ArrayList<>(Config.getInstance().getPermissions());

    public Permission(UUID uniqueId, Map<String, Boolean> permissions, String prefix, String suffix, Collection<GroupEntityData> groups) {
        super(uniqueId, permissions, prefix, suffix, groups);
    }

    @Override
    public boolean hasPermission(PermissionPool permissionPool, String permission, String group) {
        if (this.permissions.contains(permission)) {
            System.out.println(permission + " true");
            return true;
        } else {
            System.out.println(permission + " false");
        }

        return super.hasPermission(permissionPool, permission, group);
    }
}
