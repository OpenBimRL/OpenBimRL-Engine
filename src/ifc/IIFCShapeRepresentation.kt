package engine.openbimrl.inf.bi.rub.de.ifc

interface IIFCShapeRepresentation : IIFCShapeModel {
    fun addItems(item: IIFCRepresentationItem): Int
    var representationIdentifier: IIFCLabel
}