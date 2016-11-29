package main;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Application {
	
	public static FS userList = new FS(new File("Authorizedusers.txt"));
	public static boolean isTerminated = false;
	public static Scanner k = new Scanner(System.in);
	public static int portNumber = 8080;
	public static String ipAddress = "1.1.1.1";
	public static void main(String[] args) throws IOException {
		System.out.println("File Transfer Tool!\nEnter your position below!");
		String command = "";
		System.out.println("Commands:\n1. client\n2. server\n3. Exit");
		do {
			command = k.nextLine();
			action(command);
			if (isTerminated != true) {
				System.out.println("Returning to menu");
				System.out.println("Commands:\n1. Client\n2. Server\3. Exit");
			}
		} while (isTerminated != true);
		k.close();
	}
	
	public static void action(String input) throws IOException {
		switch(input) {
		case "client":
		case "1":
			System.out.println("Client Mode");
			clientAction();
			break;
		case "server":
		case "2":
			System.out.println("Server Mode");
			serverAction();
			break;
		case "exit":
		case "3":
			System.out.println("Exiting");
			isTerminated = true;
			break;
		default:
			System.out.println("Invalid command");
			break;
		}
	}
	
	public static void clientAction() throws IOException {
		portNumber = fetchPortNumber();
		ipAddress = fetchIPAddress();
		System.out.println("Enter Username and Password within the following format");
		System.out.println("'Username:Password'");
		String request = k.nextLine();
		Authentication c = new Authentication(ipAddress, portNumber);
		if(c.c_connect(request)){
			File fileName = c.fileName;
			
			XOR thing;
			if(c.flip == true){
				thing = new XOR(1);
			}else{
				thing = new XOR(0);	
			}
			File toSend= thing.cipher(fileName);
			SimpleFileClient sfc = new SimpleFileClient((portNumber+1), ipAddress, toSend);
			sfc.run();
			if(c.flip == true){thing.setflip(2);}
			else{thing.setflip(0);}
			thing.cipher(toSend);
		}
		else{
			System.out.println("Connection Terminated");
		}
		
	}

	
	public static void serverAction() throws IOException {
		portNumber = fetchPortNumber();

		Authentication s = new Authentication(portNumber);
		System.out.println("Listening...");
		if(s.s_connect(userList)){
			
			SimpleFileServer sfs = new SimpleFileServer((portNumber+1), s.flip);
			sfs.run();
		}
		else{
			System.out.println("Connection is Terminated");
		}

	}
	
	public static String fetchIPAddress() {
		System.out.println("Enter Server's ipv4 address: ");
		String ipAddress = null;
		boolean isValidIPAddress = false;
		verificationProcess:
		// Check for string form of "A.B.C.D" for ABCD is a number from 0 - 255
		while (isValidIPAddress == false) {
			try {
				ipAddress = k.nextLine();
				String[] octetArray = ipAddress.split("\\.");
				// Allow only 4 octets
				if (octetArray.length == 4) {
					for (String octet : octetArray) {
						// Octets must be of length < 3
						if (octet.length() > 3) {
							continue verificationProcess;
						}
						// Verify that the octets are actual numbers
						char[] octetCharacters = octet.toCharArray();
						for (char c: octetCharacters) {
							if (c > '9' || c < '0') {
								continue verificationProcess;
							}
						}
						// Verify that the octets are < 255
						if (Integer.parseInt(octet) > 255) {
							continue verificationProcess;
						}
					}
					isValidIPAddress = true;
				}
			} catch (Exception e) {
				k.next();
				continue verificationProcess;
			}
		}
		return ipAddress;
	}
	
	public static int fetchPortNumber() {
		System.out.println("Specify the port number: ");
		int portNumber = -1;
		while (portNumber < 1 || portNumber > 65535) {
			try {
				portNumber = k.nextInt();
				k.nextLine();
			} catch (Exception e) {
				k.next();
				continue;
			}
		}
		return portNumber;
	}
	
	public static File getFile(String message) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle(message);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}
}