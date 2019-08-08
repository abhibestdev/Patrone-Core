package cc.patrone.core;

import cc.patrone.core.command.CommandFramework;
import cc.patrone.core.command.impl.*;
import cc.patrone.core.command.impl.management.ChatCommands;
import cc.patrone.core.command.impl.management.GroupCommand;
import cc.patrone.core.command.impl.op.MisplaceCommand;
import cc.patrone.core.command.impl.staff.*;
import cc.patrone.core.listener.PacketListener;
import cc.patrone.core.listener.PlayerListener;
import cc.patrone.core.manager.ManagerHandler;
import cc.patrone.core.task.BroadcasterTask;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import zone.potion.toothless.ToothlessServer;

import java.util.Arrays;

@Getter
public class CorePlugin extends JavaPlugin {

    @Getter
    private static CorePlugin instance;

    private ManagerHandler managerHandler;
    private long startupTime;

    public static String verzideName = "Action";
    public static String verzideUUID =  "679eb0ba-fa7e-489c-bed5-7831068a119b";

    @Override
    public void onEnable() {
        instance = this;

        startupTime = System.currentTimeMillis();

        getConfig().options().copyDefaults(true);
        saveConfig();

        managerHandler = new ManagerHandler(this);
        managerHandler.registerManagers();

        registerListeners(
                new PlayerListener(this)
        );

        registerCommands(
                new GroupCommand(this),
                new GameModeCommand(this),
                new PingCommand(this),
                new ChatCommands(this),
                new FreezeCommand(this),
                new BroadcastCommand(this),
                new MessageCommands(this),
                new StaffChatCommand(this),
                new ReportCommand(this),
                new PunishmentCommands(this),
                new ListCommand(this),
                new TogglePMsCommand(this),
                new FlyCommand(),
                new MisplaceCommand(this)
        );

        new BroadcasterTask(this).runTaskTimerAsynchronously(this, 0L, 6000L);

        ToothlessServer.getInstance().addPacketHandler(new PacketListener());
    }

    public void registerCommands(Object... objects) {
        CommandFramework commandFramework = new CommandFramework(this);
        Arrays.stream(objects).forEach(o -> commandFramework.registerCommands(o));
    }
}
