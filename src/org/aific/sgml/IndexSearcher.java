package org.aific.sgml;
import java.util.*;

/** IndexSearcher provides a means of searching a collection of Documents
 * given an InvertedIndex and a DocumentIndex. It is separated
 * from Query for modular searching.
 * @see InvertedIndex
 * @see DocumentIndex
 */
public class IndexSearcher 
	{
	/** the InvertedIndex */
	InvertedIndex index;

	/** the DocumentIndex */
	DocumentIndex docIndex;

	/** create a new searcher */
	public IndexSearcher(InvertedIndex index, DocumentIndex docIndex)
		{
		this.index = index;
		this.docIndex = docIndex;
		}

	/** Search with no restriction on maximum number of document matches. */
	public DocumentResult[] search(String[] termVector)
		{
		return search(termVector, Integer.MAX_VALUE);
		}

	/** Search with a maximum number of results. */
	public DocumentResult[] search(String[] termVector, int maxResults)
		{
		HashMap docs;		// the Hashtable linking docid to tf
		Iterator docList;	// the list of documents for the current term

		Hashtable scores = new Hashtable();	// this is where scores (weights) are stored (docid -> double[1])

		String docid;		// temporary variable for document id
		DocumentData data;	// temporary variable for document data (doclen, max_tf)
		double[] wArray;	// temporary variable for the contents of scores

		// temporary variables to make reading code easier
		// they all are the current values
		int tf = 0,
			max_tf,
			df,
			doclen,
			collectionsize = docIndex.size(),
			avgdoclen = docIndex.avg_doclen();

		// loop through all term vectors and assign weights
		for (int i = 0; i < termVector.length; i++)
			{
			// get the Hashtable of all documents containing termVector[i] and their respective term frequencies
			docs = index.documentsContaining(termVector[i]);

			// if the term is not in the database, skip it
			if (docs == null)
				continue;

			// get an iterator for the documents containing this term
			docList = docs.keySet().iterator();

			// define the number of documents this term occurs in
			df = docs.size();

			// loop through all documents containing this term
			while (docList.hasNext())
				{
				// get the next document id
				docid = (String)docList.next();

				// load the term frequency
				tf = ((int[])docs.get(docid))[0];

				// get data about the document from the document index
				data = docIndex.getDocumentData(docid);
				max_tf = data.max_tf;
				doclen = data.doclen;

				// load the value for the scores hashtable or create a value for it
				wArray = (double[])scores.get(docid);
				if (wArray == null)
					{
					wArray = new double[1];
					wArray[0] = 0;
					scores.put(docid, wArray);
					}

				// add the weight from this term into the existing sum of weights
				wArray[0] += WFN.W1(tf, max_tf, df, doclen, avgdoclen, collectionsize);
				}
			}

		// second, we fill up the SDPair array to be sorted
		DocumentResult[] results = new DocumentResult[scores.size()];
		int i = 0;

		docList = scores.keySet().iterator();
		while (docList.hasNext())
			{
			docid = (String)docList.next();
			results[i++] = new DocumentResult(docIndex.getDocumentData(docid), ((double[])scores.get(docid))[0]);
			}

		// third, we create some data structure that has them sorted by weight
		Arrays.sort(results);

		// lastly, handle the maxResults thing
		if (maxResults < results.length)
			{
			DocumentResult[] newResults = new DocumentResult[maxResults];
			for (i = 0; i < newResults.length; i++)
				{
				newResults[i] = results[i];
				}
			results = newResults;
			}

		return results;
		}

	}
