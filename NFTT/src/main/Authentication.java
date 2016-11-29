package main;

import java.io.*;
import java.util.*;
import java.net.*;

public class Authentication {
	String hostAddress;
	int portNumber;
	File fileName;
	boolean flip;
	public Scanner kb;
	
	private static Socket socket;
	
	public Authentication(String ipAddress, int portNumber){
		this.hostAddress = ipAddress;
		this.portNumber = portNumber;
	}
	
	public Authentication (int portNumber)
	{
		this.portNumber = portNumber;
	}
	
	public boolean s_connect(FS userList) throws IOException{
		
			boolean type = false;
	        ServerSocket serverSocket = new ServerSocket(this.portNumber);
	        int counter = 4;
	        while(counter > 0)
	        {
	            //Reading the message from the client
	            socket = serverSocket.accept();
	            InputStream is = socket.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String request = br.readLine();
	          
	           String returnMessage;
	           if(verify(request, userList)){
	        	   returnMessage = "Login Verified\n";
	        	   counter = 0;
	        	   type = true;
	           }
	           else{
	        	   if(counter == 1){
	        		   counter -= 1;
	        		   returnMessage = "No more login attempts left\n";
	        	   }
	        	   else{
	        	   counter -= 1;
	        	   returnMessage = "Login Failed " + (counter-1) + " attempts left\n";
	        	   }
	           }

	            //Sending the response back to the client.
	            OutputStream os = socket.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            BufferedWriter bw = new BufferedWriter(osw);
	            bw.write(returnMessage);
	            bw.flush();
	            System.out.println("Message sent to the client is "+returnMessage);
	            
	            //change
	            //InputStream is1 = socket.getInputStream();
	            InputStreamReader isr1 = new InputStreamReader(is);
	            BufferedReader br1 = new BufferedReader(isr1);
	            String checkSum = br1.readLine();
	            if(checkSum != null){
	            	System.out.println("Checksum for the file before submission: " + checkSum);
	            }
	            
	            InputStreamReader isr2 = new InputStreamReader(is);
	            BufferedReader br2 = new BufferedReader(isr2);
	            String response = br2.readLine();
	            if(response.equalsIgnoreCase("Y")){
	            	flip = true;
	            }else{
	            	flip = false;
	            }
	        }
	   socket.close();
		return type;
	}
	
	
	// Parse userinput by username:password
	// Lookup if the tuple exists in FS' hashmap of users
	private boolean verify(String request, FS userList) {
		String[] arr = request.split(":");
		return userList.verify(arr[0], arr[1]);
	}

	public boolean c_connect(String request) throws IOException{
		boolean type = true;
		boolean run = true;
		kb = new Scanner(System.in);
		
		while(run)
	    {
				socket = new Socket(this.hostAddress, this.portNumber);
	            System.out.println("You're now connected to the Server"); /*this should only print once */
	            //Send the message to the server
	            OutputStream os = socket.getOutputStream();
	            OutputStreamWriter osw = new OutputStreamWriter(os);
	            BufferedWriter bw = new BufferedWriter(osw);
	
	            String sendMessage = request + "\n";
	            bw.write(sendMessage);
	            bw.flush();
	
	            //Get the return message from the server
	            InputStream is = socket.getInputStream();
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String message = br.readLine();
	            System.out.println(message);
	            
	            
	            if(message.indexOf("Verified") != -1){
	            	this.fileName = Application.getFile("Select the file to upload"); 	
		        	String checksumFile = new CheckSum(fileName).checkSum() + "\n";
		        	System.out.println("Checksum for selected File: " + checksumFile);
		        	 //OutputStream os1 = socket.getOutputStream();
		            OutputStreamWriter osw1 = new OutputStreamWriter(os);
		            BufferedWriter bw1 = new BufferedWriter(osw1);
		            bw1.write(checksumFile);
		            bw1.flush();
		            
		            System.out.println("Would you like to ascii armor the file? Y/N");
		            String response = kb.next();
		            OutputStreamWriter osw2 = new OutputStreamWriter(os);
		            BufferedWriter bw2 = new BufferedWriter(osw2);
		            bw2.write(response);
		            bw2.flush();
		            if(response.equalsIgnoreCase("Y")){
		            	flip = true;
		            }else{
		            	flip = false;
		            }
	            	type = true;
	            	run = false;
	            }
	            else{
	            	type = false;
	            	run = false;
	            }
	  
	    }
		socket.close();
		return type;
	}
	
}
