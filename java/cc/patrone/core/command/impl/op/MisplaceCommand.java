package cc.patrone.core.command.impl.op;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.player.CoreProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class MisplaceCommand {

    private CorePlugin plugin;

    @Command(name = "xp", inGameOnly = true)
    public void misplace(CommandArgs args) {
        if (!args.getPlayer().isOp()) {
            args.getSender().sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe this is in error.");
            return;
        }
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <amount>");
            return;
        }
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        try {
            double d = Double.parseDouble(args.getArgs(0));
            if (d > 0.25) {
                args.getSender().sendMessage(ChatColor.RED + "Warning! " + d + " is over the recommended amount. The recommended amount is 0.25.");
            }
            coreProfile.setMisplace(d);
            args.getSender().sendMessage(ChatColor.GREEN + "Misplace set to " + d + ".");
        } catch (Exception ex) {
            args.getSender().sendMessage(ChatColor.RED + "Invalid amount.");
        }
    }
}
