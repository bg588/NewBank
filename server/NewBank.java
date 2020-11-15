package server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;

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
	public synchronized String processRequest(CustomerID customer, ArrayList<String> request) {
		if (customers.containsKey(customer.getKey())) {
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.NEWACCOUNT)) {
				return addNewAccount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.PAY)) {
				return payPersonOrCompanyAnAmount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.MOVE)) {
				return moveAnAmountFromOneAccountToAnother(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.SHOWMYACCOUNTS)) {
				return showMyAccounts(customer);
			}
		}
		return ProtocolsAndResponses.Responses.FAIL;
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	//this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
	private String addNewAccount(CustomerID customer, List<String> commandWithAccountName) {
		Customer myCurrentCustomer = customers.get(customer.getKey());
		//flatten the list after the first split
		var flattenlist = "";
		for (String value : commandWithAccountName) {
			if (value.equals(ProtocolsAndResponses.Protocols.NEWACCOUNT)) {
				continue;
			}
			flattenlist += value;
		}
		//check the length of the remaining String passed in breaks the limit
		if (flattenlist.length() > 10 || flattenlist.isEmpty() || flattenlist.isBlank()) {
			return ProtocolsAndResponses.Responses.FAIL;
		}
		ArrayList<Account> accounts = customers.get(customer.getKey()).getAccounts();
		for (Account acc : accounts) {
			if (acc.getAccountName().equals(flattenlist)) {
				return ProtocolsAndResponses.Responses.FAIL;
			}
		}
		Account theNewAccount = new Account(flattenlist, 0);
		myCurrentCustomer.addAccount(theNewAccount);

		return ProtocolsAndResponses.Responses.SUCCESS;
	}

	private String payPersonOrCompanyAnAmount(CustomerID customer, List<String> commandWithPayeeAndAmount) {
		var myName = customer.getKey();
        if (commandWithPayeeAndAmount.size() != 3) {
			//not the correct amount of args
			return "Wrong Amount of args";
		}
        //first input in the split array is the command
		if(!commandWithPayeeAndAmount.get(0).equals(ProtocolsAndResponses.Protocols.PAY)) {
			//Somehow the wrong command came in here
			return ProtocolsAndResponses.Responses.FAIL;
		}
		//next input in the split array must be payee
		//this code will break once we have a protocol that can create customer names with spaces in them
		// This must be future work as it will be a UI change to take the parameters in steps
		var personOrCompanyToPay = commandWithPayeeAndAmount.get(1);
		double amountToPay;
		try {
			//next input in the split array must be the amount
			//uses big decimal to keep to 2 decimal places
			amountToPay = roundDouble(Double.parseDouble(commandWithPayeeAndAmount.get(2)), 2);
		}
		catch (NumberFormatException ex) {
			return "payable amount could not be converted to a valid number";
		}
		if(amountToPay <= 0.009) {
			//cannot pay someone less that 0.01 wtv currency
			return "Cannot pay someone less than 0.01";
		}
		if(personOrCompanyToPay.equalsIgnoreCase(myName)) {
			//cannot pay myself
			return "Cannot pay yourself";
		}
		//this is a for-each loop that will cycle through the customer keys (which are the names of the accounts)
		for (String customerName: customers.keySet()) {
			//when we reach the customer we want to pay
			if (personOrCompanyToPay.equalsIgnoreCase(customerName)) {
				//we pull out the customer object based on the name we matched above
				var payee = customers.get(customerName);
				//we get the customers accounts
				ArrayList<Account> PayeeAccounts = payee.getAccounts();

				//get the current users customer object
				var me = customers.get(customer.getKey());
				//get the current users list of accounts
				var myAccounts = me.getAccounts();

				//cycle through the user accounts to find one with enough money in it
				for (Account account : myAccounts) {
					if (account.getBalance() >= amountToPay) {
						//yay this account has enough - reduce my balance and pay the person
						account.reduceBalance(amountToPay);
						PayeeAccounts.get(0).addMoneyToAccount(amountToPay);
						return ProtocolsAndResponses.Responses.SUCCESS;
					}
				}
				break;
			}
		}
		return ProtocolsAndResponses.Responses.FAIL;
	}

	//Based on Ioannis's PAY code
	private String moveAnAmountFromOneAccountToAnother(CustomerID customer,
													   List<String> commandWithAmountOriginAccountDestinationAccount) {

		if (commandWithAmountOriginAccountDestinationAccount.size() != 4) {
			//not the correct amount of args
			return "Wrong Amount of args";
		}
		//first input in the split array is the command
		if (!commandWithAmountOriginAccountDestinationAccount.get(0).equals(ProtocolsAndResponses.Protocols.MOVE)) {
			//Somehow the wrong command came in here
			return ProtocolsAndResponses.Responses.FAIL;
		}
		// next input is the amount
		double amountToMove;
		try {
			//next input in the split array must be the amount
			//uses big decimal to keep to 2 decimal places
			amountToMove = roundDouble(Double.parseDouble(commandWithAmountOriginAccountDestinationAccount.get(1)), 2);
		} catch (NumberFormatException ex) {
			return "payable amount could not be converted to a valid number";
		}

		if (amountToMove <= 0.009) {
			//cannot pay someone less that 0.01 wtv currency
			return "Cannot pay someone less than 0.01";
		}

		//todo:this code will break once we have a protocol that can create customer names with spaces in them
		// This must be future work as it will be a UI change to take the parameters in steps
		//next inputs must be two accounts - we check if they exist later
		String intendedOriginAccountName = commandWithAmountOriginAccountDestinationAccount.get(2);
		String intendedDestinationAccountName = commandWithAmountOriginAccountDestinationAccount.get(3);

		if (intendedOriginAccountName.equals(intendedDestinationAccountName)) {
			//cannot move between the same account
			return "Cannot move between the same account";
		}

		Customer me = customers.get(customer.getKey());
		ArrayList<Account> allMyAccounts = me.getAccounts();

		for (Account originAccount : allMyAccounts) {
			// looping through all accounts and searching for intendedOriginAccountName
			if (intendedOriginAccountName.equalsIgnoreCase(originAccount.getAccountName())) {
				// intendedOriginAccountName is a real account, now check if intendedDestinationAccountName is real
				for (Account destinationAccount : allMyAccounts) {
					// looping through all accounts and searching for intendedDestinationAccountName
					if (intendedDestinationAccountName.equalsIgnoreCase(destinationAccount.getAccountName())) {
						// Destination account exists - carry out transfer, subject to amount being available
						if (originAccount.getBalance() >= amountToMove) {
							// this account has enough
							// reduce amount from origin account and increase balance in destination account
							originAccount.reduceBalance(amountToMove);
							destinationAccount.addMoneyToAccount(amountToMove);
							return ProtocolsAndResponses.Responses.SUCCESS;
						}
					}
				}
			}
		}
		return ProtocolsAndResponses.Responses.FAIL;
	}

	private static double roundDouble(double d, int places) {
		BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
		bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}
}
