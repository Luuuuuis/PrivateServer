package de.luuuuuis.privateserver.bungee.commands;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.luuuuuis.privateserver.bungee.util.CloudServer;
import de.luuuuuis.privateserver.bungee.util.Config;
import de.luuuuuis.privateserver.bungee.util.Invitee;
import de.luuuuuis.privateserver.bungee.util.Owner;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;
import java.util.stream.Collectors;

public class PrivateServerCmd extends Command {

    public PrivateServerCmd() {
        super("privateserver", "", "pv");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "§cYou have to be a player. :( noʎ ʃǝǝɟ"));
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;
        if (!p.hasPermission("privateserver")) {
            sender.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + Config.getInstance().getMessages().get("noPerms")));
            return;
        }

        if (strings.length == 0) {
            p.sendMessage(TextComponent.fromLegacyText(defaultMessage()));
            return;
        }

        Owner owner = Owner.getOwner(p);

        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(p.getUniqueId());
        PlayerExecutorBridge playerExecutorBridge = new PlayerExecutorBridge();

        switch (strings[0].toLowerCase()) {
            case "start":
                if (strings.length > 3 || strings[1] == null || strings[1].isEmpty()) {
                    StringJoiner joiner = new StringJoiner(", ");
                    Config.getInstance().getGroups().forEach(joiner::add);

                    p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + String.format(Config.getInstance().getMessages().get("noGroupSpecified").toString(), joiner)));
                    return;
                }

                //create owner object
                if (owner == null) {
                    new Owner(p, playerExecutorBridge, cloudPlayer);
                }


                CloudServer cloudServer = new CloudServer(strings[1], p);

                if (strings.length == 3) {
                    try {
                        int templateID = Integer.parseInt(strings[2]);
                        if (templateID < 0 || templateID > Config.getInstance().getMaxServersPerUser()) {
                            throw new OutOfRangeException(templateID + " is not in the requested range");
                        }

                        if (!p.hasPermission("privateserver.premium")) {
                            cloudServer.getOwner().sendMessage(Config.getInstance().getPrefix() + "§cYou don't have permission to start a server with an ID.");
                            return;
                        }

                        if (!cloudServer.getGroupMode().equals(ServerGroupMode.STATIC)) {
                            cloudServer.getOwner().sendMessage(Config.getInstance().getPrefix() + "§cYou may only specify a ServerID with a static group");
                            return;
                        }

                        cloudServer.setName(templateID);
                    } catch (NumberFormatException e) {
                        Collection<Template> templates = CloudAPI.getInstance().getServerGroup(strings[1]).getTemplates();

                        if (templates.stream().anyMatch(template -> template.getName().equals(strings[2]))) {
                            cloudServer.setTemplate(Objects.requireNonNull(templates.stream().filter(template -> template.getName().equals(strings[2])).findFirst().orElse(null)).getName());
                        } else {
                            String joinedTemplates = templates.stream().map(Template::getName).collect(Collectors.joining(", "));
                            p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "You may only start one of these templates: §a" + joinedTemplates));
                            return;
                        }
                    } catch (OutOfRangeException e) {
                        p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "§c" + e.getMessage()));
                        return;
                    }
                }


                cloudServer.start();
                break;
            case "status":
                p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "Private Servers: "));
                if (p.hasPermission("privateserver.list")) {
                    CloudServer.getCloudServers().forEach(server -> p.sendMessage(
                            TextComponent.fromLegacyText("  §8- [§c" + server.getName() + "§8/§c" + server.getGroup() + "§8] §7Owned by §a" + server.getOwner().getPlayer().getDisplayName()
                                    + " §8(§7" + server.getPlayers().size() + "§8/§7" + server.getMaxPlayers() + "§8)")));
                } else {
                    if (owner == null) {
                        p.sendMessage(TextComponent.fromLegacyText("   §cThere are currently no servers running."));
                        return;
                    }

                    owner.getServers().forEach(server -> p.sendMessage(
                            TextComponent.fromLegacyText("  §8- [§c" + server.getName() + "§8/§c" + server.getGroup() + "§8]"
                                    + " §8(§7" + server.getPlayers().size() + "§8/§7" + server.getMaxPlayers() + "§8)")));

                }

                break;

            case "invite":
                if (owner == null) {
                    p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "§cYou have currently no servers running."));
                    return;
                }

                if (strings.length < 2 || strings[1] == null || strings[1].isEmpty()) {
                    owner.sendMessage(Config.getInstance().getPrefix() + "You have to address a player.");
                    return;
                }

                CloudServer privateserver = owner.getServers().stream().filter(privateServer -> privateServer.getName().equals(p.getServer().getInfo().getName())).findFirst().orElse(null);
                if (privateserver == null) {
                    owner.sendMessage(Config.getInstance().getPrefix() + "§cThis is not your private server!");
                    return;
                }

                List<String> toInvite = new ArrayList<>(Arrays.asList(strings).subList(1, strings.length));
                toInvite.forEach(inv -> {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(inv);
                    if (player == null || player.equals(p)) {
                        owner.sendMessage(Config.getInstance().getPrefix() + inv + " §7is not online!");
                        return;
                    }

                    if (Invitee.getInvitee(player, owner) != null) {
                        owner.sendMessage(Config.getInstance().getPrefix() + player.getDisplayName() + " §7already received an Invitation!");
                        return;
                    }

                    new Invitee(player, privateserver).sendInvitation();
                    owner.sendMessage(Config.getInstance().getPrefix() + player.getDisplayName() + " §7was invited.");
                });

                break;
            case "join":
                if (strings.length != 2 || strings[1] == null || strings[1].isEmpty())
                    return;

                Invitee invitee = Invitee.getInvitee(p, CloudServer.getCloudServer(strings[1]));
                if (invitee == null) {
                    CloudServer cs = CloudServer.getCloudServers().stream().filter(cloudServer1 -> cloudServer1.getOwner().getPlayer().getUniqueId().equals(p.getUniqueId())).findFirst().orElse(null);
                    if (cs != null) {
                        playerExecutorBridge.sendPlayer(cloudPlayer, cs.getName());
                        return;
                    }

                    p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "§cYou are not invited to the party :("));
                    return;
                }
                invitee.send();

                break;
            case "stop":
                if (owner == null) {
                    p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "§cYou have currently no servers running."));
                    return;
                }

                String server;

                if (strings.length != 2 || strings[1] == null || strings[1].isEmpty()) {
                    if (owner.getServers().stream().filter(privateServer -> privateServer.getName().equals(p.getServer().getInfo().getName())).findFirst().orElse(null) == null) {
                        owner.sendMessage(Config.getInstance().getPrefix() + "§cYou cannot stop this server.");
                        return;
                    }
                    server = p.getServer().getInfo().getName();
                } else {
                    server = strings[1];
                }

                CloudServer privateServer = owner.getServers().stream().filter(priServer -> priServer.getName().equalsIgnoreCase(server)).findFirst().orElse(null);
                if (privateServer != null) {
                    privateServer.remove();
                } else {
                    p.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "§cYou cannot stop that server."));
                }

                break;
            default:
                sender.sendMessage(TextComponent.fromLegacyText(defaultMessage()));
        }

    }

    private String defaultMessage() {
        return Config.getInstance().getPrefix() + "/pv start [GROUP] (Template/ServerID)\n" +
                Config.getInstance().getPrefix() + "/pv status\n" +
                Config.getInstance().getPrefix() + "/pv invite [PLAYER]\n" +
                Config.getInstance().getPrefix() + "/pv join [PLAYER/SERVER]\n" +
                Config.getInstance().getPrefix() + "/pv stop [SERVER]";
    }
}

class OutOfRangeException extends Exception {
    public OutOfRangeException(String errorMessage) {
        super(errorMessage);
    }
}