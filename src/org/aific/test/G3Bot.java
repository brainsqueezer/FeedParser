package org.aific.test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.aific.blogs.Blog;
import org.aific.blogs.BlogUpdater;
import org.aific.blogs.CrawlTimer;

public class G3Bot {
	Vector<Blog> bloglist;
	
	public G3Bot() {
		bloglist = BlogUpdater.getBlogList();
		System.out.println(bloglist.size()+" elements added.");

	}
	
    public void doSomething() {
 
        Timer timer = new Timer();  // create the timer to start the TimerTask thread at the specified time
        TimerTask myTask = new CrawlTimer(bloglist);   // create the task to perform the work 
        Date now = new Date();
        long period = (1000*60);  // 1 time a minute
        timer.scheduleAtFixedRate(myTask, now, period);
 
        // do something while the Timer and TimeTask thread do their works....
 
    }
    
    public static void main(String[] args) {
    	G3Bot test = new G3Bot();
    	test.doSomething();
    	
    }
 
}
