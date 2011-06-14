package org.aific.sgml;
import java.util.*;

/** A recursive class representing an SGML tag.
 */
public class SGMLTag
	{
	/** The name of the tag.
	 * For example, "NACHO" is the name of the tag is this string:
	 * <PRE><NACHO>Hi Mom</NACHO></PRE>
	 */            
	String tagname;

	/** the character index of the beginning of the body of the tag.
	 * This can be thought of as the offset from the beginning of
	 * the file to just after the opening tag.
	 */        
	int		a;

	/** the character index of the end of the body of the tag.
	 * This can also be considered as the offest from the beginning
	 * of the file of the character before the closing tag begins.
	 */        
	int		b;			

	/** all nested tags are stored in this Hashtable.
	 * This means that there cannot be multiple tags of the
	 * same name nested and used.
	 */        
	Hashtable<String, SGMLTag> children;
	}




