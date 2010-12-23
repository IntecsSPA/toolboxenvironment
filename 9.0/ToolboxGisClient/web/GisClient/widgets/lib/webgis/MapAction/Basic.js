/**
 * WebGIS JS Library
 * Copyright(c) 2007, Sweco Position
 *
 * Licensed under GPLv3
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Author: Björn Harrtell
 *
 * @fileoverview Basic map tools implemented as WebGIS.MapAction classes
 */

Ext.namespace('WebGIS', 'WebGIS.MapAction');

/**
 * @class Activates interactive zoom in box on map
 * @extends WebGIS.MapAction
 * @param {String} config WebGIS.MapAction config options
 */
WebGIS.MapAction.ZoomInBox = function(config) {
    // default config for this action, also used by button to make it toggle correctly
    config.iconCls = 'webgis-mapaction-zoominbox';
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
	
    // define an OpenLayers control for this MapAction (is handled by MapAction constructor)
    config.olcontrol = new OpenLayers.Control.ZoomBox();
	
    // call Ext.Action constructor
    WebGIS.MapAction.ZoomInBox.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.ZoomInBox, WebGIS.MapAction, {
});

/**
 * @class Activates interactive zoom out box on map
 * @extends WebGIS.MapAction
 * @param {String} config WebGIS.MapAction config options
 */
WebGIS.MapAction.ZoomOutBox = function(config) {
    config.iconCls = 'webgis-mapaction-zoomoutbox';
    config.olcontrol = new OpenLayers.Control.ZoomBox({out:true});
	
    WebGIS.MapAction.ZoomOutBox.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.ZoomOutBox, WebGIS.MapAction, { });

/**
 * @class Zooms in one zoomstep
 * @extends WebGIS.MapAction
 * @param {String} config WebGIS.MapAction config options
 */
WebGIS.MapAction.ZoomIn = function(config) {
    config.iconCls = 'webgis-mapaction-zoomin';
    config.handler = function() {
        for (i=0; i<WebGIS.MapAction.navigationActions.length; i++) WebGIS.MapAction.navigationActions[i].disable();
        this.map.zoomIn();
    }
	
    WebGIS.MapAction.navigationActions.push(this);
	
    WebGIS.MapAction.ZoomIn.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.ZoomIn, WebGIS.MapAction, { });

/**
 * @class Zooms out one zoomstep
 * @extends WebGIS.MapAction
 * @param {String} config WebGIS.MapAction config options
 */
WebGIS.MapAction.ZoomOut = function(config) {
    config.iconCls = 'webgis-mapaction-zoomout';
    config.handler = function() {
        for (i=0; i<WebGIS.MapAction.navigationActions.length; i++) WebGIS.MapAction.navigationActions[i].disable();
        this.map.zoomOut();
    }
	
    WebGIS.MapAction.navigationActions.push(this);
	
    WebGIS.MapAction.ZoomOut.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.ZoomOut, WebGIS.MapAction, { });

/**
 * @class Zooms map to full extent
 * @extends WebGIS.MapAction
 * @param {Object} config WebGIS.MapAction config options
 */
WebGIS.MapAction.FullExtent = function(config) {
    config.iconCls = 'webgis-mapaction-fullextent';
    config.handler = function() {
        for (i=0; i<WebGIS.MapAction.navigationActions.length; i++) WebGIS.MapAction.navigationActions[i].disable();
        this.map.zoomToMaxExtent();
    }
	
    WebGIS.MapAction.navigationActions.push(this);
	
    WebGIS.MapAction.FullExtent.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.FullExtent, WebGIS.MapAction, { });

/**
 * @class Activates interactive drag pan on map
 * @extends WebGIS.MapAction
 * @param {Object} config WebGIS.MapAction config options
 */
WebGIS.MapAction.DragPan = function(config) {
    config.iconCls = 'webgis-mapaction-dragpan';
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
    config.olcontrol = new OpenLayers.Control.DragPan();
	
    WebGIS.MapAction.DragPan.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.DragPan, WebGIS.MapAction, { });

/**
 * @class
 * @extends WebGIS.MapAction
 * @param {Object} config WebGIS.MapAction config options
 */
WebGIS.MapAction.PreviousExtent = function(config) {
    config.iconCls = 'webgis-mapaction-previousextent';
    config.disabled = true;
    config.handler = function() {
        if (WebGIS.MapAction.currentHistoryPosition<(WebGIS.MapAction.navigationHistory.length-1)) {
            for (i=0; i<WebGIS.MapAction.navigationActions.length; i++) WebGIS.MapAction.navigationActions[i].disable();
            WebGIS.MapAction.currentHistoryPosition++;
            this.map.zoomToExtent(WebGIS.MapAction.navigationHistory[WebGIS.MapAction.currentHistoryPosition]);
        }
    }
	
    WebGIS.MapAction.navigationActions.push(this);
    WebGIS.MapAction.previousExtentActions.push(this);
	
    WebGIS.MapAction.PreviousExtent.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.PreviousExtent, WebGIS.MapAction, { });



/**
 * @class Select AOI
 * @extends WebGIS.MapAction
 * @param {String} config WebGIS.MapAction config options
 */
WebGIS.MapAction.SelectAOI = function(config) {
    config.iconCls = 'webgis-mapaction-selectAOI';
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
    
    var setBox=new OpenLayers.Control.SetBox();
    setBox.onChangeAOI=config.onChangeAOI;
    config.olcontrol =setBox ;
	
	
    WebGIS.MapAction.SelectAOI.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.SelectAOI, WebGIS.MapAction, { });




/**
 * @class
 * @extends WebGIS.MapAction
 * @param {Object} config WebGIS.MapAction config options
 * @param {Array Object} selectableLayers 
 */
var selectWMSControl=null; //Define Selection and Remove Selection
WebGIS.MapAction.SelectionWMS = function(config, selectableLayers, selectionStyle) {
    config.iconCls = 'webgis-mapaction-selection';
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
	
    // define an OpenLayers control for this MapAction (is handled by MapAction constructor)
    selectWMSControl=new OpenLayers.Control.SelectWMS();
    selectWMSControl.setSelectableLayers(selectableLayers);
    if(selectionStyle)
       selectWMSControl.setSimbolyzerSelection(selectionStyle); 
    config.olcontrol = selectWMSControl;
	
    // call Ext.Action constructor
    WebGIS.MapAction.SelectionWMS.superclass.constructor.call(this, config);
    
    this.setSelectableLayers= function(selectableLayers){
      selectWMSControl.setSelectableLayers(selectableLayers);
    };
   
    this.getSelectionFilter=function(layername){
       
      return selectWMSControl.getSelectionFilter(layername);  
    }
    
    this.getSelectionFilterEncoded=function(layername){
       
      return selectWMSControl.getSelectionFilterEncoded(layername);  
    }
    
    this.unSelect=function(){
       
      return selectWMSControl.removeSelectedLayer();  
    }
}
Ext.extend(WebGIS.MapAction.SelectionWMS, WebGIS.MapAction, { });

/**
 * @class
 * @extends WebGIS.MapAction
 * @param {Object} config WebGIS.MapAction config options 
 */
WebGIS.MapAction.RemoveSelectionWMS = function(config) {
    config.iconCls = 'webgis-mapaction-remove-selection';
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
	
    if(!selectWMSControl){
        alert("In order to define the RemoveSelectionWMS button is mandatory\n\
              to define the SelectionWMS button");
    }   
    else{
      // define an OpenLayers control for this MapAction (is handled by MapAction constructor)
      var removeSelectWMSControl=new OpenLayers.Control.RemoveSelectWMS();
      removeSelectWMSControl.setSelectWMSControl(selectWMSControl);
      config.olcontrol = removeSelectWMSControl;
   }
	
    // call Ext.Action constructor
    WebGIS.MapAction.RemoveSelectionWMS.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.RemoveSelectionWMS, WebGIS.MapAction, { });


var scalewindow;
WebGIS.MapAction.Scale = function(config) {
    // default config for this action, also used by button to make it toggle correctly
    config.iconCls = 'webgis-mapaction-scale';
    scaleWindow = new Ext.Window({
				title: 'Scales',
				border: false,
				layout: 'form',
                                collapsible: true,
				hideLabels: true,
				width: 200,
                                closeAction:'hide',
				height: 50,
				items: {xtype: 'webgis-scalelist', map: config.map}
			})
			//scaleWindow.show();
			scaleWindow.setPosition(0,150);
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
    config.olcontrol = new OpenLayers.Control({
                             activate: function() {
                                     if(!scaleWindow.isVisible()){
                                         scaleWindow.show();
                                         scaleWindow.setPosition(0,150);
                                     }
                                        
                                     else
                                        scaleWindow.hide(); 
                                 }
                            });
	
    // call Ext.Action constructor
    WebGIS.MapAction.Scale.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.Scale, WebGIS.MapAction, {
});

/**
 * @class
 * @extends WebGIS.MapAction
 * @param {Object} config WebGIS.MapAction config options
 */
WebGIS.MapAction.NextExtent = function(config) {
    config.iconCls = 'webgis-mapaction-nextextent';
    config.disabled = true;
    config.handler = function() {
        if (WebGIS.MapAction.currentHistoryPosition>0) {
            for (i=0; i<WebGIS.MapAction.navigationActions.length; i++) WebGIS.MapAction.navigationActions[i].disable();
            WebGIS.MapAction.currentHistoryPosition--;
            this.map.zoomToExtent(WebGIS.MapAction.navigationHistory[WebGIS.MapAction.currentHistoryPosition]);
        }
    }
	
    WebGIS.MapAction.navigationActions.push(this);
    WebGIS.MapAction.nextExtentActions.push(this);
	
    WebGIS.MapAction.NextExtent.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.NextExtent, WebGIS.MapAction, { });

var legendWindow;
WebGIS.MapAction.SimpleLegend = function(config) {
    config.iconCls = 'webgis-mapaction-simplelegend';

   var othersParam="";
   if(config.SLD)
      othersParam+="&SLD="+config.SLD
   if(config.SLD_BODY)
      othersParam+="&SLD_BODY="+config.SLD_BODY   
  var url=config.legendServiceURL+"?REQUEST=GetLegendGraphic&VERSION=1.0.0&"+
       "FORMAT="+config.format+"&WIDTH="+config.width+"&HEIGHT="+config.height+
        "&LAYER="+config.layer+othersParam
   
    
    legendWindow = new Ext.Window({
				title: config.legendTitle,
				border: false,
                                id: 'Legend',
				layout: 'fit',
                                collapsible: true,
                                closeAction:'hide',
				hideLabels: true,
                                resizable: false,
				width: config.widthWin,
				height: config.heightWin,
				html: "<img src='"+url+"'/>"
			})
			//legendWindow.show();
			//legendWindow.setPosition(0,450);
                        
    config.enableToggle = true;
    config.toggleGroup = 'WebGIS.MapAction';
    config.olcontrol = new OpenLayers.Control({
                             activate: function() {
                                     if(!legendWindow.isVisible())
                                        legendWindow.show();
                                     else
                                        legendWindow.hide(); 
                                 }
                            });
	
    // call Ext.Action constructor
    WebGIS.MapAction.SimpleLegend.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.SimpleLegend, WebGIS.MapAction, {
});

WebGIS.MapAction.OfflineMap = function(config) { 
  /*/if(config.offline)
    config.iconCls = 'webgis-mapaction-map-online';    
  else 
    config.iconCls = 'webgis-mapaction-map-offline';*/
  config.iconCls = 'webgis-mapaction-map-online-offline';
  config.enableToggle = true;
  config.toggleGroup = 'WebGIS.MapAction';
  config.olcontrol = new OpenLayers.Control({
           activate: function() {
                         if(config.offline){
                               Ext.Msg.show({
                                             title:'Toggle OnLine',
                                             msg: 'You are select the Online Map modality. You are sure?',
                                             buttons: Ext.Msg.YESNO,
                                             fn: function(btn){
                                                    if(btn == 'yes'){
                                                        //config.iconCls = 'webgis-mapaction-map-offline';
                                                        config.offline=false;
                                                        for(var i=0; i<config.map.layers.length; i++){

                                                                config.map.removeLayer(config.map.layers[i]);
                                                        } 
                                                        var formatWMC = new OpenLayers.Format.WMC(config.mapOptions);
                                                        var contextMap = Sarissa.getDomDocument();
                                                        contextMap.async=false;
                                                        contextMap.validateOnParse=false;
                                                        contextMap.load(config.wmcURL);
                                                        config.map = formatWMC.read(contextMap, {map: config.map});  
                                                        //WebGIS.MapAction.OfflineMap.superclass.constructor.call(this, config);
                                                       }
                                                  },
                                                  animEl: 'elId',
                                                  icon: Ext.MessageBox.QUESTION
                                           }); 
                         }else{
                              Ext.Msg.show({
                                            title:'Toggle OffLine',
                                            msg: 'You are select the Offline Map modality. You are sure?',
                                            buttons: Ext.Msg.YESNO,
                                            fn: function(btn){
                                                      if(btn == 'yes'){
                                                          config.offline=true;
                                                          //config.iconCls = 'webgis-mapaction-map-online';
                                                          for(var i=0; i<config.map.layers.length; i++){
                                                  
                                                                config.map.removeLayer(config.map.layers[i]);
                                                          } 
                                                          var aoiCoord=config.imageAOI.split(',');
                                                          var graphic = new OpenLayers.Layer.Image(
                                                              'BackroundImage',
                                                              config.imageUrl,
                                                              new OpenLayers.Bounds(aoiCoord[0], aoiCoord[1], aoiCoord[2], aoiCoord[3]),
                                                              new OpenLayers.Size(config.imageWidth, config.imageHeight));
                                                              config.map.addLayers([graphic]); 
                                                           //WebGIS.MapAction.OfflineMap.superclass.constructor.call(this, config);    
                                                      }
                                             },
                                              animEl: 'elId',
                                              icon: Ext.MessageBox.QUESTION
                                           });  
                                        }
             }
   });
   config.map.zoomToMaxExtent();
   WebGIS.MapAction.OfflineMap.superclass.constructor.call(this, config);
}
Ext.extend(WebGIS.MapAction.OfflineMap, WebGIS.MapAction, { });

