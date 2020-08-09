package de.luuuuuis.privateserver.events;

import de.luuuuuis.privateserver.PrivateServer;
import de.luuuuuis.privateserver.util.Config;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabComplete implements Listener {

    String regex = "^/(pv|privateserver)\\s(start|stop)\\s";

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
                    List<String> servers = new ArrayList<>();
                    PrivateServer.servers.stream().filter(privateServer -> privateServer.getOwner().equals(e.getSender())).forEach(server -> servers.add(server.getName()));
                    e.getSuggestions().addAll(servers);
                    break;
            }
        }
    }
}
