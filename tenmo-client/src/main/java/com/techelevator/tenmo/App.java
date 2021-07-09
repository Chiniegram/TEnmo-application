package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    // whenever we call server to get a list of users - we going to store it here
    private User[] currentUsers;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				// we calling for list of users to display when sendBucks option run
				getUsers();
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
    	// creating the service to display the balance
		AccountService accessAccount = new AccountService(API_BASE_URL, currentUser);
		try {
			accessAccount.getBalance();
		} catch (NullPointerException e) {
			System.out.println("No balance found");
		}
		
	}

	private void getUsers() {
    	// creating the service to get a list of users
    	TransferService getUsers = new TransferService(API_BASE_URL, currentUser);
    	try {
    		// assigning the response to the instance variable
    		currentUsers = getUsers.getUsers();
			// printing head to the display
    		System.out.println("-----------------------------------");
			System.out.println("Users ID      Name");
			System.out.println("-----------------------------------");
    		// for loop used to print each user
			for (User user : currentUsers) {
				System.out.println(user.getId() + "          " + user.getUsername());
			}
			// in case response returns null - we going to tell it to the user
		} catch (NullPointerException e) {
			System.out.println("No users found");
		}
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		// the array to store the response from server
		Transfer[] usersTransfers;
		// Map is used to store the ids and names
		Map<Long, String> usernames = new HashMap<>();
		// creating the service to get TransferUser object that contains list of transfers and list of user ids and usernames
		TransferService transfersList = new TransferService(API_BASE_URL, currentUser);
		try {
			// assigning the response to new TransferUser object
			TransferUser result = transfersList.viewTransferHistory();
			// Getting array of transfers from TransferUser object
			usersTransfers = result.getTransfers();
			// Getting map of user ids and usernames out of TransferUser object
			usernames = result.getAccountsUsernames();
			// Printing header
			System.out.println("-----------------------------------");
			System.out.println("Transfers                      ");
			System.out.println("ID       From/To         Amount");
			System.out.println("-----------------------------------");
			// Looping through transfers and printing the details
			for(Transfer transfer : usersTransfers) {
				// checking if current user is sender or receiver
				if(currentUser.getUser().getUsername().equals(result.getAccountsUsernames().get(transfer.getAccountFrom()))) {
					System.out.println(transfer.getTransferId() + "   To:   " + result.getAccountsUsernames().get(transfer.getAccountTo()) + "        $" + transfer.getAmount());
				} else {
					System.out.println(transfer.getTransferId() + "   From: " + result.getAccountsUsernames().get(transfer.getAccountFrom()) + "        $" + transfer.getAmount());
				}
			}
			// in case our TransferUser object returns empty
		} catch (NullPointerException e) {
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("                           No transfers found");
			System.out.println("-----------------------------------------------------------------------------");
			// we need to instantiate the array in case TransferUser object empty so it will not break the application later
			usersTransfers = new Transfer[0];
		}

		// initiate the Scanner
			Scanner scanner = new Scanner(System.in);
		// Asking user if he wants to see details of any certain transfer
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("            If you want to see more details about the transfer ");
			System.out.println("         Please enter the Transfer ID or press 0 to exit to main menu: ");
			System.out.println("-----------------------------------------------------------------------------");
		String scan = scanner.nextLine();
		try {
			int choice = Integer.parseInt(scan);
			if (choice != 0) {
				// this variable only to check when the user wants to get out of the loop
				String x = "";
				// The loop will start at least once
				while (!x.equals("0")) {
					try {
						boolean isListed = false;
						for (Transfer transfer : usersTransfers) {
							if (transfer.getTransferId().intValue() == choice) {
								System.out.println("-----------------------------------------------------------------------------");
								System.out.println("                            Transfer Details:");
								System.out.println("-----------------------------------------------------------------------------");
								System.out.println("ID: " + transfer.getTransferId());
								System.out.println("From: " + usernames.get(transfer.getAccountFrom()));
								System.out.println("To: " + usernames.get(transfer.getAccountTo()));
								System.out.println("Type: " + transfer.getTypeId());
								System.out.println("Status: " + transfer.getStatusId());
								System.out.println("Amount: $" + transfer.getAmount());
								isListed = true;
							}
						}
						if (!isListed) {
							System.out.println("-----------------------------------------------------------------------------");
							System.out.println("                        Invalid Transfer ID");
							System.out.println("-----------------------------------------------------------------------------");
						}

						System.out.println("-----------------------------------------------------------------------------");
						System.out.println("                  Do want to check another transfer?");
						System.out.println("      (Please press 0 to exit or any key to enter new Transfer ID):");
						x = scanner.nextLine();
						if (!x.equals("0")) {
							System.out.println("Please enter Transfer ID: ");
							choice = Integer.parseInt(scanner.nextLine());
						}

						// in case user puts dummy input - we will display that
					} catch (NumberFormatException e) {
						System.out.println("-----------------------------------------------------------------------------");
						System.out.println("                        Invalid Transfer ID");
						System.out.println("-----------------------------------------------------------------------------");
					}

				}
			}
		} catch (NumberFormatException ex){
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("                          Invalid input!");
			System.out.println("-----------------------------------------------------------------------------");
		}
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("              This feature is under construction! Coming soon!");
		System.out.println("-----------------------------------------------------------------------------");
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		// creating the service to send bucks
		TransferService transferSent = new TransferService(API_BASE_URL, currentUser);
		try {
			// creating Transfer object that will be send with request to the server
			Transfer newTransfer = new Transfer();
			Scanner scanner = new Scanner(System.in);
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("            Please provide all required information!");
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println("Choose User ID from the list above: ");
			// string to store userId to whom user want to send money
			String userId = scanner.nextLine();
			// assigning user's input to transfer object. In case user input not numbers - we will get an error before even calling the server
			Long input = Long.parseLong(userId);
			// created variable to check that user choose user from the list
			int userFromList = 0;
			// looking if user is in the list
			for (User user : currentUsers){
				Long thisUser = Long.valueOf(user.getId());
				if(thisUser.equals(input)){
					userFromList = 1;
					break;
				}
			}
			Long checkCurrentUser = Long.valueOf(currentUser.getUser().getId());
			// in case the user input is not in the list
			if(userFromList == 0){
				System.out.println("-----------------------------------------------------------------------------");
				System.out.println("                        User ID not found!");
				System.out.println("-----------------------------------------------------------------------------");
			// if user is in the list
			} else if (!checkCurrentUser.equals(input)) {
				newTransfer.setAccountTo(input);
				System.out.println("Please provide the amount you want to send(without dollar sign): ");
				// string to store amount user wants to send
				String amount = scanner.nextLine();
				// put amount into Transfer object. Same case here - user input invalid format - he gets an error
				newTransfer.setAmount(new BigDecimal(amount));
				// transfer id and status are default
				newTransfer.setTypeId(Long.parseLong("2"));
				newTransfer.setStatusId(Long.parseLong("2"));
				// send the transfer object through service
				boolean response = transferSent.createTransfer(newTransfer);
				if(response) {
					System.out.println("-----------------------------------------------------------------------------");
					System.out.println("                     You have sent $" + amount + "!");
					System.out.println("-----------------------------------------------------------------------------");
				} else {
					System.out.println("-----------------------------------------------------------------------------");
					System.out.println("                      Transfer cannot be completed!");
					System.out.println("-----------------------------------------------------------------------------");
				}

			} else {
				System.out.println("-----------------------------------------------------------------------------");
				System.out.println("                   You can't send money to yourself!");
				System.out.println("-----------------------------------------------------------------------------");
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid information provided! Transfer cannot be executed!");
		}

		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("              This feature is under construction! Coming soon!");
		System.out.println("-----------------------------------------------------------------------------");
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}



	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("Login failed: Invalid username or password");
				// we need to break out of the loop if user doesn't know his login/password
				// with previous scenario user would stuck in this loop
				break;
//				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
