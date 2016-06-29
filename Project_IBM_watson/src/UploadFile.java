import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.json.stream.JsonParserFactory;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javaFlacEncoder.FLAC_FileEncoder;
/**
 * Servlet implementation class UploadFile
 */

public class UploadFile extends HttpServlet {
	
	WebSocket_ex con;

	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public UploadFile() {
        // TODO Auto-generated constructor stub
    	con=new WebSocket_ex();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//long startTime = System.currentTimeMillis();  
		//log("Inside do post");
  
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
	        		  url="https://pvssmccs.w3ibm.mybluemix.net/mccs/kms/v2/content/a120/intent/"+nll.getclassification(text).getTopClass();
		        	  
		        	  connection = new URL(url).openConnection();
		        	  connection.setRequestProperty("Accept", "application/json");	 
		        	  InputStream is = new URL(url).openStream();
	      		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      		      StringBuilder sb = new StringBuilder();
	      		      int cp;
	      		      while ((cp = rd.read()) != -1) {
	      		    	  sb.append((char) cp);
	      		    }
	      		      
	      		      String jsonText = sb.toString();
	      	  
		        	 	       JsonParser parser =new JsonParser();
		        	 	       JsonObject json=(JsonObject) parser.parse(jsonText);
		        	 	       JsonElement vid=json.get("body");
		        	 	       JsonObject j=(JsonObject) parser.parse(vid.toString());
			           		   //System.out.print( j.get("video"));
		        	 	       JsonElement vidlink=j.get("video");
		        	 	/*       JsonElement steps_of_article=j.get("steps");
		        	 	     JsonArray steps= steps_of_article.getAsJsonArray();
		        	 	    int l=steps.size();
		        	 	    int i=0;
		        	 	    while(i<l)
		        	 	    {
		        	 	    	System.out.println(steps.get(i));
		        	 	    	i++;
		        	 	    }*/
		        	 	      System.out.print(vidlink.getAsString());
		        	 	     // os.print("<a href=");
		        	 	     // os.print("\""+vidlink.getAsString()+"\">");
		        	 	     // os.print("Solution</a>");
		        	          os.print(",");
		        	          os.print("<a href=");
		        	          os.print("\""+vidlink.getAsString()+"\"" + " target=_blank>");
		        	          os.print(vidlink.getAsString()+"</a>");

		        	 	     
		        	 //nll.getclassification(text).getTopClass();
		        	  //xml parser
		        	/*  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		              factory.setNamespaceAware(true);
		              
		              try {
						Document d= factory.newDocumentBuilder().parse(new URL(url).openStream());
						System.out.print(d);
						String nl=d.getElementsByTagName("body").item(3).getTextContent();
						os.println(nl);
					
					} catch (SAXException | ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        */
		        	  
		        	  //  os.print(nll.getclassification(text).getTopClass());
		  	  // long endTime = System.currentTimeMillis();
//		  	     //  long totalTime = endTime - startTime;
		  	       //System.out.println(totalTime);
	        	  
		          }

	        	  }
	        		  	  	       os.flush();

	       }

}
