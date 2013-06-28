package at.ac.tuwien.dbai.pdfwrap.utils.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.soap.SOAPException;

/**
 * SOAP web service interface for semantic extraction service
 * 
 * @author bastianpreindl
 * 
 */
@WebService(portName = "SemanticVisualisation")
@SOAPBinding(style = Style.RPC)
public interface ISemanticVisualisation
{
	/**
	 * Extracts a given binary file to visually enriched tags in XML
	 * 
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 * @param model
	 *            Extraction mode
	 * @param document
	 *            Document to be semantically extracted and transformed
	 * @param filetype
	 *            Filetype of the document (document postfix)
	 * @return Visual Tag XML as String following the respective schema
	 * @throws SOAPException
	 *             for any kind of extraction problems containing a detailed
	 *             error description
	 */
	@WebMethod(operationName = "extractVisualTags")
	public String extractToXML(@WebParam(name = "username") String username, @WebParam(name = "password") String password,
			@WebParam(name = "model") String model, @WebParam(name = "inputdata") byte[] document, @WebParam(name = "inputdatatype") String filetype,
			@WebParam(name = "tagfilter") String[] tagfilter) throws SOAPException;
}
