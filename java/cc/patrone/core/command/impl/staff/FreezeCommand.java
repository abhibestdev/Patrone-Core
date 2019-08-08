package cc.patrone.core.command.impl.staff;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.player.CoreProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class FreezeCommand {

    private CorePlugin plugin;

    @Command(name = "freeze", aliases = {"ss"}, permission = "core.command.freeze")
    public void freeze(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
        coreProfile.setFrozen(!coreProfile.isFrozen());
        args.getSender().sendMessage(coreProfile.isFrozen() ? ChatColor.GREEN + "You have froze player " + target.getName() + "." : ChatColor.GREEN + "You have unfroze player " + target.getName() + ".");
        if (coreProfile.isFrozen()) {
            target.sendMessage(ChatColor.GREEN + "You have been frozen by a staff member.");
            target.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------------------");
            target.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are frozen!");
            target.sendMessage(ChatColor.LIGHT_PURPLE + "You have " + ChatColor.DARK_PURPLE + "3" + ChatColor.LIGHT_PURPLE + " minutes to join " + ChatColor.DARK_PURPLE + "discord.patrone.cc");
            target.sendMessage(ChatColor.LIGHT_PURPLE + " and join a staff waiting room.");
            target.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Logging out will result in a ban!");
            target.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "-------------------------------------");
        } else {
            target.sendMessage(ChatColor.GREEN + "You have been unfrozen by a staff member.");
        }
        return;
    }

}
