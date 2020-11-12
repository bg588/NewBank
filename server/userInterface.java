

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    public String getMenuOption() {

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
                        return "NEWACCOUNT " + accountName;

                    case 2:
                        out.println();
                        out.println("Showing accounts");
                        return "SHOWMYACCOUNTS";
                    case 3:
                        out.println();
                        out.println("Pay someone");
                        out.println("Please enter a person/company to pay");
                        String payee = in.readLine();
                        out.println("Please enter an amount");
                        String pay = in.readLine();
                        int amountToPay = Integer.parseInt(pay);
                        out.println("PAY " + payee + " " + amountToPay); //test print so you can see what's being returned - to remove
                        return "PAY " + payee + " " + amountToPay;
                    case 4:
                        out.println();
                        out.println("Move money between accounts");
                        out.println("Please enter an amount to move");
                        String amount = in.readLine();
                        int amountToMove = Integer.parseInt(amount);
                        out.println("Transfer from which account?");
                        String transferFrom = in.readLine();
                        //String transferOut = in.readLine();
                        out.println("Transfer to which account?");
                        String transferTo = in.readLine();
                        //out.println(transferOut);
                        out.println("MOVE " + amountToMove + " " + transferFrom + " " + transferTo);//test print so you can see what's being returned - to remove
                        return "MOVE " + amountToMove + " " +  transferFrom+ " " + transferTo;

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
