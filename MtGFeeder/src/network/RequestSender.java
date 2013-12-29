package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class to handle requests for URLs
 * @author Daniel
 *
 */
public abstract class RequestSender {
	
	/**
	 * Sends a GET request to a site
	 * @param request the request to send
	 * @return whether the answer is satisfying as specified in {@link #isGood(String)}. Failing somewhere when trying to send the request also yields false
	 */
	public boolean send(String request) {
		if(request == null) {
			throw new NullPointerException("can't send a request to NULL");
		}
		boolean failed = false;
		String result = "";
		URL url;
		BufferedReader in = null;
		try {
			url = new URL(request);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while((line = in.readLine()) != null) {
				result += line;
			}
		} catch (MalformedURLException e) {
			System.err.println(String.format("Malformed URL '%s'.", request));
			failed = true;
		} catch (IOException e) {
			System.err.println(String.format("Failed to open the input stream from the request: '%s'.", e.getMessage()));
			failed = true;
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// fail silently
				}
			}
		}
		return !failed && isGood(result);
	}
	
	/**
	 * Checks whether the result of the previous request is satisfying
	 * @param httpResult result of the request
	 * @return whether this result is good or not
	 */
	abstract protected boolean isGood(String httpResult);
}
