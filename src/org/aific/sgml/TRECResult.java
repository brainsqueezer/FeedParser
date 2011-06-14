package org.aific.sgml;
import java.util.*;

/** DocumentResults with different ordereing and different display */
public class TRECResult extends DocumentResult
	{
	/** create a new TRECResult */
	public TRECResult(DocumentData d, double b)
		{
		doc = d;
		weight = b;
		}

	/** override the comparison operator to sort differently */
	public int compareTo(Object other)
		{
		return this.doc.docid.compareTo(((TRECResult)other).doc.docid);
		}

	/** display results differently */
	public String toString()
		{
		return "0 " + doc.docid + " " + ((isRelevant())?("1"):("0"));
		}

	/** allow for a decision of relevance */
	private boolean isRelevant()
		{
		return (weight > .5);
		}

	/** convert a list of documentresults into a list of trecresults */
	public static TRECResult[] convert(DocumentResult[] results)
		{
		TRECResult[] returnValue = new TRECResult[results.length];
		for (int i = 0; i < results.length; i++)
			{
			returnValue[i] = new TRECResult(results[i].doc, results[i].weight);
			}
		Arrays.sort(returnValue);
		return returnValue;
		}
	}
