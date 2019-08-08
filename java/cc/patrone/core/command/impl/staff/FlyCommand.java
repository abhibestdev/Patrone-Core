package cc.patrone.core.command.impl.staff;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import org.bukkit.ChatColor;

public class FlyCommand {

    @Command(name = "fly", permission = "core.command.fly", inGameOnly = true)
    public void broadcastCommand(CommandArgs args) {
        if (args.getPlayer().getAllowFlight()) {
            args.getPlayer().setFlying(false);
            args.getPlayer().setAllowFlight(false);
            args.getSender().sendMessage(ChatColor.RED + "Flight disabled.");
            return;
        }
        args.getPlayer().setAllowFlight(true);
        args.getPlayer().setFlying(true);
        args.getPlayer().setFlySpeed(0.4F);
        args.getSender().sendMessage(ChatColor.GREEN + "Flight enabled.");
        return;
    }
}
