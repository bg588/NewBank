package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class UserInterface extends Thread {
    private BufferedReader in;
    private PrintWriter out;

    public UserInterface(Socket s) throws IOException {
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }

    public void printMenu() {
        this.out.println();
        this.out.println("*************************************");
        this.out.println("*     Welcome to NewBank Online     *");
        this.out.println("*************************************");
        this.out.println("*      Please choose from           *");
        this.out.println("*                                   *");
        this.out.println("*  1)  Create a new account         *");
        this.out.println("*  2)  Deposit cash                 *");
        this.out.println("*  3)  Withdraw cash                *");
        this.out.println("*  4)  Show my accounts             *");
        this.out.println("*  5)  Pay someone                  *");
        this.out.println("*  6)  Move money between accounts  *");
        this.out.println("*  7)  Apply for personal loan      *");
        this.out.println("*  8)  Rename Account               *");
        this.out.println("*  9)  Change Password              *");
        this.out.println("*  10) Close Account                *");
        this.out.println("*  11) Logout                       *");
        this.out.println("*  12) Exit                         *");
        this.out.println("*                                   *");
        this.out.println("*************************************");
        this.out.println();
        this.out.println("Enter an option (1 - 12)");
    }

    public ArrayList<String> getMenuOption() {
        ArrayList<String> stringArrayList = new ArrayList<String>();
        boolean menuChoose = false;
        while (menuChoose == false) {
            try {
                String myOption = in.readLine();
                //out.println("My Option is (in try) " +  myOption);
                int newOption = Integer.parseInt(myOption);
                switch (newOption) {
                    case 1:
                        out.println();
                        out.println("Creating a new account");
                        stringArrayList.add(ProtocolsAndResponses.Protocols.NEWACCOUNT);
                        out.println("Please enter an account name");
                        String accountName = in.readLine();
                        stringArrayList.add(accountName);
                        out.println("Please enter an initial deposit amount");
                        String amount = in.readLine();
                        stringArrayList.add(amount);
//                        int depositAmount = Integer.parseInt(amount);
//                        out.println("NEWACCOUNT " + accountName+" "+depositAmount);//test print so you can see what's being returned - to remove
                        return stringArrayList;
                    case 2:
                        out.println();
                        out.println("Deposit cash");
                        stringArrayList.add(ProtocolsAndResponses.Protocols.DEPOSIT);
                        out.println("Please enter an account name");
                        String depositAccountName = in.readLine();
                        stringArrayList.add(depositAccountName);
                        out.println("Please enter an amount to deposit");
                        String depositAmount = in.readLine();
                        stringArrayList.add(depositAmount.trim());
//                        int depositAmount = Integer.parseInt(amount);
//                        out.println("DEPOSIT " + accountName+" "+depositAmount);//test print so you can see what's being returned - to remove
                        return stringArrayList;
                    case 3:
                        out.println();
                        stringArrayList.add(ProtocolsAndResponses.Protocols.WITHDRAW);
                        out.println("Please enter the account name of the account you'd like to withdraw from");
                        String withdrawAccountName = in.readLine();
                        stringArrayList.add(withdrawAccountName);
                        out.println("Please enter the amount you'd like to withdraw");
                        String withdrawAmount = in.readLine();
                        int withdrawnAmount= Integer.parseInt(withdrawAmount);
                        stringArrayList.add(withdrawAmount);
                        return stringArrayList;
                    case 4:
                        out.println();
                        out.println("Showing accounts");
                        stringArrayList.add(ProtocolsAndResponses.Protocols.SHOWMYACCOUNTS);
                        return stringArrayList;
                    case 5:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.PAY);
                        out.println();
                        out.println("Pay someone");
                        out.println("Please enter a person/company to pay");
                        String payee = in.readLine();
                        stringArrayList.add(payee);
                        out.println("Please enter an amount");
                        String pay = in.readLine();
                        stringArrayList.add(pay.trim());
                        out.println("Please enter account you would like to pay from");
                        out.println("(Leave blank for any)");
                        String fromAccount = in.readLine();
                        stringArrayList.add(fromAccount.trim());
                        return stringArrayList;
                    case 6:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.MOVE);
                        out.println();
                        out.println("Move money between accounts");
                        out.println("Please enter an amount to move");
                        String move = in.readLine();
                        stringArrayList.add(move.trim());
                        out.println("Transfer from which account?");
                        String transferFrom = in.readLine();
                        stringArrayList.add(transferFrom);
                        out.println("Transfer to which account?");
                        String transferTo = in.readLine();
                        stringArrayList.add(transferTo);
//                        out.println("MOVE " + amountToMove + " " + transferFrom + " "+transferTo); //test print so you can see what's being returned - to remove
                        return stringArrayList;
                    case 7:
                        while (true) {
                            stringArrayList.add(ProtocolsAndResponses.Protocols.PLOAN);//command item 1
                            out.println();
                            out.println("You are applying for a personal loan");
                            out.println("Please enter an amount you want to borrow");
                            String amountToBorrow = in.readLine();
                            stringArrayList.add(amountToBorrow.trim()); //command item 2
                            out.println("What period do you want to borrow over? Please enter in number of months (e.g. 3 years = 36 months)");
                            out.println("You can borrow between 1 month and 10 years (120 Months)");
                            String loanTerm = in.readLine();
                            stringArrayList.add(loanTerm.trim()); //command item 3
                            out.println("What is your annual salary?");
                            String salary = in.readLine();
                            stringArrayList.add(salary.trim());//command item 4
                            out.println("Below are your parameters. Please confirm before proceeding.\n" + "You want to borrow " + amountToBorrow + " over " + loanTerm + " months. Your salary is " + salary);
                            out.println("Please press 1) if you are happy to proceed, and 2) if you want to restart the process or 3) go back to main menu");
                            String choiceUponConfirmation = in.readLine();
                            try {
                                // this needs to be in a try block in case non-integer input
                                int myChoice = Integer.parseInt(choiceUponConfirmation);
                                switch (myChoice) {
                                    case 1:
                                        return stringArrayList;
                                    case 3:
                                        //clear Array and add MAINMENU, this then gets processed in NewBank
                                        stringArrayList.clear();
                                        stringArrayList.add(ProtocolsAndResponses.Protocols.MAINMENU);
                                        return stringArrayList;
                                    default:
                                        //this needs to come last, otherwise the switch/case it will keep going
                                        //until it hits a return and return that
                                        //clear stringArrayList and return to start of loop
                                        //Note this will take any integer.... not just 2.
                                        stringArrayList.clear();
                                }
                            } catch (Exception e) {
                                //we are here if someone has input a non integer
                                //clear stringArrayList and start again at top of this loop
                                stringArrayList.clear();
                                out.println("Invalid choice, please enter 1 - 3");
                            }
                        }
                    case 8:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.RENAMEACCOUNT);
                        out.println();
                        out.println("Rename Your Account");
                        out.println("Please enter the account you wish to rename : ");
                        String accountToRename = in.readLine();
                        stringArrayList.add(accountToRename.trim());
                        out.println("Please enter the new name : ");
                        String newAccountName = in.readLine();
                        stringArrayList.add(newAccountName.trim());
                        return stringArrayList;
                    case 9:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.CHANGEPW);
                        out.println();
                        out.println("Change your password");
                        out.println("Please enter a new password : ");
                        String newPassword = in.readLine();
                        stringArrayList.add(newPassword);
                        return stringArrayList;
                    case 10:
                        out.println();
                        out.println("Closing an account");
                        stringArrayList.add(ProtocolsAndResponses.Protocols.CLOSEACCOUNT);
                        out.println("Please enter an account name");
                        String accountNameToClose = in.readLine();
                        stringArrayList.add(accountNameToClose);
                        return stringArrayList;
                    case 11:
                        out.println("We are logging you out...");
                        //This will call Logout within NewBank , which saves the csv file
                        stringArrayList.add(ProtocolsAndResponses.Protocols.LOGOUT);
                        return stringArrayList;
                    case 12:
                        out.println("Thank you, and goodbye.");
                        //This will call Exit within NewBank , which saves the csv file
                        stringArrayList.add(ProtocolsAndResponses.Protocols.EXIT);
                        return stringArrayList;
                    default:
                        out.println("Invalid choice, please choose 1 - 12");
                        menuChoose = false;
                }
                //out.println(myOption);
            } catch (Exception e) {
                out.println(e.getMessage());
                out.println("Invalid choice, please choose 1 - 12");
                menuChoose = false;
            }
        }
        return null;
    }
}
