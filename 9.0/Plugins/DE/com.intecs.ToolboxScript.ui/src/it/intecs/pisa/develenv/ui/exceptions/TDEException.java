/**
 * 
 */
package it.intecs.pisa.develenv.ui.exceptions;

/**
 * @author Massimiliano
 *
 */
public class TDEException extends Exception{

	private String message;
	public enum Level {WARNING,ERROR,CRITICAL};
	private Level level;
	
	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
		
}
