package org.aific.sgml;

/** Auxilary class for searching. It supports sorting of documents. */
public class DocumentResult implements Comparable
	{
	/** Information about this document.
	 * Includes filename, etc.
	 */
	DocumentData doc;

	/** A double value representing how relevant this document is. */ 
	double weight;

	/** create a new DocumentResult */
	public DocumentResult()
		{
		doc = null;
		weight = 0;
		}

	/** create a new DocumentResult */
	public DocumentResult(DocumentData d, double b)
		{
		doc = d;
		weight = b;
		}

	/** allow for sorting by weight */
	public int compareTo(Object other)
		{
		// note: the value returned is not true, but it generates the sort we need
		DocumentResult o = (DocumentResult)other;
		return (o.weight == weight)?(0):((weight > o.weight)?(-1):(1));
		}

	/** allow for modular formatting */
	public String toString()
		{
		return doc.filename + ":\t" + doc.docid + "\n" + weight;
		}
	}
