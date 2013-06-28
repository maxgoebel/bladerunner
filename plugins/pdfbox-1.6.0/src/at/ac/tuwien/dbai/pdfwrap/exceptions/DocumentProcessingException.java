package at.ac.tuwien.dbai.pdfwrap.exceptions;

/**
 * Document conversion exception; generic exception
 * which is returned when something goes belly up
 * 
 * @author Tamir Hassan, pdfanalyser@tamirhassan.com
 * @version PDF Analyser 0.9
 */
public class DocumentProcessingException extends Exception
{
    private Exception embedded;
    
    public DocumentProcessingException(String msg)
    {
        super(msg);
    }
    
    public DocumentProcessingException(Exception e)
    {
        super(e.getMessage());
        setEmbedded(e);
    }
    
    public Exception getEmbedded()
    {
        return embedded;
    }
    
    private void setEmbedded(Exception e)
    {
        embedded = e;
    }
}