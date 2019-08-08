package cc.patrone.core.command.impl;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.core.player.CoreProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class TogglePMsCommand {

    private CorePlugin plugin;

    @Command(name = "toggleprivatemessages", aliases = {"togglepms", "tpms", "tpm"}, inGameOnly = true)
    public void togglePMs(CommandArgs args) {
        CoreProfile coreProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        coreProfile.setToggledPms(!coreProfile.isToggledPms());
        args.getSender().sendMessage(coreProfile.isToggledPms() ? ChatColor.RED + "You have disabled Private Messages." : ChatColor.GREEN + "You have enabled Private Messages.");
        return;
    }

}
