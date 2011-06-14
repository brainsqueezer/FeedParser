package org.aific.sgml;

/** An auxillary class for DocumentIndex */
public class DocumentData 
	{
	/** The number of times the most frequent term occurs in
	 * this document.
	 */
	int max_tf;

	/** The length, in words (including stop words), of this document. */
	int doclen;
	
	/** The file in which this document is located. */
	String filename;
	
	/** A unique identifier for this Document.
	 * Stored for reduncancy, because the ID is the key for
	 * the HashMap in which all of these Objects are stored.
	 */
	String docid;
	}
