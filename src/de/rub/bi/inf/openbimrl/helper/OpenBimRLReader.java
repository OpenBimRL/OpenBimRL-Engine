package de.rub.bi.inf.openbimrl.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import de.rub.bi.inf.openbimrl.OpenRule;
import de.rub.bi.inf.model.RuleBase;
import de.rub.bi.inf.openbimrl.BIMRuleType;

/**
 * A helper class to store and convert OpenBimRL files in a {@link RuleBase} context and hold them for execution.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class OpenBimRLReader {
	
	public OpenBimRLReader(List<File> files) {
		for(File f : files) {
			de.rub.bi.inf.openbimrl.io.OpenBimRLReader ruleImporter =
					new de.rub.bi.inf.openbimrl.io.OpenBimRLReader();
			try {

				Object obj = ruleImporter.readFromFile(f.getAbsolutePath());

				JAXBElement<?> element = (JAXBElement<?>) obj;
				BIMRuleType bimRuleType = (BIMRuleType) element.getValue();
				
				System.out.println(bimRuleType.getModelCheck());
				System.out.println(bimRuleType.getPrecalculations());
				
				OpenRule openRule = new OpenRule(bimRuleType.getModelCheck(), bimRuleType.getPrecalculations());
				
				RuleBase.getInstance().addRule(openRule);
				
			} catch (FileNotFoundException | JAXBException | XMLStreamException e) {
				e.printStackTrace();
			}
		}
		
	}

}
