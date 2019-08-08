package cc.patrone.core.command.impl.staff;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.util.StringUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class StaffChatCommand {

    private CorePlugin plugin;

    @Command(name = "staffchat", aliases = {"sc"}, permission = "core.command.staffchat", inGameOnly = true)
    public void staffChat(CommandArgs args) {
        if (args.getArgs().length == 0) {
            CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
            coreProfile.setStaffMode(!coreProfile.isStaffMode());
            args.getSender().sendMessage(coreProfile.isStaffMode() ? ChatColor.GREEN + "You have entered staff chat." : ChatColor.RED + "You have exited staff chat.");
            return;
        }
        String message = StringUtil.buildString(args.getArgs(), 0);
        this.plugin.getManagerHandler().getPlayerManager().sendStaffChatMessage(args.getPlayer(), message);
        return;
    }

}
