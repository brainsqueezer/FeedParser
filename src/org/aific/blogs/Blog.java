package org.aific.blogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;

public class Blog {
	private URL url;
	private Date lastUpdate;
	//private String status;
	private int failedAttemps = 0;
	private long lastModified = 1;
	
	public Blog(URL url) {
		this.url = url;
	}
	
	public String toString() {
		return url.toString();
	}
	
	public boolean update() {
		if (failedAttemps < 5) {
			StringBuffer input = new StringBuffer();
			
			try {
				URL updateURL = new URL("http://ramonantonio.net/updateblogs.php?url="+URLEncoder.encode(url.toString(), "UTF-8"));
				
				URLConnection c = updateURL.openConnection();
				c.setRequestProperty("User-agent", "G3Bot/0.5_(http://ramonantonio.net/g3/; robot)");
				
				long _lastModified = c.getLastModified();
				if (_lastModified == this.lastModified) {
					System.out.println("Skipped: "+url.toString()+ " Modified: "+lastModified);
					return true;
				} else {
					this.lastModified = _lastModified;
					System.out.println("Updating: "+url.toString());
				}
			
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
				failedAttemps++;
				return false;
			
			} catch (IOException e) {
				System.out.println(e);
				failedAttemps++;
				return false;
			}
			lastUpdate = new Date(System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}

}
