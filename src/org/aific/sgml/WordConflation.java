package org.aific.sgml;
import java.io.*;
import java.util.*;

//This unit performs simple conflation on a word.
//For further information, see an LSP Manual
//(for string manipulation procedures)
//and Porter's paper on this conflation algorithm.
//It will explain the code quite well.
//By Miroslav Martinovic

/*
Keith Trnka's editing history:

Removed debugging code

*/

//History : 03/17/01 : Unit set up
// : ??/??/?? : Conflator found to be making mistakes
// : ??/??/?? : Checked against a paper stating Porter’s algorithm
// : ??/??/?? : Fixed a number of bugs, one due to mistake in original code the rest due to
// : ??/??/?? :
// : ??/??/?? : Rare bug showed up and was fixed
// : 04/10/2001 : TESTED

/** A stemmer based on Porter's algorithm.
 * @author Miroslav Martinovic
 */
public class WordConflation	{

    /** The HashSet representation of the stopword list. It takes, on average,
     * <I>O(1)</I> to search a hashtable, as opposed to O(log n) to binary search
     * an array.
     */
	private HashSet sw;	// added by KWT
	private String[]	stopWords = {	// 319 [0-318]
					"a",
					"about",
					"above",
					"across",
					"after",
					"afterwards",
					"again",
					"against",
					"all",
					"almost",
					"alone",
					"along",
					"already",
					"also",
					"although",
					"always",
					"am",
					"among",
					"amongst",
					"amoungst",
					"amount",
					"an",
					"and",
					"another",
					"any",
					"anyhow",
					"anyone",
					"anything",
					"anyway",
					"anywhere",
					"are",
					"around",
					"as",
					"at",
					"back",
					"be",
					"became",
					"because",
					"become",
					"becomes",
					"becoming",
					"been",
					"before",
					"beforehand",
					"behind",
					"being",
					"below",
					"beside",
					"besides",
					"between",
					"beyond",
					"bill",
					"both",
					"bottom",
					"but",
					"by",
					"call",
					"can",
					"cannot",
					"cant",
					"co",
					"computer",
					"con",
					"could",
					"couldnt",
					"cry",
					"de",
					"describe",
					"detail",
					"do",
					"done",
					"down",
					"due",
					"during",
					"each",
					"eg",
					"eight",
					"either",
					"eleven",
					"else",
					"elsewhere",
					"empty",
					"enough",
					"etc",
					"even",
					"ever",
					"every",
					"everyone",
					"everything",
					"everywhere",
					"except",
					"few",
					"fifteen",
					"fify",
					"fill",
					"find",
					"fire",
					"first",
					"five",
					"for",
					"former",
					"formerly",
					"forty",
					"found",
					"four",
					"from",
					"front",
					"full",
					"further",
					"get",
					"give",
					"go",
					"had",
					"has",
					"hasnt",
					"have",
					"he",
					"hence",
					"her",
					"here",
					"hereafter",
					"hereby",
					"herein",
					"hereupon",
					"hers",
					"herself",
					"him",
					"himself",
					"his",
					"how",
					"however",
					"hundred",
					"i",
					"ie",
					"if",
					"in",
					"inc",
					"indeed",
					"interest",
					"into",
					"is",
					"it",
					"its",
					"itself",
					"keep",
					"last",
					"latter",
					"latterly",
					"least",
					"less",
					"ltd",
					"made",
					"many",
					"may",
					"me",
					"meanwhile",
					"might",
					"mill",
					"mine",
					"more",
					"moreover",
					"most",
					"mostly",
					"move",
					"much",
					"must",
					"my",
					"myself",
					"name",
					"namely",
					"neither",
					"never",
					"nevertheless",
					"next",
					"nine",
					"no",
					"nobody",
					"none",
					"noone",
					"nor",
					"not",
					"nothing",
					"now",
					"nowhere",
					"of",
					"off",
					"often",
					"on",
					"once",
					"one",
					"only",
					"onto",
					"or",
					"other",
					"others",
					"otherwise",
					"our",
					"ours",
					"ourselves",
					"out",
					"over",
					"own",
					"part",
					"per",
					"perhaps",
					"please",
					"put",
					"rather",
					"re",
					"same",
					"see",
					"seem",
					"seemed",
					"seeming",
					"seems",
					"serious",
					"several",
					"she",
					"should",
					"show",
					"side",
					"since",
					"sincere",
					"six",
					"sixty",
					"so",
					"some",
					"somehow",
					"someone",
					"something",
					"sometime",
					"sometimes",
					"somewhere",
					"still",
					"such",
					"system",
					"take",
					"ten",
					"than",
					"that",
					"the",
					"their",
					"them",
					"themselves",
					"then",
					"thence",
					"there",
					"thereafter",
					"thereby",
					"therefore",
					"therein",
					"thereupon",
					"these",
					"they",
					"thick",
					"thin",
					"third",
					"this",
					"those",
					"though",
					"three",
					"through",
					"throughout",
					"thru",
					"thus",
					"to",
					"together",
					"too",
					"top",
					"toward",
					"towards",
					"twelve",
					"twenty",
					"two",
					"un",
					"under",
					"until",
					"up",
					"upon",
					"us",
					"very",
					"via",
					"was",
					"we",
					"well",
					"were",
					"what",
					"whatever",
					"when",
					"whence",
					"whenever",
					"where",
					"whereafter",
					"whereas",
					"whereby",
					"wherein",
					"whereupon",
					"wherever",
					"whether",
					"which",
					"while",
					"whither",
					"who",
					"whoever",
					"whole",
					"whom",
					"whose",
					"why",
					"will",
					"with",
					"within",
					"without",
					"would",
					"yet",
					"you",
					"your",
					"yours",
					"yourself",
					"yourselves"
					};

	private String[] prefixes = 	{ // 9 [0-8]
					"kilo",
					"micro",
					"milli",
					"intra",
					"ultra",
					"mega",
					"nano",
					"pico",
					"pseudo"
					};

	private String[][] suffixes2 = 	{ // 22 X 2 [0-21, 0-1]
					{"ational","ate"},
					{"tional","tion"},
					{"enci","ence"},
					{"anci","ance"},
					{"izer","ize"},
					{"iser","ize"},
					{"abli","able"},
					{"alli","al"},
					{"entli","ent"},
					{"eli","e"},
					{"ousli","ous"},
					{"ization","ize"},
					{"isation","ize"},
					{"ation","ate"},
					{"ator","ate"},
					{"alism","al"},
					{"iveness","ive"},
					{"fulness","ful"},
					{"ousness","ous"},
					{"aliti","al"},
					{"iviti","ive"},
					{"biliti","ble"}
					};

	private String[][] suffixes3 = 	{ // 8 X 2 [0-7, 0-1]
					{"icate","ic"},
					{"ative",""},
					{"alize","al"},
					{"alise","al"},
					{"iciti","ic"},
					{"ical","ic"},
					{"ful",""},
					{"ness",""}
					};

	private String[] suffixes4 = 	{ // 20 [0-19]
					"al",
					"ance",
					"ence",
					"er",
					"ic",
					"able",
					"ible",
					"ant",
					"ement",
					"ment",
					"ent",
					"ion",
					"ou",
					"ism",
					"ate",
					"iti",
					"ous",
					"ive",
					"ize",
					"ise"
					};


	// Cleaning up the word of characters
	// which are not letters or digits
	// TESTED
	private String clean (String kwd) {

		String newKwd="";
		int kwdLen = kwd.length();
		char charAtI;

		for (int i=0; i<kwdLen; i++) {
			charAtI = kwd.charAt(i);
			if (Character.isLetterOrDigit(charAtI))
				newKwd += charAtI;
		}

		return newKwd;

	} // clean

	private boolean isStopWord(String word)
		{
		return sw.contains(word);
		}

	private boolean isStopWord (int bottom, int top, String word) {

		int 		mid;
		String		midWord;

		if (bottom > top)
			return false;
		else {
			mid 	= (top - bottom) / 2 + bottom;
			midWord	= stopWords[mid];
			int compWords = word.compareTo(midWord);
			if( compWords < 0) {
				return isStopWord(bottom, mid - 1, word);
			} else if ( compWords > 0) {
				return isStopWord(mid + 1, top, word);
			} else
				return true;
		}

	}


	// Cleaning up the word of prefixes
	// TESTED
	private String stripPrefixes (String word) {

		int prefixLength;
		int wordLength;


		for (int i=0; i<prefixes.length; i++) {

			prefixLength = prefixes[i].length();
			wordLength = word.length();

			if (wordLength >= prefixLength) {
				String wordPrefix = word.substring(0,prefixLength);
				if (wordPrefix.equalsIgnoreCase(prefixes[i])) {
					word = word.substring(prefixLength, wordLength);
					break;
				}
			}

		}

		return word;

	}


	// Checking if the given character is a vowel
	// TESTED
	private boolean isVowel (char ch, char prevCh) {

		switch (ch) {

			case 'a' : case 'A' :
			case 'e' : case 'E' :
			case 'i' : case 'I' :
			case 'o' : case 'O' :
			case 'u' : case 'U' :	return true;
			case 'y' : case 'Y' :	return !(isVowel(prevCh,'?'));
			default :		return false;

		}

	}


	// Checked against the Pascal version
	// TESTED as accurate translation
	private boolean cvc (String strng) {

		int len;
		char l;

		len = strng.length();

		if (len < 3)
			return false;
		else if ( !(isVowel(strng.charAt(len-1),strng.charAt(len-2))) &&
			!(strng.charAt(len-1)=='w' || strng.charAt(len-1)=='x' ||
strng.charAt(len-1)=='y') &&
			(isVowel(strng.charAt(len-2), strng.charAt(len-3)) )) {
			if (len == 3)
				if (! (isVowel(strng.charAt(0), '?')))
					return true;
				else
					return false;
			else
				if (! ( isVowel( strng.charAt(len-3), strng.charAt(len-4) ) ))
					return true;
				else
					return false;
		} else
			return false;

	}


	// TESTED on the Porter's algorithm's examples
	private int measure(String stem) {

		int i = 0, count = 0, len = stem.length();

		while (i < len) {

			while (i < len) {
				if (i>0) {
					if (isVowel(stem.charAt(i), stem.charAt(i-1)))
						break;
				} else if (isVowel(stem.charAt(i), '?'))
						break;

				i++;
			}


			// Check Point 1

			i++;

			while (i < len) {
				if (i>0) {
					if (!(isVowel(stem.charAt(i), stem.charAt(i-1))))
						break;
				} else if ( ! ( isVowel( stem.charAt(i), '?' ) ) )
					break;

				i++;
			}

			// Check Point 2

			if (i < len) {
				count++;
				i++;
			}

		}

		return count;

	}


	// TESTED
	private boolean containsVowel (String word) {

		// System.out.println ("In containsVowel. Gotten word = " + word + " .\n");

		if (word == null) return false;

		int i = 0, wordLen = word.length();
		boolean stop = false;

		while ( (! stop) && (i < wordLen) ) {

			if (i>0) {
				if (isVowel(word.charAt(i), word.charAt(i-1)))
					stop = true;
			} else if (isVowel(word.charAt(i),'?'))
				stop = true;

			i++;

		}

		return stop;

	}

	// A kind of TESTED
	private String hasSuffix(String word, String suffix) { // returns the stem

		int wordLen = word.length(), suffLen = suffix.length();

		if (wordLen < suffLen)
			return null; // OR MAYBE return word - THIS NEEDS TO BE CHECKED

		if (suffLen > 1)
			if (word.charAt(wordLen-2) != suffix.charAt(suffLen-2))
				return null; // OR MAYBE return word - THIS NEEDS TO BE CHECKED

		String stem = word.substring(0, wordLen - suffLen);

		if (stem.concat(suffix).equalsIgnoreCase(word)) {
			// System.out.println("\t\t\t\tReturned stem = " + stem );
			return stem;
		} else
			return null; // OR MAYBE return word - THIS NEEDS TO BE CHECKED

	}


	// TESTED on examples from Porter's paper - SUCCESS
	private String step1 ( String strng) {

		String stem;
		int len = strng.length();

		// Beggining of Step 1a

		if (strng.charAt(len-1) == 's') {
			stem = hasSuffix(strng, "sses");
			if (stem != null)
				strng = strng.substring(0, len-2);
			else {
				stem = hasSuffix(strng, "ies");
				if (stem != null)
					strng = strng.substring(0, len-2);
				else {
					if (strng.charAt(len-2) != 's') // if it's an 's', lob it off
						strng = strng.substring(0,len-1);
				}
			}
		}

		// End of Step 1a


		// Beggining of Step 1b

		stem = hasSuffix(strng, "eed");
		if (stem != null) {
			if (measure(stem) > 0) {
				strng = strng.substring(0, len-1);
			}
		} else {

			String stemEd = hasSuffix(strng, "ed");
			String stemIng = hasSuffix(strng, "ing");
			String stemAt;
			String stemBl;
			String stemIz;
			if ((stemEd != null) && (containsVowel(stemEd))) {
				strng = strng.substring(0, stemEd.length());
				stemAt = hasSuffix(strng, "at");
				stemBl = hasSuffix(strng, "bl");
				stemIz = hasSuffix(strng, "iz");
				if ( 	(stemAt != null) ||
					(stemBl != null) ||
					(stemIz != null) )
					strng = strng + "e";
				else {
					len = strng.length();
					if (len > 1) {
						if ( 	(strng.charAt(len-1) == strng.charAt(len-2)) &&
							(strng.charAt(len-1) != 'l') &&
						 	(strng.charAt(len-1) != 's') &&
						 	(strng.charAt(len-1) != 'z') )
							strng = strng.substring(0,len-1);
						else if (measure(strng) == 1)
							if (cvc(strng))
								strng = strng + "e";
					}
				}

			} else if ((containsVowel(stemIng)) && (stemIng != null))  {
				// System.out.println ("\t\t\t\tstemIng = " + stemIng + ".\n");
				strng = strng.substring(0, stemIng.length());
				// System.out.println ("\t\t\t\tstrng = " + strng + ".\n");
				stemAt = hasSuffix(strng, "at");
				stemBl = hasSuffix(strng, "bl");
				stemIz = hasSuffix(strng, "iz");
				if ( 	(stemAt != null) ||
					(stemBl != null) ||
					(stemIz != null) )
					strng = strng + "e";
				else {
					len = strng.length();
					if (len > 1) {
						if ( 	(strng.charAt(len-1) == strng.charAt(len-2)) &&
							(strng.charAt(len-1) != 'l') &&
						 	(strng.charAt(len-1) != 's') &&
						 	(strng.charAt(len-1) != 'z') )
							strng = strng.substring(0,len-1);
						else if (measure(strng) == 1)
							if (cvc(strng))
								strng = strng + "e";
					}
				}

			}

		}

		// End of Step 1b


		// Beggining of Step 1c

		stem = hasSuffix(strng, "y");
		if (stem != null)
			if (containsVowel(stem)) {
				len = strng.length();
				strng = strng.substring(0,len-1) + "i";
			}

		// End of Step 1c

		return strng;

	}



	private String step2 (String strng) {

		String stem;

		int index;

		for (index=0; index < suffixes2.length; index++) {
			stem = hasSuffix(strng, suffixes2[index][0]);
			if (stem != null)
				if (measure(stem) > 0) {
					strng = stem + suffixes2[index][1];
					break;
				}
		}

		return strng;


	}



	private String step3 (String strng) {

		String stem;

		int index;

		for (index=0; index < suffixes3.length; index++) {
			stem = hasSuffix(strng, suffixes3[index][0]);
			if (stem != null)
				if (measure(stem) > 0) {
					strng = stem + suffixes3[index][1];
					break;
				}
		}

		return strng;

	}



	private String step4 (String strng) {

		String stem;

		int index;


		for (index=0; index < 11; index++) {
			stem = hasSuffix(strng, suffixes4[index]);
			if (stem != null)
				if (measure(stem) > 1) {
					strng = stem;
					return strng;
				}
		}

		stem = hasSuffix(strng, "ion");
		if (stem != null)
			if (measure(stem) > 1)
				if (	stem.endsWith("s") ||
					stem.endsWith("S") ||
					stem.endsWith("t") ||
					stem.endsWith("T")	) {
						strng = stem;
						return strng;
				}

		for (index=12; index < suffixes4.length; index++) {
			stem = hasSuffix(strng, suffixes4[index]);
			if (stem != null)
				if (measure(stem) > 1) {
					strng = stem;
					return strng;
				}
		}

		return strng;

	}



	private String step5 (String strng) {

		String stem;
		int len = strng.length();

		// Beggining of Step 5a

		if (! strng.endsWith("ee") )
			if (strng.charAt(len-1) == 'e')
				if (measure(strng) > 1)
					strng = strng.substring(0,len-1);
				else if (measure(strng) == 1) {
					stem = strng.substring(0,len-1);
					if (! cvc(stem))
						strng = strng.substring(0, len-1);
				}

		// End of Step 5a


		// Beggining of Step 5b

		if ( (strng.endsWith("ll")) && (measure(strng) > 1) )
		   	strng = strng.substring(0, len-1);

		// End of Step 5b

		return strng;

	}


	private String stripSuffixes (String strng) {

		strng = step1(strng);

//		System.out.println ("\t\t\t\tOut of step1. strng = " + strng + ".\n" );

		strng = step2(strng);

//		System.out.println ("\t\t\t\tOut of step2. strng = " + strng + ".\n" );

		strng = step3(strng);

//		System.out.println ("\t\t\t\tOut of step3. strng = " + strng + ".\n" );

		strng = step4(strng);

//		System.out.println ("\t\t\t\tOut of step4. strng = " + strng + ".\n" );

		strng = step5(strng);

//		System.out.println ("\t\t\t\tOut of step5. strng = " + strng + ".\n" );

		return strng;

	}



	public String stripAffixes (String strng) {

		strng = clean(strng.toLowerCase());

		if ( (! strng.equals("")) && (strng.length() > 2) )
//			if (! isStopWord(0, stopWords.length-1, strng)) {
			if (! isStopWord(strng)) {
				strng = stripPrefixes(strng);
				if (! strng.equals(""))
					strng = stripSuffixes(strng);
			} else
				strng = "";
		else
			strng = "";

		return strng;

	}



	public static void main (String[] args) throws IOException {

		WordConflation w = new WordConflation();

		BufferedReader stdin = new BufferedReader 	(
						new InputStreamReader	(
							System.in 	)
								);
		System.out.println ( " Please, enter a string to be processed : ");

		String strng = stdin.readLine();

		//System.out.println ( " Word \"ceasate\" has a measure of " + w.measure("ceasate") + ".\n" );

//		if (w.cvc("fil"))
//			System.out.println ( " Word \"fil\" is a cvc word.\n");
//		else
//			System.out.println ( " Word \"fil\" is not a cvc word.\n" );


//		if (w.hasSuffix(strng, "eed") != null)
//			System.out.println ( " Word \"" + strng + "\" has suffix \"eed\".\n" );
//		else
//			System.out.println ( " Word \"" + strng + "\" doesn't have suffix \"eed\".\n" );

		System.out.println ( " Word \"" + strng + "\" conflated to " + w.stripAffixes(strng) + ".\n" );


	}

	public WordConflation()
		{
		// constructor: build the HashSet
		sw = new HashSet(stopWords.length);
		for (int i = 0; i < stopWords.length; i++)
			{
			sw.add(stopWords[i]);
			}
		}

}

