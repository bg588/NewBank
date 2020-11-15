

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class userInterface extends Thread {
    private NewBank bank = NewBank.getBank();
    private BufferedReader in;
    private PrintWriter out;

    public userInterface(Socket s) throws IOException {
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
        this.out.println("*  2) Show my accounts             *");
        this.out.println("*  3) Pay someone                  *");
        this.out.println("*  4) Move money between accounts  *");
        this.out.println("*                                  *");
        this.out.println("************************************");
        this.out.println();
        this.out.println("Enter an option (1 - 4)");
    }

    public ArrayList<String> getMenuOption() {
        ArrayList<String> stringArrayList = new ArrayList<String>();
        boolean menuChoose = false;
        while (menuChoose == false) {
            try {
                menuChoose = true;
                String myOption = in.readLine();
                //out.println("My Option is (in try) " +  myOption);
                int newOption = Integer.parseInt(myOption);
                switch (newOption) {
                    case 1:
                        out.println();
                        out.println("Creating a new account");
                        out.println("Please enter an account name");
                        String accountName = in.readLine();
                        out.println("NEWACCOUNT " + accountName);//test print so you can see what's being returned - to remove
                        stringArrayList.add(ProtocolsAndResponses.Protocols.NEWACCOUNT);
                        stringArrayList.add(accountName);
                        return stringArrayList;

                    case 2:
                        out.println();
                        out.println("Showing accounts");
                        stringArrayList.add(ProtocolsAndResponses.Protocols.SHOWMYACCOUNTS);
                        return stringArrayList;
                    case 3:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.PAY);
                        out.println();
                        out.println("Pay someone");
                        out.println("Please enter a person/company to pay");
                        String payee = in.readLine();
                        stringArrayList.add(payee);
                        out.println("Please enter an amount");
                        String pay = in.readLine();
                        stringArrayList.add(pay);
                        int amountToPay = Integer.parseInt(pay);
                        out.println("PAY " + payee + " " + amountToPay); //test print so you can see what's being returned - to remove
                        return stringArrayList;
                    case 4:
                        stringArrayList.add(ProtocolsAndResponses.Protocols.MOVE);
                        out.println();
                        out.println("Move money between accounts");
                        out.println("Please enter an amount to move");
                        String amount = in.readLine();
                        int amountToMove = Integer.parseInt(amount);
                        out.println("Transfer from which account?");
                        stringArrayList.add(amount.trim());
                        String transferFrom = in.readLine();
                        stringArrayList.add(transferFrom);
                        out.println("Transfer to which account?");
                        String transferTo = in.readLine();
                        stringArrayList.add(transferTo);
                        out.println("MOVE " + amountToMove + " " + transferFrom + " "+transferTo); //test print so you can see what's being returned - to remove
                        return stringArrayList;

                    default:
                        out.println("Invalid choice, please choose 1 - 4");
                        menuChoose = false;
                }
                //out.println(myOption);
            } catch (Exception e) {
                out.println(e.getMessage());
                out.println("Invalid choice, please choose 1 - 4");
                menuChoose = false;
            }



        }
        return null;
    }

}
