package cc.patrone.core.sql;

import cc.patrone.core.sql.exceptions.NotConnectedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLConnection {
    protected java.sql.Connection conn;

    public SQLConnection(java.sql.Connection conn) {
        this.conn = conn;
    }

    public boolean isConnected() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            return false;
        } else {
            return true;
        }
    }

    public void disconnect() throws SQLException, NotConnectedException {
        if (this.isConnected()) {
            this.conn.close();
        } else {
            throw new NotConnectedException("Database is not connected, cannot disconnect.");
        }
    }

    public Object query(QueryType type, String query) throws SQLException, NotConnectedException {
        if (this.isConnected()) {
            if (type == QueryType.GET) {
                PreparedStatement pst = this.conn.prepareStatement(query);
                ResultSet rs = pst.executeQuery();
                String[] column_split = query.split(" ");
                String column = column_split[1];
                if (rs.next()) {
                    return rs.getString(column);
                }
            } else if (type == QueryType.RS) {
                PreparedStatement pst = this.conn.prepareStatement(query);
                return pst.executeQuery();
            } else if (type == QueryType.POST) {
                PreparedStatement pst = this.conn.prepareStatement(query);
                pst.executeUpdate();
                return null;
            }
        } else {
            throw new NotConnectedException("Database is not connected, cannot query.");
        }

        return null;
    }
}
