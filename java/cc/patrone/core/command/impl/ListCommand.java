package cc.patrone.core.command.impl;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.group.Group;
import cc.patrone.core.player.CoreProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ListCommand {

    private CorePlugin plugin;

    @Command(name = "list")
    public void list(CommandArgs args) {
        List<String> groups = new ArrayList();
        List<String> players = new ArrayList();
        for (Group group : Group.values()) {
            groups.add(ChatColor.translateAlternateColorCodes('&', group.getTabColor()) + group.getName());
            this.plugin.getServer().getOnlinePlayers().stream().forEach(p -> {
                CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(p);
                if (coreProfile.getGroup() == group) {
                    players.add(ChatColor.translateAlternateColorCodes('&', group.getTabColor()) + p.getName());
                }
            });
        }
        Collections.reverse(groups);
        Collections.reverse(players);
        args.getSender().sendMessage(Strings.join(groups, ChatColor.GRAY + ", "));
        args.getSender().sendMessage(ChatColor.GRAY + "(" + this.plugin.getServer().getOnlinePlayers().size() + "/" + this.plugin.getServer().getMaxPlayers() + ") " + Strings.join(players, ChatColor.GRAY + ", "));
    }
}
