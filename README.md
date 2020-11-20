Group 8

This document details the protocol for interacting with the NewBank server.  

A customer enters the command below and sees the messages returned 

SHOWMYACCOUNTS
Returns a list of all the customers accounts along with their current balance 
e.g. Main: 1000.0 

NEWACCOUNT <Name>
e.g. NEWACCOUNT Savings
Returns SUCCESS or FAIL

MOVE <Amount> <From> <To>
e.g. MOVE 100 Main Savings 
Returns SUCCESS or FAIL
Plus FromAccount Balance ToAccount Balance
e.g. original balance Checking 250.0 Savings 0.0
In case of SUCCESS)
New Balance: Checking 150.0 Savings 100.0
In case of FAIL)
Balance: Checking 250.0 Savings 0.0 

PAY <Person/Company> <Ammount>
e.g. PAY John 100
Returns SUCCESS or FAIL
Plus AccountUsed Balance 
e.g. original balance Checking 250.0 
In case of SUCCESS)
New Balance: Checking 150.0
In case of FAIL)
Balance: Checking 250.0
