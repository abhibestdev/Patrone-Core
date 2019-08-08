package cc.patrone.core.command.impl.management;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class ChatCommands {

    private CorePlugin plugin;

    @Command(name = "clearchat", aliases = {"cc"}, permission = "core.command.clearchat")
    public void clearChat(CommandArgs args) {
        for (int i = 0; i <= 1000; i++) {
            this.plugin.getServer().getOnlinePlayers().stream().forEach(p -> {
                if (!p.hasPermission("core.staff")) {
                    p.sendMessage(" ");
                }
            });
        }
        this.plugin.getServer().broadcastMessage(ChatColor.RED + "Chat has been cleared by " + args.getSender().getName() + ".");
    }

    @Command(name = "mutechat", aliases = {"mc"}, permission = "core.command.mutechat")
    public void muteChat(CommandArgs args) {
        this.plugin.getManagerHandler().getSettingsManager().setChatMute(!this.plugin.getManagerHandler().getSettingsManager().isChatMute());
        this.plugin.getServer().broadcastMessage(this.plugin.getManagerHandler().getSettingsManager().isChatMute() ? ChatColor.RED + "Public chat has been muted." : ChatColor.GREEN + "Public chat has been unmuted.");
        return;
    }

    @Command(name = "slowchat", permission = "core.command.slowchat")
    public void slowChat(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <delay>");
            return;
        }
        if (!NumberUtils.isDigits(args.getArgs(0))) {
            args.getSender().sendMessage(ChatColor.RED + "Please enter a valid number.");
            return;
        }
        int delay = Integer.parseInt(args.getArgs(0));
        this.plugin.getManagerHandler().getSettingsManager().setChatDelay(delay);
        this.plugin.getServer().broadcastMessage(ChatColor.RED + "Chat slow has been updated to " + delay + " second" + (delay != 0 ? "s" : ""));
        return;
    }
}
