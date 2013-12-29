package io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether a file is passing file-extension related criterions concerning its name.
 * @author Daniel
 *
 */
public class FileExtensionFilter implements FilenameFilter {
	private List<String> _accepted;
	
	/**
	 * Constructor
	 * @param accepted list of accepted extensions (e.g.: txt, doc, docx, pdf....)
	 */
	public FileExtensionFilter(String ... accepted) {
		_accepted = new ArrayList<String>();
		for(String acc : accepted) {
			_accepted.add(acc);
		}
	}

	@Override
	public boolean accept(File dir, String name) {
		int i = 0;
		final String lowerCase = name.toLowerCase();
		while(i < _accepted.size() && !lowerCase.endsWith(_accepted.get(i))) {
			i++;
		}
		return i < _accepted.size();
	}

}
