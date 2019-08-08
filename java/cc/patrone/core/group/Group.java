package cc.patrone.core.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Group {

    DEFAULT("Default", "&a", "&a", 0),
    VIP("VIP", "&8[&fVIP&8]&f", "&f", 1),
    CHIEF("Chief", "&8[&1Chief&8]&1", "&1", 2),
    DEMON("Demon", "&8[&4Demon&8]&4", "&4", 4),
    PATRONE("Patrone", "&8[&9Patrone&8]&9", "&9", 4),
    YOUTUBE("YouTube", "&8[&6YouTube&8]&6", "&6", 5),
    FAMOUS("Famous", "&8[&dFamous&8]&d", "&d", 6),
    TRIAL_MOD("Trial-Mod", "&8[&eTrial&8-&eMod&8]&e", "&e", 7),
    MOD("Mod", "&8[&3Moderator&8]&3", "&3", 8),
    ADMIN("Admin", "&8[&cAdmin&8]&c", "&c", 9),
    PLATFORM_ADMIN("Platform-Admin", "&8[&4Plat&8-&4Admin&8]&4&o", "&4&o", 10),
    MANAGER("Manager", "&8[&3&oManager&8]&3&o", "&3&o", 11),
    DEVELOPER("Developer", "&8[&bDeveloper&8]&b", "&b", 12),
    OWNER("Owner", "&8[&5Owner&8]&5", "&5", 13);

    private final String name;
    private final String prefix;
    private final String tabColor;
    private final int weight;
    private List<String> permissions = new ArrayList<>();

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getTabColor() {
        return ChatColor.translateAlternateColorCodes('&', tabColor);
    }

    public static Group getGroup(String name) {
        return Arrays.stream(Group.values()).filter(g -> g.toString().equalsIgnoreCase(name) || g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
