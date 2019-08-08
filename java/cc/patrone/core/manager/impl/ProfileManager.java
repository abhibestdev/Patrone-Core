package cc.patrone.core.manager.impl;

import cc.patrone.core.manager.Manager;
import cc.patrone.core.manager.ManagerHandler;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.util.UUIDFetcher;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager extends Manager {

    private Map<UUID, CoreProfile> profileMap;

    public ProfileManager(ManagerHandler managerHandler) {
        super(managerHandler);
        profileMap = new HashMap<>();
    }

    public void addPlayer(Player player) {
        profileMap.put(player.getUniqueId(), new CoreProfile(UUIDFetcher.getUUID(player.getName())));
    }

    public void addUUID(UUID uuid, String name) {
        profileMap.put(uuid, new CoreProfile(UUIDFetcher.getUUID(name)));
    }

    public void removePlayer(Player player) {
        profileMap.remove(player.getUniqueId());
    }

    public CoreProfile getProfile(Player player) {
        return profileMap.get(player.getUniqueId());
    }

    public CoreProfile getProfile(UUID uuid) {
        return profileMap.get(uuid);
    }

    public boolean hasProfile(Player player) {
        return profileMap.containsKey(player.getUniqueId());
    }
}
