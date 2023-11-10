package de.rub.bi.inf.openbimrl.functions;

import de.rub.bi.inf.openbimrl.NodeProxy;
import de.rub.bi.inf.openbimrl.functions.filter.FilterByMasks;
import de.rub.bi.inf.openbimrl.functions.filter.FilterInvert;
import de.rub.bi.inf.openbimrl.functions.geometry.*;
import de.rub.bi.inf.openbimrl.functions.ifc.*;
import de.rub.bi.inf.openbimrl.functions.input.TextInput;
import de.rub.bi.inf.openbimrl.functions.list.*;
import de.rub.bi.inf.openbimrl.functions.math.Addition;
import de.rub.bi.inf.openbimrl.functions.math.Maximum;
import de.rub.bi.inf.openbimrl.functions.math.Subtraction;
import de.rub.bi.inf.openbimrl.functions.geometry.CheckIntersection;
import de.rub.bi.inf.openbimrl.functions.geometry.CheckLinecasts;

/**
 * This class manages a collection of available functions for OpenBimRL execution,
 * effectivly mapping all available function containing classes to an idenifier namespace.
 * These templates are then called on demand, depending on wich funtion should be executed.
 *
 * @author Marcel Stepien, Andre Vonthron
 */
public class FunctionFactory {

    public static AbstractFunction getFuction(NodeProxy nodeProxy) {

        String functionString = nodeProxy.getNode().getFunction();

        AbstractFunction function = null;

        switch (functionString) {
            case "ifc.addRepresentationItemToEntity":
                function = new AddRepresentationItemToEntity(nodeProxy);
                break;
            case "ifc.getCurveLength":
                function = new GetCurveLength(nodeProxy);
                break;
            case "ifc.createPolyline":
                function = new CreatePolyline(nodeProxy);
                break;
            case "ifc.filterByElement":
                function = new FilterByElement(nodeProxy);
                break;
            case "ifc.filterByGUID":
                function = new FilterByGUID(nodeProxy);
                break;
            case "ifc.filterByProperty":
                function = new FilterByProperty(nodeProxy);
                break;
            case "ifc.filterByQuantity":
                function = new FilterByQuantity(nodeProxy);
                break;
            case "ifc.getIfcType":
                function = new GetIfcType(nodeProxy);
                break;
            case "ifc.getAllPropertySetInformation":
                function = new GetAllPropertySetInformation(nodeProxy);
                break;
            case "ifc.getProperty":
                function = new GetProperty(nodeProxy);
                break;
            case "ifc.getPropertyAsString":
                function = new GetPropertyAsString(nodeProxy);
                break;
            case "ifc.getQuantity":
                function = new GetQuantity(nodeProxy);
                break;
            case "ifc.getHeight":
                function = new GetHeight(nodeProxy);
                break;
            case "ifc.getGeometry":
                function = new GetGeometry(nodeProxy);
                break;
            case "ifc.getBoundingBox":
                function = new GetBoundingBox(nodeProxy);
                break;
            case "ifc.getGeometryByElement":
                function = new GetGeometryByElement(nodeProxy);
                break;
            case "ifc.getRepresentationItems":
                function = new GetRepresentationItems(nodeProxy);
                break;
            case "ifc.getGeometryAppearance":
                function = new GetGeometryAppearance(nodeProxy);
                break;
            case "ifc.checkElementIntersection":
                function = new CheckElementIntersection(nodeProxy);
                break;
            case "ifc.checkSpaceBounds":
                function = new CheckSpaceBounds(nodeProxy);
                break;
            case "ifc.getStorey":
                function = new GetStorey(nodeProxy);
                break;
            case "ifc.getElementsInSpatialStructure":
                function = new GetElementsInSpatialStructure(nodeProxy);
                break;
            case "ifc.getGlobalID":
                function = new GetGlobalID(nodeProxy);
                break;
            case "ifc.getElementById":
                function = new GetElementById(nodeProxy);
                break;
            case "ifc.getOpeningElements":
                function = new GetOpeningElements(nodeProxy);
                break;

            case "math.addition":
                function = new Addition(nodeProxy);
                break;
            case "math.subtraction":
                function = new Subtraction(nodeProxy);
                break;
            case "math.maximum":
                function = new Maximum(nodeProxy);
                break;

            case "geometry.loadRepresentationItemGeometry":
                function = new LoadRepresentationItemGeometry(nodeProxy);
                break;
            case "geometry.convertStrToVectorList":
                function = new ConvertStrToVectorList(nodeProxy);
                break;
            case "geometry.calculateClosestDistanceToBounds":
                function = new CalculateClosestDistanceToBounds(nodeProxy);
                break;

            case "geometry.checkIntersectionByBounds":
                function = new CheckIntersectionByBounds(nodeProxy);
                break;
            case "geometry.checkIncludedInBounds":
                function = new CheckIncludedInBounds(nodeProxy);
                break;
            case "geometry.colorizeGeometryAppearance":
                function = new ColorizeGeometryAppearance(nodeProxy);
                break;
            case "geometry.checkNeighborhoodByDistance":
                function = new CheckNeighborhoodByDistance(nodeProxy);
                break;
            case "geometry.createCollisionSphere":
                function = new CreateCollisionSphere(nodeProxy);
                break;
            case "geometry.checkIntersection":
                function = new CheckIntersection(nodeProxy);
                break;
            case "geometry.convertToRegionBSPTree":
                function = new ConvertToRegionBSPTree(nodeProxy);
                break;
            case "geometry.decomposeGeometry":
                function = new DecomposeGeometry(nodeProxy);
                break;
            case "geometry.decomposeVector3D":
                function = new DecomposeVector3D(nodeProxy);
                break;
            case "geometry.createPointGraph":
                function = new CreatePointGraph(nodeProxy);
                break;
            case "geometry.createPointGraphNodes":
                function = new CreatePointGraphNodes(nodeProxy);
                break;
            case "geometry.createPointGraphEdges":
                function = new CreatePointGraphEdges(nodeProxy);
                break;
            case "geometry.createPointGraphEdgesByReference":
                function = new CreatePointGraphEdgesByReference(nodeProxy);
                break;
            case "geometry.groupGraphEdges":
                function = new GroupGraphEdges(nodeProxy);
                break;
            case "geometry.calculateDistancesByOrderedGraphEdges":
                function = new CalculateDistancesByOrderedGraphEdges(nodeProxy);
                break;
            case "geometry.calculateAStarSearch":
                function = new CalculateAStarSearch(nodeProxy);
                break;
            case "geometry.unifyRegionBSPTree":
                function = new UnifyRegionBSPTree(nodeProxy);
                break;
            case "geometry.checkLinecasts":
                function = new CheckLinecasts(nodeProxy);
                break;
            case "geometry.showGraph":
                function = new ShowGraph(nodeProxy);
                break;
            case "geometry.showGraphColoredByValues":
                function = new ShowGraphColoredByValues(nodeProxy);
                break;
            case "geometry.showPaths":
                function = new ShowPaths(nodeProxy);
                break;
            case "geometry.getCentroidOfRegionBSPTree":
                function = new GetCentroidOfRegionBSPTree(nodeProxy);
                break;
            case "geometry.convertToVector3D":
                function = new ConvertToVector3D(nodeProxy);
                break;


            case "filter.filterByMasks":
                function = new FilterByMasks(nodeProxy);
                break;
            case "filter.filterInvert":
                function = new FilterInvert(nodeProxy);
                break;

            case "list.getElementAt":
                function = new GetElementAt(nodeProxy);
                break;
            case "list.getItemIndexInList":
                function = new GetItemIndexInList(nodeProxy);
                break;

            case "list.removeFromList":
                function = new RemoveFromList(nodeProxy);
                break;
            case "list.joinCollections":
                function = new JoinCollections(nodeProxy);
                break;
            case "list.asList":
                function = new AsList(nodeProxy);
                break;
            case "list.orderBy":
                function = new OrderBy(nodeProxy);
                break;
            case "list.sum":
                function = new Sum(nodeProxy);
                break;
            case "list.groupBy":
                function = new GroupBy(nodeProxy);
                break;
            case "list.flattenCollection":
                function = new FlattenCollection(nodeProxy);
                break;
            case "list.count":
                function = new Count(nodeProxy);
                break;
            case "list.elementIncludedInList":
                function = new ElementIncludedInList(nodeProxy);
                break;
            case "list.allElementIncludedInList":
                function = new AllElementIncludedInList(nodeProxy);
                break;
            case "list.joinItemByItem":
                function = new JoinItemByItem(nodeProxy);
                break;
            case "list.repeatAsList":
                function = new RepeatAsList(nodeProxy);
                break;


            case "list.createMapByPairs":
                function = new CreateMapByPairs(nodeProxy);
                break;
            case "list.mapInvert":
                function = new MapInvert(nodeProxy);
                break;
            case "list.mapFilterByCount":
                function = new MapFilterByCount(nodeProxy);
                break;
            case "list.mapValueCount":
                function = new MapValueCount(nodeProxy);
                break;
            case "list.mapKeySet":
                function = new MapKeySet(nodeProxy);
                break;
            case "list.mapValues":
                function = new MapValues(nodeProxy);
                break;
            case "list.getMapValueAtKey":
                function = new GetMapValueAtKey(nodeProxy);
                break;
            case "list.getMapValueByKeyList":
                function = new GetMapValueByKeyList(nodeProxy);
                break;

            case "input.textInput":
                function = new TextInput(nodeProxy);
                break;
            default:
                break;
        }

        try {
            if (function == null)
                throw new Exception("Function " + functionString + " not found");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return function;

    }
}
