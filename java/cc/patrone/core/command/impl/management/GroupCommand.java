package cc.patrone.core.command.impl.management;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.group.Group;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.util.UUIDFetcher;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public class GroupCommand {

    private CorePlugin plugin;

    @Command(name = "group", permission = "core.command.group")
    public void group(CommandArgs args) {
        args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <set:permission> <possible argument>");
        return;
    }

    @Command(name = "group.set", permission = "core.command.group")
    public void groupSet(CommandArgs args) {
        if (args.getArgs().length != 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <group>");
            return;
        }
        String name = args.getArgs(0);
        UUID uuid = UUIDFetcher.getUUID(name);
        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        String groupName = args.getArgs(1);
        Group group = Group.getGroup(groupName);
        if (group == null) {
            args.getSender().sendMessage(ChatColor.RED + "That group doesn't exist.");
            return;
        }
        this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM groups WHERE uuid = '" + uuid.toString() + "';");
        this.plugin.getManagerHandler().getSqlManager().query("INSERT INTO groups VALUES ('" + uuid.toString() + "', '" + group.toString() + "');");
        args.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Updated " + ChatColor.DARK_PURPLE + name + "'s " + ChatColor.LIGHT_PURPLE + "group to " + ChatColor.DARK_PURPLE + group.getName() + ChatColor.LIGHT_PURPLE + ".");
        Player target = this.plugin.getServer().getPlayer(name);
        if (target != null) {
            CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
            coreProfile.setGroup(group);
            target.sendMessage(ChatColor.LIGHT_PURPLE + "Your group has been updated to " + ChatColor.DARK_PURPLE + group.getName() + ChatColor.LIGHT_PURPLE + ".");
        }
        return;
    }

    @Command(name = "group.permission", permission = "core.command.group")
    public void groupPermission(CommandArgs args) {
        args.getSender().sendMessage(ChatColor.RED + "Usage; /" + args.getLabel() + " <add:remove> <group> <permission>");
        return;
    }

    @Command(name = "group.permission.add", permission = "core.command.group")
    public void groupPermissionAdd(CommandArgs args) {
        if (args.getArgs().length != 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <group> <permission>");
            return;
        }
        String groupName = args.getArgs(0);
        Group group = Group.getGroup(groupName);
        if (group == null) {
            args.getSender().sendMessage(ChatColor.RED + "That group doesn't exist.");
            return;
        }
        String permission = args.getArgs(1);
        group.getPermissions().add(permission);
        this.plugin.getManagerHandler().getSqlManager().query("INSERT INTO permission VALUES ('" + group.toString() + "', '" + permission + "');");
        this.plugin.getManagerHandler().getGroupManager().updatePermissions(group);
        args.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Given the group " + ChatColor.DARK_PURPLE + group.getName() + ChatColor.LIGHT_PURPLE + " the permission " + ChatColor.DARK_PURPLE + permission + ChatColor.LIGHT_PURPLE + ".");
        return;
    }

    @Command(name = "group.permission.remove", permission = "core.command.group")
    public void groupPermissionRemove(CommandArgs args) {
        if (args.getArgs().length != 2) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <group> <permission>");
            return;
        }
        String groupName = args.getArgs(0);
        Group group = Group.getGroup(groupName);
        if (group == null) {
            args.getSender().sendMessage(ChatColor.RED + "That group doesn't exist.");
            return;
        }
        String permission = args.getArgs(1);
        group.getPermissions().remove(permission);
        this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM permission WHERE type = '" + group.toString() + "' AND permission = '" + permission + "';");
        this.plugin.getManagerHandler().getGroupManager().updatePermissions(group);
        args.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Revoked the group " + ChatColor.DARK_PURPLE + group.getName() + ChatColor.LIGHT_PURPLE + " of the permission " + ChatColor.DARK_PURPLE + permission + ChatColor.LIGHT_PURPLE + ".");
        return;
    }
}
