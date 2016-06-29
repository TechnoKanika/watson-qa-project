import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.speech_to_text.v1.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SessionStatus;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechSession;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeDelegate;
import com.ibm.watson.developer_cloud.util.GsonSingleton;
import com.neovisionaries.ws.client.WebSocket;
public class WebSocket_ex implements SpeechToTextInter {
	static String txt = "";
	//ServletOutputStream out
	@Override
	public String convert(String filename, String path, ServletOutputStream out) throws FileNotFoundException {
		// TODO Auto-generated method stub
		txt="";
		SpeechToText speechService=new SpeechToText();
		speechService.setUsernameAndPassword("d1a135b6-3bc6-48d2-973d-5e2e148774c2", "pc1N1aakA6xO");
		//s.setEndPoint("wss://stream.watsonplatform.net/speech-to-text/api");
		speechService.setEndPoint("https://stream.watsonplatform.net/speech-to-text/api");
		RecognizeOptions options = new RecognizeOptions().contentType("audio/wav")
				  .continuous(true).interimResults(true);		
		SpeechSession session = speechService.createSession();
		SessionStatus status = speechService.getRecognizeStatus(session);
		System.out.println(status);
		while(speechService.getRecognizeStatus(session).getState()=="initialized");
		SpeechResults spre=speechService.recognize(new File(path),options);
		String message=spre.toString();
		SpeechResults result = GsonSingleton.getGson().fromJson(message, SpeechResults.class);
		int i=0;
		if(result.getResults().isEmpty())
		{
			speechService.deleteSession(session);
			return "no speech input";
			
		}
		else
		{
			 while(!result.getResults().get(0).isFinal());
				String resultStr = result.getResults().get(0).getAlternatives().get(0).getTranscript();
		        if(resultStr !=  null){  
		        	
		        	txt+=resultStr;
		   
		        }else{    	
		        	txt = "Problem Occurred!";
		        }
				speechService.deleteSession(session);
				System.out.println("Out of while loop and text message is="+txt);
				return txt;
		        
			}

		}
	 }
