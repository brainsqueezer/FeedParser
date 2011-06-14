package org.aific.sgml;
import java.io.*;
import java.util.*;

/** This represents an index of documents. The index contains such
 * information as max_tf, doclen, and filename. Note that these are
 * all of the members of DocumentData.
 */
public class DocumentIndex implements Index
	{
	/** The internal representation of the index.
	 * This maps a document ID to a DocumentData Object,
	 * in which all data about the Document is stored.
	 */
	private HashMap index;

	/** The average Document length, in words. */
	private int avgdoclen;

	/** create a new document index */
	public DocumentIndex()
		{
		index = new HashMap();
		}

	/** Build the document statistics based on statistics
	 * collected on a database
	 */
	public void build(DatabaseStatistics s)
		{
		Iterator e = s.getDocumentList();

		DocumentStatistics d;
		DocumentData data;
		String docid;
		avgdoclen = s.words/s.documents.size();

		while (e.hasNext())
			{
			docid = (String)e.next();
			d = s.getDocumentStatistics(docid);

			data = new DocumentData();
			data.max_tf = d.max_tf();
			data.doclen = d.words;
			data.filename = d.filename;
			data.docid = docid;

			register(docid, data);
			}
		}

	/** register the occurrance of the given document id and data */
	public synchronized void register(String docid, DocumentData data)
		{
		index.put(docid, data);
		}

	/** Store this index using it's own file format. */
	public void store(String filename) throws IOException
		{
		// open the output file
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));

		// first, we're going to store all filenames in the beginning
		// to essentially compress this index

		// get an iterator over the document IDs
		Iterator i = index.keySet().iterator();

		// these two are used in compression
		HashMap fileIDHash = new HashMap();
		int fileID = 0;

		// these two are used in looping
		String docid;
		DocumentData data;
		String outputHolder = "";

		while (i.hasNext())
			{
			docid = (String)i.next();
			data = (DocumentData)index.get(docid);

			if (!fileIDHash.containsKey(data.filename))
				{
				outputHolder += data.filename + "\n";
				fileIDHash.put(data.filename, new Integer(fileID));
				fileID++;
				}
			}
		out.print(fileID + "\n" + outputHolder);

		// reset the iterator
		i = index.keySet().iterator();

		out.println(avgdoclen);
		out.println(index.size());

		while (i.hasNext())
			{
			docid = (String)i.next();
			data = (DocumentData)index.get(docid);

			out.println(docid + " " + data.max_tf + " " + data.doclen + " " + fileIDHash.get(data.filename));
			}

		out.flush();
		out.close();
		}

	/** Load the index from the file
	 * based on the same format used in store(String).
	 */
	public void load(String filename) throws IOException
		{
		BufferedReader r = new BufferedReader(new FileReader(filename));

		// these are used in decompression
		int fileID = 0;
		int numberOfFiles = Integer.parseInt(r.readLine());
		String[] filenames = new String[numberOfFiles];

		for (fileID = 0; fileID < numberOfFiles; fileID++)
			{
			filenames[fileID] = r.readLine();
			}		

		avgdoclen = Integer.parseInt(r.readLine());

		int size = Integer.parseInt(r.readLine());

		// we divide by .75 because that's the default load factor
		index = new HashMap((int)(size / .75) + 1);

		DocumentData docData;
		StringTokenizer tokens;
		String	docid,
				line;

		while ((line = r.readLine()) != null)
			{
			tokens = new StringTokenizer(line);

			docid = tokens.nextToken();

			docData = new DocumentData();
			docData.max_tf = Integer.parseInt(tokens.nextToken());
			docData.doclen = Integer.parseInt(tokens.nextToken());
			//docData.filename = tokens.nextToken();
			fileID = Integer.parseInt(tokens.nextToken());
			docData.filename = filenames[fileID];
			docData.docid = docid;

			index.put(docid, docData);
			}

		r.close();
		}

	/** Retrieve the data about a Document using an ID. */
	public DocumentData getDocumentData(String docid)
		{
		return (DocumentData)index.get(docid);
		}

	/** Get the average document length. */
	public int avg_doclen()
		{
		return avgdoclen;
		}

	/** Get a list of document identifiers. */
	public Iterator documents()
		{
		return index.keySet().iterator();
		}

	/** The number of documents. */
	public int size()
		{
		return index.size();
		}
	}
