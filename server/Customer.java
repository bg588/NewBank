package server;

import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Customer {
	
	private ArrayList<Account> accounts;
	private Password password;
	private String email;
	private String phone;
	private DateOfBirth dateOfBirth;
	private boolean locked;
	private int passwordAttemptsRemaining;
	private Integer customerNumber;
	
	public Customer() {
		accounts = new ArrayList<>();
		password = new Password();
		dateOfBirth = new DateOfBirth();
		locked = false;
		passwordAttemptsRemaining = 3;
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString() + "\n";
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
			if (account.getAccountName().equalsIgnoreCase(nameToSearchFor)) {
				return account;
			}
		}
		return null;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
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

	public void setDateOfBirth(String DOB){
	dateOfBirth.setDateOfBirth(DOB);
	}

	public DateOfBirth getDateOfBirth(){
	return dateOfBirth;
	}

	public void setCustomerNumber(Integer customerNumber) {
		this.customerNumber = customerNumber;
	}

	public Integer getCustomerNumber(){return customerNumber;}

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
