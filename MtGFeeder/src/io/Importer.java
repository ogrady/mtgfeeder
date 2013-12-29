package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Imports items from a file or directory to send to the database.
 * @author Daniel
 *
 */
final public class Importer {
	private static final String DUMMY_DUMP_NAME = "mtg_feeder.dump";
	private Importer(){}
	
	/**
	 * Reads the contents of a file.<br>
	 * The items in that file have to be line-separated. A quantifier can be specified as second item after a separator. If no quantifier is specified, the quantity will be 1.<br>
	 * <p><strong>Example:</strong><br>
	 * A<br>
	 * B;1<br>
	 * C<br>
	 * D;3<br>
	 * </p>
	 * Will produce 1 A, 1 B, 1 C and 3 D, assuming the separator was ";". This will be represented by having D three times in the returned list.
	 * <p>
	 * The reader is quiet fail-prone:<br>
	 * Specifying and invalid file or such returns an empty list and will print a corresponding message to the error-stream. But the execution will continue.<br>
	 * Specifying an invalid quantifier will cause the reader to fall back to 1 as quantity.
	 * </p>
	 * @param file the file to read the values from
	 * @param separator the separator in case some values have a quantity specified. It then looks like this: VALUE[SEPARATOR[QUANTITY]]LBR 
	 * @return a list of items from the specified file. Values that have a quantifier specified will be contained multiple times in the list.
	 */
	public static List<String> readFile(File file, String separator) {
		List<String> input = new ArrayList<String>();
		if(file.isDirectory()) {
			System.err.println(String.format("'%s' is a directory, not a file. Returning an empty list as result.", file.getAbsolutePath()));
		} else {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String utf8, line;
				int quantity, lineNumber = 1;
				String[] tokens;
				while((line = reader.readLine()) != null) {
					utf8 = new String(line.getBytes(), "UTF-8");
					tokens = utf8.split(separator);
					if(tokens.length == 2) {
						utf8 = tokens[0];
						try {
							quantity = Integer.parseInt(tokens[1]);
						} catch(NumberFormatException nfe) {
							System.err.println(String.format("Invalid quantity '%s' for '%s' in line %d. Defaulting quantity to 1.", tokens[1], tokens[0], lineNumber));
							quantity = 1;
						}
					} else {
						quantity = 1;
					}
					for(int i = 0; i < quantity; i++) {
						input.add(utf8);
					}
					lineNumber++;
				}
			} catch (FileNotFoundException e) {
				System.err.println(String.format("Couldn't find file '%s'. Returning an empty list as result.", file.getAbsolutePath()));
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println(String.format("Error '%s' when accessing file '%s'. List is probably incomplete.", e.getMessage(), file.getAbsolutePath()));
				e.printStackTrace();
			} catch (NumberFormatException nfe) {
				
			} finally {
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						System.err.println(String.format("Critical error when trying to close input-file '%s':\r\n'%s'", file.getAbsolutePath(), e.getMessage()));
					}
				}
			}
		}
		return input;
	}
	
	/**
	 * Reads a while directory where only files that match the passed filter are being read. Passing an invalid directory just causes the method to return an empty list. The execution will continue.
	 * @param directory the directory to browse through
	 * @param filter the filter to apply to files in the directory to determine whether to examine them or not
	 * @param separator the separator as used in {@link #readFile(File, String)}
	 * @return a concatenated list of items of all passing files in the directory as returned from {@link #readFile(File, String)}
	 */
	public static List<String> readDirectory(File directory, FilenameFilter filter, String separator) {
		List<String> input = new ArrayList<String>();
		if(!directory.isDirectory()) {
			System.err.println(String.format("'%s' is no directory. Returning an empty list as result.", directory.getAbsolutePath()));
		} else {
			for(String path : directory.list(filter)) {
				input.addAll(readFile(new File(path), separator));
			}
		}
		return input;
	}
	
	/**
	 * Dumps a list of strings to a file (line by line).<br>
	 * Is quite error-prone as it tries to recover from potential errors:<p>
	 * when specifying a directory instead of a file it will create a file called {@value #DUMMY_DUMP_NAME} in that directory<br>
	 * when failing to access the file at all it will at least dump the values to the error-stream to leave it for the user to copy and recover it himself
	 * </p>
	 * @param file file to dump the lines in
	 * @param lines list of lines to dump to the file
	 */
	public static void dump(File file, List<String> lines) {
		if(file.isDirectory()) {
			System.err.println(String.format("Can not dump to directory. Attempting to dump into dummy-file '%s'.", file.getAbsolutePath()+File.separator+DUMMY_DUMP_NAME));
			dump(new File(file.getAbsolutePath()+File.separator+DUMMY_DUMP_NAME), lines);
		} else {
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				for(String line : lines) {
					try {
						writer.write(line+"\r\n");
					} catch(IOException ioe) {
						System.err.println(String.format("Could not write '%s' to file because an error occured: '%s'. Attempting to proceed with further lines.", line, ioe.getMessage()));
					}
				}
				writer.flush();
			} catch (IOException e) {
				System.err.println(String.format("Could not access file '%s' because an error occured: '%s'. Dumping contents to error-stream:", file.getAbsolutePath(), e.getMessage()));
				for(String line : lines) {
					System.err.println(line);
				}
			} finally {
				if(writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						System.err.println(String.format("Critical error when trying to close input-file '%s':\r\n'%s'", file.getAbsolutePath(), e.getMessage()));
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		List<String> input = Importer.readDirectory(new File("C:\\Users\\Daniel\\git\\mtgfeeder\\MtGFeeder\\"), new FileExtensionFilter("tsv"), "\t");
		for(String s : input)
			System.out.println(s);
		Importer.dump(new File("C:\\Users\\Daniel\\Desktop"), input);
	}
}
