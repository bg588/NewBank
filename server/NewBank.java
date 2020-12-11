package server;


import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewBank {

	private static final NewBank bank = new NewBank();
	public HashMap<String,Customer> customers;
	public Persister persister;
	private NewBankAccountManager accountManager;

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
		accountManager = new NewBankAccountManager(this);
	}

	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		bhagy.setPassword("password");
		bhagy.setDateOfBirth("01-01-2020");
		customers.put("Bhagy", bhagy);

		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		christina.setPassword("password");
		christina.setDateOfBirth("01-01-2020");
		customers.put("Christina", christina);

		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		john.setPassword("password");
		john.setDateOfBirth("01-01-2020");
		customers.put("John", john);
	}

	public static NewBank getBank() {
		return bank;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password, String dateOfBirth) {
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
			if(customer.verifyDateOfBirth(dateOfBirth)){
				customerID.setAuthenticated(true);
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
				return accountManager.addNewAccount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.DEPOSIT)) {
				return accountManager.depositToExistingAccount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.WITHDRAW)) {
				return accountManager.withdrawFromAccount(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.SHOWMYACCOUNTS)) {
				return accountManager.showMyAccounts(customer);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.WITHDRAW)) {
				return withdrawFromAccount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.PAY)) {
				return accountManager.payPersonOrCompanyAnAmount(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.MOVE)) {
				return accountManager.moveAnAmountFromOneAccountToAnother(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.PLOAN)) {
				return accountManager.approveOrRejectLoanApplication(customer, request);
			}
			if (request.get(0).contains(ProtocolsAndResponses.Protocols.RENAMEACCOUNT)) {
				return accountManager.renameAccount(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.CHANGEPW)) {
				return changePassword(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.CLOSEACCOUNT)) {
				return closeAccount(customer, request);
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.EXIT)) {
				return exit();
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.LOGOUT)) {
				return logOut();
			}
			if (request.get(0).equals(ProtocolsAndResponses.Protocols.MAINMENU)) {
				return mainMenu();
			}

		}
		return ProtocolsAndResponses.Responses.FAIL;
	}


	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();

	private String changePassword(CustomerID customerID,  List<String> newPassword) {
		Customer me = customers.get(customerID.getKey());
		if (me.changePassword(newPassword.get(1))) {
			// this returns success if new password is accepted (conditions defined within Password class)
			return ProtocolsAndResponses.Responses.SUCCESS;
		}
		return ProtocolsAndResponses.Responses.FAIL + " " + ProtocolsAndResponses.Responses.PWRULES;

	}

	//this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
	private String closeAccount(CustomerID customer, List<String> commandWithAccountNameAndDepositAmount) {
		//Customer myCurrentCustomer = customers.get(customer.getKey());
		//flatten the list after the first split
		if (commandWithAccountNameAndDepositAmount.size() != 2) {
			//not the correct amount of args
			return "Wrong Amount of args";
		}
		//first input in the split array is the command
		if(!commandWithAccountNameAndDepositAmount.get(0).equals(ProtocolsAndResponses.Protocols.CLOSEACCOUNT)) {
			//Somehow the wrong command came in here
			return ProtocolsAndResponses.Responses.FAIL;
		}
		if (commandWithAccountNameAndDepositAmount.get(1).equals("")) {
			//account name cannot be blank
			return "Account name cannot be blank";
		}
		Customer me = customers.get(customer.getKey());
		Account accountToClose = me.getAccountWithName(commandWithAccountNameAndDepositAmount.get(1));
		ArrayList<Account> accounts = customers.get(customer.getKey()).getAccounts();


    private String withdrawFromAccount(CustomerID customer, List<String> withdrawFromAccount) {
        if (withdrawFromAccount.size() != 3) {
            return "Wrong Amount of args";
        }

        if (!withdrawFromAccount.get(0).equals(ProtocolsAndResponses.Protocols.WITHDRAW)) {

            return ProtocolsAndResponses.Responses.FAIL;
        }
        if (withdrawFromAccount.get(1).equals("")) {

            return "Account name cannot be blank";
        }
        double withdrawAmount;
        try {

            withdrawAmount = roundDouble(Double.parseDouble(withdrawFromAccount.get(2)), 2);
        } catch (NumberFormatException ex) {
            return " withdraw amount could not be converted to a valid number";
        }
        if (withdrawAmount <= 0.009) {

            return "Cannot withdraw less than 0.01";
        }
        //get the current users customer object
        var cust = customers.get(customer.getKey());
        //get the current users list of accounts
        var custAccounts = cust.getAccounts();
		String intendedWithdrawAccount = withdrawFromAccount.get(1);

		for (Account acc1 : custAccounts) {
			if (acc1.getAccountName().equalsIgnoreCase(intendedWithdrawAccount)) {
				var priorBal = acc1.getBalance();
				acc1.reduceBalance(withdrawAmount);
				var newBal = priorBal-withdrawAmount;
				return "SUCCESS\n" + "AccountName:"+acc1.getAccountName()+" Withdrawn:"+withdrawAmount+" NewBalance:"+newBal;
			}
			return "Cannot withdraw from an account that does not exist. Please create account first";
		}
		return ProtocolsAndResponses.Responses.FAIL;
    }

    //this will move to public once we tie in "Improve Command Line Interface to a menu based system" story
    private String depositToExistingAccount(CustomerID customer, List<String> commandWithExistingAccountNameAndDepositAmount) {

		if (accountToClose != null) {


			if(accountToClose.getBalance()==0){
			//an account exists, so remove it
				accounts.remove(accountToClose);
				return "SUCCESS\n" + "Account "+accountToClose+" has been closed successfully";
			} else{
			return "You still have an account balance of "+accountToClose.getBalance()+"\nPlease withdraw the amount first";
			}

		}else{
			return ProtocolsAndResponses.Responses.FAIL;
		}


	}

	private String mainMenu() {
		return ProtocolsAndResponses.Responses.SUCCESS;
	}

	private String exit() {
		// save down data
		persister.setPersistedData(customers);
		// return exit
		return ProtocolsAndResponses.Responses.EXIT;
	}

	private String logOut() {
		// save down data
		persister.setPersistedData(customers);
		// return logout
		return ProtocolsAndResponses.Responses.LOGOUT;
	}

}
