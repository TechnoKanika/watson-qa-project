import java.io.File;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.*;
public class NllClassification {

	public Classification getclassification(String text) {
		// TODO Auto-generated method stub
		
		NaturalLanguageClassifier service = new NaturalLanguageClassifier();
		service.setUsernameAndPassword("7246a044-202f-410a-856b-ef9cd510c1c6", "CEupKtobLzqg");
		Classification classification = service.classify("2373f5x67-nlc-783", text);
		//System.out.println(classification);
		return classification;
	}

}
