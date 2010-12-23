// MapWin Example

Ext.namespace('MapWin');

MapWin.Application = function()
{
 var mapPanel,mapWindow,map;
 var toolbarMap;
 var mapOptions = {};
 var contextMapUrl="jsScripts/widgets/mapWin/resources/WebMapContext.xml";
 return {
	 init: function()
	      {},
          viewMapGML: function(gmlUrl){
                            
              toolbarMap = new Ext.Toolbar(); 
              mapPanel = new Ext.Panel({
                          border: false,
                          layout:'anchor',
                          anchor:'100% 94%',
			  tbar: toolbarMap
              }); 
              mapWindow = new Ext.Window({
		 title: 'Map',
                 border: false,
                 animCollapse : true,
                 autoScroll : false,
                 collapsible: true,
                 maximizable: true,
		 layout: 'fit',
		 width: 700,
		 height: 400,
                 closeAction:'close',
                 items:[mapPanel]
              }); 
             mapWindow.show();
             mapWindow.setPosition(500,200);   
             map = new OpenLayers.Map(mapPanel.body.dom, mapOptions);
             var formatWMC = new OpenLayers.Format.WMC(mapOptions);
             var contextMap = Sarissa.getDomDocument();
             contextMap.async=false;
             contextMap.validateOnParse=false;
             contextMap.load(contextMapUrl);
             map = formatWMC.read(contextMap, {map: map});
             
             var gmlDocument=Sarissa.getDomDocument();
             gmlDocument.async=false;
             gmlDocument.validateOnParse=false;
             gmlDocument.load(gmlUrl);
             gmlDocument.setProperty("SelectionLanguage","XPath");
             Sarissa.setXpathNamespaces(gmlDocument,
                       "xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
             var coord=gmlDocument.selectNodes("wfs:FeatureCollection/gml:boundedBy/gml:Box/gml:coordinates");
             var pointSeparator=' ';
             var coordinateSeparator=',';
             
             map.addLayer(new OpenLayers.Layer.GML("GML", gmlUrl));
             
             if(coord[0]){
                var points=coord[0].firstChild.nodeValue.split(pointSeparator); 
                var ws=points[0].split(coordinateSeparator);
                var en=points[1].split(coordinateSeparator);
                map.zoomToExtent(new OpenLayers.Bounds(ws[0],en[1],en[0],ws[1]));
             }
             else    
               map.zoomToMaxExtent(); 
            // 
             var toc = new WebGIS.Control.Toc({map: map, parseWMS: false, autoScroll: true});
           // standard Open Layers
             map.addControl(new OpenLayers.Control.MousePosition());
           //  map.addControl(new OpenLayers.Control.MouseDefaults());
           //  map.addControl(new OpenLayers.Control.KeyboardDefaults());
           //map.addControl(new OpenLayers.Control.LayerSwitcher());
      
           // map action is an extended Ext.Action that can be used as a button or menu item
             toolbarMap.add(new WebGIS.MapAction.DragPan({map: map}));
             toolbarMap.add(new WebGIS.MapAction.ZoomInBox({map: map}));
             toolbarMap.add(new WebGIS.MapAction.ZoomOutBox({map: map}));
             toolbarMap.add(new WebGIS.MapAction.ZoomIn({map: map}));                    
             toolbarMap.add(new WebGIS.MapAction.ZoomOut({map: map}));
             toolbarMap.add(new WebGIS.MapAction.PreviousExtent({map: map}));
             toolbarMap.add(new WebGIS.MapAction.NextExtent({map: map}));
             toolbarMap.add(new WebGIS.MapAction.FullExtent({map: map}));
             toolbarMap.add(new WebGIS.MapAction.Scale({map: map}));
             toc.update(); 
          }      
	};
    
}();

var mapWin=MapWin.Application;
// Run the application when browser is ready
Ext.onReady(mapWin.init, MapWin.Application);




