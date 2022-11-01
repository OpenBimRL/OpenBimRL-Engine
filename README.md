# OpenBimRL Engine

> **Current Version:** 2022.11.1 <br>
> **Publication Date:** 01.11.2022 <br>
> **Updated Date:** 01.11.2022 <br>
> **Autors:** Marcel Stepien, André Vonthron <br>
> **E-Mail:** marcel.stepien@ruhr-uni-bochum.de
<br>


This is a implementation of an engine for the OpenBimRL api. The framework includes functions for OpenBimRL parsing, execution and generating results. This project also containes a set of already predefined functions that can be used in OpenBimRL documents for defining precalculations and in extension perform rule checking. 

## Getting started

### Requirements

**Note:** This engine is currently incorporating the _Apstex IFC Framework_ and _Viewer_ into the parsing and interpretation of the building model context. As a requirement, these has to be included in or replaced for this project to work.

**Dependencies:**
- [Apstex IFC-Framework + Viewer](https://www.apstex.com/)
- [OpenBimRL-API](https://github.com/RUB-Informatik-im-Bauwesen/OpenBimRL)

More dependencies are provided in the _pom.xml_ and automatically included via maven.

### Minimum Example
An example execution of this engine is provided in _RunExample.java_ and can be used to perform rule checking out of the box. Results will be printed in the terminal. However, for a more simplistic approach this code provides an example:

```
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
```
