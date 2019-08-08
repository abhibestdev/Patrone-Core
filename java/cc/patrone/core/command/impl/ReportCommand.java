package cc.patrone.core.command.impl;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.util.StringUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class ReportCommand {

    private CorePlugin plugin;

    @Command(name = "report", inGameOnly = true)
    public void report(CommandArgs args) {
        if (args.getArgs().length < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <reason>");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        if (System.currentTimeMillis() - coreProfile.getLastReport() <= 120000) {
            args.getSender().sendMessage(ChatColor.RED + "You can only report every 2 minutes.");
            return;
        }
        String report = StringUtil.buildString(args.getArgs(), 1);
        this.plugin.getManagerHandler().getPlayerManager().sendStaffReport(args.getPlayer(), target, report);
        coreProfile.setLastReport(System.currentTimeMillis());
        args.getSender().sendMessage(ChatColor.GREEN + "Your report has been received.");
    }
}
