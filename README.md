Group 8

This document details the protocol for interacting with the NewBank server.  

A customer enters the command below and sees the messages returned 

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


9.EXIT
This will exit you out of ExampleClient but stay connected to NewBankServer

10.LOGOUT
This will bring you back to pre-log in dialogue
