package de.luuuuuis.privateserver.spigot.util.reflections;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class NMSUtils {

    /**
     * Send a packet to a player without using direct nms imports
     *
     * @param player to send the packet to
     * @param packet to send
     */
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get an NMS Class by its Name
     * Class Path: Net.Ninecraft.Server.<VersionString>.ClassName
     *
     * @param name of the Class
     * @return Class
     */
    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an CraftBukkit Class by its Name
     * Class Path: org.bukkit.craftbukkit.<VersionString>.ClassName
     *
     * @param name of the Class
     * @return Class
     */
    public static Class<?> getCraftBukkitClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the NMS Version String from Bukkit.getServer() - package
     *
     * @return String
     */
    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Get the {@link GameProfile} from a player
     *
     * @param player to get the GameProfile from
     * @return GameProfile
     */
    public static GameProfile getProfile(Player player) {
        try {
            return (GameProfile) Objects.requireNonNull(NMSUtils.getHandle(player)).getClass().getMethod("getProfile").invoke(NMSUtils.getHandle(player));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an NMS - EntityPlayer - Object by a org.bukkit.Player - Object
     *
     * @param player (Bukkit Player)
     * @return NMS EntityPlayer Object
     */
    public static Object getHandle(Player player) {
        try {
            return player.getClass().getMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            return null;
        }
    }
}