package cc.patrone.core.command.impl.staff;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.punishment.PunishmentType;
import cc.patrone.core.punishment.impl.MutePunishment;
import cc.patrone.core.util.StringUtil;
import cc.patrone.core.util.TimeUtil;
import cc.patrone.core.util.UUIDFetcher;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PunishmentCommands {

    private CorePlugin plugin;

    @Command(name = "ban", aliases = {"mute", "kick", "blacklist"}, permission = "core.command.punish")
    public void punish(CommandArgs args) {
        PunishmentType punishmentType = PunishmentType.valueOf(args.getLabel().toUpperCase());
        if (!args.getSender().hasPermission("core.command." + punishmentType.toString().toLowerCase())) {
            return;
        }
        if (args.getArgs().length < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> " + (punishmentType.isCanBeTemp() ? "<time> " : "") + "<reason> <-s>");
            return;
        }
        String playerName = args.getArgs(0);
        UUID uuid = UUIDFetcher.getUUID(playerName);
        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "Player not found.");
            return;
        }
        long time = (args.getArgs().length > 1) ? TimeUtil.parseTime(args.getArgs(1)) : 1L;
        String timeFormatted = TimeUtil.millisToRoundedTime(time);
        String reason = "";
        boolean silent = false;
        Timestamp expiry = null;
        for (int i = (time > 1L && (punishmentType.isCanBeTemp()) ? 2 : 1); i < args.getArgs().length; i++) {
            if (args.getArgs(i).equalsIgnoreCase("-s")) {
                silent = true;
            } else {
                reason = reason + (reason.equalsIgnoreCase("") ? "" : " ") + args.getArgs(i);
            }
        }
        if (reason.equalsIgnoreCase("") || reason.equalsIgnoreCase(" ")) {
            reason = "Misconduct";
        }
        if (time > 1L) {
            expiry = new Timestamp(System.currentTimeMillis() + time);
        }
        String broadcast = ChatColor.DARK_PURPLE + playerName + ChatColor.LIGHT_PURPLE + " has been " + punishmentType.getPastTense() + " by " + ChatColor.DARK_PURPLE + args.getSender().getName() + ChatColor.LIGHT_PURPLE + (punishmentType.isCanBeTemp() ? " for a period of " + ChatColor.DARK_PURPLE + (time > 1L ? timeFormatted : "forever") : "") + ChatColor.LIGHT_PURPLE + " for " + ChatColor.DARK_PURPLE + reason + ChatColor.LIGHT_PURPLE + ".";
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        switch (punishmentType) {
            case KICK: {
                if (target == null) {
                    args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
                    break;
                }
                target.kickPlayer(ChatColor.RED + "You have been kicked from the server for:\n" + ChatColor.GRAY + reason);
                break;
            }
            case BLACKLIST: {
                OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(playerName);
                if (!offlinePlayer.hasPlayedBefore()) {
                    args.getSender().sendMessage(ChatColor.RED + "That player has never played before.");
                    return;
                }
                List<String> ips = StringUtil.getList((String) this.plugin.getManagerHandler().getSqlManager().callback("SELECT * FROM iphistory WHERE uuid = '" + uuid.toString() + "';", "ips"));
                for (String ip : ips) {
                    this.plugin.getManagerHandler().getSqlManager().query("INSERT INTO blacklists VALUES ('" + uuid.toString() + "', '" + reason + "', '" + (args.getSender() instanceof Player ? args.getPlayer().getUniqueId() : "CONSOLE") + "', '" + ip + "');");
                }
                for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                    if (ips.contains(player.getAddress().toString().split(":")[0].replace("/", ""))) {
                        player.kickPlayer(ChatColor.RED + "You have been blacklisted from this server for:\n" + ChatColor.GRAY + reason + " \n\n" + ChatColor.RED + "This punishment can not be appealed.");
                    }
                }
                break;
            }
            case BAN: {
                this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + uuid.toString() + "' and type = 'ban';");
                this.plugin.getManagerHandler().getSqlManager().query("INSERT INTO punishments VALUES ('" + uuid.toString() + "', 'ban', '" + reason + "', '" + (time > 1L ? expiry.getTime() : "Never") + "', '" + TimeUtil.dateFormat.format(new Date()) + "', '" + (args.getSender() instanceof Player ? args.getPlayer().getUniqueId() : "CONSOLE") + "' )");
                if (target != null) {
                    target.kickPlayer(ChatColor.RED + "You have been banned from this server for:\n" + ChatColor.GRAY + reason + " \n\n" + ChatColor.RED + "Expiry: " + ChatColor.GRAY + (time > 1L ? timeFormatted : "Never") + ChatColor.RED + "\n\nTo appeal, please join discord.patrone.cc");
                }
                break;
            }
            case MUTE: {
                this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + uuid.toString() + "' and type = 'mute';");
                this.plugin.getManagerHandler().getSqlManager().query("INSERT INTO punishments VALUES ('" + uuid.toString() + "', 'mute', '" + reason + "', '" + (time > 1L ? expiry.getTime() : "Never") + "', '" + TimeUtil.dateFormat.format(new Date()) + "', '" + (args.getSender() instanceof Player ? args.getPlayer().getUniqueId() : "CONSOLE") + "' )");
                if (target != null) {
                    CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
                    coreProfile.setMutePunishment(new MutePunishment(reason, (time > 1L ? String.valueOf(expiry.getTime()) : "Never"), null));
                }
                break;
            }
        }
        if (silent) {
            this.plugin.getServer().broadcast(ChatColor.GRAY + "[Silent] " + broadcast, "core.silent");
        } else {
            this.plugin.getServer().broadcastMessage(broadcast);
        }
    }

    @Command(name = "unban", aliases = {"unmute", "unblacklist", "pardon"}, permission = "core.command.unpunish")
    public void unpunish(CommandArgs args) {
        PunishmentType punishmentType = PunishmentType.valueOf(args.getLabel().replaceFirst("pardon", "ban").replace("un", "").toUpperCase());
        if (!args.getSender().hasPermission("core.command." + punishmentType.toString().replace("un", "").toLowerCase())) {
            return;
        }
        if (args.getArgs().length < 1) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> <-s>");
            return;
        }
        String playerName = args.getArgs(0);
        UUID uuid = UUIDFetcher.getUUID(playerName);
        if (uuid == null) {
            args.getSender().sendMessage(ChatColor.RED + "Could not find player.");
            return;
        }
        Player target = this.plugin.getServer().getPlayer(playerName);
        boolean silent = args.getArgs().length > 1 && args.getArgs(1).equalsIgnoreCase("-s");
        String broadcast = ChatColor.DARK_PURPLE + playerName + ChatColor.LIGHT_PURPLE + " was un" + punishmentType.getPastTense() + " by " + ChatColor.DARK_PURPLE + args.getSender().getName() + ChatColor.LIGHT_PURPLE + ".";
        switch (punishmentType) {
            case BLACKLIST: {
                this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM blacklists WHERE uuid = '" + uuid.toString() + "';");
                break;
            }
            case BAN: {
                this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + uuid.toString() + "' AND type = 'ban';");
                break;
            }
            case MUTE: {
                this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + uuid.toString() + "' AND type = 'mute';");
                if (target != null) {
                    CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(target);
                    coreProfile.setMutePunishment(null);
                }
                break;
            }
        }
        if (silent) {
            this.plugin.getServer().broadcast(ChatColor.GRAY + "[Silent] " + broadcast, "core.silent");
        } else {
            this.plugin.getServer().broadcastMessage(broadcast);
        }
    }
}
