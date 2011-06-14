package org.aific.sgml;
import java.io.*;


/** BuildThread is a class to support parallel generation of the indices.
 * The code in BuildThread is essentially the same as the code in the 
 * old backend class Build. It is initialized with references to the 
 * indices and with a list of files to process.
 */
public class BuildThread extends Thread
	{
	/** the total number of threads defined */
	public static int numThreads = 0;

	/** a unique ID for this object */
	public int threadID;

	/** the list of files to process */
	public String[] files;

	/** the statistics class to use */
	public DatabaseStatistics stats;

	/** the inverted index to build */
	public InvertedIndex invInd;

	/** the document index to build */
	public DocumentIndex docInd;

	/** initialize BuildThread with a list of files */
	public BuildThread(String[] args)
		{
		numThreads++;
		threadID = numThreads;
		files = args;
		stats = new DatabaseStatistics();
		}
		
	/** initialize BuildThread with no files */
	public BuildThread()
		{
		numThreads++;
		threadID = numThreads;
		stats = new DatabaseStatistics();
		}

	/** process the list of files and add to the indices */
	public void run()
		{
		// temporary variable for the current document
		Document c;
		// temporary variable for the current reader (for current file)
		SGMLReader reader;
		// the analyzer
		DatabaseAnalyzer analyzer = new DatabaseAnalyzer(stats);

		// load the stopword list if possible
		if (!BuildSettings.stopwordFilename.equals(""))
			{
			try
				{
				analyzer.loadStopWords(BuildSettings.stopwordFilename);
				}
			catch (IOException e)
				{
				System.out.println("Couldn't load stopword file.");
				}
			}

		// load the case-sensitive substitution list if possible
		if (!BuildSettings.csCacheFilename.equals(""))
			{
			try
				{
				analyzer.loadCSCache(BuildSettings.csCacheFilename);
				}
			catch (IOException e)
				{
				System.out.println("Couldn't load case-sensitive substitution file.");
				}
			}

		// load the case-insensitive substitution list if possible
		if (!BuildSettings.ciFilename.equals(""))
			{
			try
				{
				analyzer.loadCSCache(BuildSettings.ciFilename);
				}
			catch (IOException e)
				{
				System.out.println("Couldn't load case-insensitive substitution file.");
				}
			}

		// loop through files, one-by-one
		for (int i = 0; i < files.length; i++)
			{
			// print each filename if we should (progress indicator)
			if (BuildSettings.printEachDocumentName)
				System.out.println(files[i] + "\t(" + (i + 1) + "/" + files.length + ":" + threadID + ")");

			// define an SGMLReader for this file
			reader = new SGMLReader(files[i]);

			// loop through Documents, one-by-one
			while ((c = reader.readDocument()) != null)
				{
				// analyze this document
				analyzer.analyze(c);
				}
			}

		// pass the generated statistics to the indices if they have been specified
		if (invInd != null)
			invInd.build(stats);
		if (docInd != null)
			docInd.build(stats);
		}
	}

