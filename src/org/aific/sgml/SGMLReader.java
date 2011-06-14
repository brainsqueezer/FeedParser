package org.aific.sgml;
import java.util.*;
import java.io.*;

/** An interface for Build to an SGML formatted document.
 * It specifically supports the TREC format.
 * Internally it uses the SGMLParser class.
 * @see SGMLParser
 */
public class SGMLReader implements DocumentReader
	{
	/** The SGMLParser that will be used internally to
	 * load SGML code into a parse tree.
	 */            
	SGMLParser parser;

	/** The path of the file that this SGMLReader is reading from.
	 */        
	String filename;
	
	/** The SGMLReader uses a RandomAccessFile to load strings using
	 * a beginning and ending offset in the file, as specified by
	 * the SGMLTag class.
	 * @see SGMLTag
	 */        
	RandomAccessFile file;

	/** the number of milliseconds spent doing random file access */
	long randomAccessTime;

	/** the number of milliseconds spent in the SGMLParser */
	long sequentialAccessTime;

	/** the number of milliseconds spent cleaning up the document (removing some nested tags) */
	long cleanupTime;

	/** the total number of documents loaded */
	int documents;

	/** Create a new SGMLReader and open the specified file.
	 */        
	public SGMLReader(String filename) {
		// initialize performance stats
		randomAccessTime = 0;
		sequentialAccessTime = 0;
		cleanupTime = 0;
		documents = 0;

		// get the absolute path
		try
			{
			this.filename = (new File(filename)).getCanonicalPath();
			}
		catch (Exception e)
			{
			this.filename = filename;
			}

		// create a parser
		parser = new SGMLParser(filename);

		// load the file
		try
			{
			file = new RandomAccessFile(filename, "r");
			}
		catch (IOException e)
			{
			System.out.println("IO Error: Could not open file");
			}
		}

	/** Reads the next Document from the file.<BR>
	 * Loads the contents of that Document.<br>
	 * Builds the appropriate Document to be returned.
	 * @see Document
	 */                
	public Document readDocument()
		{
		// parse the next document from the file
		long t = System.currentTimeMillis();
		SGMLTag tag = parser.parse();
		t = System.currentTimeMillis() - t;
		sequentialAccessTime += t;

		// return if we're at the end of the file
		if (tag == null)
			return null;

		// log time for random access
		t = System.currentTimeMillis();

		// define a new document
		Document current = new Document();

		// set the filename
		current.filename = filename;

		// temporarily load the body tag
		SGMLTag body = (SGMLTag)tag.children.get("TEXT");

		// load all tags from the document

		// load the body tag of the document
		current.body = loadString(body.a, body.b);

		// attempt to load the title (if defined)
		if (body.children.containsKey("TTL"))
			current.title = loadString(((SGMLTag)body.children.get("TTL")).a, ((SGMLTag)body.children.get("TTL")).b);
		else if (tag.children.containsKey("TTL"))
			current.title = loadString(((SGMLTag)tag.children.get("TTL")).a, ((SGMLTag)tag.children.get("TTL")).b);

		// load the document id if defined
		if (tag.children.containsKey("DOCID"))
			current.id = loadString(((SGMLTag)tag.children.get("DOCID")).a, ((SGMLTag)tag.children.get("DOCID")).b);

		// load the document number (unique identifier)
		current.docno = loadString(((SGMLTag)tag.children.get("DOCNO")).a, ((SGMLTag)tag.children.get("DOCNO")).b).trim();

		// load the date field if defined
		if (tag.children.containsKey("DATE"))
			current.date = loadString(((SGMLTag)tag.children.get("DATE")).a, ((SGMLTag)tag.children.get("DATE")).b);

		// load all other fields into a hashtable in case we need them
		current.other = new Hashtable();
		Enumeration others = tag.children.keys();
		String cur = null;
		while (others.hasMoreElements())
			{
			cur = (String)others.nextElement();
			if (cur.equals("DOCID") || cur.equals("TEXT") || cur.equals("DOCNO") || cur.equals("DATE"))
				continue;

			current.other.put(cur, loadString(((SGMLTag)tag.children.get(cur)).a, ((SGMLTag)tag.children.get(cur)).b));
			}
		t = System.currentTimeMillis() - t;
		randomAccessTime += t;

		// clean up the body tag by removing any nested tags (needed for parsing)
		t = System.currentTimeMillis();
		StringBuffer temp = new StringBuffer();
		int pos = 0;
		int i = 0, j = 0;
		while ((i = current.body.indexOf("<", pos)) != -1 && (j = current.body.indexOf(">", pos)) != -1)
			{
			temp.append(current.body.substring(pos, i));
			pos = j + 1;
			}
		temp.append(current.body.substring(pos));
		current.body = temp.toString();
		t = System.currentTimeMillis() - t;
		cleanupTime += t;

		return current;
		}

	/** display performance statistics to standard output */
	public void showStats()
		{
		long totalTime = randomAccessTime + sequentialAccessTime + cleanupTime;
		System.out.println("Time spent in SGMLReader: " + totalTime);
		System.out.println("Time spent doing random access: " + (randomAccessTime / (double)totalTime));
		System.out.println("Time spent doing sequential access: " + (sequentialAccessTime / (double)totalTime));
		System.out.println("Time spent doing cleanup: " + (cleanupTime / (double)totalTime));
		}

	/** Retrieves a string given the beginning and ending offsets
	 * within the file.
	 * @see SGMLTag
	 */                
	private String loadString(int start, int end)
		{
		try
			{
			byte[] chars = new byte[end - start];
			file.seek(start);
			file.read(chars);
			return new String(chars);
			}
		catch (IOException e)
			{
			return null;
			}
		}
	}
