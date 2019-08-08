package cc.patrone.core.sql.exceptions;

@SuppressWarnings("serial")
public class NotConnectedException extends Exception {
	public NotConnectedException(String error) {
		super(error);
	}
}
