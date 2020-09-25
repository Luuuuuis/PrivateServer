package de.luuuuuis.privateserver.spigot.events;

import de.luuuuuis.privateserver.spigot.PrivateServer;
import de.luuuuuis.privateserver.spigot.util.reflections.NMSUtils;
import de.luuuuuis.privateserver.spigot.util.reflections.Reflections;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import net.jitse.npclib.internal.NPCBase;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Deprecated
public class NPCListener implements Listener {

    private final PrivateServer privateServer;
    private final Map<Player, NPC> NPCs = new HashMap<>();

    public NPCListener(PrivateServer privateServer) {
        this.privateServer = privateServer;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Location location = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        NPC npc = privateServer.getNpcLib().createNPC(Collections.singletonList("§aPrivate Server"));
        npc.setLocation(location);

        Objects.requireNonNull(NMSUtils.getProfile(p)).getProperties().get("textures").stream()
                .findFirst().ifPresent(playerProperty -> npc.setSkin(new Skin(playerProperty.getValue(), playerProperty.getSignature())));

        npc.create();
        npc.show(p);

        NPCs.put(p, npc);

        // Sets head rotation for the npc if the player is in a range of 15 blocks
        // Author: @yanjulang
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!p.isOnline()) {
                    cancel();
                    return;
                }

                if (npc.getLocation().getWorld() == p.getWorld() && p.getLocation().distance(npc.getLocation()) < 15) {
                    Vector look = p.getLocation().toVector().add(new Vector(0, 1, 0)).subtract(npc.getLocation().toVector().add(new Vector(0, 1.54, 0)));
                    float[] rots = vecToRots(look);

                    if (rots[0] > 180) {
                        rots[0] -= 360;
                    }

                    Object packet = null;
                    try {
                        packet = Objects.requireNonNull(NMSUtils.getNMSClass("PacketPlayOutEntityHeadRotation")).getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException instantiationException) {
                        instantiationException.printStackTrace();
                    }

                    Reflections.setValue(packet, "a", ((NPCBase) npc).getEntityId());
                    Reflections.setValue(packet, "b", toAngle(rots[0]));
                    NMSUtils.sendPacket(p, packet);
                    NMSUtils.sendPacket(p, new PacketPlayOutEntity.PacketPlayOutEntityLook(((NPCBase) npc).getEntityId(), toAngle(rots[0]), toAngle(rots[1]), true));
                }
            }

            private float[] vecToRots(Vector vector) {
                double x = vector.getX();
                double z = vector.getZ();
                if (x == 0.0D && z == 0.0D) {
                    return new float[]{0f, (float) (vector.getY() > 0.0D ? -90 : 90)};
                } else {
                    double theta = Math.atan2(-x, z);
                    float yaw = (float) Math.toDegrees((theta + 6.283185307179586D) % 6.283185307179586D);
                    double x2 = NumberConversions.square(x);
                    double z2 = NumberConversions.square(z);
                    double xz = Math.sqrt(x2 + z2);
                    float pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));
                    return new float[]{yaw, pitch};
                }
            }

            public byte toAngle(float value) {
                return (byte) ((int) (value * 256.0F / 360.0F));
            }
        }.runTaskTimerAsynchronously(privateServer, 0, 2);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        NPC npc = NPCs.get(p);
        npc.destroy();

        NPCs.remove(p);
    }

    @EventHandler
    public void onInteract(NPCInteractEvent e) {
        NPC npc = NPCs.get(e.getWhoClicked());

        if (e.getNPC().equals(npc)) {
            // do things with NPC. (e.g. open inv)
        }

    }

}
