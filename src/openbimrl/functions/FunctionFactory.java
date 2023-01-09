package openbimrl.functions;

import openbimrl.NodeProxy;
import openbimrl.functions.filter.FilterByMasks;
import openbimrl.functions.filter.FilterInvert;
import openbimrl.functions.geometry.CalculateClosestDistanceToBounds;
import openbimrl.functions.geometry.CalculateDistancesByOrderedGraphEdges;
import openbimrl.functions.geometry.CheckIncludedInBounds;
import openbimrl.functions.geometry.CheckIntersection;
import openbimrl.functions.geometry.CheckIntersectionByBounds;
import openbimrl.functions.geometry.CheckLinecasts;
import openbimrl.functions.geometry.CheckNeighborhoodByDistance;
import openbimrl.functions.geometry.ColorizeGeometryAppearance;
import openbimrl.functions.geometry.ConvertStrToVectorList;
import openbimrl.functions.geometry.ConvertToRegionBSPTree;
import openbimrl.functions.geometry.ConvertToVector3D;
import openbimrl.functions.geometry.CreateCollisionSphere;
import openbimrl.functions.geometry.CreatePointGraph;
import openbimrl.functions.geometry.CreatePointGraphEdges;
import openbimrl.functions.geometry.CreatePointGraphEdgesByReference;
import openbimrl.functions.geometry.CreatePointGraphNodes;
import openbimrl.functions.geometry.DecomposeGeometry;
import openbimrl.functions.geometry.DecomposeVector3D;
import openbimrl.functions.geometry.ShowGraph;
import openbimrl.functions.geometry.ShowGraphColoredByValues;
import openbimrl.functions.geometry.UnifyRegionBSPTree;
import openbimrl.functions.geometry.GetCentroidOfRegionBSPTree;
import openbimrl.functions.geometry.GroupGraphEdges;
import openbimrl.functions.geometry.LoadRepresentationItemGeometry;
import openbimrl.functions.ifc.AddRepresentationItemToEntity;
import openbimrl.functions.ifc.CheckElementIntersection;
import openbimrl.functions.ifc.CheckSpaceBounds;
import openbimrl.functions.ifc.CreatePolyline;
import openbimrl.functions.ifc.FilterByElement;
import openbimrl.functions.ifc.FilterByGUID;
import openbimrl.functions.ifc.FilterByProperty;
import openbimrl.functions.ifc.FilterByQuantity;
import openbimrl.functions.ifc.GetAllPropertySetInformation;
import openbimrl.functions.ifc.GetBoundingBox;
import openbimrl.functions.ifc.GetCurveLength;
import openbimrl.functions.ifc.GetElementById;
import openbimrl.functions.ifc.GetElementsInSpatialStructure;
import openbimrl.functions.ifc.GetGeometry;
import openbimrl.functions.ifc.GetGeometryAppearance;
import openbimrl.functions.ifc.GetGeometryByElement;
import openbimrl.functions.ifc.GetGlobalID;
import openbimrl.functions.ifc.GetHeight;
import openbimrl.functions.ifc.GetIfcType;
import openbimrl.functions.ifc.GetOpeningElements;
import openbimrl.functions.ifc.GetProperty;
import openbimrl.functions.ifc.GetPropertyAsString;
import openbimrl.functions.ifc.GetQuantity;
import openbimrl.functions.ifc.GetRepresentationItems;
import openbimrl.functions.ifc.GetStorey;
import openbimrl.functions.input.TextInput;
import openbimrl.functions.list.AllElementIncludedInList;
import openbimrl.functions.list.AsList;
import openbimrl.functions.list.Count;
import openbimrl.functions.list.CreateMapByPairs;
import openbimrl.functions.list.ElementIncludedInList;
import openbimrl.functions.list.FlattenCollection;
import openbimrl.functions.list.GetElementAt;
import openbimrl.functions.list.GetItemIndexInList;
import openbimrl.functions.list.GetMapValueAtKey;
import openbimrl.functions.list.GetMapValueByKeyList;
import openbimrl.functions.list.GroupBy;
import openbimrl.functions.list.JoinCollections;
import openbimrl.functions.list.JoinItemByItem;
import openbimrl.functions.list.MapFilterByCount;
import openbimrl.functions.list.MapInvert;
import openbimrl.functions.list.MapKeySet;
import openbimrl.functions.list.MapValueCount;
import openbimrl.functions.list.MapValues;
import openbimrl.functions.list.OrderBy;
import openbimrl.functions.list.RemoveFromList;
import openbimrl.functions.list.RepeatAsList;
import openbimrl.functions.list.Sum;
import openbimrl.functions.math.Addition;
import openbimrl.functions.math.Maximum;
import openbimrl.functions.math.Subtraction;

/**
 * This class manages a collection of available functions for OpenBimRL execution,
 * effectivly mapping all available function containing classes to an idenifier namespace.
 * These templates are then called on demand, depending on wich funtion should be executed.
 * 
 * @author Marcel Stepien, Andre Vonthron
 *
 */
public class FunctionFactory {

	public static AbstractFunction getFuction(NodeProxy nodeProxy){
		
		String functionString = nodeProxy.getNode().getFunction();
		
		AbstractFunction function = null;
		
		switch (functionString) {
			case "ifc.addRepresentationItemToEntity": function = new AddRepresentationItemToEntity(nodeProxy); break;
			case "ifc.getCurveLength": function = new GetCurveLength(nodeProxy); break;
			case "ifc.createPolyline": function = new CreatePolyline(nodeProxy); break;
			case "ifc.filterByElement": function = new FilterByElement(nodeProxy); break;
			case "ifc.filterByGUID": function = new FilterByGUID(nodeProxy); break;
			case "ifc.filterByProperty": function = new FilterByProperty(nodeProxy); break;
			case "ifc.filterByQuantity": function = new FilterByQuantity(nodeProxy); break;
			case "ifc.getIfcType": function = new GetIfcType(nodeProxy); break;
			case "ifc.getAllPropertySetInformation": function = new GetAllPropertySetInformation(nodeProxy); break;
			case "ifc.getProperty": function = new GetProperty(nodeProxy); break;
			case "ifc.getPropertyAsString": function = new GetPropertyAsString(nodeProxy); break;
			case "ifc.getQuantity": function = new GetQuantity(nodeProxy); break;
			case "ifc.getHeight": function = new GetHeight(nodeProxy); break;
			case "ifc.getGeometry": function = new GetGeometry(nodeProxy); break;
			case "ifc.getBoundingBox": function = new GetBoundingBox(nodeProxy); break;
			case "ifc.getGeometryByElement": function = new GetGeometryByElement(nodeProxy); break;
			case "ifc.getRepresentationItems": function = new GetRepresentationItems(nodeProxy); break;
			case "ifc.getGeometryAppearance": function = new GetGeometryAppearance(nodeProxy); break;
			case "ifc.checkElementIntersection": function = new CheckElementIntersection(nodeProxy); break;
			case "ifc.checkSpaceBounds": function = new CheckSpaceBounds(nodeProxy); break;
			case "ifc.getStorey" : function = new GetStorey(nodeProxy); break;
			case "ifc.getElementsInSpatialStructure" : function = new GetElementsInSpatialStructure(nodeProxy); break;
			case "ifc.getGlobalID" : function = new GetGlobalID(nodeProxy); break;
			case "ifc.getElementById" : function = new GetElementById(nodeProxy); break;
			case "ifc.getOpeningElements": function = new GetOpeningElements(nodeProxy); break;
			
			case "math.addition" : function = new Addition(nodeProxy); break;
			case "math.subtraction" : function = new Subtraction(nodeProxy); break;
			case "math.maximum" : function = new Maximum(nodeProxy); break;
			
			case "geometry.loadRepresentationItemGeometry" : function = new LoadRepresentationItemGeometry(nodeProxy); break;
			case "geometry.convertStrToVectorList" : function = new ConvertStrToVectorList(nodeProxy); break;
			case "geometry.calculateClosestDistanceToBounds" : function = new CalculateClosestDistanceToBounds(nodeProxy); break;
			
			case "geometry.checkIntersectionByBounds" : function = new CheckIntersectionByBounds(nodeProxy); break;
			case "geometry.checkIncludedInBounds" : function = new CheckIncludedInBounds(nodeProxy); break;
			case "geometry.colorizeGeometryAppearance" : function = new ColorizeGeometryAppearance(nodeProxy); break;
			case "geometry.checkNeighborhoodByDistance" : function = new CheckNeighborhoodByDistance(nodeProxy); break;
			case "geometry.createCollisionSphere" : function = new CreateCollisionSphere(nodeProxy); break;
			case "geometry.checkIntersection" : function = new CheckIntersection(nodeProxy); break;
			case "geometry.convertToRegionBSPTree" : function = new ConvertToRegionBSPTree(nodeProxy); break;
			case "geometry.decomposeGeometry" : function = new DecomposeGeometry(nodeProxy); break;
			case "geometry.decomposeVector3D" : function = new DecomposeVector3D(nodeProxy); break;
			case "geometry.createPointGraph" : function = new CreatePointGraph(nodeProxy); break;
			case "geometry.createPointGraphNodes" : function = new CreatePointGraphNodes(nodeProxy); break;
			case "geometry.createPointGraphEdges" : function = new CreatePointGraphEdges(nodeProxy); break;
			case "geometry.createPointGraphEdgesByReference" : function = new CreatePointGraphEdgesByReference(nodeProxy); break;
			case "geometry.groupGraphEdges" : function = new GroupGraphEdges(nodeProxy); break;
			case "geometry.calculateDistancesByOrderedGraphEdges" : function = new CalculateDistancesByOrderedGraphEdges(nodeProxy); break;
			case "geometry.unifyRegionBSPTree" : function = new UnifyRegionBSPTree(nodeProxy); break;
			case "geometry.checkLinecasts" : function = new CheckLinecasts(nodeProxy); break;
			case "geometry.showGraph" : function = new ShowGraph(nodeProxy); break;
			case "geometry.showGraphColoredByValues" : function = new ShowGraphColoredByValues(nodeProxy); break;
			case "geometry.getCentroidOfRegionBSPTree" : function = new GetCentroidOfRegionBSPTree(nodeProxy); break;
			case "geometry.convertToVector3D" : function = new ConvertToVector3D(nodeProxy); break;

			case "filter.filterByMasks" : function = new FilterByMasks(nodeProxy); break;
			case "filter.filterInvert" : function = new FilterInvert(nodeProxy); break;
			
			case "list.getElementAt" : function = new GetElementAt(nodeProxy); break;
			case "list.getItemIndexInList" : function = new GetItemIndexInList(nodeProxy); break;
			
			case "list.removeFromList" : function = new RemoveFromList(nodeProxy); break;
			case "list.joinCollections" : function = new JoinCollections(nodeProxy); break;
			case "list.asList" : function = new AsList(nodeProxy); break;
			case "list.orderBy" : function = new OrderBy(nodeProxy); break;
			case "list.sum" : function = new Sum(nodeProxy); break;
			case "list.groupBy" : function = new GroupBy(nodeProxy); break;
			case "list.flattenCollection" : function = new FlattenCollection(nodeProxy); break;
			case "list.count" : function = new Count(nodeProxy); break;
			case "list.elementIncludedInList" : function = new ElementIncludedInList(nodeProxy); break;
			case "list.allElementIncludedInList" : function = new AllElementIncludedInList(nodeProxy); break;
			case "list.joinItemByItem" : function = new JoinItemByItem(nodeProxy); break;
			case "list.repeatAsList" : function = new RepeatAsList(nodeProxy); break;
			
			
			case "list.createMapByPairs" : function = new CreateMapByPairs(nodeProxy); break;
			case "list.mapInvert" : function = new MapInvert(nodeProxy); break;
			case "list.mapFilterByCount" : function = new MapFilterByCount(nodeProxy); break;
			case "list.mapValueCount" : function = new MapValueCount(nodeProxy); break;
			case "list.mapKeySet" : function = new MapKeySet(nodeProxy); break;
			case "list.mapValues" : function = new MapValues(nodeProxy); break;
			case "list.getMapValueAtKey" : function = new GetMapValueAtKey(nodeProxy); break;
			case "list.getMapValueByKeyList" : function = new GetMapValueByKeyList(nodeProxy); break;
			
			case "input.textInput" : function = new TextInput(nodeProxy); break;
			default: break;
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
