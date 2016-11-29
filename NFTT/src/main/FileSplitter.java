package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Just feed this method a file and it will create several new files
 * that have the input file name with the extension .00x where x is
 * the part # of the file
 * @author Thomas Nguyen
 *
 */

public class FileSplitter {
	
	public void splitFile(File f) throws IOException {
        int partCounter = 1;

        int sizeOfFiles = 1024 * 1024;
        byte[] buffer = new byte[sizeOfFiles];

        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(f))) {
            String name = f.getName();

            int tmp = 0;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = new File(f.getParent(), name + "."
                        + String.format("%03d", partCounter++)); //creates a file of the same name, but
                												 //with .00x as a file extension
                												//stored where u chose the file
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, tmp);//tmp is chunk size
                }
            }
        }
    }
}
