package org.aific.sgml;
import java.io.*;
import java.util.*;


/** InvertedIndex represents an inverted index.
 *
 * It contains methods for creating, storing/loading, and using
 * an inverted index.
 */
public class InvertedIndex implements Index
	{
	/** The data structure for the inverted index.
	 * <P>It is a hashtable of hashtables. The index for the outer
	 * hashtable (this structure) is the term (stemmed word). The index for
	 * the inner hashtable (the structure returned) is the document ID.
	 */
	private HashMap index;

	/** Initialize the Hashtable */
	public InvertedIndex()
		{
		index = new HashMap();
		}

	/** Build an inverted index based on a colllection of statistics about documents. */
	public void build(DatabaseStatistics s)
		{
		// define an Iterator over the collection of documents
		Iterator e = s.getDocumentList();

		// temporary variables
		String docid;
		HashMap currentFL;
		DocumentStatistics current;
		Iterator f;
		String word;

		// for performance stats
		long totalKeywords = 0;
		int documents = 0;

		// BUILD THE INDEX

		// while there are more documents
		while (e.hasNext())
			{
			// load the unique identifier
			docid = (String)e.next();

			// load doc stats
			current = s.getDocumentStatistics(docid);

			// decide how to pull out the terms for this document
			if (BuildSettings.useAP)
				{
				currentFL = current.destructiveAdaptiveMostOccurring(BuildSettings.mostSignificantTerms);
				}
			else
				{
				if (BuildSettings.useProbCutoff)
					currentFL = current.mostOccurring(BuildSettings.probabilityCutoff);
				else
					currentFL = current.destructiveMostOccurring(BuildSettings.mostSignificantTerms);
				}

			// get an Iterator over the terms
			f = currentFL.keySet().iterator();

			//while there are more terms
			while (f.hasNext())
				{
				word = (String)f.next();

				// count the occurrance of this word in the internal data structure
				register(docid, word, ((int[])currentFL.get(word))[0]);
				totalKeywords++;
				}
			documents++;
			}

		// print statistics
		if (BuildSettings.printAverageKeywords)
			{
			System.out.println("Average number of keywords: " + (totalKeywords / (double)documents));
			}
		}

	/** Register a frequency of a term within a document into this
	 * index. This method is synchronized to allow for multiple threads
	 * to build the same index concurrently without needing a synchronized
	 * data structure.
	 * @param docid The document ID
	 * @param word The stemmed word
	 * @param frequency The number of times word occurred in the document
	 */
	public synchronized void register(String docid, String word, int frequency)
		{
		// load the hashtable for this term
		Object val = index.get(word);

		// define a new int[1] to store the frequency
		int[] fArray = new int[1];
		fArray[0] = frequency;

		if (val == null)
			{
			// if this term isn't in the index, create a new hashtable and store it
			HashMap newList = new HashMap();
			newList.put(docid, fArray);
			index.put(word, newList);
			}
		else
			{
			// if the term exists, simply store appropriately
			((HashMap)val).put(docid, fArray);
			}
		}

	/** Store the index in the specified filename in the class' internal format.
	 * @param filename The filename where this index will be stored
	 */
	public void store(String filename) throws IOException
		{
		// open the file
		// the false part means that the buffer won't be automatically flushed
		// we flush after every term
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)), false);

		// STORE THE INDEX
		
		// an iterator over all terms
		Iterator i = index.keySet().iterator();

		// temporary variables
		Iterator docs;
		HashMap docList;
		String doc;
		int[] t;

		// the first line is the number of terms
		out.println(index.size());
		String word;

		// loop through each term
		while (i.hasNext())
			{
			word = (String)i.next();

			// print the term
			out.println(word);

			// get variables to loop through documents containing the term
			docList = (HashMap)index.get(word);
			docs = docList.keySet().iterator();

			// loop through documents containing the term
			while (docs.hasNext())
				{
				// get the document and frequency
				doc = (String)docs.next();
				t = (int[])docList.get(doc);

				// store the document and frequency
				out.println(doc);
				out.println(t[0]);
				}

			// put another newline on there and flush the buffer
			out.println();
			out.flush();
			}

		// close the file
		out.close();
		}

	/** Load the index from the specified filename in the class' internal format.
	 * @param filename The filename where this index is stored.
	 */
	public void load(String filename) throws IOException
		{
		// open the file
		BufferedReader r = new BufferedReader(new FileReader(filename));

		// load the number of terms
		int size = Integer.parseInt(r.readLine());

		// must divide by load factor (0.75) to not need to rehash
		index = new HashMap((int)(size / .75) + 1);

		// temporary variables
		String line;
		StringTokenizer tokens;
		String word;
		HashMap docList;
		String docid;
		int[] fArray;
		int lineNumber = 1;

		// while there are more lines in the document
		while ((line = r.readLine()) != null)
			{
			// increment the line number
			lineNumber++;

			// the word is the only thing on the line
			word = line;

			// load all documents containign this term
			docList = new HashMap();
			index.put(word, docList);

			line = r.readLine();
			while (line != null && !line.equals(""))
				{
				fArray = new int[1];

				docid = line;
				fArray[0] = Integer.parseInt(r.readLine());

				docList.put(docid, fArray);
				line = r.readLine();
				}
			}

		// close the file
		r.close();
		}

	/**
	 * the number of documents containing the word
	 * @return The number of documents containing this word
	 */
	public HashMap documentsContaining(String word)
		{
		return (HashMap)index.get(word);
		}

	/**
	 * get an Iterator to loop through all words
	 * @return An Iterator over the words in the index
	 */
	public Iterator words()
		{
		return index.keySet().iterator();
		}

	/**
	 * the size of the index
	 * @return The size of the index
	 */
	public int size()
		{
		return index.size();
		}
	}
