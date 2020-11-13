package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private static final String SUCCESS = "SUCCESS";
	private static final String FAIL = "FAIL";
	private static final String NEWACCOUNT = "NEWACCOUNT";
	private static final String PAY = "PAY";

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
		List<String> splitRequest = Arrays.asList(request.split(" "));
		if(customers.containsKey(customer.getKey())) {
			if(splitRequest.get(0).contains(NEWACCOUNT))
			{
				return addNewAccount(customer, splitRequest);
			}
			if(splitRequest.get(0).contains(PAY))
			{
				return payPersonOrCompanyAnAmmount(customer, splitRequest);
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


	//this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
	private String addNewAccount(CustomerID customer, List<String> commandWithAccountName)
	{
		Customer myCurrentCustomer = customers.get(customer.getKey());
		//flatten the list after the first split
		var flattenlist = new String();
		for (String value :
				commandWithAccountName) {
			if(value.equals(NEWACCOUNT)){
				continue;
			}
			flattenlist += value;
		}
		//check the length of the remaining String passed in breaks the limit
		if (flattenlist.length()>10 || flattenlist.isEmpty() || flattenlist.isBlank())
		{
			return FAIL;
		}
		ArrayList<Account> accounts = customers.get(customer.getKey()).getAccounts();
		for (Account acc:accounts
			 ) {
			if(acc.getAccountName().equals(flattenlist))
			{
				return FAIL;
			}
		}
		Account theNewAccount = new Account(flattenlist, 0);
		myCurrentCustomer.addAccount(theNewAccount);

		return SUCCESS;
	}

	private String payPersonOrCompanyAnAmmount(CustomerID customer, List<String> commandWithPayeeAndAmmount)
	{
		var myName = customer.getKey();
        if(commandWithPayeeAndAmmount.size() !=3)
		{
			//no the correct amount of args
			return "Wrong Amount of args";
		}
		if(!commandWithPayeeAndAmmount.get(0).equals(PAY))
		{
			//Somehow the wrong command came in here
			return FAIL;
		}
		//next input must be a person or company name
		var personOrCompanyToPay = commandWithPayeeAndAmmount.get(1);
		double ammountToPay = 0.0;
		try {
			ammountToPay = Double.parseDouble(commandWithPayeeAndAmmount.get(2));
		}
		catch (NumberFormatException ex)
		{
			return "payable amount could not be converted to a valid number";
		}
		if(ammountToPay <=1)
		{
			//cannot pay someone less that 1 wtv currency
			return "Cannot pay someone less than 1";
		}
		if(personOrCompanyToPay.equals(myName))
		{
			//cannot pay myself
			return "Cannot pay yourself";
		}
		for (String customerName: customers.keySet()
			 ) {
			if(personOrCompanyToPay.equals(customerName))
			{
				//found the person we want to pay
				//doesnt specify which account, by default we will pay the first account for the customer we find
				var payee = customers.get(customerName);
				ArrayList<Account> PayeeAccounts = payee.getAccounts();

				var me = customers.get(customer.getKey());
				var myAccounts = me.getAccounts();
				for (Account account :
						myAccounts) {
					//As we dont specify which account we want to pay from, we check if we have the money to pay
					if (account.getBalance() >= ammountToPay)
					{
						//yay this account has enough - reduce my balance and pay the person
						account.reduceBalance(ammountToPay);
						PayeeAccounts.get(0).addMoneyToAccount(ammountToPay);
						return SUCCESS;
					}
				}
			}
		}
		return FAIL;
	}
}
