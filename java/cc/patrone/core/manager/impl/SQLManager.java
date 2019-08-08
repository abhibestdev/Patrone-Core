package cc.patrone.core.manager.impl;

import cc.patrone.core.manager.Manager;
import cc.patrone.core.manager.ManagerHandler;
import cc.patrone.core.sql.QueryType;
import cc.patrone.core.sql.SQL;
import cc.patrone.core.sql.SQLConnection;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.Arrays;

@Getter
public class SQLManager extends Manager {

    private String ip;
    private int port;
    private String username;
    private String password;
    private String database;
    private SQLConnection sqlConnection;

    public SQLManager(ManagerHandler managerHandler) {
        super(managerHandler);
        establishConnection();
    }

    private void establishConnection() {
        ip = managerHandler.getPlugin().getConfig().getString("sql.ip");
        port = managerHandler.getPlugin().getConfig().getInt("sql.port");
        username = managerHandler.getPlugin().getConfig().getString("sql.username");
        password = managerHandler.getPlugin().getConfig().getString("sql.password");
        database = managerHandler.getPlugin().getConfig().getString("sql.database");
        try {
            sqlConnection = new SQL().ip(ip).port(port).user(username).password(password).database(database).build();
            sqlConnection.query(QueryType.POST, "CREATE TABLE IF NOT EXISTS groups (uuid VARCHAR(2000), type VARCHAR(2000));");
            sqlConnection.query(QueryType.POST, "CREATE TABLE IF NOT EXISTS punishments (uuid VARCHAR(2000), type VARCHAR(2000), reason VARCHAR(2000), expire VARCHAR(2000), date VARCHAR(2000), executor VARCHAR(200));");
            sqlConnection.query(QueryType.POST, "CREATE TABLE IF NOT EXISTS blacklists (uuid VARCHAR(2000), reason VARCHAR(2000), executor VARCHAR(2000), ip VARCHAR(2000));");
            sqlConnection.query(QueryType.POST, "CREATE TABLE IF NOT EXISTS iphistory (uuid VARCHAR(2000), ips VARCHAR(20000));");
            sqlConnection.query(QueryType.POST, "CREATE TABLE IF NOT EXISTS ip (uuid VARCHAR(2000), ip VARCHAR(2000));");
            sqlConnection.query(QueryType.POST, "CREATE TABLE IF NOT EXISTS permission (type VARCHAR(2000), permission VARCHAR(2000));");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object[] callbackArray(String query, String... value) {
        try {
            Object[] objects = new Object[value.length];
            ResultSet resultSet = (ResultSet) sqlConnection.query(QueryType.RS, query);
            if (resultSet.next()) {
                for (int i = 0; i < objects.length; i++) {
                    objects[i] = resultSet.getObject(value[i]);
                }
                return objects;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Object callback(String query, String value) {
        try {
            ResultSet resultSet = (ResultSet) sqlConnection.query(QueryType.RS, query);
            if (resultSet.next()) {
                return resultSet.getObject(value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void query(String query) {
        this.managerHandler.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.managerHandler.getPlugin(), () -> {
            try {
                sqlConnection.query(QueryType.POST, query);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
