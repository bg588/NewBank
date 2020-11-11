package server;

import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private static final String SUCCESS = "SUCCESS";
	private static final String FAIL = "FAIL";
	private static final String NEWACCOUNT = "NEWACCOUNT";
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			if(request.contains(NEWACCOUNT))
			{
				return addNewAccount(customer, request);
			}

			if(request.equals("SHOWMYACCOUNTS"))
			{
				return showMyAccounts(customer);
			}
		}
		return FAIL;
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String addNewAccount(CustomerID customer, String commandWithAccountName)
	{
		Customer myCurrentCustomer = customers.get(customer.getKey());
		//validate command, it must be NEWACCOUNT <Name>
		String remainingString = commandWithAccountName.replace(NEWACCOUNT, "");
		//check the length of the remaining String passed in breaks the limit
		if (remainingString.length()>10)
		{
			return FAIL;
		}

		//check name on account doesnt already exist
		ArrayList<Account> accounts = customers.get(customer.getKey()).getAccounts();
		for (Account acc:accounts
			 ) {
			if(acc.getAccountName().equals(remainingString))
			{
				return FAIL;
			}
		}
		Account theNewAccount = new Account(remainingString, 0);
		myCurrentCustomer.addAccount(theNewAccount);

		return SUCCESS;
	}

}
