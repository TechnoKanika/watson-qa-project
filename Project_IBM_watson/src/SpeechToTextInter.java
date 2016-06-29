import java.io.FileNotFoundException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletOutputStream;

public interface SpeechToTextInter {
	public String convert(String filename,String path, ServletOutputStream out) throws FileNotFoundException ;

	
}
