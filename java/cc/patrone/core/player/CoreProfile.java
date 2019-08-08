package cc.patrone.core.player;

import cc.patrone.core.group.Group;
import cc.patrone.core.punishment.impl.MutePunishment;
import cc.patrone.core.util.CustomLocation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class CoreProfile {

    private final UUID uuid;
    private Group group = Group.DEFAULT;
    private boolean frozen;
    private Player lastMessage;
    private boolean staffMode;
    private long lastReport;
    private MutePunishment mutePunishment;
    private long lastChat;
    private PermissionAttachment permissionAttachment;
    private boolean toggledPms;
    private double misplace;
    private CustomLocation lastMovePacket;
    private Map<UUID, List<CustomLocation>> recentPlayerPackets = new HashMap<>();

    public void addPlayerPacket(final UUID playerUUID, final CustomLocation customLocation) {
        List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations == null) {
            customLocations = new ArrayList<CustomLocation>();
        }
        if (customLocations.size() == 20) {
            customLocations.remove(0);
        }
        customLocations.add(customLocation);
        this.recentPlayerPackets.put(playerUUID, customLocations);
    }

    public CustomLocation getLastPlayerPacket(final UUID playerUUID, final int index) {
        final List<CustomLocation> customLocations = this.recentPlayerPackets.get(playerUUID);
        if (customLocations != null && customLocations.size() > index) {
            return customLocations.get(customLocations.size() - index);
        }
        return null;
    }

}
