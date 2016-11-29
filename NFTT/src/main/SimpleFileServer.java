package main;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;

public class SimpleFileServer {

	private int SOCKET_PORT;	 	// ie:8080
	private String FILE_TO_SEND;	// name of file to send. May be full path.
	private File ActualFile;
	FileMerger fm = new FileMerger();
	FileSplitter fs = new FileSplitter();
	private String ipAddress;
	private String fileName;
	private boolean flip;

	public SimpleFileServer(int portNumber, boolean flip) {
		this.SOCKET_PORT = portNumber;
		this.flip = flip;

	}

	public void get() throws IOException{

		String dirPath = System.getProperty("user.dir");
		ServerSocket serverSocket = new ServerSocket(this.SOCKET_PORT);
		Socket socket = serverSocket.accept();

		BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
		DataInputStream dis = new DataInputStream(bis);

		int filesCount = dis.readInt();
		File[] files = new File[filesCount];

		for(int i = 0; i < filesCount; i++)
		{
			long fileLength = dis.readLong();
			fileName = dis.readUTF();
			files[i] = new File(dirPath + "/" + fileName);

			FileOutputStream fos = new FileOutputStream(files[i]);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			for(int j = 0; j < fileLength; j++) bos.write(bis.read());

			bos.close();
		}

		dis.close();
	}

	public void run() throws IOException {
	
		get();
		fm.merge(fileName);
		
		File returnedFile = new File(fm.fileName);
		File cipherFile;
		if(flip == true){
			cipherFile = new XOR(2).cipher(returnedFile);
		}else{
			cipherFile = new XOR(0).cipher(returnedFile);
		}
		String cs = new CheckSum(cipherFile).checkSum();
		System.out.println("Confirm this checksum with the intial checksum " + cs);

	}
}