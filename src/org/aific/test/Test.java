package org.aific.test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.aific.sgml.SGMLParser;



public class Test {

	public Test() throws MalformedURLException {
		URL url = new URL("http://ramonantonio.net/g3/ompl.php");
		SGMLParser parser = new SGMLParser(url);
		parser.parse();
		System.out.println("exit");
		
	}
	 public static void test2() throws Exception {
	        URL yahoo = new URL("http://www.yahoo.com/");
	        URLConnection yc = yahoo.openConnection();
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                yc.getInputStream()));
	        String inputLine;

	        while ((inputLine = in.readLine()) != null) 
	            System.out.println(inputLine);
	        in.close();
	    }
	 
	public static void main(String args[]) throws Exception {
		new Test();
		//Test.test2();
	}
	
}
