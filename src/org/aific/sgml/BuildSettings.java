package org.aific.sgml;


/**
 * BuildSettings is a class to support {@link BuildMP}.
 * It takes the command-line arguments and parses them
 * into static members. The command-line arguments are
 * proccessed like so:<P>
 * There will be a number of options followed by a list
 * of files. The list of files to process must be at the
 * end of the argument list. The command-line options
 * are specified by preceding a predefined string with a 
 * dash. For example, the way to specify the number of 
 * threads is by using the switch <I>-t</I>. The <I>-t</I>
 * switch in particular must have an integer value as the
 * next argument. <P>
 * Each parameter also has a default value. The default 
 * inverted index filename, for example, is "inverted.index"<P>
 * The predefined switches are:<BR>
 * <I>-t</I>: Specify the number of threads. Next argument must be an integer.<BR>
 * <I>-n</I>: Use a fixed number of terms from each document. Next argument must be an integer.<BR>
 * <I>-p</I>: Use a probability cutoff to select terms from each document. Next argument must be a decimal.<BR>
 * <I>-ii</I>: Specify the inverted index filename. Next argument must be the filename.<BR>
 * <I>-di</I>: Specify the document index filename. Next argument must be the filename.<BR>
 * <I>-append</I>: Indices will be loaded and added to if this switch is present.<BR>
 * <I>-sw</I>: Specify a file containing stop words, one per line. Next argument must be the filename.<BR>
 * <I>-cs</I>: Specify a file containing case-sensitive substitutions, one per line. The word to be replaced must be the the first word on the line. The replacement must follow the word by a space. Next argument must be the filename.<BR>
 */
public class BuildSettings 
	{
	// all settings are defined here

	/** the number of threads to use when executing */
	public static int NumberOfThreads = 4;
	
	/** if true, Build loads the inverted index and appends to it. */
	public static boolean appendToInvertedIndex = false;
	/** if true, Build loads the document index and appends to it. */
	public static boolean appendToDocumentIndex = false;

	/** defines the index at which filenames begin */
	public static int startOfList = 2;

	/** Inverted index filename. Not used. */
	public static String invertedIndexFilename = "index.inverted";
	/** Document index filename. Not used. */
	public static String documentIndexFilename = "index.document";

	// stopword filename
	/** file containing the list of stopwords. */
	public static String stopwordFilename = "";
	/** file containing the list of case-sensitive substitutions.
	    Used for acronyms. Loading this file is implemented.
	    However, case-sensitive substitutions are not yet implemented
	    for suspected performance reasons.
	*/
	public static String csCacheFilename = "";
	/** file containing case-insensitive substitutions */
	public static String ciFilename = "";

	// what runtime stats to print
	/** show the time it takes to complete */
	public static boolean printTimes = true;
	/** show the hit ratio and size of the stemmer cache */
	public static boolean printStopWordCache = false;
	/** show each filename before loading it */
	public static boolean printEachDocumentName = true;
	/** show words that cause the stemmer to fail */
	public static boolean printErrorWords = true;
	/** show a breakdown of filesystem access */
	public static boolean printDetailedFileOpTimes = false;
	/** show the average number of keywords per document */
	public static boolean printAverageKeywords = true;

	// used to build the inverted index
	/** how many terms will be used in building the index (when useProbCutoff is false)*/
	public static int mostSignificantTerms = 30;
	/** what probabiliy a term must have in order to be in the index (when useProbCutoff is true) */
	public static double probabilityCutoff = .0055;
	/** if true, uses the probability cutoff to generate the index, otherwise uses a fixed number of terms */
	public static boolean useProbCutoff = true;

	/** use adaptive probability.
	    The concept behind what I called adaptive probability here
	    is similar to the concept of a binary search. There is a specific
	    probability at which the number of terms with greater or equal
	    probability is equal to a specific number. This number is 
	    mostSignificantTerms. The idea is that we know that probability
	    exists; we simply must find it. We search using a binary search 
	    algorithm. When the number of terms is about (+-5% to save speed)
	    mostSignificantTerms, we stop binary searching. Also, we stop
	    searching if we have tried more than APMaxPasses times. 
	*/
	public static boolean useAP = false;	// adaptive probability
	/** the maximum number of passes to make on the frequency list when using adaptive probability */
	public static int APMaxPasses = 5;	// the max. no. of passes to make
	/** the starting point for the correct probability search */
	public static double APGuess = .0041;	// guess the correct probabilty

/*
	Notes on the probability cutoff
	.0041 gets an average of about 32-34 terms per document
	.0045 gets about 26
	.0049 gets about 22
	.0055 gets about 18
*/


	/** ways to load settings from arguments defined here */
	public static void loadSettings(String[] args)
		{
		if (args.length == 0)
			return;

		String[] files;

		// essentially, if the first argument is a flag
		// we parse it a certain way (arguments specified)
		int i = 0;
		while (args[i].charAt(0) == '-' && i < args.length)
			{
			if (args[i].equals("-n"))
				{
				useAP = false;
				useProbCutoff = false;
				try
					{
					mostSignificantTerms = Integer.parseInt(args[i+1]);
					i++;
					}
				catch (Exception e)
					{
					// mostSignificatTerms will stay at default
					}
				}
			else if (args[i].equals("-p") || args[i].equals("-probability"))
				{
				useAP = false;
				useProbCutoff = true;
				try
					{
					probabilityCutoff = Double.parseDouble(args[i+1]);
					i++;
					}
				catch (Exception e)
					{
					}
				}
			else if (args[i].equals("-ap") || args[i].equals("-adaptive-probability"))
				{
				useAP = true;
				}
			else if (args[i].equals("-i") || args[i].equals("-ii") || args[i].equals("-inverted-index"))
				{
				invertedIndexFilename = args[i+1];
				i++;
				}
			else if (args[i].equals("-di") || args[i].equals("-d") || args[i].equals("-document-index"))
				{
				documentIndexFilename = args[i+1];
				i++;
				}
			else if (args[i].equals("-append"))
				{
				appendToDocumentIndex = true;
				appendToInvertedIndex = true;
				}
			else if (args[i].equals("-sw"))
				{
				stopwordFilename = args[i+1];
				i++;
				}
			else if (args[i].equals("-cs"))
				{
				csCacheFilename = args[i+1];
				i++;
				}
			else if (args[i].equals("-ci"))
				{
				ciFilename = args[i+1];
				i++;
				}
			else if (args[i].equals("-t"))
				{
				NumberOfThreads = Integer.parseInt(args[i+1]);
				i++;
				}
			i++;
			}

		// if the first argument isn't a dashed kind, assume a fixed format
		if (i == 0)
			{
			invertedIndexFilename = args[0];
			documentIndexFilename = args[1];
			startOfList = 2;
			}
		else
			{
			startOfList = i;
			}
		}
	}
