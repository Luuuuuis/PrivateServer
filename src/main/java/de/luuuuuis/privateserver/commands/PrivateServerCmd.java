package de.luuuuuis.privateserver.commands;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.luuuuuis.privateserver.PrivateServer;
import de.luuuuuis.privateserver.util.CloudServer;
import de.luuuuuis.privateserver.util.Config;
import de.luuuuuis.privateserver.util.Owner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.StringJoiner;

public class PrivateServerCmd extends Command {

    public PrivateServerCmd() {
        super("privateserver", "", "pv");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cYou have to be a player. :( noʎ ʃǝǝɟ"));
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;
        if (!p.hasPermission("privateserver")) {
            sender.sendMessage(new TextComponent(Config.getInstance().getPrefix() + Config.getInstance().getMessages().get("noPerms")));
            return;
        }

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

                //create owner object
                Owner.getOwner(p, playerExecutorBridge, cloudPlayer);

                CloudServer cloudServer = new CloudServer(strings[1], Config.getInstance().getTemplate(), p);
                cloudServer.start();
                break;
            case "status":
                p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "Private Servers: "));
                if (p.hasPermission("privateserver.list")) {
                    PrivateServer.servers.forEach(server -> p.sendMessage(
                            new TextComponent("  §8- [§c" + server.getName() + "§8/§c" + server.getGroup() + "§8] §7Owned by §a" + server.getOwner().getPlayer().getDisplayName()
                                    + " §8(§7" + server.getPlayers().size() + "§8/§7" + server.getMaxPlayers() + "§8)")));
                } else {
                    Owner owner = Owner.getOwner(p);
                    if (owner == null) {
                        p.sendMessage(new TextComponent("   §cThere are currently no servers running."));
                        return;
                    }

                    owner.getServers().forEach(server -> p.sendMessage(
                            new TextComponent("  §8- [§c" + server.getName() + "§8/§c" + server.getGroup() + "§8]"
                                    + " §8(§7" + server.getPlayers().size() + "§8/§7" + server.getMaxPlayers() + "§8)")));

                }

                break;
            case "stop":
                Owner owner = Owner.getOwner(p);
                if (owner == null) {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cYou have currently no servers running."));
                    return;
                }

                List<CloudServer> privateServers = owner.getServers();
                String server;

                if (strings.length != 2 || strings[1] == null || strings[1].isEmpty()) {
                    if (privateServers.stream().filter(privateServer -> privateServer.getName().equals(p.getServer().getInfo().getName())).findFirst().orElse(null) != null) {
                        server = p.getServer().getInfo().getName();
                    } else {
                        p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cYou cannot stop this server."));
                        return;
                    }
                } else {
                    server = strings[1];
                }

                CloudServer privateServer = owner.getServers().stream().filter(priServer -> priServer.getName().equalsIgnoreCase(server)).findFirst().orElse(null);
                if (privateServer != null) {
                    privateServer.stop();
                } else {
                    p.sendMessage(new TextComponent(Config.getInstance().getPrefix() + "§cYou cannot stop that server."));
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