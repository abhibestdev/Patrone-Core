package cc.patrone.core.task;

import cc.patrone.core.CorePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@RequiredArgsConstructor
public class BroadcasterTask extends BukkitRunnable {

    private final CorePlugin plugin;
    private int i;

    public void run() {
        List<String> broadcasts = this.plugin.getManagerHandler().getPlugin().getConfig().getStringList("messages");

        this.plugin.getServer().broadcastMessage(" ");
        this.plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcasts.get(i)));
        this.plugin.getServer().broadcastMessage(" ");

        i += 1;

        if (broadcasts.size() - i <= 1) {
            i = 0;
        }
    }
}
