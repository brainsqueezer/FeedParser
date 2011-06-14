package org.aific.xml;

import org.aific.xml.XMLParserListener;


/**
 * Simple XML SAX Parser
 * @author Ramon Antonio Parada <rap@ramonantonio.net>
 * @version 0.1
 */
public class RSSParser implements XMLParserListener {

	private int tab=0;
	

	
	public RSSParser() {
	}
	
	public void startTag(String tagName) {
		tab++;
		show("<"+tagName+">");
	}

	
	public void text(String text) {
		 tab++;
		show("-->"+text);
		 tab--;
	}
	
	public void endTag(String tagName) {
		show("</"+tagName+">");
		tab--;
	}
	
	public void show(String s){
		String a="";
		for (int i=0;i<tab;i++)
			a+="  ";
		System.out.println(a+s);
	}


	public void property(String pName, String pValue) {
		show("Prop:"+pName+" = "+pValue);
		
	}

	
}
