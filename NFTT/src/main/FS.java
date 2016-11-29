package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FS {
	private HashMap<String, String> hm;

	public FS(File f) {
		hm = new HashMap<String, String>();
		String[] userNPass = new String[2];
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String uP = br.readLine();
			while (uP != null) {
				userNPass = uP.split(":");
				hm.put(userNPass[0], userNPass[1]);
				uP = br.readLine();
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean verify(String user, String pass) {
		boolean f = false;
		if (hm.get(user) != null && hm.get(user).equals(pass)) {
			f = true;
		}
		return f;
	}
	
}
