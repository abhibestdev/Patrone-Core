package cc.patrone.core.command.impl;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PingCommand {

    private CorePlugin plugin;

    @Command(name = "ping", inGameOnly = true)
    public void ping(CommandArgs args) {
        if (args.getArgs().length != 1) {
            int ping = ((CraftPlayer) args.getPlayer()).getHandle().ping;
            args.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Your ping is " + ChatColor.DARK_PURPLE + ping + ChatColor.LIGHT_PURPLE + ".");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        int ping = ((CraftPlayer) target).getHandle().ping;
        args.getSender().sendMessage(ChatColor.DARK_PURPLE + target.getName() + "'s " + ChatColor.LIGHT_PURPLE + "ping is " + ChatColor.DARK_PURPLE + ping + ChatColor.LIGHT_PURPLE + ".");
        return;
    }

}
