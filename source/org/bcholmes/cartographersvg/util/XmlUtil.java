package org.bcholmes.cartographersvg.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XmlUtil {
	
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
	public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
	public static final String INKSCAPE_NAMESPACE = "http://www.inkscape.org/namespaces/inkscape";
	public static final String SODIPODI_NAMESPACE = "http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd";

	public static Document createEmptyDocument() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeXml(Document document, File file) {
	    try {
	        Source source = new DOMSource(document);
	        Result result = new StreamResult(file);
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	    } catch (TransformerConfigurationException e) {
	    	throw new RuntimeException(e);
	    } catch (TransformerException e) {
	    	throw new RuntimeException(e);
	    }		
	}

}
