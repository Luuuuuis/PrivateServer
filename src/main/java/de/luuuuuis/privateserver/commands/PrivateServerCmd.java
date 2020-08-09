package de.luuuuuis.privateserver.commands;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.luuuuuis.privateserver.PrivateServer;
import de.luuuuuis.privateserver.util.CloudServer;
import de.luuuuuis.privateserver.util.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.StringJoiner;

public class PrivateServerCmd extends Command {

    public PrivateServerCmd() {
        super("privateserver", "privateserver", "pv");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        ProxiedPlayer p = (ProxiedPlayer) sender;
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());
        PlayerExecutorBridge playerExecutorBridge = new PlayerExecutorBridge();

        if (strings.length == 0) {
            p.sendMessage(new TextComponent(defaultMessage()));
            return;
        }

        switch (strings[0].toLowerCase()) {
            case "start":
                if (strings.length != 2 || strings[1] == null || strings[1].isEmpty()) {
                    StringJoiner joiner = new StringJoiner(", ");
                    Config.getInstance().getGroups().forEach(joiner::add);

                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + String.format(Config.getInstance().getMessages().get("noGroupSpecified").toString(), joiner)));
                    return;
                }
                String group = strings[1];

                if (CloudAPI.getInstance().getServerGroupData(group) == null) {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cThis server group doesnt exist!"));
                    return;
                }

                if (PrivateServer.servers.size() >= Config.getInstance().getMaxServersRunning()) {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cThere are too many servers running! Please try again later."));
                    return;
                }
                long serverOfUser = PrivateServer.servers.stream().filter(cloudServer -> cloudServer.getOwner().equals(p)).count();
                if (serverOfUser >= Config.getInstance().getMaxServersPerUser()) {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cYour server quota is exhausted! Stop a server before starting a new one."));
                    return;
                }


                CloudServer cloudServer = new CloudServer(strings[1], Config.getInstance().getTemplate(), p);
                cloudServer.start();

                p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + String.format(Config.getInstance().getMessages().get("startingServer").toString(), group)));


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
                    playerExecutorBridge.sendPlayer(cloudPlayer, cloudServer.getName());

                });
                th.start();

                break;
            case "status":
                p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "Private Servers: "));
                PrivateServer.servers.forEach(server -> p.sendMessage(
                        new TextComponent("  §8- [§c" + server.getName() + "§8/§c" + server.getGroup() + "§8] §7Owned by §a" + server.getOwner()
                                + " §8(§7" + server.getPlayers().size() + "§8/§7" + server.getMaxPlayers() + "§8)")));
                break;
            case "stop":
                if (strings.length != 2 || strings[1] == null || strings[1].isEmpty()) {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + Config.getInstance().getMessages().get("noGroupSpecified")));
                    return;
                }
                String server = strings[1];

                CloudServer privateServer = PrivateServer.servers.stream().filter(hisServer -> hisServer.getName().equalsIgnoreCase(server) && hisServer.getOwner().equals(p)).findFirst().orElse(null);
                if (privateServer != null) {
                    privateServer.stop();
                    PrivateServer.servers.remove(privateServer);
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "Stopping " + server));
                } else {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cThis is not your private server!"));
                }
                break;
            default:
                sender.sendMessage(new TextComponent(defaultMessage()));
        }

    }

    private String defaultMessage() {
        return Config.getInstance().getPrefix() + "/pv start [GROUP]\n"
                + Config.getInstance().getPrefix() + "/pv status\n"
                + Config.getInstance().getPrefix() + "/pv stop [SERVER]";
    }
}