package at.ac.tuwien.dbai.pdfwrap.utils.ws;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import at.tuwien.prip.common.utils.file.FileReader;

/**
 * WsUtils.java
 *
 * Web Service Utilities.
 *
 * @author mcg <mcgoebel@gmail.com>
 * @date Jun 7, 2011
 */
public class WsUtils {

	/**
	 * TODO: externalize constants
	 */
	public final static String defaultFileName = "/home/max/work/classifieds2.pdf";

	public final static String serviceName = "SemanticVisualisationService";

	public final static String servicePort = "SemanticVisualisationPort";

	public final static String serverName = "http://servlets.iex.jv.com/";
	
	public final static String wsdlLocation = "http://tic.joinvision.com:8081/cvlizer/visservicesoap?wsdl";

	private final static String userName = "docwrap";
	
	private final static String password = "dwrap23";
	
	private final static String model = "cvlizer_2_0";
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Document processWsQuery (String fileName) 
	throws FileNotFoundException 
	{
		Document result = null;

		if (fileName==null) {
			fileName = defaultFileName;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException(fileName+" could not be found...");
		}

		byte[] testFile = null;
		URL url = null;
		try {
			testFile = FileReader.getBytesFromFile(file);
			url = new URL(wsdlLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}

		QName qname = new QName(serverName, serviceName);
		QName port = new QName(serverName, servicePort);
		Service service = Service.create(url, qname);
		ISemanticVisualisation ss = service.getPort(port, ISemanticVisualisation.class);
		try {
			String xmlString = ss.extractToXML(
					userName, password, model, 
					testFile , "pdf", new String[] {});

			if (xmlString!=null) {
				result = parseXML(xmlString);
			}

		} catch (SOAPException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Stax parse an XML string.
	 * @param input
	 * @return
	 */
	public static Document parseXML (String input)
	{
		Document result = null;

		try
		{
			// ---- Parse XML file ----
			DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
			DocumentBuilder        builder  = factory.newDocumentBuilder();
			
			ByteArrayInputStream bs = new ByteArrayInputStream(input.getBytes());
			result = builder.parse( bs ); 

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return result;
	}
	
}//WsUtils
