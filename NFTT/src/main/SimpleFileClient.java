package main;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class SimpleFileClient {

	String file;
	int port;
	String address;
	static String directory = System.getProperty("user.dir");
	FileSplitter fs = new FileSplitter();
	FileMerger fm = new FileMerger();
	File toSend; //Already XOR/Encoded

	public SimpleFileClient(int portNumber, String ipAddress, File toSend) {
		this.address = ipAddress;
		this.port = portNumber;
		this.toSend = toSend;
	}

	public void choices(File[] listOfFiles, String input) throws IOException {
		switch (input) {
		case "1":
			// SUCCESS SEND
			send(listOfFiles, 0);
			break;
		case "2":
			send(listOfFiles, 1);
			// Some failures but SUCCESS SEND
			break;
		case "3":
			send(listOfFiles, 4);
			// Complete Failure -> fail to send. Too many retries for corrupted files. Terminate.
			// TODO: DO THIS THING
			break;
		}
	}

	public void send(File[] files, int numberOfScrambles) throws IOException {

		Socket socket = new Socket(this.address, this.port);

		BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
		DataOutputStream dos = new DataOutputStream(bos);

		/*
		 * OutputStream os = socket.getOutputStream(); OutputStreamWriter osw =
		 * new OutputStreamWriter(os); BufferedWriter bw = new
		 * BufferedWriter(osw);
		 */
		String serverSum;
		for (int i = 0; i < files.length; i++)
		{
			//Checksum
			serverSum = new CheckSum(files[i]).checkSum();
			bw.write(serverSum);
			bw.flush();

			
			long length = files[i].length();
			dos.writeLong(length);

			String name = files[i].getName();
			dos.writeUTF(name);


		dos.writeInt(files.length);

		//TODO: scrambles should technically scramble 1 byte per send of a chunk
		// As it is now, it is only scrambling n times for only 1 chunk
		int scrambles = numberOfScrambles;

		// for(File file : files)
		/**
		 * For sending files through the BufferedWriter make sure the read and
		 * write are parallel to where the reader is in simple file server
		 */
		// String serverSum;

		// This is where the Server and Client Sync
		// Client needs to Send the data to Server
		// Server needs to verify the data was not changed
		// If it is, request to send same chunk again
		for (int i = 0; i < files.length; i++) {
			// Checksum
			// serverSum = new CheckSum(files[i]).checkSum();
			// bw.write(serverSum); bw.flush();

			String checkSumBeforeSend = "temp string";

			long length = files[i].length();
			dos.writeLong(length);

			String name = files[i].getName();
			dos.writeUTF(name);

			FileInputStream fis = new FileInputStream(files[i]);
			BufferedInputStream bis = new BufferedInputStream(fis);

			// Used to write 1 byte value at a time
			int theByte = 0;

			/**
			 * Loop here writes the information into small chunk until it reads
			 * -1 for EOF
			 */
			while ((theByte = bis.read()) != -1) {
				// scrambles used in BAD SCENARIO
				if (i == 0 && (scrambles != 0)) {
					theByte += 1;
					scrambles--;
				}
				// Finally, write the single byte data in
				bos.write(theByte);
			}
			bis.close();
			
			//TODO: Handshake here to either re-send or send next.
			//GET INPUT from Server. 
			String serverResponse = "0";
			if (serverResponse.equals("0")) {
				// All Good. Carry on.
			} else if (serverResponse.equals("1")) {
				// Re-send chunk.
				i--;
			} else {
				// Server tells us to Cut-connection, FAILURE
				i = files.length;
				System.out.println("The server cut connection. File transfer was attacked by enemy ninjas");
			}
		}
		dos.close();
	}

	public void run() throws IOException {
		File[] listOfFiles;
		// File toList;
		String pathname = toSend.getAbsolutePath() + ".001";
		fs.splitFile(toSend);
		// toList = getFile();
		// listOfFiles will contain all the XOR/Encoded split files
		listOfFiles = fm.listOfFiles(new File(pathname)/* toList */);

		// Here we provide the user demo options
		Scanner g = new Scanner(System.in);
		System.out.println(
				"Which test case? Enter 1-3 ONLY. \n1. Success\n2. Some failure but sent\n3. Complete Failure");
		String input = g.nextLine();
		choices(listOfFiles, input);
		// Destroy the split files on client side
		deleteSplit(listOfFiles);
	}

	public void deleteSplit(File[] toDelete) {
		for (int i = 0; i < toDelete.length; i++) {
			toDelete[i].delete();
		}

	}

	public File getFile(String message) {
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
