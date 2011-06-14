package org.aific.sgml;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/** A generic SGML Parser.
 * It's methods return a parse tree.
 * The driver code doesn't actually see this class;
 * it's auxilary to support SGMLReader.
 * @see SGMLReader
 */
public class SGMLParser {
	/** The filename this parser is running against. */
	String filename;

	/** Internally, SGMLParser uses a BuffredReader for all IO operations. */
	BufferedReader in;

	/** The current line of input. */
	String x;

	/** file offset for the current line */
	int i;

	/** character offset from the beginning of the current line */
	int j;

	/** Create a new SGMLParser and open the specified file. */
	public SGMLParser(String f) {
		// store the filename
		filename = f;

		// initialize variables
		x = "";
		i = 0;
		j = 0;

		// open the file
		try {
			in = new BufferedReader(new FileReader(filename));
		} catch (IOException e) {
			System.out.println("IO Error: Could not open file");
		}
	}
	
	
	public SGMLParser(URL url) {
		// store the filename
		filename = url.toString();
	
		// initialize variables
		x = "";
		i = 0;
		j = 0;
	
		// open the file
		try {
			URLConnection yc = url.openConnection();
	        in = new BufferedReader(new InputStreamReader(
	                                yc.getInputStream()));
		} catch (IOException e) {
			System.out.println("IO Error: Could not open file");
		}
	}

	/** Parses an SGML file until it reaches the root of the parse tree again.
	 * This means that it can be called multiple times if there are multiple
	 * documents within one file.
	 * @return The parse tree for the next tag under root.
	 */
	public SGMLTag parse() {
		Stack<SGMLTag> inside = new Stack<SGMLTag>();
		SGMLTag root = new SGMLTag();
		root.tagname = "Root";
		root.children = new Hashtable<String, SGMLTag>();
		root.a = i + j;
		SGMLTag current = root;

		int startIndex = i;

		try {
			/*
			The while condition is becoming very complex.
			Conditions:
			1. We need to loop until the Stack is empty, but we must attempt the first time.
				<=> (startIndex == i || current != root)
				This failed, creating a whole new tag if there were blank lines
				in Root.
				We'll try something like:
				if the Hashtable has elements and current == root
			2. We need to skip the reading of the next line if j < x.length()
				<=> ((j != 0 && j < x.length()) || x...)
			3. We need to check the newline
				<=> x...
			*/
			String temp = "";	// pulled out of loop
			int j2;				// added cause of error in hfiles/cr93h100
			while ((root.children.isEmpty() || current != root) && ((j != 0 && j < x.length()) || (x = in.readLine()) != null)) {
				while (j < x.length()) {
					j = x.indexOf("<", j);
					j2 = x.indexOf(">", j);		// added cause we got an error (out-of-bounds: -39) in hfiles/cr93h100
					if (j > -1 && j2 > -1) {
						temp = x.substring(j + 1, j2);
						if (temp.startsWith("/")) {
							if (!temp.substring(1).equalsIgnoreCase(current.tagname)) {
								System.out.println("Syntax error: overlapping tags at character " + (i + j));
								System.out.println("Expected:\t/" + current.tagname);
								System.out.println("Found:\t\t" + temp);
							} else {
								current.b = i + j;
								current = inside.pop();

								// exit mid-line if we need to
								if (current == root)
									break;
							}
						} else {
							SGMLTag newtag = new SGMLTag();
							newtag.tagname = temp;
							newtag.children = new Hashtable<String, SGMLTag>();
							newtag.a = x.indexOf(">", j) + 1 + i;
							current.children.put(newtag.tagname, newtag);
							inside.push(current);
							current = newtag;
						}
						j = x.indexOf(">", j) + 1;
					} else {
						break;
					}
				}
				i += x.length() + 1;	// the extra 1 is for \n
				j = 0;
			}
		} catch (IOException e) {
			System.out.println("IO Error: File read error.");
		}
		root.b = i + 1;

		if (inside.size() != 0) {
			System.out.println("Syntax error: " + inside.size() + " tags not closed...");
			while (inside.size() > 0) {
				System.out.println(current.tagname + " not closed...");
				current = inside.pop();
			}
		}

		if (root.children.isEmpty()) {	// if we never got a non-null line
			return null;
		} else {
			Enumeration<SGMLTag> children = root.children.elements();
			return children.nextElement();
		}
	}

}
