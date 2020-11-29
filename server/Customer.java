package server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private Password password;
	private boolean locked;
	private int passwordAttemptsRemaining;
	
	public Customer() {
		accounts = new ArrayList<>();
		password = new Password();
		locked = false;
		passwordAttemptsRemaining = 3;
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString()+" ";
		}
		return s;
	}

	public ArrayList<Account> getAccounts()
	{
		return accounts;
	}

	public Account getAccountWithName(String nameToSearchFor) {
		//if an account exists, returns that account
		for (Account account : accounts) {
			if (account.getAccountName().equals(nameToSearchFor)) {
				return account;
			}
		}
		return null;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	public void setPassword(String password) {
		this.password.setPassword(password);
	}

	public boolean changePassword(String newPassword) {
		return password.changePassword(newPassword);
	}

	public Password getPassword() {
		return password;
	}

	public boolean verifyPassword(String inputPassword) {
		return password.getPassword().equals(inputPassword);
	}

	public boolean isLocked() {
		return locked;
	}

	public void failedPasswordAttempt() {
		passwordAttemptsRemaining--;
		if (passwordAttemptsRemaining == 0) {
			locked = true;
		}
	}

	public void resetPasswordAttemptsRemaining() {
		// this is called on a successful login
		passwordAttemptsRemaining = 3;
	}

	public int getPasswordAttemptsRemaining() {
		return passwordAttemptsRemaining;
	}
}
