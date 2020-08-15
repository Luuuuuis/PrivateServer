package de.luuuuuis.privateserver.bungee.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class Invitee {

    private final static List<Invitee> invitees = new ArrayList<>();
    private final ProxiedPlayer player;
    private final Owner owner;
    private final CloudServer cloudServer;

    public Invitee(ProxiedPlayer player, CloudServer cloudServer) {
        this.player = player;
        this.owner = cloudServer.getOwner();
        this.cloudServer = cloudServer;
    }

    public static Invitee getInvitee(ProxiedPlayer player, Owner owner) {
        return invitees.stream().filter(invitee -> invitee.getPlayer().equals(player) && invitee.getOwner().equals(owner)).findFirst().orElse(null);
    }

    public static Invitee getInvitee(ProxiedPlayer player, CloudServer cloudServer) {
        return invitees.stream().filter(invitee -> invitee.getPlayer().equals(player) && invitee.getCloudServer().equals(cloudServer)).findFirst().orElse(null);
    }

    public static List<Invitee> getInvitees() {
        return invitees;
    }

    public void sendInvitation() {
        TextComponent joinClick = new TextComponent("§a§lJOIN");
        joinClick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/privateserver join " + cloudServer.getName()));
        joinClick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aClick here to join")));

        TextComponent serverHover = new TextComponent("§a" + cloudServer.getGroup());
        serverHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§a§l" + cloudServer.getName())));

        player.sendMessage(new ComponentBuilder(Config.getInstance().getPrefix() + owner.getPlayer().getDisplayName() + " §7invited you to join him playing a private round of ")
                .append(serverHover).append("§7.\n")
                .append(Config.getInstance().getPrefix() + "Click here to join: ")
                .append(joinClick).create());

        invitees.add(this);
    }

    public void send() {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(cloudServer.getName());
        if (serverInfo == null) {
            sendMessage(Config.getInstance().getPrefix() + "§cThis invitation doesn't exists any longer.");
            return;
        }

        player.connect(serverInfo);

        //remove of invitees list
        invitees.remove(this);
    }

    public void revoke() {
        sendMessage(Config.getInstance().getPrefix() + "Your invitation to join §a" + owner.getPlayer().getDisplayName() + " §7just expired.");
        invitees.remove(this);
    }

    public void sendMessage(String message) {
        player.sendMessage(TextComponent.fromLegacyText(message));
    }

    public Owner getOwner() {
        return owner;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public CloudServer getCloudServer() {
        return cloudServer;
    }
}
