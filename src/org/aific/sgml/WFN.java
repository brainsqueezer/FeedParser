package org.aific.sgml;
/** A collection of weighting functions.
 *
 * All methods are static because there is no need for an object.
 */
public class WFN
	{
	/** A fairly old weighting function, called 'max_tf' term weighting
	 * by the Project 3 page of the CMSC380 course offered by Dr. Martinovic.
	 * @param tf Term frequency - the number of times a term occurs in a document
	 * @param max_tf The maximum frequency of the most frequent term
	 * @param df Document frequency - the number of documents this term
	 * occurs in
	 * @param doclen The length, in words, of the document.
	 * @param avg_doclen The average length, in words, of all documents.
	 * @param colsize The number of documents in the collection
	 * @return A weight stored as a double.
	 */            
	public static double W1(int tf, int max_tf, int df, int doclen, int avg_doclen, int colsize)
		{
		double temp = 0;
		temp += .4 + .6 * Math.log(tf + .5) / Math.log(max_tf + 1);
		temp *= (Math.log(colsize / (double)df) / Math.log(colsize));

		return temp;
		}

	/** A variation on Okapi term weighting, as mentioned in Project 3 of the CMSC380
	 * course offered by Dr. Martinovic. Please see the other weighting function for
	 * an explanation of the return value and the parameters.
	 */                
	public static double W2(int tf, int max_tf, int df, int doclen, int avg_doclen, int colsize)
		{
		double temp = 0.4;
		temp += .6 * (tf / (tf + .5 + 1.5 * Math.log(doclen/avg_doclen))) * Math.log(colsize / df) / Math.log(colsize);

		return temp;
		}
	}
