package org.aific.blogs;

import java.util.TimerTask;
import java.util.Vector;


/**
 * This is the actual thread that will be invoke and run
 * (Timer will start this thread at the specified time)
 */
public class CrawlTimer extends TimerTask {
	Vector<Blog> bloglist;
    public CrawlTimer(Vector<Blog> bloglist) {
        this.bloglist = bloglist;
        
    }
 
    /**
     * This method will automatically be invoke when the Timer
     * start this object
     */
    public void run() {
		BlogUpdater bupdater = new BlogUpdater(bloglist);
		bupdater.run();
		NewsUpdater nupdater = new NewsUpdater();
		nupdater.run();
    }
}
 


