package engine.openbimrl.inf.bi.rub.de

import adapter.openbimrl.inf.bi.rub.de.IFCJAVAAdapter
import de.rub.bi.inf.openbimrl.engine.ifc.IIFCLabel

fun main() {

    // initialize adapter
    val adapter = IFCJAVAAdapter()

    val label = adapter.IFC4.create(IIFCLabel::class.java, "lol")

    println(label.label)

}
