

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javaFlacEncoder.FLAC_FileEncoder;

/**
 * Servlet implementation class UploadFile1
 */
@WebServlet("/UploadFile1")
public class UploadFile1 extends HttpServlet {
	WebSocket_ex con;

	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFile1() {
        //super();
    	con=new WebSocket_ex();

        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		   Boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		   Boolean isMultipart = ServletFileUpload.isMultipartContent(request);
 		log(isMultipart.toString());
		   File file,file2 ;
		   int maxFileSize = 5000 * 1024;
		   int maxMemSize = 5000 * 1024;
		   ServletContext context = getServletContext();
		   String filePath=this.getClass().getClassLoader().getResource("").getPath();
	       if (isMultipart) {
	        	// Create a factory for disk-based file items
	        	FileItemFactory factory = new DiskFileItemFactory();
	        	((DiskFileItemFactory) factory).setSizeThreshold(maxMemSize);
	            // Location to save data that is larger than maxMemSize.
	            ((DiskFileItemFactory) factory).setRepository(new File("c:\\temp"));
	        	// Create a new file upload handler
	        	ServletFileUpload upload = new ServletFileUpload(factory);
	        	try {
	            	// Parse the request
					List /* FileItem */ items = upload.parseRequest(request);
					Iterator iterator = items.iterator();
	                while (iterator.hasNext()) {
	                    FileItem fi = (FileItem)iterator.next();
	                    if ( !fi.isFormField () )	
	                    {
	                    // Get the uploaded file parameters
	                    String fieldName = fi.getFieldName();
	                    String fileName = "test.wav";
	                    String fileName2="test2.flac";
	                    boolean isInMemory = fi.isInMemory();
	                    long sizeInBytes = fi.getSize();
	                    // Write the file
	                    if( fileName.lastIndexOf("\\") >= 0 ){
	                        file = new File( filePath +  fileName.substring( fileName.lastIndexOf("\\"))) ;
	                        file2=new File( filePath +  fileName2.substring( fileName.lastIndexOf("\\"))) ;
	                    }
	                    else{
	                        file = new File( filePath + fileName.substring(fileName.lastIndexOf("\\")+1)) ;
	                        file2=new File( filePath +  fileName2.substring( fileName.lastIndexOf("\\")+1)) ;
	                        
	                    }
	                    /* FLAC encoding */
       
	                    FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
	                    
	                    fi.write( file ) ;
              
	                    }
	                }
	            } catch (FileUploadException e) { 
	                e.printStackTrace();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	           
	            
	        }
	       
	       ServletOutputStream os = response.getOutputStream();
	       String text =  con.convert("test.wav", filePath+"test.wav",os);
	       System.out.println("Uploadfile: text="+text);
	  /*     try {
			Thread.sleep(10*1000);
		   } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }*/
	       os.print(text);
	      // os.flush();
	     //  os.flush();
	          if(text.compareTo("no speech input")!=0)
	          {
	        	  String url;
	        	  URLConnection connection;
	        	  NllClassification nll= new NllClassification();
	        	  System.setProperty("java.net.useSystemProxies", "true");
	        	//  String url="http://pvssmccs.w3ibm.mybluemix.net/mccs/kms/v2/content/a120/intent/Office365_Mail_Delegate_Assign";
	        	  if(nll.getclassification(text).getTopClass()==null)
	        		  os.println("can't get your question!!");
	        	  else
	        	  {
	        		  os.print(",");
	        		  os.println(nll.getclassification(text).getTopClass());
        		  }
	           os.flush();

	       }

	//	doGet(request, response);
	}

}
