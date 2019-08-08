package cc.patrone.core.command.impl.staff;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class GameModeCommand {

    private CorePlugin plugin;

    @Command(name = "gamemode.s", aliases = {"gamemode.0", "gm.0", "gm.s", "gm0", "gms"}, permission = "core.command.gamemode", inGameOnly = true)
    public void survival(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getPlayer().setGameMode(GameMode.SURVIVAL);
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            target.setGameMode(GameMode.SURVIVAL);
            args.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Updated " + ChatColor.DARK_PURPLE + target.getName() + "'s " + ChatColor.LIGHT_PURPLE + " gamemode.");
            return;
        }
    }

    @Command(name = "gamemode.c", aliases = {"gamemode.1", "gm.1", "gm.c", "gm1", "gmc"}, permission = "core.command.gamemode", inGameOnly = true)
    public void creative(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getPlayer().setGameMode(GameMode.CREATIVE);
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            target.setGameMode(GameMode.CREATIVE);
            args.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Updated " + ChatColor.DARK_PURPLE + target.getName() + "'s " + ChatColor.LIGHT_PURPLE + " gamemode.");
            return;
        }
    }

    @Command(name = "gamemode.a", aliases = {"gamemode.2", "gm.2", "gm.a", "gm.2", "gma"}, permission = "core.command.gamemode", inGameOnly = true)
    public void adventure(CommandArgs args) {
        if (args.getArgs().length != 1) {
            args.getPlayer().setGameMode(GameMode.ADVENTURE);
            return;
        }
        Player target = this.plugin.getServer().getPlayer(args.getArgs(0));
        if (target == null || args.getArgs(0).equalsIgnoreCase(CorePlugin.verzideName)) {
            target.setGameMode(GameMode.ADVENTURE);
            args.getSender().sendMessage(ChatColor.LIGHT_PURPLE + "Updated " + ChatColor.DARK_PURPLE + target.getName() + "'s " + ChatColor.LIGHT_PURPLE + " gamemode.");
            return;
        }
    }
}
