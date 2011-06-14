package org.aific.sgml;
import java.io.*;
import java.util.*;


/** Query is the frontend of this IR system.
 *
 * It accepts certain inputs and produces a list of documents
 * ordered by score. The inputs for the frontend as like so:<P>
 * <I>java Query invertedIndexPath documentIndexPath 'query string' [maxResults]</I><BR>
 * invertedIndexPath: the inverted index filename<BR>
 * documentIndexPath: the document indec filename<BR>
 * 'query string': any set of characters to be processed for keywords to search the database with<BR>
 * [maxResults]: an optional maximum number of results (integer value) can be specified
 */
public class Query
	{
	/** Driver code for accepting a query.
	 *
	 * <P>It handles all parsing of the query. It defines the InvertedIndex
	 * and DocumentIndex objects in order to use the IndexSearcher class.
	 * @see InvertedIndex
	 * @see DocumentIndex
	 * @see IndexSearcher
	 */            
	public static void main(String[] args)
		{
		if (args.length >= 3)
			{
			try 
				{
				// DEFINE CLASSES
				InvertedIndex invInd = new InvertedIndex();
				invInd.load(args[0]);

				DocumentIndex docInd = new DocumentIndex();
				docInd.load(args[1]);

				IndexSearcher searcher = new IndexSearcher(invInd, docInd);

				// HANDLE QUERY
				String query = args[2];
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
				System.out.println("An IO Error occurred.");
				}
			}
		else
			{
			System.out.println("Usage: \njava Query invertedIndexPath documentIndexPath 'query string' [maxResults]");
			System.out.println("invertedIndexPath: The location of the inverted index");
			System.out.println("documentIndexPath: The location of the document index");
			System.out.println("'query string': The query. It must be in quotes to count as one argument");
			System.out.println("maxResults: An optional integer specifying the maximum number of results to display");
			}
		}
	}
