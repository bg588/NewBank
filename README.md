Group 8

This document details the protocol for interacting with the NewBank server.  

Initial Log in:
Customer is allowed to key in password up to 3 times, before the account is locked and server disconnects.

Upon successful log in, customer is brought to a menu based options of command.
A customer enters the command below from numbered menu and sees the messages returned.

1.NEWACCOUNT <Name>
e.g. NEWACCOUNT Savings 100
e.g. original balance Checking 250.0
Returns SUCCESS or FAIL
In case of SUCCESS)
NewBalance:Checking 150.0 Savings 100.0

2.DEPOSIT <Account><Amount>
e.g. DEPOSIT Main 100
e.g. original balance Checking 250.0
Return SUCCESS or FAIL
In case of SUCCESS)
AccountName:Checking Deposited:100.0 NewBalance:350.0

3.WITHDRAW <Account><Amount>
e.g. WITHDRAW Main 100
e.g. original balance Checking 250.0
Return SUCCESS or FAIL
In case of SUCCESS)
AccountName:Checking Withdrawn:100.0 NewBalance:150.0

4.SHOWMYACCOUNTS
Returns a list of all the customers accounts along with their current balance 
e.g. Main: 1000.0 

5.PAY <Person/Company> <Ammount>
e.g. PAY John 100
Returns SUCCESS or FAIL
Plus AccountUsed Balance 
e.g. original balance Checking 250.0 
In case of SUCCESS)
New Balance:Checking 150.0
In case of FAIL)
Balance:Checking 250.0

6.MOVE <Amount> <From> <To>
e.g. MOVE 100 Main Savings 
Returns SUCCESS or FAIL
Plus FromAccount Balance ToAccount Balance
e.g. original balance Checking 250.0 Savings 0.0
In case of SUCCESS)
NewBalance:Checking 150.0 Savings 100.0
In case of FAIL)
Balance:Checking 250.0 Savings 0.0 

7.Personal Loan
PLOAN <amountToBorrow><loanTerm(in months)><salary>
e.g. John 10000 36 50000
Returns SUCEESS or FAIL depending on affordability
In case of SUCCESS
New Blaance: Checking 10250, Personal Loan -10000

8.Change Password
Customer can change password using below criteria:
At least 8 characters with 1 capital and 1 non-number/alphabet character.
It will return SUCCESS or FAIL.
On a returned FAIL it will reiterate the requirements for a valid password

9. Close Account
Close <accountToClose>
SUCCESS OR FAIL depending on account balance
If the account balance is zero it should close, otherwise it would show message that customer needs to withdraw it first.

10.EXIT
This will exit you out of ExampleClient but stay connected to NewBankServer

11.LOGOUT
This will bring you back to pre-log in dialogue
