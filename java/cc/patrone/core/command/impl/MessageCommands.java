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
public class MessageCommands {

    private CorePlugin plugin;

    @Command(name = "message", aliases = {"msg", "m", "tell", "whisper", "w", "pm"}, inGameOnly = true)
    public void message(CommandArgs args) {
        if (args.getArgs().length < 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage; /" + args.getLabel() + " <player> <message>");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        if (coreProfile.isToggledPms()) {
            args.getSender().sendMessage(ChatColor.RED + "You have private messaging disabled.");
            return;
        }
        CoreProfile targetProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
        if (targetProfile.isToggledPms()) {
            args.getSender().sendMessage(ChatColor.RED + "That player has private messaging disabled.");
            return;
        }
        coreProfile.setLastMessage(target);
        targetProfile.setLastMessage(args.getPlayer());
        String message = StringUtil.buildString(args.getArgs(), 1);
        args.getSender().sendMessage(ChatColor.GRAY + "(To " + targetProfile.getGroup().getTabColor() + target.getName() + ChatColor.GRAY + ") " + message);
        target.sendMessage(ChatColor.GRAY + "(From " + coreProfile.getGroup().getTabColor() + args.getSender().getName() + ChatColor.GRAY + ") " + message);
        return;
    }

    @Command(name = "reply", aliases = {"r"}, inGameOnly = true)
    public void reply(CommandArgs args) {
        if (args.getArgs().length < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage; /" + args.getLabel() + " <message>");
            return;
        }
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        if (coreProfile.getLastMessage() == null) {
            args.getSender().sendMessage(ChatColor.RED + "You have no one to reply to.");
            return;
        }
        if (!coreProfile.getLastMessage().isOnline()) {
            args.getSender().sendMessage(ChatColor.RED + "That player is no longer online.");
            return;
        }
        if (coreProfile.isToggledPms()) {
            args.getSender().sendMessage(ChatColor.RED + "You have private messaging disabled.");
            return;
        }
        Player target = coreProfile.getLastMessage();
        CoreProfile targetProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
        if (targetProfile.isToggledPms()) {
            args.getSender().sendMessage(ChatColor.RED + "That player has private messaging disabled.");
            return;
        }
        coreProfile.setLastMessage(target);
        targetProfile.setLastMessage(args.getPlayer());
        String message = StringUtil.buildString(args.getArgs(), 0);
        args.getSender().sendMessage(ChatColor.GRAY + "(To " + targetProfile.getGroup().getTabColor() + target.getName() + ChatColor.GRAY + ") " + message);
        target.sendMessage(ChatColor.GRAY + "(From " + coreProfile.getGroup().getTabColor() + args.getSender().getName() + ChatColor.GRAY + ") " + message);
        return;
    }
}
