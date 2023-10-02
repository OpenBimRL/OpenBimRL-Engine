import de.rub.bi.inf.model.AbstractRuleDefinition;
import de.rub.bi.inf.model.RuleBase;
import de.rub.bi.inf.openbimrl.helper.OpenBimRLReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RunExample {

    /**
     * An example showcasing the use of an OpenBimRL rule execution on an IFC model of choice.
     *
     * @author Marcel Stepien, Andre Vonthron
     */
    public static void main(String[] args) {

        try {
            File modelFile = Kernel.getApplicationController().openLoadFileDialog("IFC Model File", null);
            IfcLoadManager.getInstance().loadFile(modelFile);

            File ruleFile = Kernel.getApplicationController().openLoadFileDialog("OpenBimRL File", null);
            ArrayList<File> openBimRLFiles = new ArrayList<File>();
            openBimRLFiles.add(ruleFile);

            new OpenBimRLReader(openBimRLFiles);

            for (AbstractRuleDefinition ruleDef : RuleBase.getInstance().getRules()) {
                ruleDef.check(
                        Kernel.getApplicationModelRoot().getNode(0)
                );

                System.out.println(ruleDef.getCheckedStatus());
                System.out.println(ruleDef.getResultObjects().size());
                System.out.println(ruleDef.getCheckingProtocol());
            }

        } catch (FileNotFoundException | NotSupportedException e) {
            e.printStackTrace();
        }

    }

}
