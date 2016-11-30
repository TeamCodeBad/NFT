package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {

	private int SOCKET_PORT;
	FileMerger fm = new FileMerger();
	FileSplitter fs = new FileSplitter();
	private String fileName;
	private boolean flip;

	public SimpleFileServer(int portNumber, boolean flip) {
		this.SOCKET_PORT = portNumber;
		this.flip = flip;
	}

	public void get() throws IOException {

		int totalRetryAttempts = 3;
		String dirPath = System.getProperty("user.dir");
		ServerSocket serverSocket = new ServerSocket(this.SOCKET_PORT);
		Socket socket = serverSocket.accept();

		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
		DataInputStream dis = new DataInputStream(bis);

		/*
		 * InputStream is = socket.getInputStream(); InputStreamReader isw = new
		 * InputStreamReader(is); BufferedReader br = new BufferedReader(isw);
		 */

		int filesCount = dis.readInt();
		File[] files = new File[filesCount];
		/**
		 * Make sure the reader matches where the writer is in simple file
		 * client
		 */
		/*
		 * String clientSum; String serverSum;
		 */

		// This is where the Server and Client Sync
		// Client needs to Send the data to Server
		// Server needs to verify the data was not changed
		// If it is, request to send same chunk again
		for (int i = 0; i < filesCount; i++) {
			String serverSum = "Calculate this Right after Getting File";
			String clientSum = "Client sends this with the chunks";
			
			while(!(serverSum.equals(clientSum))){
				long fileLength = dis.readLong();
				fileName = dis.readUTF();
				files[i] = new File(dirPath + "/" + fileName);

				FileOutputStream fos = new FileOutputStream(files[i]);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				/**
				 * This loop reads the small bytes and adds them into that small sub
				 * file and writes the small sub file in the local directory
				 */

				for (int j = 0; j < fileLength; j++) {
					bos.write(bis.read());
				}

				// checksum from received chunk files
				/*
				 * serverSum = new CheckSum(files[i]).checkSum();
				 * if(clientSum.equals(serverSum)){
				 * 
				 * System.out.println("[SENT] Confirmed Chunk "+ i); }
				 */
				
				
				bos.close();
			}

			// checksum from received chunk files
			/*
			 * serverSum = new CheckSum(files[i]).checkSum();
			 * if(clientSum.equals(serverSum)){
			 * 
			 * System.out.println("[SENT] Confirmed Chunk "+ i); }
			 */
			
			//TODO: Handshake here to either re-send or send next.
			//SEND RESPONSE from Server. 
			serverSum = new CheckSum(files[i]).checkSum();
			
			if (serverSum.equals(clientSum)) {
				// All Good. Carry on.
			} else if (totalRetryAttempts != 0) {
				// Ask to Re-send chunk.
				// totalRetryAttempts--;
				// i--;
			} else {
				// Send a non "0"/"1" response to client. Cut-connection, FAILURE
				i = files.length;
				System.out.println("The server cut connection. File transfer was attacked by enemy ninjas");
			}
		}

		dis.close();
	}

	public void run() throws IOException {
		get();
		fm.merge(fileName);

		File returnedFile = new File(fm.fileName);
		File cipherFile;
		if (flip == true) {
			cipherFile = new XOR(2).cipher(returnedFile);
		} else {
			cipherFile = new XOR(0).cipher(returnedFile);
		}
		String cs = new CheckSum(cipherFile).checkSum();
		System.out.println("Confirm this checksum with the intial checksum " + cs);
	}
}
