package de.luuuuuis.privateserver.commands;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.luuuuuis.privateserver.PrivateServerSystem;
import de.luuuuuis.privateserver.util.Config;
import de.luuuuuis.privateserver.util.PrivateServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
            p.sendMessage(defaultMessage());
            return;
        }

        switch (strings[0].toLowerCase()) {
            case "start":
                Thread th = new Thread(() -> {
                    if (strings.length != 2 || strings[1] == null || strings[1].isEmpty()) {
                        p.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getMessages().get("noGroupSpecified"));
                        return;
                    }
                    String group = strings[1];

                    if (CloudAPI.getInstance().getServerGroupData(group) == null) {
                        p.sendMessage(Config.getInstance().getPrefix() + "§cThis server group doesnt exist!");
                        return;
                    }

                    if (PrivateServerSystem.servers.size() >= Config.getInstance().getMaxServersRunning()) {
                        p.sendMessage(Config.getInstance().getPrefix() + "§cThere are too many servers running! Please try again later.");
                        return;
                    }
                    long serverOfUser = PrivateServerSystem.servers.stream().filter(privateServer -> privateServer.getOwner().equals(p)).count();
                    if (serverOfUser >= Config.getInstance().getMaxServersPerUser()) {
                        p.sendMessage(Config.getInstance().getPrefix() + "§cYour server quota is exhausted! Stop a server before starting a new one.");
                        return;
                    }


                    PrivateServer privateServer = new PrivateServer(strings[1], Config.getInstance().getTemplate(), p);
                    privateServer.start();

                    p.sendMessage(Config.getInstance().getPrefix() + String.format(Config.getInstance().getMessages().get("startingServer").toString(), group));

                    int i = 0;
                    while (CloudAPI.getInstance().getServerInfo(privateServer.getName()) == null || !CloudAPI.getInstance().getServerInfo(privateServer.getName()).isOnline()) {
                        i++;
                        StringBuilder dots = new StringBuilder();
                        for (int j = 0; j < i; j++) {
                            dots.append(".");
                        }

                        playerExecutorBridge.sendTitle(cloudPlayer, "", String.format(Config.getInstance().getMessages().get("startingTitle").toString(), dots), 0, 20, 0);

                        if (i >= 3) i = 0;

                        try {
                            // wait 10 milliseconds
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    playerExecutorBridge.sendPlayer(cloudPlayer, privateServer.getName());
                });
                th.start();


                break;
            case "status":
                p.sendMessage(Config.getInstance().getPrefix() + "Private Servers: ");
                PrivateServerSystem.servers.forEach(server -> p.sendMessage("  §8- [§c" + server.getName() + "§8/§c" + server.getGroup() + "§8] §7Owned by §a" + server.getOwner()));
                break;
            case "stop":
                if (strings.length != 2 || strings[1] == null || strings[1].isEmpty()) {
                    p.sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getMessages().get("noGroupSpecified"));
                    return;
                }
                String server = strings[1];

                PrivateServer privateServer = PrivateServerSystem.servers.stream().filter(hisServer -> hisServer.getName().equalsIgnoreCase(server) && hisServer.getOwner().equals(p)).findFirst().orElse(null);
                if (privateServer != null) {
                    privateServer.stop();
                    PrivateServerSystem.servers.remove(privateServer);
                    p.sendMessage(Config.getInstance().getPrefix() + "Stopping " + server);
                } else {
                    p.sendMessage(Config.getInstance().getPrefix() + "§cThis is not your private server!");
                }
                break;
            default:
                sender.sendMessage(defaultMessage());
        }

    }

    private String defaultMessage() {
        return Config.getInstance().getPrefix() + "/pv start [GROUP]\n"
                + Config.getInstance().getPrefix() + "/pv status\n"
                + Config.getInstance().getPrefix() + "/pv stop [SERVER]";
    }
}