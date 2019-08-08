package cc.patrone.core.sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import cc.patrone.core.sql.exceptions.MissingArgumentException;

import java.sql.SQLException;

public class SQL {
	protected String ip;
	protected int port;
	protected String user;
	protected String db;
	protected String pass;
	protected int timeout;
	protected boolean reconnect;
	
	public SQL() {
		this.ip = "localhost";
		this.port = 3306;
		this.user = null;
		this.db = null;
		this.pass = null;
		this.timeout = 10*1000;
		this.reconnect = true;
	}
	
	public SQL ip(String value) {
		this.ip = value;
		return this;
	}
	
	public SQL port(int value) {
		this.port = value;
		return this;
	}
	
	public SQL user(String value) {
		this.user = value;
		return this;
	}
	
	public SQL database(String value) {
		this.db = value;
		return this;
	}
	
	public SQL password(String value) {
		this.pass = value;
		return this;
	}
	
	public SQL timeout(int value) {
		this.timeout = value;
		return this;
	}
	
	public SQL reconnect(boolean value) {
		this.reconnect = value;
		return this;
	}
	
	public SQLConnection build() throws SQLException, MissingArgumentException {
		if(this.ip == null) {
			throw new MissingArgumentException("IP is not defined.");
		} else if(this.user == null) {
			throw new MissingArgumentException("User is not defined.");
		} else if(this.db == null) {
			throw new MissingArgumentException("Database is not defined.");
		} else if(this.pass == null) {
			throw new MissingArgumentException("Password is not defined.");
		}
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUser(this.user);
		ds.setDatabaseName(this.db);
		ds.setPassword(this.pass);
		ds.setServerName(this.ip);
		ds.setPort(this.port);
		ds.setConnectTimeout(this.timeout);
		ds.setAutoReconnect(this.reconnect);
		return new SQLConnection(ds.getConnection());
	}
}
