package server;

public class CustomerID {
	private String key;
	private boolean authenticated;
	private boolean locked;
	private int passwordAttemptsRemaining;
	
	public CustomerID(String key) {
		this.key = key;
		passwordAttemptsRemaining = 3;
	}
	
	public String getKey() {
		return key;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		locked = true;
	}

	public int getPasswordAttemptsRemaining() {
		return passwordAttemptsRemaining;
	}

	public void setPasswordAttemptsRemaining(int passwordAttemptsRemaining) {
		this.passwordAttemptsRemaining = passwordAttemptsRemaining;
	}
}
