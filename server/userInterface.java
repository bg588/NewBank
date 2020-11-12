

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


        try {
            String myOption = in.readLine();
            //out.println("My Option is (in try) " +  myOption);
            int newOption = Integer.parseInt(myOption);
            switch (newOption) {
                case 1:
                    //return NEWACCOUNT
                    break;
                case 2:
                    out.println("Show accounts");
                    return "SHOWMYACCOUNTS";
                case 3:
                    //run pay code
                    break;
                case 4:
                    //Move money code
                    break;
                default:
                    return "";

            }
            out.println(myOption);
        } catch (IOException var2) {

        }


        return null;
    }

    private void parseInt(String myOption) {
    }
}
