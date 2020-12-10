package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
* This is used to get and set data in a CSV file called data.csv which is stored on the classpath
* setPersistedData is called at present during Exit, and will save all data to CSV
* getPersistedData is called when we create NewBank, as long as a file exists, it will read the CSV.
* Note if no file exists, getPersistedData is not called and test data is populated within NewBank instead
 */

public class Persister {

    private HashMap<String, Customer> data;
    // this points to file location on classpath ie out/production/newbank/data.csv
    // so we should be able to use on any platform (ie PC, Mac, Linux etc)
    private final String fileLocation;

    public Persister() {
        data = new HashMap<>();
        fileLocation = System.getProperty("java.class.path") + "\\data.csv";
    }

    public boolean fileExists() {
        return (new File(fileLocation).exists());
    }

    public void setPersistedData(HashMap<String,Customer> customers) {
        ArrayList<String> linesToWrite = new ArrayList<>();
        // populate the linesToWrite from the customers HashMap :
        for (String customersKey : customers.keySet()) {
            // for each customer in the loop
            // start our line with the string for the customer name
            String line = customersKey + ",";
            // create a customer object, pulling from the hashmap
            Customer customer = customers.get(customersKey);
            // add password to the line
            line += customer.getPassword().getPassword();
            line+= customer.getDateOfBirth().getDateOfBirth();
            // get the list of accounts for this customer
            ArrayList<Account> accounts = customer.getAccounts();
            // then loop through them
            for (Account account : accounts) {
                // for each account for this customer, add account name and balance to the line
                line += "," + account.getAccountName() + ",";
                line += account.getBalance();
            }
            // all accounts are done, add this line to array to be written
            linesToWrite.add(line);
        }
        // now try to write those linesToWrite to a CSV file
        try {
            FileWriter writer = new FileWriter(new File(fileLocation));
                for (String line : linesToWrite){
                    //for each line in linesToWrite
                    writer.append(line);
                    //add a line break
                    writer.append('\n');
                }
            writer.flush();
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Customer> getPersistedData() {
        File file = new File(fileLocation);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                // for each line in the file, split the line by commas and have each "chunk" in a String[]
                String[] results = scanner.nextLine().split(",");
                // name is the first entry on the line
                String name = results[0];
                // create a customer object to represent this customer
                Customer customer = new Customer();
                // set the password for the customer
                customer.setPassword(results[1]);
                customer.setDateOfBirth(results[2]);
                for (int i = 2; i < results.length - 1; i = i + 2) {
                    // each remaining entry on this is an account/balance, add account name, and account balance to
                    // customer object
                    String accountName = results[i];
                    double balance = Double.parseDouble(results[i+1]);
                    customer.addAccount(new Account(accountName,balance));
                    //then increment the loop by 2
                }
                // populate the data HashMap with this customer's details
                data.put(name,customer);
            }
            scanner.close();
        }
        catch (Exception e) {
            System.out.println("Exception in getPersistedData() : " + e);
        }
        return data;
    }
}
