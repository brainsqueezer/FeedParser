package org.aific.test;
import java.io.IOException;
import org.aific.xml.XMLParser;
import org.aific.xml.XMLParserListener;



public class SampleXMLParsing implements XMLParserListener{

	static int tab=0;
	
	public static void main(String[] args) throws IOException{
        //URL yahoo = new URL("http://www.yahoo.com/");
      /*  URL url = new URL("http://www.aific.org/ompl.php");
		URLConnection c = url.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                c.getInputStream()));
        String inputLine;
        StringBuffer input = new StringBuffer();

       
        while ((inputLine = in.readLine()) != null) 
            input.append(inputLine);
        in.close();
        
        */
        
		String xml="<T1 mm=110><T2 pco=44 mao=22 ola=9999>gocdsacdsacller</T2><TX/></T1><bau></bau>";
		XMLParser xParser1 = new XMLParser(xml);
		xParser1.setListener( new SampleXMLParsing() );
		xParser1.parse();
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
