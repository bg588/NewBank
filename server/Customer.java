package server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private Password password;
	private DateOfBirth DateOfBirth;

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

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	public void setPassword(String inputPassword) {
		password.setPassword(inputPassword);
	}

	public Password getPassword() {
		return password;
	}

	public boolean verifyPassword(String inputPassword) {
		return password.getPassword().equals(inputPassword);
	}

	public void setDateOfBirth(String DOB){
	DateOfBirth.setDateOfBirth(DOB);
	}
	public DateOfBirth getDateOfBirth(){
return DateOfBirth;
	}
	public boolean verifyDateOfBirth(String DOB){
return DateOfBirth.getDateOfBirth().equals(DateOfBirth);
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
