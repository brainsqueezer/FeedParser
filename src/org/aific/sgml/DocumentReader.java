package org.aific.sgml;

/** An interface between the DocumentAnalyzer and the file. */
public interface DocumentReader
    {
	/**
	* Read a single Document from the specified file.
	* @returns The next Document if there are more, or null if there are not.
	*/
    public Document readDocument();
    }
