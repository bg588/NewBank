package server;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private Persister persister;

	private NewBank() {
		customers = new HashMap<>();
		persister = new Persister();
		if (persister.fileExists()) {
			// a persisted file exists, so load our customer data from that
			customers = persister.getPersistedData();
		} else {
			// no persisted file exists, so go with the test data that we were provided
			// this could potentially be moved later to the Persister to tidy things up
			addTestData();
		}
	}

	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		bhagy.setPassword("password");
		customers.put("Bhagy", bhagy);

		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		christina.setPassword("password");
		customers.put("Christina", christina);

		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		john.setPassword("password");
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if (customers.containsKey(userName)) {
			// create CustomerID as username exists
			CustomerID customerID = new CustomerID(userName);
			// create a local customer which represents the valid customer
			// customer holds password etc. customerID is what is passed back to NewBankClientHandler
			Customer customer = customers.get(userName);
			if (customer.isLocked()) {
				//check whether account is locked, if it is then set customerID to locked and return it
				customerID.lock();
				return customerID;
			}
			if (customer.verifyPassword(password)) {
				// user and password are correct
				// reset password attempts remaining for customer
				customer.resetPasswordAttemptsRemaining();
				// password matches, set CustomerID as authenticated
				customerID.setAuthenticated(true);
				// return customerID to allow login
				return customerID;
			}
			// user exists, password does not match - increment failedPasswordAttempt counter on customer
			customer.failedPasswordAttempt();
			// set customerID authenticated to false
			customerID.setAuthenticated(false);
			// set attempts remaining on customerID to be passed back to NewBankClientHandler
			customerID.setPasswordAttemptsRemaining(customer.getPasswordAttemptsRemaining());
			return customerID;
		}
		// username does not exist, return null CustomerID
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, ArrayList<String> request) {
		if (customers.containsKey(customer.getKey())) {
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.NEWACCOUNT)) {
				return addNewAccount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.DEPOSIT)) {
				return depositToExistingAccount(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.SHOWMYACCOUNTS)) {
				return showMyAccounts(customer);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.PAY)) {
				return payPersonOrCompanyAnAmount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.MOVE)) {
				return moveAnAmountFromOneAccountToAnother(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.PLOAN)) {
				return approveOrRejectLoanApplication(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.CHANGEPW)) {
				return changePassword(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.EXIT)) {
				return exit();
			}
		}
		return ProtocolsAndResponses.Responses.FAIL;
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	//this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
	private String addNewAccount(CustomerID customer, List<String> commandWithAccountNameAndDepositAmount) {
		Customer myCurrentCustomer = customers.get(customer.getKey());
		//flatten the list after the first split
		if (commandWithAccountNameAndDepositAmount.size() != 3) {
			//not the correct amount of args
			return "Wrong Amount of args";
		}
		//first input in the split array is the command
		if(!commandWithAccountNameAndDepositAmount.get(0).equals(ProtocolsAndResponses.Protocols.NEWACCOUNT)) {
			//Somehow the wrong command came in here
			return ProtocolsAndResponses.Responses.FAIL;
		}
		if (commandWithAccountNameAndDepositAmount.get(1).equals("")) {
			//account name cannot be blank
			return "Account name cannot be blank";
		}
		//var accountToDeposit = commandWithAccountNameAndDepositAmount.get(1);
		double amountToDeposit;
		try {
			//next input in the split array must be the amount
			//uses big decimal to keep to 2 decimal places
			amountToDeposit = roundDouble(Double.parseDouble(commandWithAccountNameAndDepositAmount.get(2)), 2);
		}
		catch (NumberFormatException ex) {
			return "deposit amount could not be converted to a valid number";
		}
		if(amountToDeposit <= 0.009) {
			//cannot pay someone less that 0.01 wtv currency
			return "Cannot deposit less than 0.01";
		}
		ArrayList<Account> accounts = customers.get(customer.getKey()).getAccounts();
		for (Account acc : accounts) {
			if (acc.getAccountName().equalsIgnoreCase(commandWithAccountNameAndDepositAmount.get(1))) {
				return "There is already an existing account";
			}
		}
		Account theNewAccount = new Account(commandWithAccountNameAndDepositAmount.get(1), amountToDeposit);
		myCurrentCustomer.addAccount(theNewAccount);

		return "SUCCESS\n" + "NewAccountName:"+theNewAccount.getAccountName()+" InitialDepositAmount:"+theNewAccount.getBalance().toString();
	}

	//this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
	private String depositToExistingAccount(CustomerID customer, List<String> commandWithExistingAccountNameAndDepositAmount) {

		if (commandWithExistingAccountNameAndDepositAmount.size() != 3) {
			//not the correct amount of args
			return "Wrong Amount of args";
		}
		//first input in the split array is the command
		if(!commandWithExistingAccountNameAndDepositAmount.get(0).equals(ProtocolsAndResponses.Protocols.DEPOSIT)) {
			//Somehow the wrong command came in here
			return ProtocolsAndResponses.Responses.FAIL;
		}
		if (commandWithExistingAccountNameAndDepositAmount.get(1).equals("")) {
			//account name cannot be blank
			return "Account name cannot be blank";
		}
		//var accountToDeposit = commandWithAccountNameAndDepositAmount.get(1);
		double amountToDepositToExistingAccount;
		try {
			//next input in the split array must be the amount
			//uses big decimal to keep to 2 decimal places
			amountToDepositToExistingAccount = roundDouble(Double.parseDouble(commandWithExistingAccountNameAndDepositAmount.get(2)), 2);
		}
		catch (NumberFormatException ex) {
			return "deposit amount could not be converted to a valid number";
		}
		if(amountToDepositToExistingAccount <= 0.009) {
			//cannot pay someone less that 0.01 wtv currency
			return "Cannot deposit less than 0.01";
		}
		//get the current users customer object
		var me = customers.get(customer.getKey());
		//get the current users list of accounts
		var myAccounts = me.getAccounts();

		String intendedDepositAccount = commandWithExistingAccountNameAndDepositAmount.get(1);
		String accountNames = "";
		for (Account acc : myAccounts) {
			accountNames += acc.getAccountName()+" ";
			if (acc.getAccountName().equalsIgnoreCase(intendedDepositAccount)) {
				var priorBal = acc.getBalance();
				acc.addMoneyToAccount(amountToDepositToExistingAccount);
				var newBal = priorBal+amountToDepositToExistingAccount;
				return "SUCCESS\n" + "AccountName:"+acc.getAccountName()+" Deposited:"+amountToDepositToExistingAccount+" NewBalance:"+newBal;
			}
		}
		return String.format("No account name: %s \nCannot deposit to an account that does not exist. " +
				"Please create account first or choose from the available accounts: %s", intendedDepositAccount, accountNames);
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
						return "SUCCESS\n" + "NewBalance:"+account.getAccountName()+" "+account.getBalance().toString();
					}
				}
				break;
			}
		}
		Customer me = customers.get(customer.getKey());
		ArrayList<Account> allMyAccounts = me.getAccounts();

		ArrayList<Account> listOfMyAccountsAndBalance = new ArrayList<Account>();
		for (Account myAccount : allMyAccounts) {
			listOfMyAccountsAndBalance.add(new Account(myAccount.getAccountName(), myAccount.getBalance()));
		}
		StringBuffer sb = new StringBuffer();
		for(Account eachItemInArray:listOfMyAccountsAndBalance){
			sb.append(eachItemInArray);
			sb.append(" ");
		}
		String balance = sb.toString();
		return "FAIL\n" + "Balance:"+balance;
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

		//get the current users customer object
		Customer me = customers.get(customer.getKey());
		ArrayList<Account> allMyAccounts = me.getAccounts();

		String accountNames = "";


		for (Account originAccount : allMyAccounts) {
			accountNames += originAccount.getAccountName()+" ";
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
							return "SUCCESS\n" + "New Balance:"+intendedOriginAccountName + " " + originAccount.getBalance().toString() +
									" " + intendedDestinationAccountName + " " + destinationAccount.getBalance().toString();
						}
					}
				}
			}
		}

		if(!accountNames.contains(intendedOriginAccountName))
		{
			return String.format("No account name: %s\nCannot move from an account that does not exist. " +
					"Please create an account first or choose from the available accounts: %s", intendedOriginAccountName, accountNames);
		}
		if(!accountNames.contains(intendedDestinationAccountName))
		{
			return String.format("No account name: %s\nCannot move to an account that does not exist. " +
					"Please create an account first or choose from the available accounts: %s", intendedDestinationAccountName, accountNames);
		}

		ArrayList<Account> listOfMyAccountsAndBalance = new ArrayList<Account>();
		for (Account myAccount : allMyAccounts) {
			listOfMyAccountsAndBalance.add(new Account(myAccount.getAccountName(), myAccount.getBalance()));
		}
		StringBuffer sb = new StringBuffer();
		for(Account eachItemInArray:listOfMyAccountsAndBalance){
			sb.append(eachItemInArray);
			sb.append(" ");
		}
		String balance = sb.toString();
		return "FAIL\n" + "Balance:"+balance;
	}


	//this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
	private String approveOrRejectLoanApplication(CustomerID customer, List<String> commandWithLoanApplicationParameters) {

		if (commandWithLoanApplicationParameters.size() != 4) { //Command(0), amountToBorrow(1), loanTerm(2), salary(3)
			//not the correct amount of args
			return "Wrong Amount of args";
		}
		//first input in the split array is the command
		if(!commandWithLoanApplicationParameters.get(0).equals(ProtocolsAndResponses.Protocols.PLOAN)) {
			//Somehow the wrong command came in here
			return ProtocolsAndResponses.Responses.FAIL;
		}
		if (commandWithLoanApplicationParameters.get(1).equals("")) {
			//account name cannot be blank
			return "Loan amount cannot be blank";
		}
		if (commandWithLoanApplicationParameters.get(2).equals("")) {
			//account name cannot be blank
			return "Loan term cannot be blank";
		}
		if (commandWithLoanApplicationParameters.get(3).equals("")) {
			//account name cannot be blank
			return "Your salary cannot be blank";
		}

		double amountToBorrow;
		try {
			//next input in the split array must be the amount
			//uses big decimal to keep to 2 decimal places
			amountToBorrow = roundDouble(Double.parseDouble(commandWithLoanApplicationParameters.get(1)), 2);
		} catch (NumberFormatException ex) {
			return "deposit amount could not be converted to a valid number";
		}
		if(amountToBorrow <= 0.009) {
			//cannot borrow less than 0.01 wtv currency
			return "Cannot borrow less than 0.01";
		}

		int loanTerm;
		try {
			//next input in the split array must be the amount
			loanTerm = Integer.parseInt(commandWithLoanApplicationParameters.get(2));
		} catch (NumberFormatException ex) {
			return "loan term could not be converted to a valid number";
		}
		if (loanTerm <= 0 || loanTerm > 120) {
			//cannot borrow in a period less than 0 month or less or greater than 120 months
			return "Please enter an term between 1 and 120 months";
		}

		int salary;
		try {
			//next input in the split array must be the amount
			salary = Integer.parseInt(commandWithLoanApplicationParameters.get(3));
		} catch (NumberFormatException ex) {
			return "salary could not be converted to a valid number";
		}
		if(salary <= 0) {
			//cannot borrow on a salary less than 0 month or less
			return "Cannot borrow with 0 salary";
		}

		double affordability;
		affordability = (salary / 12.0) * FinancialParameters.BorrowingLimit;
		double overallInterestPerMonth;
		overallInterestPerMonth = FinancialParameters.BankOfEnglandBaseRate /
				(12 + (FinancialParameters.NewBankMargin / 12));
		double monthlyRepayment;
		monthlyRepayment = amountToBorrow * (overallInterestPerMonth * (Math.pow(1 + overallInterestPerMonth,loanTerm)))/
				(Math.pow(1 + overallInterestPerMonth,loanTerm) - 1);
		System.out.println("Monthly repayments would be " + monthlyRepayment);
		System.out.println("Affordability says " + affordability);
		//if the bank thinks the loan maount is affordable to pay
		if (monthlyRepayment <= affordability) {
			//get the current users list of accounts
			Customer me = customers.get(customer.getKey());
			ArrayList<Account> myAccounts = me.getAccounts();

			Account loanAccount = me.getAccountWithName("Personal Loan");

			if (loanAccount != null) {
				//a loan account exists already, so update that
				loanAccount.reduceBalance(amountToBorrow);
			} else {
				//a loan account doesn't exist yet, so create one
				loanAccount = new Account("Personal Loan", -amountToBorrow);
				me.addAccount(loanAccount);
			}

			//get first account from client account list and pay loan amount into the account
			Account accountToAddMoneyInto = myAccounts.get(0);
			accountToAddMoneyInto.addMoneyToAccount(amountToBorrow);

			return "SUCCESS\n" + "Your New Balance is:" + accountToAddMoneyInto.getAccountName() + " " + accountToAddMoneyInto.getBalance().toString() +
					"\nAnd your new balance of loan account is:" + loanAccount.getBalance().toString();

		}
		return ProtocolsAndResponses.Responses.FAIL;
	}

	private String changePassword(CustomerID customerID,  List<String> newPassword) {
		Customer me = customers.get(customerID.getKey());
		if (me.changePassword(newPassword.get(1))) {
			// this returns success if new password is accepted (conditions defined within Password class)
			return ProtocolsAndResponses.Responses.SUCCESS;
		}
		return ProtocolsAndResponses.Responses.FAIL;
	}

	private String exit() {
		// save down data
		persister.setPersistedData(customers);
		// return exit
		return ProtocolsAndResponses.Responses.EXIT;
	}

	private static double roundDouble(double d, int places) {
		BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
		bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}
}
