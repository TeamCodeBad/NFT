package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class XOR {
	private byte[] key;
	private  byte[] message;
	File f;
	int state;
	
	XOR(int state){
		System.out.println("Select Key for Ciphyer");
		this.key = convertFile(Application.getFile("Select the key file to use"));
		this.state = state;
	}
	
	public void setflip(int state){
		this.state = state;
	}
	
	
	private byte[] convertFile(File filename) {
		
		  byte[] bFile = new byte[(int) filename.length()];

	        try {
	        	//comment
	            //convert file into array of bytes
	        FileInputStream fis = new FileInputStream(filename);
		    fis.read(bFile);
		    fis.close();

//		    for (int i = 0; i < bFile.length; i++) {
//		       	System.out.print((char)bFile[i]);
//	            }

		    System.out.println("Done");
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
		return bFile;
	}
	public void setMessage(byte[] message){
		this.message = message;
	}
	
	public void setKeys(byte[] key){
		this.key = key;
	}
	
	public File cipher(File returnedFile){
		
		this.message = convertFile(returnedFile);
		int size = message.length;
		byte[] new_message = new byte[size];
		
		if(state == 2){
			new_message = new ArmorCoder().decodedManyChunks(message);
			message = new_message;
			size = new_message.length;
		}
		
		for(int i = 0; i < size; i++){
			new_message[i] = (byte) (message[i] ^ key[i % key.length]);
		}
		
		//encoding within XOR
		//why
		if(state == 1){
			new_message = new ArmorCoder().encodeManyChunks(new_message);
		}
		
//		else if(state == 2){
//			new_message = new ArmorCoder().decodedManyChunks(new_message);
//		}
//		
		try {
			PrintWriter pw = new PrintWriter(returnedFile);
			pw.close();
			FileOutputStream fos = new FileOutputStream(returnedFile);
			fos.write(new_message);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return returnedFile;
		
	}

	public void deleteFile() {
		f.delete();
		
	}

}
