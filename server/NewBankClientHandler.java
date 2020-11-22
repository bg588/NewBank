package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class


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

	public static String timeStamp() {
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		String formattedDate = myDateObj.format(myFormatObj) + ": ";
		return (formattedDate);
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
			//Server Message
			System.out.println(timeStamp() +"Attempted login by " +userName) ;
			// authenticate user and get customer ID token from bank for use in subsequent requests
			CustomerID customerID = bank.checkLogInDetails(userName, password);
			if (customerID != null && customerID.isLocked()) {
				//customer exists, but account is locked
				out.println("Your account has been locked. Please contact customer services");
				//send this to disconnect client :
				out.println("DisconnectClient");

			} else if (customerID != null && !customerID.isAuthenticated()) {
				// customer exists, but password does not match
				out.println("Password does not match");
				if (customerID.getPasswordAttemptsRemaining() == 0) {
					out.println("You have exceeded you maximum attempts and your account is now locked.");
					out.println("Please contact customer services");
					//Exceed max login attempts. Print DisconnectClient to stop ExampleClient.
					out.println("DisconnectClient");
					//Server Message
					System.out.println(timeStamp() + "*** Warning *** - Account with username " +userName + " has been locked due to exceeding incorrect password attempts") ;
				} else {
					out.println("Please try again, you have " + customerID.getPasswordAttemptsRemaining() + " attempt(s) remaining");
					//Server Message
					System.out.println(timeStamp() + "Invalid password from " +userName) ;
					run();
				}
			} else if (customerID != null && customerID.isAuthenticated()) {
				// if the user is authenticated then get requests from the user and process them
				//customer exists, password matches
				//Log in message
				System.out.println(timeStamp() + "New Login by user " + customerID.getKey());
				while(true) {
					ui.printMenu();
					ArrayList<String> request = ui.getMenuOption();
					String response = bank.processRequest(customerID, request);
					out.println(response);
					//Server messages
					if (request.get(0) == "Logout") {
						System.out.println(timeStamp() + "Logout by user " + customerID.getKey());
					} else {
						System.out.println(timeStamp() + "Request from " + customerID.getKey() + ":" + request + ". Result: " + response);
					}
				}
			} else if (customerID == null) {
				// we are here if the customer is null, ie the username does not exist
				out.println("Log In Failed. Username does not exist");
				//Server Message
				System.out.println(timeStamp() + "Unknown username " +userName) ;
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
