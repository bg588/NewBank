package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	private UserInterface ui;
	private Integer loginAttemptsRemaining;
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
		ui = new UserInterface(s);
		loginAttemptsRemaining = 3;
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			// ask for user name
			out.println();
			out.println("** NewBank Online Login **");
			out.println("Enter Username");
			String userName = in.readLine();
			// ask for password
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");
			// authenticate user and get customer ID token from bank for use in subsequent requests
			CustomerID customerID = bank.checkLogInDetails(userName, password);
			if (customerID != null && customerID.isLocked()) {
				//customer exists, but account is locked
				out.println("Your account has been locked. Please contact customer services");
				//send this to disconnect client :
				out.println(ProtocolsAndResponses.Responses.EXIT);
			} else if (customerID != null && !customerID.isAuthenticated()) {
				// customer exists, but password does not match
				out.println("Password does not match");
				if (customerID.getPasswordAttemptsRemaining() == 0) {
					out.println("You have exceeded you maximum attempts and your account is now locked.");
					out.println("Please contact customer services");
					//Exceed max login attempts. Print DisconnectClient to stop ExampleClient.
					out.println(ProtocolsAndResponses.Responses.EXIT);
				} else {
					out.println("Please try again, you have " + customerID.getPasswordAttemptsRemaining() + " attempt(s) remaining");
					run();
				}
			} else if (customerID != null && customerID.isAuthenticated()) {
				// if the user is authenticated then get requests from the user and process them
				//customer exists, password matches
				out.println("You have successfully logged in, welcome...");
				while(true) {
					ui.printMenu();
					ArrayList<String> request = ui.getMenuOption();
					System.out.println("Request from " + customerID.getKey());
					String response = bank.processRequest(customerID, request);
					out.println(response);
				}
			} else if (customerID == null) {
				// we are here if the customer is null, ie the username does not exist
				out.println("Log In Failed. Username does not exist");
				loginAttemptsRemaining--;
				if (loginAttemptsRemaining == 0) {
					//Exceed max login attempts. Print DisconnectClient to stop ExampleClient.
					out.println(ProtocolsAndResponses.Responses.EXIT);
				} else {
					out.println("You have " + (loginAttemptsRemaining) + " username attempt(s) remaining.");
					run();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
}
