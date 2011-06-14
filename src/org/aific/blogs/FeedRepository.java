package org.aific.blogs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.aific.xml.OMPLParser;
import org.aific.xml.XMLParser;


/**
 * Simple XML SAX Parser
 * @author Ramon Antonio Parada <rap@ramonantonio.net>
 * @version 0.1
 */
public class FeedRepository {
	StringBuffer input = new StringBuffer();
	public Vector<URL> bloglist;
	
	public FeedRepository() {
		bloglist = new Vector<URL>();
	}
	
	public void update() {
		URL url;
		try {
			url = new URL("http://www.aific.org/ompl.php");
			System.out.println("Opening conection: "+url.toString());
			URLConnection c = url.openConnection();
			
			System.out.println("Dowloading...");
	        BufferedReader in;
			in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			
			String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            input.append(inputLine);
	        in.close();

			XMLParser parser = new XMLParser(input.toString());
			OMPLParser listener = new OMPLParser();
			parser.setListener(listener);
			System.out.println("Parsing...");
			parser.parse();
		
			//for (int i = 0; i < listener.bloglist.size(); i++) {
			//	System.out.println(listener.bloglist.elementAt(i));
			//}
			this.bloglist = listener.bloglist;
			System.out.println(listener.bloglist.size()+" elements added.");

			System.out.println("Done.");
		} catch (MalformedURLException e) {
			System.out.println(e);
		
		} catch (IOException e) {
			System.out.println("IO Exception. Todo es culpa de la Timofonica");
		}
        
        
	}

	

	public static void main(String[] args) throws IOException{
	//String str = "\"hi\"";
		//str = str.replace("\"", "");
		//System.out.println(str);
		new FeedRepository();
	}
	
}
