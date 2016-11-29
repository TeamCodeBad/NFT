package main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class that can MIME Base64 encode 1 - 3 bytes of data at a time.
 * Also able to decode data.
 */
public class ArmorCoder {
	
	// The MIME Base64 Encoding table.
	private class Bytetable {
		private final HashMap<Byte, Byte> MAP = new HashMap<Byte, Byte>();
		private final byte[] TABLE = {'A','B','C','D','E','F','G','H',
									'I','J','K','L','M','N','O','P',
									'Q','R','S','T','U','V','W','X',
									'Y','Z','a','b','c','d','e','f',
									'g','h','i','j','k','l','m','n',
									'o','p','q','r','s','t','u','v',
									'w','x','y','z','0','1','2','3',
									'4','5','6','7','8','9','+','/'};
				
		public Bytetable() {
			for(int i = 0; i < TABLE.length; i++) {
				MAP.put(TABLE[i], (byte) i);
			}
		}
		
		public byte getByte(int i) {
			return TABLE[i];
		}
		
		public byte getByteIndex(byte i) {
			return MAP.get(i);
		}
	}
		
	private Bytetable table = new Bytetable();
	
	// Takes in a byte array of size 1 - 3
	// Returns encoded version of byte array of size 4.
	private byte[] encodeChunks(byte[] data) {
		byte[] encodedData = new byte[4];
		
		// Copy the byte array data to a temporary
		// with a dummy value of 1 at index 0.
		byte[] temp = new byte[1 + data.length];
		temp[0] = 1;
		for (int i = 0; i < data.length; i++) {
			temp[1 + i] = data[i];
		}
		// Use temp to create a BigInteger object
		// To hold all bits and get String form.
		BigInteger dataHolder = new BigInteger(temp);
		String strData = dataHolder.toString(2).substring(1);
		// Optional padding with zero bits if not 24 bits.
		while (strData.length() != 24) {
			strData += "A";
		}
				
		int offsetA = 0;
		int offsetB = 0;
		
		// Create 4 byte chunks from the 24 bits
		for (int i = 0; i < 4; i++) {
			offsetB += 6;
			String stringChunk = strData.substring(offsetA, offsetB);
			// Fully padded bits assigns '='
			if (stringChunk.equals("AAAAAA")) {
				encodedData[i] = '=';
			} else {
				// Otherwise assign a value from the table
				stringChunk = stringChunk.replaceAll("A", "0");
				encodedData[i] = table.getByte(Byte.parseByte(stringChunk, 2));
			}
			offsetA = offsetB;
		}
		return encodedData;
	}
	
	/**
	 * This method takes in a File as a byte[] and returns it encoded.
	 * The size will differ. Use this before sending file.
	 */
	public byte[] encodeManyChunks(byte[] entireChunk) {
		// chunkHolder is to keep all encoded data
		// processedData gets at most 3 bytes for encoding at a time
		ArrayList<Byte> chunkHolder = new ArrayList<Byte>();
		byte[] processedData = null;
		int counter = 0;

		// Iterate through entire chunk of byte array
		for(int i = 0; i < entireChunk.length; i++) {
			// Make proccessedData an appropriate size
			if (counter == 0) {
				if ((entireChunk.length - i) >= 3) {
					processedData = new byte[3];
				} else {
					processedData = new byte[entireChunk.length - i];
				}
			}
			processedData[counter] = entireChunk[i];
			counter++;
			// Once fully populated, encode the tracked bytes
			// and add it to the chunkHolder
			if (counter == 3 || i == entireChunk.length - 1) {
				counter = 0;
				byte[] encodedChunks = encodeChunks(processedData);
				for (byte b: encodedChunks) {
					chunkHolder.add(b);
				}
			}
		}
		processedData = new byte[chunkHolder.size()];
		for (int i = 0; i < chunkHolder.size(); i++) {
			processedData[i] = chunkHolder.get(i);
		}
		
		return processedData;
	}
	
	/**
	 * Takes in an entire encoded file as byte[] and decodes it back
	 * Does this 4 encoded bytes at a time. Use this after sending encoded file?
	 */
	public byte[] decodedManyChunks(byte[] encodedData) {
		// chunkHolder is to keep all decoded data
		// processedData will always be of size 4
		ArrayList<Byte> chunkHolder = new ArrayList<Byte>();
		byte[] processedData = null;
		int counter = 0;

		// Iterate through entire chunk of encoded byte array
		for(int i = 0; i < encodedData.length; i++) {
			// Make proccessedData an appropriate size
			if (counter == 0) {
				if ((encodedData.length - i) >= 4) {
					processedData = new byte[4];
				}
			}
			processedData[counter] = encodedData[i];
			counter++;
			// Once fully populated, decode the tracked bytes
			// and add it to the chunkHolder
			if (counter == 4 || i == encodedData.length - 1) {
				counter = 0;
				byte[] decodedChunks = decodeChunks(processedData);
				for (byte b: decodedChunks) {
					chunkHolder.add(b);
				}
			}
		}
		processedData = new byte[chunkHolder.size()];
		for (int i = 0; i < chunkHolder.size(); i++) {
			processedData[i] = chunkHolder.get(i);
		}
		return processedData;
	}
	
	// Takes a byte array of encoded bytes, size 4
	// Returns the decoded bytes of array size 1 - 3
	private byte[] decodeChunks(byte[] data) {
		String strData = "";
		int size = 3;
		
		// For the four encoded bytes
		for(byte b : data) {
			// If not fully padded, get index form, and convert to 6 bits
			if (b != '=') {
				byte index = table.getByteIndex(b);
				strData += String.format("%8s", Integer.toBinaryString(index & 0xFF)).replace(' ', '0').substring(2);
			} else {
				// Otherwise ignore padded 6 bits and subtract 2 bits from data
				// in order to account for padded 8 bits total.
				strData = strData.substring(0, strData.length() - 2);
				--size;
			}
		}
		
		byte[] decodedData = new byte[size];
		int offsetA = 0;
		int offsetB = 0;
		
		// Parse out an original byte for every 8 bits
		for(int i = 0; i < size; i++) {
			offsetB += 8;
			decodedData[i] = (byte)Integer.parseInt(strData.substring(offsetA, offsetB), 2);
			offsetA = offsetB;
		}		
		return decodedData;
	}
}