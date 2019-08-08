package cc.patrone.core.manager.impl;

import cc.patrone.core.manager.Manager;
import cc.patrone.core.manager.ManagerHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsManager extends Manager {

    private boolean chatMute;
    private int chatDelay;

    public SettingsManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }
}
