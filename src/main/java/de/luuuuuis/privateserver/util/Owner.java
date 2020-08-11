package de.luuuuuis.privateserver.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Owner {

    private final static List<Owner> owners = new ArrayList<>();
    private final List<CloudServer> servers = new ArrayList<>();
    private final ProxiedPlayer player;
    private final PlayerExecutorBridge playerExecutorBridge;
    private final CloudPlayer cloudPlayer;

    public Owner(ProxiedPlayer player, PlayerExecutorBridge playerExecutorBridge, CloudPlayer cloudPlayer) {
        this.player = player;
        this.playerExecutorBridge = playerExecutorBridge;
        this.cloudPlayer = cloudPlayer;

        owners.add(this);
    }

    /**
     * Use this to create the owner if not he doesn't exist
     *
     * @param player               ProxiedPlayer provided by BungeeCord API
     * @param playerExecutorBridge provided by CloudNetAPI
     * @param cloudPlayer          provided by CloudNetAPI
     * @return either an already existing Owner or a new one.
     */
    public static Owner getOwner(ProxiedPlayer player, PlayerExecutorBridge playerExecutorBridge, CloudPlayer cloudPlayer) {
        return owners.stream().filter(owner -> owner.getPlayer().equals(player)).findFirst().orElse(new Owner(player, playerExecutorBridge, cloudPlayer));
    }

    /**
     * Only use this if you know there is an Owner
     *
     * @param player ProxiedPlayer provided by BungeeCord API
     * @return the already existing owner or null
     */
    public static Owner getOwner(ProxiedPlayer player) {
        return owners.stream().filter(owner -> owner.getPlayer().equals(player)).findFirst().orElse(null);
    }

    /**
     * sends the loading title to the user.
     * Furthermore it moves the player after that on the server
     *
     * @param cloudServer the CloudServer you want to send the player
     */
    public void sendTitle(CloudServer cloudServer) {
        Thread th = new Thread(() -> {
            //send title & to server
            int i = 0;
            while (CloudAPI.getInstance().getServerInfo(cloudServer.getName()) == null || !CloudAPI.getInstance().getServerInfo(cloudServer.getName()).isOnline()) {
                i++;
                StringBuilder dots = new StringBuilder();
                for (int j = 0; j < i; j++) {
                    dots.append(".");
                }

                playerExecutorBridge.sendTitle(cloudPlayer, "", String.format(Config.getInstance().getMessages().get("startingTitle").toString(), dots), 0, 20, 0);

                if (i >= 3) i = 0;

                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sendPlayer(cloudServer);

        });
        th.start();
    }

    private void sendPlayer(CloudServer cloudServer) {
        playerExecutorBridge.sendPlayer(cloudPlayer, cloudServer.getName());
    }

    /**
     * Just a better method to send a messages to the  ProxiedPlayer.
     *
     * @param message a simple String
     */
    public void sendMessage(String message) {
        player.sendMessage(TextComponent.fromLegacyText(message));
    }

    /**
     * @param cloudServer the CloudServer you want to remove
     */
    public void removeServer(CloudServer cloudServer) {
        servers.remove(cloudServer);
        if (servers.isEmpty())
            owners.remove(this);
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public List<CloudServer> getServers() {
        return servers;
    }
}
