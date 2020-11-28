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
        this.out.println("************************************");
        this.out.println("*     Welcome to NewBank Online    *");
        this.out.println("************************************");
        this.out.println("*      Please choose from          *");
        this.out.println("*                                  *");
        this.out.println("*  1) Create a new account         *");
        this.out.println("*  2) Deposit cash                 *");
        this.out.println("*  3) Show my accounts             *");
        this.out.println("*  4) Pay someone                  *");
        this.out.println("*  5) Move money between accounts  *");
        this.out.println("*  6) Apply for personal loan      *");
        this.out.println("*  7) Change Password              *");
        this.out.println("*  8) Exit                         *");
        this.out.println("*                                  *");
        this.out.println("************************************");
        this.out.println();
        this.out.println("Enter an option (1 - 8)");
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
                        out.println("Showing accounts");
                        stringArrayList.add(ProtocolsAndResponses.Protocols.SHOWMYACCOUNTS);
                        return stringArrayList;
                    case 4:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.PAY);
                        out.println();
                        out.println("Pay someone");
                        out.println("Please enter a person/company to pay");
                        String payee = in.readLine();
                        stringArrayList.add(payee);
                        out.println("Please enter an amount");
                        String pay = in.readLine();
                        stringArrayList.add(pay.trim());
//                        out.println("PAY " + payee + " " + amountToPay); //test print so you can see what's being returned - to remove
                        return stringArrayList;
                    case 5:
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
                    case 6:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.PLOAN);//command item 1
                        out.println();
                        out.println("You are applying for a personal loan");
                        out.println("Please enter an amount you want to borrow");
                        String amountToBorrow = in.readLine();
                        stringArrayList.add(amountToBorrow.trim()); //command item 2
                        out.println("What period do you want to borrow over? Please enter in number of months (e.g. 3 years = 36 months)");
                        String loanTerm = in.readLine();
                        stringArrayList.add(loanTerm.trim()); //command item 3
                        out.println("What is your annual salary?");
                        String salary = in.readLine();
                        stringArrayList.add(salary.trim());//command item 4
                        out.println("Below are your parameters. Please confirm before proceeding.\n" + "You want to borrow "+amountToBorrow + " over " + loanTerm + " months. Your salary is "+salary);
                        out.println("Please press 1) if you are happy to proceed, and 2) if you want to restart the process or 3) go back to main menu");
                        String choiceUponConfirmation = in.readLine();
                        int Mychoice = Integer.parseInt(choiceUponConfirmation);
                        switch (Mychoice) {
                            case 1:
                                return stringArrayList;
                            case 2:
                                break;//I want to bring user back to beginning of personal loan application dialogue";
                            case 3:
                                break;//I want to bring user back to the main menu";
                        }

                    case 7:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.CHANGEPW);
                        out.println();
                        out.println("Change your password");
                        out.println("Please enter a new password : ");
                        String newPassword = in.readLine();
                        stringArrayList.add(newPassword);
                        return stringArrayList;
                    case 8:
                        out.println("Thank you, and goodbye.");
                        // when this command arrives at client, client will gracefully exit :
                        out.println("DisconnectClient");
                        stringArrayList.add("Logout");
                        return (stringArrayList);
                    default:
                        out.println("Invalid choice, please choose 1 - 8");
                        menuChoose = false;
                }
                //out.println(myOption);
            } catch (Exception e) {
                out.println(e.getMessage());
                out.println("Invalid choice, please choose 1 - 8");
                menuChoose = false;
            }
        }
        return null;
    }
}
