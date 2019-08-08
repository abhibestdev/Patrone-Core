package cc.patrone.core.command.impl.staff;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.util.StringUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class BroadcastCommand {

    private CorePlugin plugin;

    @Command(name = "broadcast", aliases = {"bc", "alert", "shout"}, permission = "core.command.broadcast")
    public void broadcastCommand(CommandArgs args) {
        if (args.getArgs().length == 0) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <message>");
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Alert" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', StringUtil.buildString(args.getArgs(), 0)));
        return;
    }
}
