package cc.patrone.core.manager.impl;

import cc.patrone.core.group.Group;
import cc.patrone.core.manager.Manager;
import cc.patrone.core.manager.ManagerHandler;
import cc.patrone.core.player.CoreProfile;
import cc.patrone.core.sql.QueryType;

import java.sql.ResultSet;
import java.util.Arrays;

public class GroupManager extends Manager {

    public GroupManager(ManagerHandler managerHandler) {
        super(managerHandler);
        loadPermissions();
    }

    private void loadPermissions() {
        Arrays.stream(Group.values()).forEach(g -> {
            try {
                ResultSet resultSet = (ResultSet) this.managerHandler.getSqlManager().getSqlConnection().query(QueryType.RS, "SELECT * FROM permission WHERE type = '" + g.toString() + "';");
                while (resultSet.next()) {
                    g.getPermissions().add(resultSet.getString("permission"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void updatePermissions(Group group) {
        this.managerHandler.getPlugin().getServer().getOnlinePlayers().stream().forEach(p -> {
            CoreProfile coreProfile = this.managerHandler.getProfileManager().getProfile(p);
            if (coreProfile.getGroup() == group) {
                this.managerHandler.getPlayerManager().updatePermissions(p);
            }
        });
    }
}
