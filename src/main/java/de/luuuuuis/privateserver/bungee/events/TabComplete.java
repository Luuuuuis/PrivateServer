package de.luuuuuis.privateserver.bungee.events;

import de.luuuuuis.privateserver.bungee.util.CloudServer;
import de.luuuuuis.privateserver.bungee.util.Config;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TabComplete implements Listener {

    String regex = "^/(pv|privateserver)\\s(start|stop|join)\\s$";

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(e.getCursor().toLowerCase());
        if (matcher.find()) {
            switch (matcher.group(2)) {
                case "start":
                    e.getSuggestions().addAll(Config.getInstance().getGroups());
                    break;
                case "stop":
                case "join":
                    e.getSuggestions().addAll(CloudServer.getCloudServers().stream().filter(privateServer -> privateServer.getOwner().getPlayer().equals(e.getSender())).map(CloudServer::getName).collect(Collectors.toList()));
                    break;
            }
        }
    }
}
