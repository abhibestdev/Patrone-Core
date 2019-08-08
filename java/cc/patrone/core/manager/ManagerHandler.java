package cc.patrone.core.manager;

import cc.patrone.core.CorePlugin;
import cc.patrone.core.manager.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ManagerHandler {

    private final CorePlugin plugin;
    private SQLManager sqlManager;
    private ProfileManager profileManager;
    private PlayerManager playerManager;
    private SettingsManager settingsManager;
    private GroupManager groupManager;

    public void registerManagers() {
        sqlManager = new SQLManager(this);
        profileManager = new ProfileManager(this);
        playerManager = new PlayerManager(this);
        settingsManager = new SettingsManager(this);
        groupManager = new GroupManager(this);
    }

}
