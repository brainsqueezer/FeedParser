package org.aific.sgml;
import java.util.*;

/** Represents a Document that has only been read from a file.
 * This says nothing about the contents of the document, except what they are.
 */
public class Document
    {
	/** The contents of DOCNO */
    String docno;

	/** A unique identifier for this document. */
    String id;

	/** The date field of this document. */
    String date;

	/** The title of this document. */
    String title;

	/** The body of the document, with any SGML tags stripped out. */
    String body;

	/** Other tags which are not explicitly stored. */
    Hashtable other;

    /** The file in which this Document is located. */
    String filename;
    }
