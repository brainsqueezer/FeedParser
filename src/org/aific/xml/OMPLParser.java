package org.aific.xml;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Vector;
import org.aific.xml.XMLParserListener;


/**
 * Simple XML SAX Parser
 * @author Ramon Antonio Parada <rap@ramonantonio.net>
 * @version 0.1
 */
public class OMPLParser implements XMLParserListener {

	public Vector<URL> bloglist;
	//private int tab=0;
	

	
	public OMPLParser() {
		bloglist = new Vector<URL>();
	}
	
	public void startTag(String tagName) {
		//tab++;
		//show("<"+tagName+">");
	}

	
	public void text(String text) {
		// tab++;
		//show("-->"+text);
		 //tab--;
	}
	
	public void endTag(String tagName) {
		//show("</"+tagName+">");
		//tab--;
	}
	
	public void show(String s){
		//String a="";
		//for (int i=0;i<tab;i++)
		//	a+="  ";
		//System.out.println(a+s);
	}


	public void property(String pName, String pValue) {
		//show("Prop:"+pName+" = "+pValue);
		if (pName.equals("xmlUrl")) {
			try {
				pValue = URLDecoder.decode(pValue, "UTF-8");
				//
				URL url = new URL(pValue);
				//System.out.println("Adding: "+pValue);
				bloglist.add(url);
			} catch (Exception e) {
				System.out.println("MalformedURL: "+pValue);
			}
		}
			
	}

	
}
