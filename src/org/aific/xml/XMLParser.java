package org.aific.xml;


/**
 * Simple XML SAX Parser
 * @author Ramon Antonio Parada <rap@ramonantonio.net>
 *
 */
public class XMLParser {
    private XMLParserListener xmlParserListener;
    private String xml;
    public boolean debug = false;

    /**
     * Creates a new instance of the XML Parser
     * @param xml Text to be parsed
     */
    public XMLParser(String xml) {
    	this.xml = xml;
    }

    /**
     * Set a listener to process it
     * @param xmlPInt
     */
    public void setListener(XMLParserListener xmlPInt) {
        xmlParserListener = xmlPInt;
    }

    private String removeText(String xml, String text) {
        String tempXml;
        for(tempXml = xml;
        tempXml.indexOf(text) >= 0;
        tempXml = tempXml.substring(0, tempXml.indexOf(text)) + tempXml.substring(tempXml.indexOf(text) + text.length(), tempXml.length()));
        return tempXml;
    }

    public void parse() {
        xml = removeText(xml, "<![CDATA[");
        xml = removeText(xml, "]]>");
        boolean ended = false;
        boolean finishedNameProp = false;
        String token = "";
        String textValue = "";
        String justStarted = "";
        String c = "";
        String propName = "";
        String propValue = "";
        int i = 0;
        int status = 0;
        int beforeStatus = 0;
        while(!ended) {
            c = xml.substring(i, i + 1);
            beforeStatus = status;
            if (debug)
            	System.out.println(status);
            if (c.equals("<")) {
                status = 1;
                token = "";
            } else
            if (c.equals(">")) {
                if (beforeStatus == 4 && !token.startsWith("xml")) {
                    text(textValue);
                    textValue = "";
                    endTag(token);
                    justStarted = "";
                    status = 6;
                } else
                if (beforeStatus == 1 || beforeStatus == 7) {
                    if(justStarted != token) {
                        startTag(token);
                        textValue = "";
                        status = 2;
                        justStarted = token;
                    }
                    if(beforeStatus == 7) {
                        property(propName, propValue);
                        status = 2;
                    }
                }
                textValue = "";
            } else
            if (c.equals(" ") && (status == 1 || status == 7)) {
                finishedNameProp = false;
                if(status == 1) {
                    startTag(token);
                    justStarted = token;
                } else {
                    property(propName, propValue.replace("\"", ""));
                }
                propName = "";
                propValue = "";
                status = 7;
            } else
            if((c.equals("/") || c.equals("?")) && status == 1) {
                status = 4;
                if(token != null && token.length() != 0)
                    startTag(token);
            } else
            if(c.equals("=") && status == 7) {
                textValue = "";
                propValue = "";
                finishedNameProp = true;
            } else
            if(status != 7) {
                token = token + c;
                if(status == 2)
                    textValue = textValue + c;
            } else
            if(status == 7) {
                if(!finishedNameProp)
                    propName = propName + c;
                propValue = propValue + c;
            }
            if(++i >= xml.length())
                ended = true;
        }
    }

    public void startTag(String tagName) {
        xmlParserListener.startTag(tagName);
    }

    public void text(String text) {
        if(text != null && text != "")
            xmlParserListener.text(text);
    }

    public void endTag(String tagName) {
        xmlParserListener.endTag(tagName);
    }

    public void property(String pName, String pValue) {
        xmlParserListener.property(pName, pValue.replace("\"", ""));
    }

}
