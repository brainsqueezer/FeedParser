package org.aific.sgml;
import java.util.*;
import java.io.*;

/** DatabaseStatistics represents statistics on a collection of
 * Documents. It contains the statistics for each individual Document
 * as well as statistics on the entire collection.
 */
public class DatabaseStatistics extends DocumentStatistics
	{
	/** The collection of DocumentStatistics, indexed by DOCID. */
	HashMap documents;

	/** The current DocumentStatistics to register words with */
	DocumentStatistics currentDocument;

	/** Initialize all members. */        
	public DatabaseStatistics()
		{
		frequencyList = new HashMap();
		words = 0;
		documents = new HashMap();
		}

  /**
   * register
   *
   * Record the occurrance of this word (stemmed) into the statistics.
   * The word is also registered with it's respective DocumentStatistics object.
   * No longer used.
   * @param docid the unique ID of the document being logged
   * @param word the stemmed word to register into the statistics
   */
	public void register(String docid, String word)
		{
		// save some memory by ignoring this
		// but it skews the avgdoclen
		// so we have to correct for that by incrementing words
		//super.register(word);
		words++;

		DocumentStatistics x = (DocumentStatistics)documents.get(docid);
		x.register(word);
		}

	/** register the occurrance of this stem for the current document */
	public void register(String word)
		{
		words++;
		currentDocument.register(word);
		}

	/** set the current document */
	public void setCurrentDocument(String docid)
		{
		currentDocument = (DocumentStatistics)documents.get(docid);
		}

	/** unset the current document */
	public void unsetCurrentDocument()
		{
		currentDocument = null;
		}

	/** Sets up the Hashtable so that there is no conditional code when
	 * each word is registered.
	 */                
	public void registerDocument(String docid, String filename)
		{
		DocumentStatistics x = new DocumentStatistics(filename);
		x.docid = docid;
		documents.put(docid, x);
		}

	/** Sets up the Hashtable so that there's less conditional code when
	 * registering each word.
	 */                
	public void registerDocument(String docid)
		{
		registerDocument(docid, "");
		}

	/**
	* getDocumentStatistics
	*
	* Gets the specific statistics for the document specified by the docid.
	* @param docid the unique identifier of the Document
	* @returns the object with the specific statistics for the specified document
	*/
	public DocumentStatistics getDocumentStatistics(String docid)
		{
		return (DocumentStatistics)documents.get(docid);
		}

	/** Get a list of DOCID in random order. */
	public Iterator getDocumentList()
		{
		return documents.keySet().iterator();
		}
	}
