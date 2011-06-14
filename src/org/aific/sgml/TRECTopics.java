package org.aific.sgml;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;




class TRECTopics {
	public static void main(String[] args) {
		//if (args.length == 3) {
			try {
				// DEFINE CLASSES
				String path = "C:\\Documents and Settings\\rap\\workspac\\e\\FeedParser\\src\\";
				path = "./";
				path = System.getProperty("user.dir");
				System.out.println(path);
				
				// load the inverted index
				InvertedIndex invInd = new InvertedIndex();
				invInd.load("index.inverted");

				// load the document index
				DocumentIndex docInd = new DocumentIndex();
				docInd.load("index.document");

				// define a class to search with
				IndexSearcher searcher = new IndexSearcher(invInd, docInd);

				/*
				All query handling code should be within a while loop of sorts.
				The outside of the loop should define an SGMLParser for use
				on the topics file. The inside of the loop should use the 
				returned SGMLTag structure appropriately, calling the appropriate 
				code to handle the topic as a query.
				*/

				// HANDLE QUERY
				String query = "David";
				Vector t = new Vector();

				// clean up the string query
				query = DatabaseAnalyzer.cleanUp(query);

				// tokenize using same delimiters using in document processing
				StringTokenizer tokens = new StringTokenizer(query, DatabaseAnalyzer.delimiters);
				// define a stemmer class
				WordConflation w = new WordConflation();
				// temporary variable
				String current;

				// loop through tokens in the query
				while (tokens.hasMoreTokens())
					{
					// treat the token the same as a document
					current = tokens.nextToken();
					current = current.toLowerCase();
					current = DatabaseAnalyzer.preStem1(current);
					current = w.stripAffixes(current);

					if (current.length() != 0)
						t.add(current);
					}

				// convert the ArrayList to an array for speedier access
				String[] termVector = new String[t.size()];
				for (int i = 0; i < termVector.length; i++)
					{
					termVector[i] = (String)t.get(i);
					}

				// define the list of results
				DocumentResult list[];

				// perform the search
				if (args.length >= 4)
					list = searcher.search(termVector, Integer.parseInt(args[3]));
				else
					list = searcher.search(termVector);

				// display the list of documents
				for (int i = 0; i < list.length; i++)
					System.out.println(list[i]);
				}
			catch (IOException e)
				{
				System.out.println("An IO Error occurred."+e);
				}
		/*	}
		else
			{
			System.out.println("Usage: \njava TRECTopics invertedIndexPath documentIndexPath topicsPath");
			System.out.println("invertedIndexPath: The location of the inverted index");
			System.out.println("documentIndexPath: The location of the document index");
			System.out.println("topicsPath: The location of the TREC topics file, converted to an XML document using the provided tool");
			} */
		}
	}
