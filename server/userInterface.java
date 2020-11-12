

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
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new PrintWriter(s.getOutputStream(), true);
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
            Integer var1 = this.in.read();
        } catch (IOException var2) {
        }

        return null;
    }
}
