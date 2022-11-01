import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.apstex.gui.core.kernel.Kernel;
import com.apstex.gui.ifc.controller.IfcLoadManager;
import com.apstex.loader3d.core.NotSupportedException;

import model.AbstractRuleDefinition;
import model.RuleBase;
import openbimrl.helper.OpenBimRLReader;

public class RunExample {

	/**
	 * An example showcasing the use of an OpenBimRL rule execution on an IFC model of choice.
	 * 
	 * @author Marcel Stepien, Andre Vonthron
	 *
	 */
	public static void main(String[] args) {
		
		try {
			File modelFile = Kernel.getApplicationController().openLoadFileDialog("IFC Model File", null);
			IfcLoadManager.getInstance().loadFile(modelFile);

			File ruleFile = Kernel.getApplicationController().openLoadFileDialog("OpenBimRL File", null);
			ArrayList<File> openBimRLFiles = new ArrayList<File>();
			openBimRLFiles.add(ruleFile);
			
			new OpenBimRLReader(openBimRLFiles);
			
			for(AbstractRuleDefinition ruleDef : RuleBase.getInstance().getRules()) {
				ruleDef.check(
					Kernel.getApplicationModelRoot().getNode(0)
				);
			}
			
		} catch (FileNotFoundException | NotSupportedException e) {
			e.printStackTrace();
		}
		
	}

}
