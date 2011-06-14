package org.aific.sgml;
import java.io.*;
import java.util.*;


/** DatabaseAnalyzer is a class devoted to the analysis of Document objects.
 * It takes the Documents and registers the appropriate stemmed words
 * with the DatabaseStatistics class that was passed to it. The class could
 * really be a set of static methods if it weren't for new performance
 * optimizations.
 */
public class DatabaseAnalyzer
	{
	/** The output of DatabaseStatistics.
	 *
	 * The DatabaseAnalyzer is created with one DatabaseStatistic object
	 * as it's output. This is that object.
	 */            
	DatabaseStatistics o;

	/** Tokens to be trivially ignored.
	 *
	 * Example: quote characters
	 */        
	HashSet stopwords;

	/** A cache of the output from the stemmer.
	 *
	 * This gives a speedup of
	 * about 30-40% on the 10MB database. Rather than using the stemmer, it
	 * looks here first.
	 */        
	Properties stemmerCache;

	/** Case-sensitive substitution cache.
	 * I planned to make this work the same way as stopwordCache, but this
	 * would be checked before the word is made lowercase.
	 */        
	Properties csCache;
	
	/** An instance of the WordConflation class for stemming. */
	WordConflation c;

	/** A list of single character delimiters. */
	public static final String delimiters = "\r\n\t ?!;:(){}[]";
	
	/** Contains the number of total database tokens.
	 * Used in computing the hit ratio for the stopwordCache.
	 */        
	long totalTokens;
	
	/** Create a new DatabaseStatics object.
	 * @param out The destination of all analysis.
	 */        
	public DatabaseAnalyzer(DatabaseStatistics out)
		{
		// initialize variables
		totalTokens = 0;
		o = out;
		stopwords = new HashSet();
		c = new WordConflation();
		csCache = new Properties();
		stemmerCache = new Properties();

		// define a static list of strings to be ignored
		stopwords.add("");
		stopwords.add("'");
		stopwords.add("-");
		stopwords.add("--");
		stopwords.add("*");
		stopwords.add(",");

		// add words that cause errors in the stemmer
		stemmerCache.put("kilos", "kilos");
		stemmerCache.put("kilos.", "kilos");
		}


	/** Loads a list of stopwords from a file.
	 * The format of the file is one stop word per line.
	 */
	public void loadStopWords(String filename) throws IOException
		{
		// loads stop words from a file
		// format: one stop word per line
		BufferedReader r = new BufferedReader(new FileReader(filename));

		// temporary variable
		String currentWord;

		while ((currentWord = r.readLine()) != null)
			{
			// store the empty string in the cache (WordConflation does that anyway...)
			stemmerCache.put(currentWord.toLowerCase(), "");
			}

		// close the file
		r.close();
		}

	/** Loads a list of case-sensitive substitutions.
	 * Used for acronyms, but nothing is used (yet) after it is loaded.
	 */
	public void loadCSCache(String filename) throws IOException
		{
		// basically loads the stemmerCache from a file
		// however, this cache holds case-sensitive definitions
		// which have not been trimmed of trailing periods

		// format: each line contains a word and it's substitution
		//	separated by a space
		BufferedReader r = new BufferedReader(new FileReader(filename));

		String currentLine;
		while ((currentLine = r.readLine()) != null)
			{
			// store the substitution
			csCache.put(currentLine.substring(0, currentLine.indexOf(' ')), currentLine.substring(currentLine.lastIndexOf(' ') + 1, currentLine.length()));
			}

		// close the file
		r.close();
		}

	/** Loads a file of case-insensitive substitutions */
	public void loadCICache(String filename) throws IOException
		{
		// basically loads the stemmerCache from a file

		// format: each line contains a word and it's substitution
		//	separated by a space
		BufferedReader r = new BufferedReader(new FileReader(filename));

		String currentLine;
		while ((currentLine = r.readLine()) != null)
			{
			// store the substitution
			stemmerCache.put(currentLine.substring(0, currentLine.indexOf(' ')), currentLine.substring(currentLine.lastIndexOf(' ') + 1, currentLine.length()));
			}

		// close the file
		r.close();
		}

	/** Analyze a Document.
	 *
	 * It loads the body of the document, removes special characters.
	 * Then it passes the body to a StringTokenizer using th delimiters.
	 * For each token, it calls preStem(), then checks the stop word cache,
	 * then registers the stemmed word.
	 * @param doc The Document to be analyzed.
	 */
	public void analyze(Document doc)
		{
		// register this document in the statistics class and set it as the current doc
		o.registerDocument(doc.docno, doc.filename);
		o.setCurrentDocument(doc.docno);

		// first, clean up the text
		doc.body = cleanUp(doc.body);

		// then use the StringTokenizer to register words
		StringTokenizer tokenizer = new StringTokenizer(doc.body, delimiters);

		// temporary variables
		String t = "", s;

		// loop through all words in this document
		while (tokenizer.hasMoreTokens())
			{
			t = tokenizer.nextToken();

			// lowercase the word to make processing simpler
			t = t.toLowerCase();

			// remove certain punctutation marks from both sides of the word
			t = preStem1(t);

			// if the word shouldn't be ignored
			if (!stopwords.contains(t))
				{
				// attempt to load word from the cache
				s = stemmerCache.getProperty(t);

				// if it's not in the cache, process it and store results in the cache
				if (s == null)
					{
					s = c.stripAffixes(t);
					stemmerCache.put(t, s);
					}

				// register the occurrance of this word in the statistics
				o.register(s);

				// increment the total number of words (for system stats)
				totalTokens++;
				}
			}
		}

	/** Show some basic statistics about the performance of this component. */
	public void showStats()
		{
		// to show that our stopword cache is working well
		long cacheMisses = stemmerCache.size();
		long cacheHits = totalTokens - cacheMisses;
		System.out.println("Stopword cache hit ratio: " + (cacheHits / (double)totalTokens));
		System.out.println("Hits: " + cacheHits);
		System.out.println("Misses (cache size): " + cacheMisses);
		System.out.println("Total tokens: " + totalTokens);
		}

	/** A first pass over a word.
	 *
	 * It removes leading and trailing quotation marks, periods, etc.
	 * Also removes "'s" for possesives.
	 * @param word The word to be cleaned up.
	 * @return The word with bad characters removed.
	 */                
	public static String preStem1(String word)
		{
		// return if it's too short
		if (word.length() == 1)
			return word;

		int a = 0,
			b = word.length() - 1;

		// rather than redefining the string every time, we'll just move pointers/offsets

		// remove leading bad characters
		while (a < b && isRemovableFirstChar(word.charAt(a)))
			a++;

		// remove trailing bad characters
		while (a < b && isRemovableLastChar(word.charAt(b)))
			b--;

		// shrink the string if necessary
		if (a != 0 || b != word.length() - 1)
			word = word.substring(a, b + 1);

		// remove "'s"
		if (word.endsWith("'s"))
			word = word.substring(0, word.length() - 2);
			
		return word;
		}

	/** Tests whether this character can be removed  from the beginning or not.
	 * @return True if it's a quotation mark or some other bad leading character.
	 */                
	private static boolean isRemovableFirstChar(char c)
		{
		return (c == '\'' || c == '"' || c == '`' || c == ',');
		}

	/** Tests whether the character can be removed from the end of a String.
	 * @return True if the character can be removed.
	 */                
	private static boolean isRemovableLastChar(char c)
		{
		return (c == '\'' || c == '"' || c == '`' || c == ',');
		}

	/** Cleans up a String.
	 *
	 * It replaces all occurrances of "--" with "  " to make things easier
	 * for a Tokenizer.
	 * @param line The String to be cleaned up.
	 * @return The String with "--" renmoved.
	 */                
	public static String cleanUp(String line)
		{
		StringBuffer w = new StringBuffer(line);
		
		int i = 0;
		
		i = line.indexOf("--", i);

		while (i != -1)
                    {
                    w.setCharAt(i, ' ');
                    w.setCharAt(i + 1, ' ');
                    i = line.indexOf("--", i + 2);
                    }

		return w.toString();
		}
	}
