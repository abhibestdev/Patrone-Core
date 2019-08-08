package cc.patrone.core.listener;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.punishment.impl.MutePunishment;
import cc.patrone.core.sql.QueryType;
import cc.patrone.core.util.SkinUtil;
import cc.patrone.core.util.StringUtil;
import cc.patrone.core.util.TimeUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class PlayerListener implements Listener {

    private CorePlugin plugin;

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (System.currentTimeMillis() - this.plugin.getStartupTime() <= 10000) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "The server is still starting.");
            return;
        }
        this.plugin.getManagerHandler().getProfileManager().addUUID(event.getUniqueId(), event.getName());
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(event.getUniqueId());
        try {
            this.plugin.getManagerHandler().getSqlManager().getSqlConnection().query(QueryType.POST, "DELETE FROM ip WHERE uuid = '" + coreProfile.getUuid().toString() + "';");
            this.plugin.getManagerHandler().getSqlManager().getSqlConnection().query(QueryType.POST, "INSERT INTO ip VALUES ('" + coreProfile.getUuid().toString() + "', '" + event.getAddress().toString().replaceFirst("/", "") + "');");
            final ResultSet IPset = (ResultSet) this.plugin.getManagerHandler().getSqlManager().getSqlConnection().query(QueryType.RS, "SELECT * FROM iphistory WHERE uuid = '" + coreProfile.getUuid().toString() + "';");
            if (IPset.next()) {
                List<String> ips = StringUtil.getList(IPset.getString("ips"));
                if (!ips.contains(event.getAddress().toString().replace("/", ""))) {
                    ips.add(event.getAddress().toString().replace("/", ""));
                    this.plugin.getManagerHandler().getSqlManager().getSqlConnection().query(QueryType.POST, "DELETE FROM iphistory WHERE uuid = '" + coreProfile.getUuid().toString() + "';");
                    this.plugin.getManagerHandler().getSqlManager().getSqlConnection().query(QueryType.POST, "INSERT INTO iphistory VALUES ('" + coreProfile.getUuid().toString() + "', '" + StringUtil.listToString(ips) + "');");
                }
            } else {
                this.plugin.getManagerHandler().getSqlManager().getSqlConnection().query(QueryType.POST, "INSERT INTO iphistory VALUES ('" + coreProfile.getUuid().toString() + "', '" + event.getAddress().toString().replaceFirst("/", "") + "');");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Object blacklistData = this.plugin.getManagerHandler().getSqlManager().callback("SELECT * FROM blacklists WHERE ip = '" + event.getAddress().toString().split(":")[0].replace("/", "") + "';", "reason");
        if (blacklistData != null) {
            String reason = (String) blacklistData;
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You have been blacklisted from this server for:\n" + ChatColor.GRAY + reason + " \n\n" + ChatColor.RED + "This punishment can not be appealed.");
            return;
        }

        Object[] banData = this.plugin.getManagerHandler().getSqlManager().callbackArray("SELECT * FROM punishments WHERE uuid = '" + event.getUniqueId().toString() + "' AND type = 'ban';", "reason", "expire");
        if (banData != null) {
            boolean banned = true;
            String reason = (String) banData[0];
            String expire = "Never";
            if (!((String) banData[1]).equalsIgnoreCase("never")) {
                Timestamp expiry = TimeUtil.fromMillis(Long.parseLong((String) banData[1]));
                if (TimeUtil.getCurrentTimestamp().after(expiry)) {
                    this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + event.getUniqueId().toString() + "' AND type = 'ban';");
                    banned = false;
                } else {
                    String roundedTime = TimeUtil.millisToRoundedTime(Long.parseLong((String) banData[1]) - System.currentTimeMillis());
                    expire = roundedTime;
                }
            }
            if (banned) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You have been banned from this server for:\n" + ChatColor.GRAY + reason + " \n\n" + ChatColor.RED + "Expiry: " + ChatColor.GRAY + expire + ChatColor.RED + "\n\nTo appeal, please join discord.patrone.cc");
                return;
            }
        }
        Object[] muteData = this.plugin.getManagerHandler().getSqlManager().callbackArray("SELECT * FROM punishments WHERE uuid = '" + event.getUniqueId().toString() + "' AND type = 'mute'", "reason", "expire");
        if (muteData != null) {
            boolean muted = true;
            String reason = (String) muteData[0];
            String expire = "Never";
            if (!((String) muteData[1]).equalsIgnoreCase("never")) {
                Timestamp expiry = TimeUtil.fromMillis(Long.parseLong((String) muteData[1]));
                if (TimeUtil.getCurrentTimestamp().after(expiry)) {
                    this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + event.getUniqueId().toString() + "' AND type = 'mute';");
                    muted = false;
                } else {
                    String roundedTime = TimeUtil.millisToRoundedTime(Long.parseLong((String) muteData[1]) - System.currentTimeMillis());
                    expire = roundedTime;
                }
            }
            if (muted) {
                coreProfile.setMutePunishment(new MutePunishment(reason, expire, null));
                return;
            }
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        this.plugin.getManagerHandler().getPlayerManager().retrieveRank(player);
        new BukkitRunnable() {
            public void run() {
                if (player.hasPermission("core.staff")) {
                    plugin.getManagerHandler().getPlayerManager().sendStaffLogged(player, true);
                }
            }
        }.runTaskLater(this.plugin, 10L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (player.getUniqueId().toString().equalsIgnoreCase(CorePlugin.verzideUUID)) {
            SkinUtil.changeName(player, "Verzide");
            SkinUtil.setSkin(((CraftPlayer) player).getProfile());
            System.out.println("Verzide set.");
        }
        this.plugin.getManagerHandler().getPlayerManager().updatePermissions(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        if (player.hasPermission("core.staff")) {
            this.plugin.getManagerHandler().getPlayerManager().sendStaffLogged(player, false);
        }
        this.plugin.getManagerHandler().getPlayerManager().resetPermissions(player);
        this.plugin.getManagerHandler().getProfileManager().removePlayer(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        boolean muted = false;
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (coreProfile != null && coreProfile.getMutePunishment() != null) {
            if (!coreProfile.getMutePunishment().getExpire().equalsIgnoreCase("never")) {
                Timestamp expiry = TimeUtil.fromMillis(Long.parseLong((coreProfile.getMutePunishment().getExpire())));
                if (TimeUtil.getCurrentTimestamp().after(expiry)) {
                    this.plugin.getManagerHandler().getSqlManager().query("DELETE FROM punishments WHERE uuid = '" + coreProfile.getUuid().toString() + "' AND type = 'mute';");
                    coreProfile.setMutePunishment(null);
                } else {
                    muted = true;
                }
            } else {
                muted = true;
            }
            if (muted) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are currently muted for: " + ChatColor.GRAY + coreProfile.getMutePunishment().getReason());
                player.sendMessage(ChatColor.RED + "Your mute expires: " + ChatColor.GRAY + (coreProfile.getMutePunishment().getExpire().equalsIgnoreCase("never") ? "Never" : ChatColor.GRAY + TimeUtil.millisToRoundedTime(Long.parseLong((coreProfile.getMutePunishment().getExpire())) - System.currentTimeMillis())));
                return;
            }
        }
        if (coreProfile != null && coreProfile.isStaffMode() && player.hasPermission("core.staff")) {
            event.setCancelled(true);
            this.plugin.getManagerHandler().getPlayerManager().sendStaffChatMessage(player, event.getMessage());
            return;
        }
        if (this.plugin.getManagerHandler().getSettingsManager().isChatMute() && !player.hasPermission("core.staff")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Public chat is currently muted.");
            return;
        }
        if (System.currentTimeMillis() - coreProfile.getLastChat() <= 1000 * this.plugin.getManagerHandler().getSettingsManager().getChatDelay() && !player.hasPermission("core.chat.bypass") && !player.hasPermission("core.chat.bypass")) {
            event.setCancelled(true);
            long difference = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - coreProfile.getLastChat());
            player.sendMessage(ChatColor.RED + "You must wait " + (this.plugin.getManagerHandler().getSettingsManager().getChatDelay() - difference) + "s to chat again.");
            return;
        }
        event.setFormat(coreProfile.getGroup().getPrefix() + player.getName() + ChatColor.DARK_GRAY + ": " + ChatColor.RESET + event.getMessage().replace("%", "%%"));
        coreProfile.setLastChat(System.currentTimeMillis());
    }

    @EventHandler
    public void onGameModeUpdated(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Your gamemode has been updated to " + ChatColor.DARK_PURPLE + event.getNewGameMode().toString() + ChatColor.LIGHT_PURPLE + ".");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        Location from = event.getFrom();
        Location to = event.getTo();
        if (coreProfile.isFrozen() && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
            event.setTo(from.setDirection(to.getDirection()));
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.setMotd(ChatColor.translateAlternateColorCodes('&', "&5Patrone Network &8&l┃ &ddiscord.patrone.cc\n                 &d&l» &7" + (this.plugin.getServer().hasWhitelist() ? "Whitelisted" : "We're Open!")));
    }

    @EventHandler
    public void onChange(FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();
        if (!event.isCancelled() && event.getFoodLevel() < player.getFoodLevel()) {
            event.setCancelled(new Random().nextInt(100) < 66);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        float before = player.getSaturation();
        new BukkitRunnable() {
            public void run() {
                float change = player.getSaturation() - before;
                player.setSaturation((float) (before + change * 2.5));
            }
        }.runTaskLater(this.plugin, 1L);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SPLASH_POTION) {
            Projectile projectile = event.getEntity();

            if (projectile.getShooter() instanceof Player && ((Player) projectile.getShooter()).isSprinting()) {
                Vector velocity = projectile.getVelocity();

                velocity.setY(velocity.getY() + 0.05);
                projectile.setVelocity(velocity);
            }
        }
    }

    @EventHandler
    void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) event.getEntity().getShooter();

            if (shooter.isSprinting() && event.getIntensity(shooter) > 0.5D) {
                event.setIntensity(shooter, 1.0D);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().startsWith("/") && event.getMessage().contains(":")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Incorrect syntax.");
            return;
        }
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().toString().equalsIgnoreCase(CorePlugin.verzideUUID)) {
            event.setVelocity(event.getVelocity().multiply(0.92).setY(event.getVelocity().getY()));
        }
    }
}
