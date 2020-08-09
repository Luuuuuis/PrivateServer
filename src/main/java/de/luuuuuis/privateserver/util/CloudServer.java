package de.luuuuuis.privateserver.util;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.server.template.TemplateResource;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.luuuuuis.privateserver.PrivateServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class CloudServer {

    private final String group, template;
    private final ProxiedPlayer owner;
    private String name;

    public CloudServer(String group, String template, ProxiedPlayer owner) {
        this.group = group;
        this.template = template;
        this.owner = owner;

        name = createName();

        PrivateServer.servers.add(this);
    }

    public void start() {
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
    }

    public void stop() {
        CloudAPI.getInstance().stopServer(name);
    }

    private String createName() {
        name = "PV-" + new Random().nextInt(1000);

        //check if already there
        while (PrivateServer.servers.stream().anyMatch(server -> server.name.equals(name))) {
            name = "PV-" + new Random().nextInt(1000);
        }

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

    public String getGroup() {
        return group;
    }

    public String getTemplate() {
        return template;
    }

    public ProxiedPlayer getOwner() {
        return owner;
    }
}
