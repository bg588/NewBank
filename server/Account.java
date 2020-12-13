package server;

public class Account {
	
	private String accountName;
	private double openingBalance;
	private long accountNumber;

	public Account(String accountName, double openingBalance, long accountNumber) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.accountNumber= accountNumber;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public long getAccountNumber(){ return accountNumber; }

	public String toString() {
		return (accountName + " (" + accountNumber + "). Balance : " + NewBankAccountManager.roundDouble(openingBalance,2));
	}

	public void addMoneyToAccount(double amount)
	{
		openingBalance += amount;
	}

	public void reduceBalance(double amount)
	{
		openingBalance = openingBalance - amount;
	}

	public void renameAccount(String newAccountName) {
		this.accountName = newAccountName;
	}

	public Double getBalance()
	{
		return openingBalance;
	}

}
