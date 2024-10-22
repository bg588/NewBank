package server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NewBankAccountManager {
    private NewBank newBank;

    public NewBankAccountManager(NewBank bank)
    {
        newBank = bank;
    }

    public String addNewAccount(CustomerID customer, List<String> commandWithAccountNameAndDepositAmount) {
        Customer myCurrentCustomer = newBank.customers.get(customer.getKey());
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
        ArrayList<Account> accounts = newBank.customers.get(customer.getKey()).getAccounts();
        for (Account acc : accounts) {
            if (acc.getAccountName().equalsIgnoreCase(commandWithAccountNameAndDepositAmount.get(1))) {
                return "There is already an existing account";
            }
        }
        long accountNumber = ThreadLocalRandom.current().nextLong(100000000, 999999999);

        Account theNewAccount = new Account(commandWithAccountNameAndDepositAmount.get(1), amountToDeposit, accountNumber);
        myCurrentCustomer.addAccount(theNewAccount);


        return "SUCCESS\n" + "New Account Name: " + theNewAccount.getAccountName() + "\nAccount Number: "+
                theNewAccount.getAccountNumber() + "\nInitial Deposit Amount: " + theNewAccount.getBalance().toString();
    }

    public String depositToExistingAccount(CustomerID customer, List<String> commandWithExistingAccountNameAndDepositAmount) {

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
        var me = newBank.customers.get(customer.getKey());
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
                return "SUCCESS\n" + "AccountName:"+acc.getAccountName()+" Deposited:"+amountToDepositToExistingAccount+" NewBalance:"+ roundDouble(newBal,2);
            }
        }
        return String.format("No account name: %s \nCannot deposit to an account that does not exist. " +
                "Please create account first or choose from the available accounts: %s", intendedDepositAccount, accountNames);
    }

    public String withdrawFromAccount (CustomerID customer, List<String> withdrawFromAccount) {

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
        Customer cust = newBank.customers.get(customer.getKey());
        //get the current users list of accounts
        String intendedWithdrawAccount = withdrawFromAccount.get(1);

        if (cust.getAccountWithName(intendedWithdrawAccount) == null) {
            //account doesn't exist
            return ProtocolsAndResponses.Responses.FAIL + "\nCannot withdraw from an account that does not exist.";
        } else {
            Account withdrawAccount = cust.getAccountWithName(intendedWithdrawAccount);
            if (withdrawAccount.getBalance() >= withdrawAmount) {
                //we have enough in account to withdraw
                withdrawAccount.reduceBalance(withdrawAmount);
                return "SUCCESS\n" + "AccountName:" + withdrawAccount.getAccountName() + " Withdrawn:" + withdrawAmount +
                        " NewBalance:" + withdrawAccount.getBalance();
            } else {
                //not enough in account to withdraw
                return ProtocolsAndResponses.Responses.FAIL + ". You only have " + withdrawAccount.getBalance() +
                        " in account " + withdrawAccount.getAccountName();
            }
        }
    }

    public String showMyAccounts(CustomerID customer) {

        String temp =  (newBank.customers.get(customer.getKey())).accountsToString();

        if (temp.isBlank()){
            return ("You have no accounts available to show\nOpen a new account using menu option 1");}
        else {
            return(temp);
        }
    }
    public String showPersonalInfo(CustomerID customer) {
        Customer me = newBank.customers.get(customer.getKey());
        return "Customer number: " + me.getCustomerNumber() + "\n" +
                "Email: " + me.getEmail() + "\n" +
                "Phone: " + me.getPhone();
    }

    public String payPersonOrCompanyAnAmount(CustomerID customer, List<String> commandWithPayeeAndAmount) {
        var myName = customer.getKey();
        if (commandWithPayeeAndAmount.size() != 4) {
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
        //Account to pay from, note this may be empty
        String accountToPayFrom = commandWithPayeeAndAmount.get(3);

        //Now check if the payee that we are sending money to exists
        boolean validPayee = false;
        for (Object entry : newBank.customers.keySet().toArray()) {
            //bit messy, but as we need to ignore case we do this instead of checking if the keyset contains a string
            String validCustomer = entry.toString();
            if (validCustomer.equalsIgnoreCase(personOrCompanyToPay)) {
                //the payee exists, so we can break and set validPayee to true
                validPayee = true;
                break;
            }
        }

        if (!validPayee) {
            //if valid payee is still false, the customer doesn't have an account here
            return "Payee is not valid, " + personOrCompanyToPay + " does not have an account here";
        }

            //this is a for-each loop that will cycle through the customer keys (which are the names of the accounts)
            for (String customerName : newBank.customers.keySet()) {
                //when we reach the customer we want to pay
                if (personOrCompanyToPay.equalsIgnoreCase(customerName)) {
                    //we pull out the customer object based on the name we matched above
                    var payee = newBank.customers.get(customerName);
                    //we get the customers accounts
                    ArrayList<Account> PayeeAccounts = payee.getAccounts();

                //get the current users customer object
                var me = newBank.customers.get(customer.getKey());
                //get the current users list of accounts
                var myAccounts = me.getAccounts();

                if (accountToPayFrom.length() > 0) {
                    //customer has specified an account they want to pay from
                    if (me.getAccountWithName(accountToPayFrom) == null) {
                        //account doesn't exist
                        return ProtocolsAndResponses.Responses.FAIL + " account : " + accountToPayFrom + " doesn't exist";
                    }
                    //account to pay from has been specified
                    if(me.getAccountWithName(accountToPayFrom).getBalance() >= amountToPay) {
                        //account has enough money to pay
                        me.getAccountWithName(accountToPayFrom).reduceBalance(amountToPay);
                        PayeeAccounts.get(0).addMoneyToAccount(amountToPay);
                        return ProtocolsAndResponses.Responses.SUCCESS + " New Balance : " + accountToPayFrom +
                                " : " + me.getAccountWithName(accountToPayFrom).getBalance().toString();
                    }
                    //not enough money in account
                    return ProtocolsAndResponses.Responses.FAIL + " not enough money in account " + accountToPayFrom;
                }

                //if we get here, the customer has left account to pay from blank, so continue normal behaviour

                //cycle through the user accounts to find one with enough money in it
                for (Account account : myAccounts) {
                    if (account.getBalance() >= amountToPay) {
                        //yay this account has enough - reduce my balance and pay the person
                        account.reduceBalance(amountToPay);
                        PayeeAccounts.get(0).addMoneyToAccount(amountToPay);

                        return "SUCCESS\n" + "NewBalance:"+account.getAccountName()+" "+ roundDouble(Double.parseDouble(account.getBalance().toString()),2); //added to return the rounded value

                    }
                }
                break;
            }
        }

        Customer me = newBank.customers.get(customer.getKey());
        ArrayList<Account> allMyAccounts = me.getAccounts();
        // We reach this point if we haven't specified an account and no account has enough balance to make payment
        return "Not enough in any account. Your accounts are as follows : " + allMyAccounts;
    }

    public String moveAnAmountFromOneAccountToAnother(CustomerID customer,
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
        Customer me = newBank.customers.get(customer.getKey());
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
                            return "SUCCESS\n" + "New Balance:"+intendedOriginAccountName + " " + roundDouble(Double.parseDouble(originAccount.getBalance().toString()),2) +
                                    " " + intendedDestinationAccountName + " " + roundDouble(Double.parseDouble(destinationAccount.getBalance().toString()),2);

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
            listOfMyAccountsAndBalance.add(new Account(myAccount.getAccountName(), myAccount.getBalance(),myAccount.getAccountNumber()));
        }
        StringBuffer sb = new StringBuffer();
        for(Account eachItemInArray:listOfMyAccountsAndBalance){
            sb.append(eachItemInArray);
            sb.append(" ");
        }
        String balance = sb.toString();
        return "FAIL\n" + "Balance:"+ roundDouble(Double.parseDouble(balance),2);
    }

    public static double roundDouble(double d, int places) { //changed to public to use function elsewhere
        BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public String approveOrRejectLoanApplication(CustomerID customer, List<String> commandWithLoanApplicationParameters) {

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
        //if the bank thinks the loan amount is affordable to pay
        if (monthlyRepayment <= affordability) {
            //get the current users list of accounts
            Customer me = newBank.customers.get(customer.getKey());
            ArrayList<Account> myAccounts = me.getAccounts();

            Account loanAccount = me.getAccountWithName("Personal Loan");
            Account accountToAddMoneyInto = myAccounts.get(0);

            if (loanAccount != null) {
                //a loan account exists already, so update that
                double newAmountToBorrow = -(loanAccount.getBalance()-amountToBorrow);
                double newMonthlyRepayment = newAmountToBorrow * (overallInterestPerMonth * (Math.pow(1 + overallInterestPerMonth,loanTerm)))/
                        (Math.pow(1 + overallInterestPerMonth,loanTerm) - 1);
                if (newMonthlyRepayment <= affordability) {
                    //get the current users list of accounts
                    loanAccount.reduceBalance(amountToBorrow);
                    accountToAddMoneyInto.addMoneyToAccount(amountToBorrow);
                    return "SUCCESS\n" + "Your New Credit Balance including existing loan is: " + accountToAddMoneyInto.getAccountName() + " " + accountToAddMoneyInto.getBalance() +
                            "\nAnd your new balance of loan account is:" + roundDouble(Double.parseDouble(loanAccount.getBalance().toString()),2);
                    //+" And repayment is:"+newMonthlyRepayment;//this is used for testing

                } else{
                    return "FAIL\n" + "Your New Balance would exceed overall affordability - you new loan application has been rejected";
                }
            } else {
                //a loan account doesn't exist yet, so create one
                loanAccount = new Account("Personal Loan", -amountToBorrow,loanAccount.getAccountNumber());
                me.addAccount(loanAccount);

                //get first account from client account list and pay loan amount into the account
                accountToAddMoneyInto.addMoneyToAccount(amountToBorrow);
                return "SUCCESS\n" + "Your New Balance is: " + accountToAddMoneyInto.getAccountName() + " " + accountToAddMoneyInto.getBalance() +
                        "\nAnd your new balance of loan account is:" + roundDouble(Double.parseDouble(loanAccount.getBalance().toString()),2);
                //+"Monthly repayment is : "+monthlyRepayment; this is used for testing

            }
        } else {
            return "Sorry, we have not been able to approve your loan at this time. \nYour new loan balance would exceed overall affordability. \nPlease call us on 01225 383214 if you would like to discuss this further.";
        }
    }

    public String renameAccount(CustomerID customer, List<String> commandWithRenameParameters) {

        if (commandWithRenameParameters.size() != 3) {
            //not the correct amount of args
            return "Wrong Amount of args";
        }
        //first input in the split array is the command
        if (!commandWithRenameParameters.get(0).equals(ProtocolsAndResponses.Protocols.RENAMEACCOUNT)) {
            //Somehow the wrong command came in here
            return ProtocolsAndResponses.Responses.FAIL;
        }
        // next inputs are old and new account names
        String oldAccountName = commandWithRenameParameters.get(1);
        if(oldAccountName.equals("Personal Loan")) {
            //cannot rename the personal loan account
            return ProtocolsAndResponses.Responses.FAIL;
        }
        String newAccountName = commandWithRenameParameters.get(2);

        //get the current users customer object
        Customer me = newBank.customers.get(customer.getKey());
        ArrayList<Account> allMyAccounts = me.getAccounts();

        for (Account account : allMyAccounts) {
            if (account.getAccountName().equals(oldAccountName)) {
                //we have found the oldAccountName. Set this to the newAccountName
                account.renameAccount(newAccountName);
                return ProtocolsAndResponses.Responses.SUCCESS;
            }
        }
        // if we are here, the oldAccountName didn't exist, so return a fail
        return ProtocolsAndResponses.Responses.FAIL;
    }
}
