package de.luuuuuis.privateserver.bungee.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class CloudServer {

    private final static List<CloudServer> cloudServers = new ArrayList<>();
    private final String group, template;
    private final Owner owner;
    private final ServerGroupMode groupMode;
    private String name;

    public CloudServer(String group, ProxiedPlayer player) {
        this.group = group;
        this.template = Config.getInstance().getTemplate();
        this.owner = Owner.getOwner(player);

        name = createName();
        groupMode = CloudAPI.getInstance().getServerGroupData(group).getMode();
    }

    public static CloudServer getCloudServer(String serverName) {
        return cloudServers.stream().filter(cloudServer -> cloudServer.getName().equals(serverName)).findFirst().orElse(null);
    }

    public static List<CloudServer> getCloudServers() {
        return cloudServers;
    }

    public void start() {
        if (!isAllowed())
            return;


        CloudAPI.getInstance().startGameServer(CloudAPI.getInstance().getServerGroupData(group),
                new ServerConfig(true, "null", new Document("uniqueId", owner.getUniqueId()), System.currentTimeMillis()),
                Config.getInstance().getMemory(),
                new String[0],
                new Template(template, TemplateResource.LOCAL, null, new String[0], Collections.emptyList()),
                name,
                false,
                true,
                new Properties(),
                null,
                Collections.emptyList());


        cloudServers.add(this);
        owner.getServers().add(this);

        owner.sendMessage(Config.getInstance().getPrefix() + String.format(Config.getInstance().getMessages().get("startingServer").toString(), group));
        owner.sendTitle(this);
    }

    private void stop() {
        if (CloudAPI.getInstance().getServerInfo(name) != null) {
            getPlayers().forEach(player -> {
                ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
                proxiedPlayer.sendMessage(TextComponent.fromLegacyText(Config.getInstance().getPrefix() + "The server you were on was shutdown. You were moved to the fallback server."));
            });

            CloudAPI.getInstance().stopServer(name);
        }
    }

    public void remove() {
        owner.sendMessage(Config.getInstance().getPrefix() + "Stopping " + name + "...");

        // remove from Invitee list
        Invitee.getInvitees().stream()
                .filter(invitee -> invitee.getOwner().equals(owner))
                .collect(Collectors.toList())
                .forEach(Invitee::revoke);

        stop();
        cloudServers.remove(this);
        owner.removeServer(this);
    }

    private boolean isAllowed() {
        List<String> groups = Config.getInstance().getGroups();
        if (CloudAPI.getInstance().getServerGroupData(group) == null || !groups.contains(group)) {
            StringJoiner joiner = new StringJoiner(", ");
            groups.forEach(joiner::add);

            owner.sendMessage(Config.getInstance().getPrefix() + String.format(Config.getInstance().getMessages().get("noGroupSpecified").toString(), joiner));
            return false;
        }

        if (cloudServers.size() >= Config.getInstance().getMaxServersRunning()) {
            owner.sendMessage(Config.getInstance().getPrefix() + "§cThere are too many servers running! Please try again later.");
            return false;
        }

        long serverOfUser = cloudServers.stream().filter(cloudServer -> cloudServer.getOwner().equals(owner)).count();
        int maxServersPerUser = (owner.getPlayer().hasPermission("privateserver.premium") ? Config.getInstance().getMaxServersPerUser() : 1);
        if (serverOfUser >= maxServersPerUser) {
            owner.sendMessage(Config.getInstance().getPrefix() + "§cYour server quota is exhausted! Stop a server before starting a new one.");
            return false;
        }

        return true;
    }

    private String createName() {
        //check if already there
        int i = 1;
        do {
            name = owner.getPlayer().getName() + "-" + i;
            i++;
        } while (cloudServers.stream().anyMatch(server -> server.name.equals(name)));

        return name;
    }

    public List<String> getPlayers() {
        return CloudAPI.getInstance().getServerInfo(name).getPlayers();
    }

    public int getMaxPlayers() {
        return CloudAPI.getInstance().getServerInfo(name).getMaxPlayers();
    }

    public String getName() {
        return name;
    }

    public void setName(int ID) {
        this.name = owner.getPlayer().getName() + "-" + ID;
    }

    public String getGroup() {
        return group;
    }

    public ServerGroupMode getGroupMode() {
        return groupMode;
    }

    public Owner getOwner() {
        return owner;
    }


}
