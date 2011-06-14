package org.aific.blogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class NewsUpdater {
	
    /**
     * This method will automatically be invoke when the Timer
     * start this object
     */
    public void run() {
		URL url;
		StringBuffer input = new StringBuffer();
		
		try {
			url = new URL("http://ramonantonio.net/updatenews.php");
			System.out.println("Opening conection: "+url.toString());
			URLConnection c = url.openConnection();
			
			System.out.println("Dowloading...");
	        BufferedReader in;
			in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			
			String inputLine;
	        while ((inputLine = in.readLine()) != null) { 
	            input.append(inputLine);
	            System.out.println(inputLine);
			}
	        in.close();
		} catch (MalformedURLException e) {
			System.out.println(e);
		
		} catch (IOException e) {
			System.out.println(e);
		}
    }

}
