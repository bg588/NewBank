package server;

public class Account {
	
	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}

	public String getAccountName()
	{
		return accountName;
	}
	
	public String toString() {
		return (accountName + " " + openingBalance);
	}

	public void addMoneyToAccount(double amount)
	{
		openingBalance += amount;
	}

	public void reduceBalance(double amount)
	{
		openingBalance = openingBalance - amount;
	}

	public Double getBalance()
	{
		return openingBalance;
	}

}
