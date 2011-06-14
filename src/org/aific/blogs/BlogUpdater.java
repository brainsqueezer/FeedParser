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
 * 
 * @author Ramon Antonio Parada <rap@ramonantonio.net>
 * @version 0.5.1
 */
public class BlogUpdater {
	Vector<Blog> bloglist;
	public static Vector<Blog> getBlogList() {
		StringBuffer input = new StringBuffer();
		OMPLParser listener = new OMPLParser();
		URL url;
		try {
			url = new URL("http://ramonantonio.net/g3/ompl.php");
			System.out.println("Opening conection: "+url.toString());
			URLConnection c = url.openConnection();
			c.setRequestProperty ( "User-agent", "G3Bot/0.5_(http://ramonantonio.net/g3/; robot)");
			System.out.println("Dowloading...");
	        BufferedReader in;
			in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			
			String inputLine;
	        while ((inputLine = in.readLine()) != null) 
	            input.append(inputLine);
	        in.close();

			XMLParser parser = new XMLParser(input.toString());
			
			parser.setListener(listener);
			System.out.println("Parsing...");
			parser.parse();
		
			//for (int i = 0; i < listener.bloglist.size(); i++) {
			//	System.out.println(listener.bloglist.elementAt(i));
			//}
			
			
			Vector<Blog> bloglist = new Vector<Blog>();
			
			for (URL feed:listener.bloglist) {
				bloglist.add(new Blog(feed));
			}
			
			return bloglist;
		} catch (MalformedURLException e) {
			System.out.println(e);
			return null;
		
		} catch (IOException e) {
			System.out.println("IO Exception. Todo es culpa de la Timofonica");
			return null;
		}
        
        
	}

	public BlogUpdater(Vector<Blog> bloglist) {
		this.bloglist = bloglist;
	}
    /**
     * This method will automatically be invoke when the Timer
     * start this object
     */
    public void run() {
		for (Blog blog:bloglist) {
			blog.update();
		}
    }

}
