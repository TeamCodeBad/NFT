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
			String fileName = dis.readUTF();

			files[i] = new File(dirPath + "/" + fileName);

			FileOutputStream fos = new FileOutputStream(files[i]);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			for(int j = 0; j < fileLength; j++) bos.write(bis.read());

			bos.close();
		}

		dis.close();
	}

	public void run() throws IOException {
		//		boolean fileSuccessfullySent = false;
		//		FileInputStream fis = null;
		//		BufferedInputStream bis = null;
		//		OutputStream os = null;
		//		ServerSocket servsock = null;
		//		Socket sock = null;
		//		try {
		//			servsock = new ServerSocket(SOCKET_PORT);
		//			while (fileSuccessfullySent == false) {
		//				System.out.println("Waiting...");
		//				try {
		//					sock = servsock.accept();
		//					System.out.println("Accepted connection : " + sock);
		//					if(Auth(sock)){
		//						sock.setKeepAlive(true);
		//					}
		//					else{
		//						sock.setKeepAlive(false);
		//					}
		//					// send file
		//					
		//					while(sock.isConnected()){
		//						File myFile = ActualFile;
		//						byte [] mybytearray  = new byte [(int)myFile.length()];
		//						fis = new FileInputStream(myFile);
		//						bis = new BufferedInputStream(fis);
		//						bis.read(mybytearray,0,mybytearray.length);
		//						os = sock.getOutputStream();
		//						System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)"); 
		//						os.write(mybytearray,0,mybytearray.length);
		//						os.flush();	
		//					}
		//					System.out.println("Done.");
		//					fileSuccessfullySent = true;
		//				}
		//				finally {
		//					if (bis != null) bis.close();
		//					if (os != null) os.close();
		//					if (sock!=null) sock.close();
		//				}
		//			}
		//		}
		//		finally {
		//			if (servsock != null) servsock.close();
		//		}

		get();
		fm.merge();
		
		File returnedFile = new File(fm.fileName);
		File cipherFile;
		if(flip == true){
			cipherFile = new XOR(2).cipher(returnedFile);
		}else{
			cipherFile = new XOR(0).cipher(returnedFile);
		}
		String cs = new CheckSum(cipherFile).checkSum();
		System.out.println("Confirm this checksum with the intial checksum " + cs);
//		File[] listOfFiles;
//		File toList;
//		File toSend = new File(FILE_TO_SEND);
//		fs.splitFile(toSend);
//		toList = getFile();
//		listOfFiles = fm.listOfFiles(toList);
//		send(listOfFiles);

	}
//
//	public void send(File[] files) throws UnknownHostException, IOException{
//
//
//		ServerSocket sersoc = new ServerSocket(SOCKET_PORT);
//		Socket socket = sersoc.accept();
//
//		BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
//		DataOutputStream dos = new DataOutputStream(bos);
//
//
//		dos.writeInt(files.length);
//
//		for(File file : files)
//		{
//			long length = file.length();
//			dos.writeLong(length);
//
//			String name = file.getName();
//			dos.writeUTF(name);
//
//			FileInputStream fis = new FileInputStream(file);
//			BufferedInputStream bis = new BufferedInputStream(fis);
//
//			int theByte = 0;
//			while((theByte = bis.read()) != -1) bos.write(theByte);
//
//			bis.close();
//		}
//		dos.close();
//	}

//	public File getFile() {
//		System.out.println("Select a file with the window:");
//		JFileChooser chooser = new JFileChooser();
//		chooser.setCurrentDirectory(new java.io.File("."));
//		chooser.setDialogTitle("Upload File");
//		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		chooser.setAcceptAllFileFilterUsed(false);
//		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//			return chooser.getSelectedFile();
//		} else {
//			return null;
//		}
//	}

}