package org.aific.sgml;
import java.io.*;


/** BuildMP is driver code for the multithreaded backend of this statistical
 * IR system. It makes the appropriate calls to load the database,
 * collect word frequencies, and store indices. The class {@link BuildThread}
 * contains all the real work, because that is the parallel part of the code.
 * <P>It can run like so (using Java 1.4):<br>
 * <I>java -server -Xms1g -Xmx1g BuildMP invertedIndex documentIndex [files]</I><br>
 * <I>invertedIndex</I>: where to store the inverted index<br>
 * <I>documentIndex</I>: where to store the document index<br>
 * <I>[files]</I>: there can be as many files in this database as you want (at least one)
 * <P>There is a more complicated form to run the backend using a notation like:<BR>
 * <I>java -server -Xms1g -Xmx1g BuildMP -n 5 -t 3 -ii invertedIndex -di documentIndex [files]</I><br>
 * <I>-n 5</I>: use the standard algorithm to use the five most frequent terms in a document for index creation. 
 * In this situation, 5 terms will be used<br>
 * <I>-t 3</I>: specify the number of threads to use in processing.
 * In this situation, three threads will be used (not including main thread)<P>
 * Refer to {@link BuildSettings} for details regarding arguments.
 * @version 0.9
 * @author Keith W. Trnka
 * @see BuildThread
 * @see BuildSettings
 */
public class BuildMP
	{
	/** Used for debugging purposes. Specifically, used to find out how the
	 * current application is using memory resources.
	 * @return A String describing total JRE memory and free JRE memory.
	 */
	public static String memMsg()
		{
		return "Total memory: " + Runtime.getRuntime().totalMemory() + " Free memory: " + Runtime.getRuntime().freeMemory();
		}

	/** Driver code to build the indices. This code represents the backend of
	 * the IR system.
	 */
	public static void main(String[] args)
		{
		// check if there are enough arguments
		if (args.length >= 3)
			{
			// load all arguments
			BuildSettings.loadSettings(args);

			// define variables to print statistics
			long time = System.currentTimeMillis(),
				threadTime = 0,
				startupTime = System.currentTimeMillis(),
				storeTime = 0;

			// define the indices
			InvertedIndex invInd = new InvertedIndex();
			DocumentIndex docInd = new DocumentIndex();

			// optionally append to the inverted index
			if (BuildSettings.appendToInvertedIndex)
				{
				try
					{
					invInd.load(BuildSettings.invertedIndexFilename);
					}
				catch (IOException e)
					{
					System.out.println("Error loading file '" + BuildSettings.invertedIndexFilename + "'");
					}
				}

			// optionally append to the document index
			if (BuildSettings.appendToDocumentIndex)
				{
				try
					{
					docInd.load(BuildSettings.documentIndexFilename);
					}
				catch (IOException e)
					{
					System.out.println("Error loading file '" + BuildSettings.documentIndexFilename + "'");
					}
				}

			// number of threads
			int threads = BuildSettings.NumberOfThreads;
			// the threads
			BuildThread[] threadGroup = new BuildThread[threads];

			// the beginning of the list of files
			int s = BuildSettings.startOfList;
			// the total number of files
			int numFiles = args.length - s;
			// the rounded number of files per thread (used for (n-1) threads
			int filesPerThread = (int)Math.round(numFiles / (double)threads);

			// this will contain all of the files to be assigned to a given thread
			String[] stripe;

			// handle the n-1 threads
			for (int i = 0; i < threads - 1; i++)
				{
				stripe = new String[filesPerThread];
				for (int j = i * filesPerThread; j < (i+1) * filesPerThread; j++)
					{
					stripe[j - i * filesPerThread] = args[j + s];
					}
				// create the new thread with it's files
				threadGroup[i] = new BuildThread(stripe);
				}
				
			// give leftovers to the last thread
			// temp is the starting point within the list of files
			int temp = (threads - 1) * filesPerThread;
			stripe = new String[numFiles - temp];
			for (int j = temp; j < numFiles; j++)
				{
				stripe[j - temp] = args[j + s];
				}
			// create the final thread
			threadGroup[threads - 1] = new BuildThread(stripe);

			// store the time it took to divvy up the work
			startupTime = System.currentTimeMillis() - startupTime;

			// initialize the time it takes for threads to complete
			threadTime = System.currentTimeMillis();

			// minimize the priority of this thread for now
			// because we don't want CPU time in this thread
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

			// loop through and start all of the threads
			for (int i = 0; i < threads; i++)
				{
				threadGroup[i].invInd = invInd;		// assign the inverted index
				threadGroup[i].docInd = docInd;		// assign the document index

				// maximize its priority so that it gets even more CPU
				// than this thread
				threadGroup[i].setPriority(Thread.MAX_PRIORITY);

				// start this thread
				threadGroup[i].start();
				}

			for (int i = 0; i < threads; i++)
				{
				// yield() means preempt yourself
				// in more words, give the CPU to someone else while there is
				// someone else
				while (threadGroup[i].isAlive())
					Thread.currentThread().yield();
				}

			// compute the time it took for all threads to finish
			threadTime = System.currentTimeMillis() - threadTime;

			// store the indices
			// time it takes to store the files
			storeTime = System.currentTimeMillis();
			try
				{
				invInd.store(BuildSettings.invertedIndexFilename);
				}
			catch (IOException e)
				{
				System.out.println("Error storing file '" + BuildSettings.invertedIndexFilename + "'");
				}

			try
				{
				docInd.store(BuildSettings.documentIndexFilename);
				}
			catch (IOException e)
				{
				System.out.println("Error storing file '" + BuildSettings.documentIndexFilename + "'");
				}
			// compute the time it took to store the indices
			storeTime = System.currentTimeMillis() - storeTime;

			// compute total execution time
			time = System.currentTimeMillis() - time;
			// show performance computations
			if (BuildSettings.printTimes)
				{
				System.out.println("Thread setup %:\t" + (startupTime / (double) time));
				System.out.println("Parallel execution %:\t" + (threadTime / (double)time));
				System.out.println("Index storage %:\t" + (storeTime / (double)time));
				System.out.println("Total exectution time:\t" + time);
				}
			}
		else
			{
			System.out.println("Usage:\njava Build invertedindexoutput documentindexoutput inputfile1 inputfile2 ...");
			}
		}
	}
