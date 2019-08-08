package cc.patrone.core.sql.exceptions;

@SuppressWarnings("serial")
public class MissingArgumentException extends Exception {
	public MissingArgumentException(String error) {
		super(error);
	}
}
