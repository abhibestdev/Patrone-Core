package cc.patrone.core.manager.impl;

import cc.patrone.core.group.Group;
import cc.patrone.core.manager.Manager;
import cc.patrone.core.manager.ManagerHandler;
import cc.patrone.core.player.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerManager extends Manager {

    public PlayerManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }

    public void retrieveRank(Player player) {
        CoreProfile coreProfile = this.managerHandler.getProfileManager().getProfile(player);
        this.managerHandler.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.managerHandler.getPlugin(), () -> {
            String rankName = (String) this.managerHandler.getSqlManager().callback("SELECT * FROM groups WHERE uuid = '" + coreProfile.getUuid().toString() + "';", "type");
            if (rankName != null && Group.getGroup(rankName) != null) {
                coreProfile.setGroup(Group.getGroup(rankName));
                this.updatePermissions(player);
            }
        });
    }

    public void sendStaffChatMessage(Player player, String message) {
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            CoreProfile coreProfile = this.managerHandler.getProfileManager().getProfile(player);
            if (p.hasPermission("core.staff")) {
                p.sendMessage(ChatColor.DARK_PURPLE + "[SC] " + coreProfile.getGroup().getTabColor() + player.getName() + ChatColor.DARK_PURPLE + ": " + ChatColor.LIGHT_PURPLE + message);
            }
        });
    }

    public void sendStaffLogged(Player player, boolean in) {
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            CoreProfile coreProfile = this.managerHandler.getProfileManager().getProfile(player);
            if (p.hasPermission("core.staff")) {
                p.sendMessage(ChatColor.DARK_PURPLE + "[SC] " + coreProfile.getGroup().getTabColor() + player.getName() + ChatColor.LIGHT_PURPLE + " has logged " + (in ? "in" : "out") + ".");
            }
        });
    }

    public void sendStaffReport(Player player, Player target, String reason) {
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            if (p.hasPermission("core.staff")) {
                p.sendMessage(ChatColor.RED + player.getName() + ChatColor.AQUA + " has reported " + ChatColor.RED + target.getName() + ChatColor.AQUA + " for " + ChatColor.RED + reason);
            }
        });
    }

    public void resetPermissions(Player player) {
        CoreProfile coreProfile = this.managerHandler.getProfileManager().getProfile(player);
        if (coreProfile.getPermissionAttachment() == null) {
            coreProfile.setPermissionAttachment(player.addAttachment(this.managerHandler.getPlugin()));
        }
        for (String permission : coreProfile.getPermissionAttachment().getPermissions().keySet()) {
            coreProfile.getPermissionAttachment().setPermission(permission, false);
        }
    }

    public void updatePermissions(Player player) {
        resetPermissions(player);
        CoreProfile coreProfile = this.managerHandler.getProfileManager().getProfile(player);
        if (coreProfile.getPermissionAttachment() == null) {
            coreProfile.setPermissionAttachment(player.addAttachment(this.managerHandler.getPlugin()));
        }
        for (String permission : coreProfile.getGroup().getPermissions()) {
            coreProfile.getPermissionAttachment().setPermission(permission, true);
        }
        coreProfile.getPermissionAttachment().setPermission("minecraft.command.me", false);
    }

}
