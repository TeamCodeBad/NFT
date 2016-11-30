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
			String serverSum = "temp value";
			String clientSum = "another temp value";
			int attempts = 0;

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

				//SENDING CHECKSUM STARTS HERE

				serverSum = new CheckSum(files[i]).checkSum();

				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				bw.write(serverSum);
				bw.flush();


				//SENDING CHECKSUM ENDS HERE

				
				//RECIEVE CLIENT CHECKSUM STARTS HERE
				
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				clientSum = br.readLine();
				
				//RECIEVE CLIENT CHECKSUM ENDS HERE
				
				attempts++;
				
				System.out.println("ServerSum = "+ serverSum);
				System.out.println("ClientSum = "+ clientSum);
				
				// checksum from received chunk files
				/*
				 * serverSum = new CheckSum(files[i]).checkSum();
				 * if(clientSum.equals(serverSum)){
				 * 
				 * System.out.println("[SENT] Confirmed Chunk "+ i); }
				 */
				
				
				bos.close();
				
				if(attempts > 3){
					System.out.println("Max attempts reached, closing connection");
					System.exit(0);
				}
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