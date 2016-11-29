package main;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class CheckSum {
	
	private byte[] array;
	
	public CheckSum(File filename){
		this.array = convertFile(filename);
	}

	private byte[] convertFile(File filename) {
		
		  byte[] bFile = new byte[(int) filename.length()];

	        try {
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

	public String checkSum() {
//		ArrayList<Byte> sentinel = new ArrayList<Byte>();
		String result = "";
		int count = 0;

		BigInteger piece1;
		BigInteger piece2;
		BigInteger temp = new BigInteger("0");
		String order = (new BigInteger(array)).toString(8);
		// 0 1 2 3 4 5 6 7
		// v - x * + / ^ !
		long factor = 1000;
		for (int i = 0; i  < array.length; i++){
			// System.out.println(order.charAt(count % 8));
			switch (order.charAt(count % 8)) {
			case '0':
				//System.out.println("0");
				piece1 = new BigInteger("" + (long) Math.abs((Math.cos(array[i]) * 5000 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sin(array[i]) * 5000 * factor)));
				piece1 = piece1.xor(piece2);
				
				temp = temp.add(piece1);
				break;
			case '1':
				//System.out.println("1");
				piece1 = new BigInteger("" + ((long)(Math.pow((array[i]), 5) * 2 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sin(array[i]) * 5000 * factor)));
				piece1 = piece1.or(piece2);

				temp = temp.add(piece1.shiftLeft(10));
				break;
			case '2':
				//System.out.println("2");
				piece1 = new BigInteger("" + (long) Math.abs((Math.cos(array[i]) * 5000 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((factorial(array[i]) * 6 * factor)));
				piece1 = piece1.add(piece2);

				temp = temp.add(piece1);
				break;
			case '3':
				//System.out.println("3");
				piece1 = new BigInteger("" + (long) Math.abs((fibonacci(array[i]) * 1469 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sin(array[i]) * 5000 * factor)));
				piece1 = piece1.and((piece2));

				temp = temp.add(piece1);
				break;
			case '4':
				//System.out.println("4");
				piece1 = new BigInteger("" + (long) Math.abs((Math.cosh(array[i]) * 55 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sinh(array[i]) * 55 * factor)));
				piece1 = piece1.andNot(piece2);

				temp = temp.add(piece1);
				break;
			case '5':
				//System.out.println("5");
				piece1 = new BigInteger("" + (long) Math.abs((Math.cosh(array[i]) * 5000 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sin(array[i]) * 55 * factor)));
				piece1 = piece1.or(piece2);

				temp = temp.add(piece1);
				break;
			case '6':
				//System.out.println("6");
				piece1 = new BigInteger("" + (long) Math.abs((Math.cos(array[i]) * 5000 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sinh(array[i]) * 55 * factor)));
				piece1 = piece1.subtract(piece2);

				temp = temp.add(piece1);
				break;
			case '7':
				//System.out.println("7");
				piece1 = new BigInteger("" + (long) Math.abs((Math.cos(array[i]) * 5000 * factor)));
				piece2 = new BigInteger("" + (long) Math.abs((Math.sin(array[i]) * 5000 * factor)));
				piece1 = piece1.add(piece2);

				temp = temp.subtract(piece1);
				break;
			default:
				break;
			}

			count++;
		}

		temp = temp.mod(new BigInteger("1298074214633706835075030044377087"));
		
		result = temp.toString(16);
		return result;
	}

	private double factorial(int x) {
		int j = 1;
		for (int i = 1; i < x; i++) {
			j *= i;
		}
		return j;
	}

	private double fibonacci(int x) {
		int a = 0;
		int b = 1;
		int temp;
		for (int i = 0; i < x; i++) {
			temp = a + b;
			a = b;
			b = temp;
		}
		return b;
	}
}
