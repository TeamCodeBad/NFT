package main;

import java.util.Arrays;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JOptionPane;

/**
 * Just call the merge method and pick one of the "parts or chunks" of the
 * file that was taken apart and it will put it all back together with an 
 * output name of your choice
 * @author Thomas Nguyen
 *
 */
public class FileMerger {

	public String fileName;
	
	private static void mergeFiles(List<File> files, File into)
	        throws IOException {
	    try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))) 
	    {
	        for (File f : files) {
	            Files.copy(f.toPath(), mergingStream);
	        }
	    }
	    
	    for(File f : files) {
	    	f.delete();
	    }
	}

	private static void mergeFiles(String oneOfFiles, String into) throws IOException{
	    mergeFiles(new File(oneOfFiles), new File(into));
	}
	
	private static void mergeFiles(File oneOfFiles, File into)
	        throws IOException {
	    mergeFiles(listOfFilesToMerge(oneOfFiles), into);
	}
	
	/**
	 * For the merge() method, we can change what the output name is later
	 * probably on another user end thing that passes a string to this one
	 * @throws IOException
	 */
	public void merge(String filePath) throws IOException{
		File temp = new File(filePath);
		//filePath " ".001
		String outPutName = JOptionPane.showInputDialog(
                null, "Enter Output FileName for incoming file " + filePath );
		fileName = outPutName;
		mergeFiles(temp.getAbsolutePath(), fileName);
	}
	
	private static List<File> listOfFilesToMerge(File oneOfFiles) {
	    String tmpName = oneOfFiles.getName();//{name}.{number}
	    String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));//remove .{number}
	    File[] files = oneOfFiles.getParentFile().listFiles(
	            (File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
	    Arrays.sort(files);//ensuring order 001, 002, ..., 010, ...
	    return Arrays.asList(files);
	}
	
	public File[] listOfFiles(File oneOfFiles) {
	    String tmpName = oneOfFiles.getName();//{name}.{number}
	    String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));//remove .{number}
	    File[] files = oneOfFiles.getParentFile().listFiles(
	            (File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
	    Arrays.sort(files);//ensuring order 001, 002, ..., 010, ...
	    return files;
	}
}
