

Ext.namespace('ToolboxCatalogues');

var map;
var selectionLayer=null;
var proxyRedirect="ProxyRedirect";
var timeOutRequestCatalog=500;
var screenWidth=screen.availWidth;
var screenHeight=screen.availHeight;
var formsCatInterfObject;

function set_selectionLayer(newValue){ 
  selectionLayer=newValue;
}
ToolboxCatalogues.Application = function()
{
  var toolbar,toolbarSouth;
  var formsObject;
  var south=null, west=null;
  var mapOptions = {
        maxResolution: 1.40625/2,
        enebaleMapEvent: true
  };
  return {
	   init: function()
		{
		 toolbar = new Ext.Toolbar();
                 toolbarSouth = new Ext.Toolbar();
		 var panel = new Ext.Panel({
			border: false,
                        layout:'anchor',
                        anchor:'100% 94%',
                        tbar: toolbar
		 });
                        
                 var southToolbarPanel = new Ext.Panel({
				border: false,
                                anchor:'100% 6%',
                                html:'',
                                bodyStyle : {background: '#99bbe8'},
				tbar: toolbarSouth
                  });

                  south =new Ext.Panel({region:'south',
                                  split:true,
                                  height: 300,
                                  minSize: 200,
                                  maxSize: 700,
                                  collapsible: true,
                                  collapsed : true,
                                  title:'Response',
                                  autoScroll : true,
                                  margins:'0 0 0 0', 
                                  html: "<div id='responsePanelID'></div>" 
                  });
                 
                  var xmlDocumentUrl="resources/xml/CataloguesPanel.xml";
                  formsObject=createPanelExjFormByXml(xmlDocumentUrl);

                  var catalogueInterfacePanel= new Ext.Panel({
                            title: 'Catalogue Request Parameter',
                            border: true,
                            autoScroll : true,
                           // renderTo: "catalogueInterface",
                            //resizable : true,
                           // collapsible: true,
                           // maximizable: true,
                          /*  layout: 'border',
                            width: 400,
			    height: 350,*/
                           // layout:'accordion',

                           // items:[formsCatInterfObject.formsPanel]
                           html: "<div id='catalogueInterface'>"
                       });

                  west =new Ext.Panel({region:'west',
                                  split:true,
                                  width: (screenWidth/100)*32,
                                  //minSize: (screenWidth/100)*30,
                                  maxSize: (screenWidth/100)*50,
                                  collapsible: true,
                                  collapsed : false,
                                  title:'Catalogue Interfaces',
                                  bodyStyle : {background: '#99bbe8'},
                                  autoScroll : true,
                                  margins:'0 0 0 0',
                                  items:[formsObject.formsPanel,catalogueInterfacePanel]
                                  /*html:"<table width='100%'>"+
                                            "<tr><td><div id='cataloguePanel'></div></td></tr>"+
                                            "<tr><td><div id='catalogueInterface'></div></td></tr>"+
                                        "</table>"*/
                  });
                  
		
                      var mapTool=new Ext.Panel({
                          region: 'center',
                          layout:'anchor',
                          anchorSize: {width:1280, height:1024},
                          items:[panel,southToolbarPanel]
                      });
                      
                      var viewport = new Ext.Viewport({
                            layout:'border',
                            items:[west,south,mapTool]
                      });
                      /* var xmlDocumentUrl="resources/xml/CataloguesPanel.xml";
                       formsObject=createPanelExjFormByXml(xmlDocumentUrl);
                       
                       var catalogueWindow = new Ext.Window({
				title: 'Catalogue Interface',
				border: false,
                                animCollapse : true,
                                autoScroll : true,
                                resizable : true,
                                collapsible: true,
				layout: 'fit',
				width: 300,
				height: 220,
                                closeAction:'hide',
                                items:[formsObject.formsPanel]
			});
                       catalogueWindow.show();
                       catalogueWindow.setPosition(0,200);*/
                       formsObject.formsPanel.render(document.getElementById("cataloguePanel"));
                       formsObject.render();     
                       map = new OpenLayers.Map(panel.body.dom, mapOptions);
                       var formatWMC = new OpenLayers.Format.WMC(mapOptions);
                       var contextMap = Sarissa.getDomDocument();
                       contextMap.async=false;
                       contextMap.validateOnParse=false;
                       contextMap.load("resources/xml/WebMapContext.xml");
                       map = formatWMC.read(contextMap, {map: map});       
                       map.zoomToMaxExtent();
                       var toc = new WebGIS.Control.Toc({map: map, parseWMS: false, autoScroll: true});
                       // standard Open Layers
                        map.addControl(new OpenLayers.Control.MousePosition());
			map.addControl(new OpenLayers.Control.MouseDefaults());
			//map.addControl(new OpenLayers.Control.KeyboardDefaults());
			map.addControl(new OpenLayers.Control.LayerSwitcher());
      
			// map action is an extended Ext.Action that can be used as a button or menu item
			toolbar.add(new WebGIS.MapAction.DragPan({map: map}));
			toolbar.add(new WebGIS.MapAction.ZoomInBox({map: map}));
			toolbar.add(new WebGIS.MapAction.ZoomOutBox({map: map}));
			toolbar.add(new WebGIS.MapAction.ZoomIn({map: map}));                    
			toolbar.add(new WebGIS.MapAction.ZoomOut({map: map}));
			toolbar.add(new WebGIS.MapAction.PreviousExtent({map: map}));
			toolbar.add(new WebGIS.MapAction.NextExtent({map: map}));
			toolbar.add(new WebGIS.MapAction.FullExtent({map: map}));
                        toolbar.add(new WebGIS.MapAction.Scale({map: map}));
                        toolbar.add(new WebGIS.MapAction.OfflineMap({map: map, 
                                      offline: false,
                                      wmcURL: "resources/xml/WebMapContext.xml", 
                                      imageUrl: "resources/images/backround.png",
                                      imageAOI: "-180,-90,180,90",
                                      imageWidth: 1260,
                                      mapOptions: mapOptions,
                                      imageHeight: 630
                                  }));
			toc.update();
		},
         onChangeCatalog: function (){
           /*  if(catalogueInterfaceWindow != null){*/
            
                 formsCatInterfObject=null;
                // catalogueInterfaceWindow.close();
                document.getElementById("catalogueInterface").innerHTML="";
            /* }*/
             var value=this.getValue();
             var index=this.store.find("name", value);
             var description=this.store.getAt(index).get('description');
             formsObject.formsArray[0].getForm().findField("catalogueDescription").setValue(description);
             var catalogueInterfaceXML=this.store.getAt(index).get('formFileXml');
             
             formsCatInterfObject=createPanelExjFormByXml(catalogueInterfaceXML);
             formsCatInterfObject.formsPanel.render(document.getElementById("catalogueInterface"));
             /*catalogueInterfaceWindow= new Ext.Panel({
                            title: value+' Request Parameter',
                            border: true,
                            autoScroll : true,
                            renderTo: "catalogueInterface",
                    

                            items:[formsCatInterfObject.formsPanel]
                       });*/
            /* catalogueInterfaceWindow= new Ext.Window({
                            title: value+' Request Parameter',
                            border: false,
                            animCollapse : true,
                            autoScroll : true,
                            resizable : true,
                            collapsible: true,
                            maximizable: true,
                            layout: 'fit',
                            width: 400,
			    height: 350,
                            closeAction:'close',
                            items:[formsCatInterfObject.formsPanel]
                       });
            catalogueInterfaceWindow.show();
            catalogueInterfaceWindow.setPosition(screen.width-400-100,100);*/
            formsCatInterfObject.render();                
            
         },
         SendCatalogueRequest: function(){
            south.expand(true);
            var serviceRequest=formsCatInterfObject.sendXmlKeyValueRequest("responsePanelID",timeOutRequestCatalog);
         },
          // Reset Alphanumeric Filter       
         Reset: function(){
             formsCatInterfObject.resetFormValues();
         }
	};
}();

var tbxCat=ToolboxCatalogues.Application;
// Run the application when browser is ready
Ext.onReady(tbxCat.init, ToolboxCatalogues.Application);

