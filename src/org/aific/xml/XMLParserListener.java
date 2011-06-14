
package org.aific.xml;

/**
 * Simple XML SAX Parser
 * @author Ramon Antonio Parada <rap@ramonantonio.net>
 *
 */
public interface XMLParserListener {

    public abstract void startTag(String s);

    public abstract void text(String s);

    public abstract void endTag(String s);

    public abstract void property(String s, String s1);
}
