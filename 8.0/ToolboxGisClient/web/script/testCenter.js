

Ext.namespace('ToolboxTestCenter');
/*Global Definitions*/
  /*alert(localToolboxURL); */
 // alert(serviceSelected); 
  var proxyRedirect="ProxyRedirect";
  var toolsServlet="Tools";
  var featureCollectionTagOpen="<wfs:FeatureCollection wfs:xmnls=\"http://www.opengis.net/wfs\">";
  var featureCollectionTagClose="</wfs:FeatureCollection>";
  var aoiTagOpen ="<areaOfInterest xmlns=\"http://www.esa.int/xml/schemas/mass/aoifeatures\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.esa.int/xml/schemas/mass/aoifeatures http://services.eoportal.org/schemas/1.5/GML212/aoifeatures.xsd\">";
  var aoiTagClose ="</areaOfInterest>";
  var keyValueTagClose="</KeyValues>";
  var tempResponse;
  var map;
  var contextMapUrl="resources/xml/WebMapContext.xml";
  var xmlToolboxGenericServiceUrl="resources/xml/ToolboxServicesGenericInterface.xml";
  var xmlToolboxInfoUrl="resources/xml/ToolboxServicePanel.xml";
  var xmlWebServiceGenericUrl="resources/xml/WebServicePanel.xml";
  var mapActive=false;
  var selectionLayer=null;
  var desktopImageURL="style/img/desktop/ToolboxDesktop.png";
  var numberToolboxInput=0;
  var editAreaPath="jsScripts/import/edit_area/edit_area_full.js";
  var screenWidth=screen.availWidth;
  var screenHeight=screen.availHeight;
  var screenRes=screen.availWidth/screen.availHeight;

  var genericOutputTabPanel,genericClientPanel;

function set_selectionLayer(newValue){
    selectionLayer=newValue;
}
/*End Global Definitions*/

ToolboxTestCenter.Application = function()
{
 
 var toolbarMap,toolbarApplication;
 var formsToolboxInfo, formsCatInterfObject,newOnChange,toolboxInforamtionsPanel;
 var toolboxClientWindow=null;
 var webServicesClientWindow=null;
 var desktopPanel=null;
 var mapWindow=null;
 var mapPanel=null;
 var layerAOI=null;
 var southToolbarPanel,toolboxOperationTabPanel;
 var toolboxClientWindowHeight=(screen.height/100)*71;
 var toolboxClientWindowWidth=(screen.width/100)*80;
 var mapWindowHeight=(screen.height/100)*70;
 var mapWindowWidth=(screen.width/100)*65;
 
 var textAreaFrameHToolbox=(screen.height/100)*44;
 var textAreaFrameWToolbox=(screen.width/100)*75;
 var textAreaWToolbox=(screen.width/100)*74;
 var textAreaHToolbox=(screen.height/100)*43;

 var textAreaFrameHGeneric=(screen.height/100)*55;
 var textAreaFrameWGeneric=(screen.width/100)*46;
 var textAreaWGeneric=(screen.width/100)*45;
 var textAreaHGeneric=(screen.height/100)*54;

 var textAreaWGenericOutput=(screen.width/100)*45;
 var textAreaHGenericOutput=(screen.height/100)*59;

 var mapOptions = {
		maxResolution: 1.40625/2
 };  
 var operationRendering;

	return {
	 init: function(){
                  toolbarApplication = new Ext.Toolbar();
                  this.desktopChange('image');
                  this.createMapWindow();
                  mapWindow.hide();
                  
                  toolboxInforamtionsPanel=new Ext.Panel({
                                                region:'west',
                                                id:'toolboxInformationsRegion',
                                                title:'Toolbox Informations',
                                                split:true,
                                                width: toolboxClientWindowWidth-screen.width/100,
                                                minSize: 175,
                                                bodyStyle : {background: '#99bbe8'},
                                                maxSize: toolboxClientWindowWidth-screen.width/100,
                                                collapsible: true,
                                                autoScroll:true,
                                                margins:'0 0 0 0',
                                                html: "<div id='toolboxInformationInterface'/>",
                                                layoutConfig:{
                                                    animate:true
                                                }
                  });
                  southToolbarPanel = new Ext.Panel({
                      border: false,
                      anchor:'100% 6%',
                      html:'',
                      bodyStyle : {background: '#99bbe8'},
                      tbar: toolbarApplication
                  });

                  toolboxOperationTabPanel =new Ext.TabPanel({
                           region:'center',
                           deferredRender:false,
                           activeTab:0,
                           autoScroll:true,
                           //animScroll: true,
                           enableTabScroll : true,
                        /*/   listeners: {
                                tabchange: function(tp,newTab){
                                    alert("toolbox Tab change");
                             }
                    }, */
                    items:[{
                        //contentEl:'welcome',
                        html: "<div id='toolboxOutputInformation'></div>",
                        bodyStyle : {background: '#dfe8f6'},
                        title: 'Operation Interfaces',
                        id: 'operationInterface',
                        closable:false,
                        autoScroll:true
                    }]
                });
   
                  var testCenterPanel=new Ext.Panel({
                       region: 'center',
                       layout:'anchor',
                       anchorSize: {width:1280, height:1024},
                       items:[desktopPanel,southToolbarPanel]
                  });
                      
                  var viewport = new Ext.Viewport({
                        layout:'border',
                        items:[testCenterPanel]
                  });
                  if(mapActive)
                     this.setMap();
                  
                  formsToolboxInfo=createPanelExjFormByXml(xmlToolboxInfoUrl);
                  toolboxClientWindow = new Ext.Window({
				title: 'Toolbox Services Client',
				border: false,
                                animCollapse : true,
                                autoScroll : true,
                                collapsible: true,
                                maximizable: true,
				layout: 'fit',
				width: toolboxClientWindowWidth,
				height: toolboxClientWindowHeight,
                                closeAction:'hide',
                                items:[new Ext.Panel({
                                            layout:'border',
                                            items:[toolboxOperationTabPanel,toolboxInforamtionsPanel
                                              ]     
                                     })  
                                    ]
                  });
                        
	          toolboxClientWindow.show();
                  toolboxClientWindow.setPosition(0,0);
                  formsToolboxInfo.formsPanel.render(document.getElementById("toolboxInformationInterface"));    
                  formsToolboxInfo.render();  
                  toolboxClientWindow.hide();        
                  
                  newOnChange=function(){
                               //formsToolboxInfo.formsArray[0].getForm().findField('serviceOperation').disable();
                               var index=this.store.find("name", this.value);
                               var functionOn=this.store.getAt(index).get('onChange'); 
                               eval(functionOn)/*.call()*/;
                  };
                  toolbarApplication.add({
                          cls: 'x-btn-text-icon webgis-mapaction-toolboxclient', // icon and text class
                          text:'Toolbox Services Client',
                          handler: function() {
                                     if(!toolboxClientWindow.isVisible())
                                        toolboxClientWindow.show();
                                     else
                                        toolboxClientWindow.hide(); 
                                 }
                          //tooltipText: 'Open/Close Web Services Generic Client Panel'
                 });  

                 var catalogueClientWindow=new Ext.Window({
				title: 'Catalog Client',
				border: false,
                                animCollapse : true,
                                autoScroll : true,
                                maximizable: true,
                                collapsible: true,
				layout: 'fit',
                                x: 0,
                                y: 0,
				width: screenWidth,
				height: screenHeight/100*74,
                                closeAction:'hide',
                                html:"<iframe scrolling='auto' src='catalogues.html' name='catalogues' width='99.9%' height='99%' marginwidth='0' marginheight='0'></iframe>"
                 });
                 toolbarApplication.add({
                          cls: 'x-btn-text-icon webgis-mapaction-toolboxcatalogueclient', // icon and text class
                          text:'Catalogues Client',
                          handler: function() {
                                     if(!catalogueClientWindow.isVisible())
                                        catalogueClientWindow.show();
                                     else
                                        catalogueClientWindow.hide(); 
                                 }
                          //tooltipText: 'Open/Close Web Services Generic Client Panel'
                 });  

                 toolbarApplication.add({
                          cls: 'x-btn-text-icon webgis-mapaction-wsclient', // icon and text class
                          text:'Web Services Generic Client',
                          handler: function() {
                                     
                                     if(webServicesClientWindow){
                                         //alert(webServicesClientWindow.isVisible());
                                        if(webServicesClientWindow.isVisible()){ 
                                            webServicesClientWindow.close();
                                            webServicesClientWindow=null;
                                        }else{
                                           webServicesClientWindow.close();
                                           webServicesClientWindow=null; 
                                           tbxTCenter.createGenericWSClient(); 
                                        }
                                     }   
                                     else
                                        tbxTCenter.createGenericWSClient(); 
                                 }
                          //tooltipText: 'Open/Close Web Services Generic Client Panel'
                 }); 	
                 if((serviceSelected!="null")&&(serviceSelected!="")){
                    toolboxClientWindow.show();
                    formsToolboxInfo.formsArray[0].getForm().findField("toolboxURL").setValue(localToolboxURL);
                    tbxTCenter.getToolboxServices();
                    formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").setValue(serviceSelected);
                 }else{
                    webServicesClientWindow=null; 
                    formsToolboxInfo.formsArray[0].getForm().findField("toolboxURL").setValue(localToolboxURL);
                    tbxTCenter.createGenericWSClient(localToolboxURL); 
                 }
                  
		},	
         createMapWindow:function(){
             
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
                 maximizable: true,
                 collapsible: true,
		 layout: 'fit',
		 width: mapWindowWidth,
		 height: mapWindowHeight,
                 closeAction:'hide',
                 items:[mapPanel]
              }); 
             mapWindow.show();
             mapWindow.setPosition(toolboxClientWindowWidth,0);   
             map = new OpenLayers.Map(mapPanel.body.dom, mapOptions);
             var formatWMC = new OpenLayers.Format.WMC(mapOptions);
             var contextMap = Sarissa.getDomDocument();
             contextMap.async=false;
             contextMap.validateOnParse=false;
             contextMap.load(contextMapUrl);
             
             
             
             layerAOI = new OpenLayers.Layer.Vector( "AOI" );
             map.addLayer(layerAOI);
             map = formatWMC.read(contextMap, {map: map});
             map.zoomToMaxExtent();
             var toc = new WebGIS.Control.Toc({map: map, parseWMS: false, autoScroll: true});
             
             WebGIS.MapAction.DeleteAOI = function(config) {
                config.iconCls = 'webgis-mapaction-removeAOI';
                config.enableToggle = true;
                config.toggleGroup = 'WebGIS.MapAction';
                config.olcontrol = new OpenLayers.Control({
                    activate: function() {
                                 
                                 config.layerAOI.destroyFeatures();
                                 
                              }
                });
                WebGIS.MapAction.DeleteAOI.superclass.constructor.call(this, config);
             }
             Ext.extend(WebGIS.MapAction.DeleteAOI, WebGIS.MapAction, {});
             Ext.apply(WebGIS.MapAction.DeleteAOI.prototype, {
                   titleText: 'Remove AOI Polygon',
                   tooltipText: 'Remove Area Of Interest Polygon'
             }); 
             
             
             Ext.extend(WebGIS.MapAction.DrawFeature, WebGIS.MapAction, {});
             Ext.apply(WebGIS.MapAction.DrawFeature.prototype, {
                   titleText: 'Draw AOI Polygon',
                   tooltipText: 'Draw Area Of Interest Polygon'
             }); 
           // standard Open Layers
             map.addControl(new OpenLayers.Control.MousePosition());
           //  map.addControl(new OpenLayers.Control.MouseDefaults());
           //  map.addControl(new OpenLayers.Control.KeyboardDefaults());
            map.addControl(new OpenLayers.Control.LayerSwitcher());
      
           // map action is an extended Ext.Action that can be used as a button or menu item
             toolbarMap.add(new WebGIS.MapAction.DragPan({map: map}));
             toolbarMap.add(new WebGIS.MapAction.ZoomInBox({map: map}));
             toolbarMap.add(new WebGIS.MapAction.ZoomOutBox({map: map}));
            /*/ toolbarMap.add(new WebGIS.MapAction.ZoomIn({map: map}));                    
             toolbarMap.add(new WebGIS.MapAction.ZoomOut({map: map}));*/
             toolbarMap.add(new WebGIS.MapAction.PreviousExtent({map: map}));
             toolbarMap.add(new WebGIS.MapAction.NextExtent({map: map}));
             toolbarMap.add(new WebGIS.MapAction.FullExtent({map: map}));
             toolbarMap.add(new WebGIS.MapAction.DrawFeature({map: map, iconCls: 'webgis-mapaction-drawAOI', geometryType:'OpenLayers.Geometry.Polygon', layer: layerAOI}));
             toolbarMap.add(new WebGIS.MapAction.DeleteAOI({map: map, layerAOI: layerAOI }));
             toolbarMap.add(new WebGIS.MapAction.Scale({map: map}));
             /*/toolbarMap.add(new WebGIS.MapAction.OfflineMap({map: map, 
                                      offline: false,
                                      wmcURL: contextMapUrl, 
                                      imageUrl: "resources/images/backround.png",
                                      imageAOI: "-180,-90,180,90",
                                      imageWidth: 1260,
                                      mapOptions: mapOptions,
                                      imageHeight: 630
                                  }));*/
             toc.update(); 
         },  
         createGenericWSClient: function(toolboxUrl){
             if(!toolboxUrl)
                 toolboxUrl="http://localhost:8080/TOOLBOX/services/WPSTest";
             var id="genericWebServicesClientInput";
             var serviceInfo="<table align='center'><tr><td><br><table width='100%'><tr width='100%'rowspan='2' BGCOLOR='#325e8f'><td><b style='color: #ffffff;'>Web Service URL: </b><br></td></tr>"+
                                  "<tr><td><input type='text' name='genericWebServiceOperationURL' id='genericWebServiceOperationURL' size='50' value='"+toolboxUrl+"'/></td></tr>"+
                                  "<br><tr width='100%'rowspan='2' BGCOLOR='#325e8f'><td><b style='color: #ffffff;'>SOAP Action: </b><br></td></tr>"+
                                  "<tr><td><input type='text' name='genericWebServiceSoapAction' id='genericWebServiceSoapAction' size='40' value='ExecuteProcess_TestOperation'/></td></tr>"+
                               "</td></tr></table><br></td></tr>";
             var genericClientHtml=serviceInfo+"<tr><td><table width='100%'><tr rowspan='2' BGCOLOR='#325e8f' width='100%'><td><b style='color: #ffffff;'>SOAP message: </b><br></td></tr><tr><td align='center'>"+
                                     "<form name='formFile_"+id+"' action='"+toolsServlet+"?cmd=putFile&type=multipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaWGeneric+"&rows="+textAreaHGeneric+"' method='POST' enctype='multipart/form-data' target='iframeRequest_"+id+"'>"+
                                        "<input type='file' id='FILE' name='FILE' value='' width='25' />"+
                                        "<input type='submit' name='buttonSubmit_"+id+"' value='Load Request'/>"+
                                        "<input type='button' name='buttonSendGenericRequest_"+id+"' value='Send Request' onclick='tbxTCenter.sendGenericRequest(document.getElementById(\"genericWebServiceOperationURL\").value, document.getElementById(\"genericWebServiceSoapAction\").value, parent.iframeRequest_genericWebServicesClientInput.window.getEditAreaValue());'/>"+
                                    "</form></td></tr><tr align='center'><td>"+
                                    "<iframe scrolling='no' FRAMEBORDER='0' src='"+toolsServlet+"?cmd=putFile&type=nomultipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaWGeneric+"&rows="+textAreaHGeneric+"' name='iframeRequest_"+id+"' width='"+textAreaFrameWGeneric+"' height='"+textAreaFrameHGeneric+"' marginwidth='0' marginheight='0'></iframe>"+
                                   // "<tr><td><input type='button' name='buttonSendGenericRequest_"+id+"' value='Send Request' onclick='tbxTCenter.sendGenericRequest(document.getElementById(\"genericWebServiceOperationURL\").value, document.getElementById(\"genericWebServiceSoapAction\").value, parent.iframeRequest_genericWebServicesClientInput.window.getEditAreaValue());'/></td></tr>"+
                                    "</table>"+
                                     "</td></tr></table>";
//genericOutputTabPanel.collapse(true);

             genericClientPanel=new Ext.Panel({
                       region:'west',
                       id:'genericClientInformationsRegion',
                       title:'Web Service Information',
                       width: toolboxClientWindowWidth*1.2/2,
                       minSize: 175,
                       autoScroll:true ,
                       bodyStyle : {background: '#dfe8f6'},
                       maxSize: toolboxClientWindowWidth*1.2/2,
                       margins:'0 0 0 0',
                       //collapsible: true,
                       html: genericClientHtml,
                       layoutConfig:{
                          animate:true
                       }

            });

           

             genericOutputTabPanel =new Ext.TabPanel({
                    region:'center',
                    deferredRender:false,
                    activeTab:0,
                   /* width: toolboxClientWindowWidth-screen.width/100,
                    minSize: 175,*/
                    enableTabScroll : true,
                    autoScroll:true,
                    //collapsible: true,
                    items:[{
                        html: "<div id='genericOutputInformation'></div>",
                        bodyStyle : {background: '#dfe8f6'},
                        title: 'Operation Response',
                       // id: 'operationInterface',
                        closable:false,
                        autoScroll:true
                    }]
                });

         
            webServicesClientWindow = new Ext.Window({
				title: 'Web Services Generic Client',
				border: false,
                                animCollapse : true,
                                autoScroll : true,
                               // collapsible: true,
                                resizable: true,
                                maximizable: true,
                              //  maximized: true,
				layout: 'fit',
				width: toolboxClientWindowWidth*1.2,
                                height: toolboxClientWindowHeight*1.1,
                                closeAction:'hide',
                                items:[new Ext.Panel({
                                            layout:'border',
                                            items:[genericOutputTabPanel,genericClientPanel
                                              ]     
                                     })  
                                    ]
                  });
             webServicesClientWindow.show();
         },
         getToolboxServices: function(){
             
             var toolboxUrl=formsToolboxInfo.formsArray[0].getForm().findField("toolboxURL").getValue();
             formsToolboxInfo.render();
           
             if(toolboxUrl.substr(toolboxUrl.length-1,toolboxUrl.length) != '/')
                 toolboxUrl+='/';
             var ajax = assignXMLHttpRequest(); 
             //barProgress=Ext.Msg.progress("Toolbox Test Center", 'Sending request', "Please Wait.." );
             var loading="<table width='100%'><tr><td align='center'><img src='style/img/loader/loader1.gif'></td></tr><tr><td align='center'>Please Wait...</td></tr></table>";
             formsToolboxInfo.formsArray[0].getForm().findField("toolboxInformation").setValue(loading);
             ajax.open("GET", proxyRedirect+"?url="+toolboxUrl+"manager?cmd=getVersion", true);
             
                   /*/ajax.setRequestHeader("content-type", "text/xml; charset=utf-8");
                   ajax.setRequestHeader("Content-Length", serviceRequest.length);*/
             ajax.setRequestHeader("connection", "close");	
                  
                   ajax.onreadystatechange= function(){
                      if(ajax.readyState == 4) {
                          //alert(ajax.responseText);
                          var versionResponse = (new DOMParser()).parseFromString(ajax.responseText, "text/xml");  
                        
                          var temp= versionResponse.selectNodes("versionDescription");
                          var version=temp[0].getAttribute("ToolboxVersion");
                          var info="<p><b>Version :</b> &nbsp;"+version +"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=''>Details</a></p></br><b>Client : </b>";
                          if(version && version >= "7.1"){
                              info+="&nbsp;&nbsp;&nbsp; Supported";
                              var newRequest=assignXMLHttpRequest();
                              newRequest.open("GET", proxyRedirect+"?url="+toolboxUrl+"manager?cmd=GetServiceList", true);
                              newRequest.setRequestHeader("connection", "close");
                              newRequest.onreadystatechange= function(){
                                                            if(newRequest.readyState == 4) {
                                                                var xmlDocumentUrl='resources/xml/ToolboxServicePanel.xml';
                                                                formsToolboxInfo.formsPanel.destroy();
                                                                var oldUrl=formsToolboxInfo.formsArray[0].getForm().findField("toolboxURL").getValue();
                                                                formsToolboxInfo=createPanelExjFormByXml(xmlDocumentUrl);
                                                                formsToolboxInfo.formsPanel.render(document.getElementById("toolboxInformationInterface"));
                                                                formsToolboxInfo.render();
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxURL").setValue(oldUrl);    
                                                                var servicesResponse=(new DOMParser()).parseFromString(newRequest.responseText, "text/xml");
                                                                var services= servicesResponse.selectNodes("serviceList/service");
                                                                var newServiceList=new Array();
                                                                var temp,operations,operationList,k,functionOnChange,operationOnChange;
                                                                var newStoreField="['name','soapAction','onChange']";
                                                                for(var i=0;i<services.length; i++){
                                                                    temp=new Array();
                                                                    operationList="[";
                                                                    operations=services[i].selectNodes("operation");
                                                                    for(k=0;k<operations.length;k++){
                                                                        operationOnChange="tbxTCenter.xslServiceRequestInformations('"+oldUrl+"','"+services[i].getAttribute("name")+"','"+operations[k].getAttribute("name")+"');";
                                                                        operationList+="['"+operations[k].getAttribute("name")+"','"+operations[k].getAttribute("soapAction")+"',\""+operationOnChange+"\"],";
                                                                    }
                                                                    operationList=operationList.substr(0,operationList.length-1);
                                                                   
                                                                    operationList+="]";
                                                                    
                                                                    temp.push(services[i].getAttribute("name"));
                                                                    functionOnChange=//"function(){"+
                                                                        "storeData="+operationList+";"+
                                                                        "storeFields="+newStoreField+";"+
                                                                        "var oldStoreDataServices=formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').arrayStore;"+
                                                                        "var oldUrl=formsToolboxInfo.formsArray[0].getForm().findField('toolboxURL').getValue();"+
                                                                        "var oldToolboxInformations=formsToolboxInfo.formsArray[0].getForm().findField('toolboxInformation').value;"+
                                                                         "var oldService=formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').getValue();"+
                                                                        "formsToolboxInfo.formsPanel.destroy();"+   
                                                                        "var xmlDocumentUrl='resources/xml/ToolboxServicePanel.xml';"+
                                                                        "formsToolboxInfo=createPanelExjFormByXml(xmlDocumentUrl);"+
                                                                        "formsToolboxInfo.formsPanel.render(document.getElementById('toolboxInformationInterface'));"+
                                                                        "formsToolboxInfo.render();"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxURL').setValue(oldUrl);"+
                                                                          /*TEST*/
                                                                       // "tbxTCenter.xslServiceRequestInformations(oldUrl,oldService);"+
                                                                          /*FINE TEST*/
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxInformation').setValue(oldToolboxInformations);"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').setValue(oldService);"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').on('select', newOnChange);"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').on('change', newOnChange);"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').setStore(oldStoreDataServices,['name','onChange'],'name');"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('toolboxSerivces').enable();"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('serviceOperation').setStore(storeData,storeFields,'name');"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('serviceOperation').on('select', newOnChange);"+
                                                                        "formsToolboxInfo.formsArray[0].getForm().findField('serviceOperation').enable();";//+
                                                                     //   "}";
                                                                    temp.push(functionOnChange);
                                                                    newServiceList.push(temp);
                                                                } 
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").reset();
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").initComponent();
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").on('select', newOnChange);
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").setStore(newServiceList,['name','onChange'],'name');
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").enable();
                                                                formsToolboxInfo.formsArray[0].getForm().findField("toolboxInformation").setValue(info);
                                                            }
                                                        }
                             newRequest.send(" "); 
                          }else
                              info+="&nbsp;&nbsp;&nbsp; Not Supported";
                          
                      }
                          
                       
                         
                      }
                  
                  ajax.send(" "); 
                 // barProgress=barProgress.updateProgress(0.2,  "Waiting Response", "Request Sent" );
               
             
         },
         xslServiceRequestInformations: function(toolboxUrl, toolboxService, toolboxOperation){ 
            var loading="<table width='100%'><tr><td align='center'><img src='style/img/loader/loader1.gif'></td></tr><tr><td align='center'>Please Wait...</td></tr></table>";
            formsToolboxInfo.formsArray[0].getForm().findField("operationsInformations").setValue(loading);
            var last = toolboxUrl.charAt(toolboxUrl.length - 1);
            if(last != '/')
               toolboxUrl+="/";
            var eventResponseSSE=function(response){
                if(response){
                  var sseResponse = (new DOMParser()).parseFromString(response, "text/xml");
                  var result=sseResponse.selectNodes("hasSSEStylesheet")[0].getAttribute("value");
                  var hasMap=sseResponse.selectNodes("hasSSEStylesheet")[0].getAttribute("hasMap");
      
                  formsToolboxInfo.formsArray[0].getForm().findField("hasSSEXSL").setValue(result);
                  formsToolboxInfo.formsArray[0].getForm().findField("hasMap").setValue(hasMap);
                  var htmlResponse;
                  if(result == "true"){
                    if(hasMap=="true"){
                      htmlResponse="<p><b>SSE XSL Defined whit the AOI input</b></br> &nbsp;"; 
                      mapWindow.show();
                    }else{
                      mapWindow.hide();
                      htmlResponse="<p><b>SSE XSL defined whitout the AOI input</b></br> &nbsp;";
                    }
                  formsToolboxInfo.formsArray[0].getForm().findField("operationsInformations").setValue(htmlResponse);
                }else{ 
                      mapWindow.hide();
                      var eventResponseGML=function(response){
                          var gmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                          var result=gmlResponse.selectNodes("hasGMLOnMapStylesheet")[0].getAttribute("value");
                          formsToolboxInfo.formsArray[0].getForm().findField("hasGMLXSL").setValue(result);
                          var htmlResponse;
                          if(result == "true"){
                            mapWindow.show();  
                            htmlResponse="<p><b>GML XSL Defined </b></br> &nbsp;";
                          }  
                          else
                            htmlResponse="<p><b>No XSL Defined </b></br> &nbsp;"; 
                          formsToolboxInfo.formsArray[0].getForm().findField("operationsInformations").setValue(htmlResponse);
                      };
                      var eventTimeOutGML=function(){
                          var htmlResponse="<b>HasXslGml Request: Time-out Exception</b>";
                          formsToolboxInfo.formsArray[0].getForm().findField("operationsInformations").setValue(htmlResponse);
                      };
                      sendXmlHttpRequestTimeOut("GET", 
                        proxyRedirect+"?url="+toolboxUrl+"manager?cmd=hasGMLOnMapStylesheet&serviceName="+toolboxService, 
                        true, null, 99999999, eventResponseGML, eventTimeOutGML);
                    } 
                }else{
                   mapWindow.hide(); 
                   htmlResponse="<b>HasXslSSE Request: Error Exception</b>";
                   formsToolboxInfo.formsArray[0].getForm().findField("operationsInformations").setValue(htmlResponse); 
                }    
            };
            var eventTimeOutSSE=function(){
                var htmlResponse="<b>HasXslSSE Request: Time-out Exception</b>";
                formsToolboxInfo.formsArray[0].getForm().findField("operationsInformations").setValue(htmlResponse);
            };
           sendXmlHttpRequestTimeOut("GET", proxyRedirect+"?url="+toolboxUrl+"manager?cmd=hasSSEStylesheet%26serviceName="+toolboxService+"%26operationName="+toolboxOperation, true, null, 15, eventResponseSSE, eventTimeOutSSE);        
         },
         desktopChange: function(desktopType){
             if(desktopPanel) 
                  desktopPanel.destroy();
             if(desktopType == 'map'){
                toolbarMap = new Ext.Toolbar();
                desktopPanel = new Ext.Panel({
                          border: false,
                          layout:'anchor',
                          anchor:'100% 94%',
                          
			  tbar: toolbarMap
                     });
             }else{
               desktopPanel = new Ext.Panel({
                  border: false,
                  layout:'anchor',
                  anchor:'100% 94%',
                  html: "<TABLE BGCOLOR='#617992' width='100%' height='100%'><tr><td align='center'><img height='"+(screen.heigth*0.2)+"' width='"+(screen.width*0.4)+"' src='"+desktopImageURL+"'/></tr></td></TABLE>"
               }); 
                 
             }
         },
         setMap: function (){
           map = new OpenLayers.Map(desktopPanel.body.dom, mapOptions);
           var formatWMC = new OpenLayers.Format.WMC(mapOptions);
           var contextMap = Sarissa.getDomDocument();
           contextMap.async=false;
           contextMap.validateOnParse=false;
           contextMap.load(contextMapUrl);
           map = formatWMC.read(contextMap, {map: map});
           map.zoomToMaxExtent();
           var toc = new WebGIS.Control.Toc({map: map, parseWMS: false, autoScroll: true});
           // standard Open Layers
           map.addControl(new OpenLayers.Control.MousePosition());
           map.addControl(new OpenLayers.Control.MouseDefaults());
           map.addControl(new OpenLayers.Control.KeyboardDefaults());
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
         },
         serviceControl: function (){
            // return false;+
            var g = new OpenLayers.Format.GML();
             layerAOI.attributes = {};
            var name = prompt("Name for feature?");
            layerAOI.attributes['name'] = name;
            var data = g.write(layerAOI.features);
            //OpenLayers.Util.getElement("gml").value = data;
            //alert(data);
         },
          // Reset Alphanumeric Filter       
         Reset: function(){

             formsCatInterfObject.resetFormValues();
             
         },
         /*INIZIO: Da rimpiazzare con una funzione*/
         addToolboxOperationInputTab: function (toolboxUrl, service, operation, tabContent){
              var tabId="tabInput_"+toolboxUrl+service+operation;
              if (!toolboxOperationTabPanel.findById(tabId)){
                  var sseTabCenter;
                  if(tabContent.html)
                      sseTabCenter =new Ext.Panel({
                         region:'center',
                         split:true,
                         layout:'form',
                         id: "input_"+toolboxUrl+service+operation,
                         height: (screen.height/100)*65,
                         bodyStyle : {background: '#dfe8f6'},
                        maxSize: (screen.height/100)*60,
                         collapsible: true,
                         collapsed : false,
                         title:operation+" Input Interface",
                         autoScroll : true,
                         margins:'0 0 0 0', 
                         html: tabContent.html
                  });
                  else
                     sseTabCenter =new Ext.Panel({
                         region:'center',
                         split:true,
                         id: "input_"+toolboxUrl+service+operation,
                         height: (screen.height/100)*60,
                         maxSize: (screen.height/100)*60,
                         bodyStyle : {background: '#dfe8f6'},
                         collapsible: true,
                         collapsed : false,
                         title:operation+" Input Interface",
                         autoScroll : true,
                         margins:'0 0 0 0', 
                         items: [tabContent.panel]
                  }); 
                  var panelSSETab =new Ext.Panel({
                         layout:'form',
                         id: "tabInput_"+toolboxUrl+service+operation,
                         title: operation + " Input",
                         bodyStyle : {background: '#99bbe8'},
                         closable:true,
                         items:[/*sseTabSouth,*/sseTabCenter]
                             
                  });  
              toolboxOperationTabPanel.add(panelSSETab).show();
              toolboxOperationTabPanel.setActiveTab(tabId);
          }else{
              toolboxOperationTabPanel.setActiveTab(tabId);
          }
        }, 
         addToolboxOperationOutputTab: function (toolboxUrl, service, operation, tabContent){
             
              var tabId="tabOutput_"+toolboxUrl+service+operation;
              if (!toolboxOperationTabPanel.findById(tabId)){
                  var sseTabCenter;
                  if(tabContent.html)
                      sseTabCenter =new Ext.Panel({
                         region:'center',
                         bodyStyle : {background: '#dfe8f6'},
                         split:true,
                         id: "output_"+toolboxUrl+service+operation,
                         height: (screen.height/100)*60,
                         
                         maxSize: (screen.height/100)*60,
                         collapsible: true,
                         collapsed : false,
                         title:operation+" Output",
                         autoScroll : true,
                         margins:'0 0 0 0', 
                         html: tabContent.html
                  });
                  var panelSSETab =new Ext.Panel({
                         layout:'border',
                         bodyStyle : {background: '#dfe8f6'},
                         id: "tabOutput_"+toolboxUrl+service+operation,
                         title: operation + " Output",
                         //autoScroll:true,
                         closable:true,
                         items:[/*sseTabSouth,*/sseTabCenter]
                             
                  });  
              toolboxOperationTabPanel.add(panelSSETab).show();
              toolboxOperationTabPanel.setActiveTab(tabId);
          }else{
              toolboxOperationTabPanel.setActiveTab(tabId);
          }
        }, 
         addWSGenericOperationOutputTab: function(WSURL,soapAction,tabContent,id,title){
          /* var id=WSURL+soapAction;
           var title=soapAction + " Output"; 
              if (genericOutputTabPanel.findById("tabWSOutput_"+id)){
                  var i=1; 
                  while(genericOutputTabPanel.findById("tabWSOutput_"+id)){
                     i++;
                     id=WSURL+soapAction+i;
                     title=soapAction + " Output"+"("+i+")";
                 }
              }    */
                  var sseTabCenter;
                 // alert(tabContent.html);
                  if(tabContent.html)
                      sseTabCenter =new Ext.Panel({
                         region:'center',
                         bodyStyle : {background: '#dfe8f6'},
                         split:true,
                         id: "outputWS_"+id,
                         height: (screen.height/100)*60,
                         maxSize: (screen.height/100)*60,
                         title:soapAction + " Output",
                         autoScroll : true,
                         margins:'0 0 0 0', 
                         html: tabContent.html
                  });
                  var panelSSETab =new Ext.Panel({
                         layout:'border',
                         bodyStyle : {background: '#dfe8f6'},
                         id: "tabWSOutput_"+id,
                         title: title,
                         autoScroll:true,
                         closable:true,
                         items:[sseTabCenter]
                        // html: tabContent.html
                             
                  });  
              genericOutputTabPanel.add(panelSSETab).show();
          //    genericOutputTabPanel.setActiveTab("tabWSOutput_"+id);
          
         },
         /*FINE: Da rimpiazzare con una funzione*/
         showToolboxOperationInterface: function(){
            var sse=formsToolboxInfo.formsArray[0].getForm().findField("hasSSEXSL").value;
            var hasMap=formsToolboxInfo.formsArray[0].getForm().findField("hasMap").value;
            var soapAction=formsToolboxInfo.formsArray[0].getForm().findField("serviceOperation").getValueInformation('soapAction');
            var gml=formsToolboxInfo.formsArray[0].getForm().findField("hasGMLXSL").value;
            var toolboxUrl=formsToolboxInfo.formsArray[0].getForm().findField("toolboxURL").value;
            var service=formsToolboxInfo.formsArray[0].getForm().findField("toolboxSerivces").value;
            var operation=formsToolboxInfo.formsArray[0].getForm().findField("serviceOperation").value;
            if(toolboxUrl && toolboxUrl!="" && toolboxUrl!="http://")
              if(service && service!="")
                if(operation && operation!=""){
                    toolboxInforamtionsPanel.collapse(true);
                    if(sse == "true"){
                        var sseHtmlResponse="<iframe scrolling='auto' src='"+proxyRedirect+"?url="+toolboxUrl+"/manager?cmd=getOpTestInputPage&toolboxUrl="+toolboxUrl+"&serviceName="+service+"&operationName="+operation+"&AOI="+hasMap+"' name='inputHtmlSSE"+toolboxUrl+service+operation+"' width='98%' height='98%' marginwidth='0' marginheight='0'></iframe>";
                        tbxTCenter.addToolboxOperationInputTab(toolboxUrl, service, operation, {html:sseHtmlResponse}); 
                    }else{
                        numberToolboxInput++;
                        

                        var id=/*/toolboxUrl+*/service+operation+numberToolboxInput;
                        id=replaceAll(id, "-", "_");
                        var radioSelect="<br><table align='center'><tr><td><form name='formModeGenericToolbox"+id+"' id='formModeGenericToolbox"+id+"'><table width='100%'><tr width='100%'rowspan='2' BGCOLOR='#325e8f'><td><b style='color: #ffffff;'>SOAP message content Type: </b><br></td></tr>"+
                                  "<tr><td><input type='radio' name='requestMode' value='SyncPayload'/>&nbsp;&nbsp;&nbsp;Synchronous Payload</td></tr>"+
                                  "<tr><td><input type='radio' name='requestMode' value='AsyncPayload'/>&nbsp;&nbsp;&nbsp;Asynchronous Payload</td></tr>"+
                                  "<tr><td><input type='radio' name='requestMode' value='SOAP'/>&nbsp;&nbsp;&nbsp;SOAP message</td></tr>"+
                                  "</td></tr></table></form><br></td></tr>";
                        var formfileIframe="<tr><td><table width='100%'><tr rowspan='2' BGCOLOR='#325e8f' width='100%'><td><b style='color: #ffffff;'>SOAP message: </b><br></td></tr><tr><td align='center'>"+
                                     "<form name='formFile_"+id+"' action='"+toolsServlet+"?cmd=putFile&type=multipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaWToolbox+"&rows="+textAreaHToolbox+"' method='POST' enctype='multipart/form-data' target='iframeRequest_"+id+"'>"+
                                        "<input type='file' id='FILE' name='FILE' value='' width='50' />"+
                                        "<input type='submit' name='buttonSubmit_"+id+"' value='Load Request'/>"+
                                        "<input type='button' name='buttonSendToolboxGenericRequest_"+id+"' value='Send Request' onclick=\"javascript:alert(parent.iframeRequest_"+id+".window.getEditAreaValue());tbxTCenter.sendToolboxGenericRequest(document.getElementById('formModeGenericToolbox"+id+"').requestMode , parent.iframeRequest_"+id+".window.getEditAreaValue(), '"+toolboxUrl+"','"+service+"','"+soapAction+"','"+operation+"','test')\"/>"+//document.getElementById('formModeGenericToolbox"+id+"').requestMode , parent.iframeRequest_"+id+".window.getEditAreaValue(), '"+toolboxUrl+"','"+service+"','"+soapAction+"','"+operation+"','test'
                                    "</form></td></tr><tr align='center'><td>"+
                                    "<iframe scrolling='no' FRAMEBORDER='0' src='"+toolsServlet+"?cmd=putFile&type=nomultipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaWToolbox+"&rows="+textAreaHToolbox+"' name='iframeRequest_"+id+"' width='"+textAreaFrameWToolbox+"' height='"+textAreaFrameHToolbox+"' marginwidth='0' marginheight='0'></iframe>"+
                                 //   "<tr><td><input type='button' name='buttonSendToolboxGenericRequest_"+id+"' value='Send Request' onclick='tbxTCenter.sendToolboxGenericRequest( document.getElementById(\"formModeGenericToolbox"+id+"\").requestMode , parent.iframeRequest_"+id+".window.getEditAreaValue(), \""+toolboxUrl+"\",\""+service+"\",\""+soapAction+"\",\""+operation+"\",\"test\");'/></td></tr>"+
                                    "</table>"+
                                     "</td></tr></table>";
                        var htmlToolboxGeneric=radioSelect+formfileIframe;
                        tbxTCenter.addToolboxOperationInputTab(toolboxUrl, service, operation,{html:  htmlToolboxGeneric});    
                  }
              }else
                Ext.Msg.alert('Error', 'Please select a service operation');
             else
              Ext.Msg.alert('Error', 'Please select a service');  
           else
            Ext.Msg.alert('Error', 'Please insert the TOOLBOX URL');   
         },
         sseManager: function(toolboxUrl,service,operation,aoi){
             var last = toolboxUrl.charAt(toolboxUrl.length - 1);
             if(last != '/')
               toolboxUrl+="/";
             var nameFrame="inputHtmlSSE"+toolboxUrl+service+operation;
             var massFormObj=parent[nameFrame].document.getElementById("MASSFORM");
             if(!aoi)
                aoi=true; 
             if(aoi){ 
                var g = new OpenLayers.Format.GML(); 
                var aoiPolygonString = g.write(layerAOI.features);
                var tmpAOI=aoiPolygonString.substr(featureCollectionTagOpen.length);
                tmpAOI=tmpAOI.substr(0,tmpAOI.length-featureCollectionTagClose.length);
                if(tmpAOI!=""){ 
                   aoiPolygonString=aoiTagOpen+tmpAOI+aoiTagClose;
                   var inputElements=massFormObj.getElementsByTagName("input");
                   var textAreaElements=massFormObj.getElementsByTagName("textarea");
                   var comboElements=massFormObj.getElementsByTagName("select");
                   var i=0;
                   var valuesControl=new Array();
                   var values=new Object();
                   if(inputElements)
                      for(i=0;i<inputElements.length;i++){
                        if(inputElements[i].value != ""){  

                            valuesControl.push({
                                name:inputElements[i].name,
                                id:inputElements[i].name,
                                type:"text",
                                value: ""
                            }); 
                        values[inputElements[i].name]=inputElements[i].value;
                       }
                      }
                   if(comboElements)
                        for(i=0;i<comboElements.length;i++){
                            if(comboElements[i].value != ""){ 
                                valuesControl.push({
                                  name:comboElements[i].name,
                                  id:comboElements[i].name,
                                  type:"combo",
                                  value: ""
                                }); 
                            values[comboElements[i].name]=comboElements[i].value;
                        }    
                      }
                     if(textAreaElements)
                        for(i=0;i<textAreaElements.length;i++){
                            if(textAreaElements[i].value != ""){ 
                                valuesControl.push({
                                  name:textAreaElements[i].name,
                                  id:textAreaElements[i].name,
                                  type:"textarea",
                                  value: ""
                                }); 
                                values[textAreaElements[i].name]=textAreaElements[i].value;
                            }     
                        }
                     var formObj=new Array();
                     formObj.push(valuesControl);
                     var formKeyValue=new OpenLayers.Format.XMLKeyValue();
                           var keyValueObj={
                                confValues: formObj,
                                formValues: values
                            };
                     var xmlKeyValueString=formKeyValue.write(keyValueObj, {returnType: "String", namespace: false });
                     tempAOI=xmlKeyValueString.substr(0,xmlKeyValueString.length-keyValueTagClose.length);
                     xmlKeyValueString=tempAOI+"<AOI>"+aoiPolygonString+"</AOI>"+keyValueTagClose;

                     var eventSSEResponse= function(response){
                       if(!response){
                          response="Service Exception!";
                        }  
                       var sseResponse = (new DOMParser()).parseFromString(response, "text/xml");
                       var responseNode=sseResponse.selectNodes("response")[0];
                       if(responseNode){
                          var orderID=responseNode.selectNodes("orderId")[0].getAttribute("value");
                          var messageID=responseNode.selectNodes("messageId")[0].getAttribute("value");
                          var serivceType=responseNode.getAttribute("type"); 
                          var htmlAsync="<br><br><b>The request for the asynchronous operation "+operation+" has been sent</b><br><br>"+  
                          "<p> The Order ID is: &nbsp;&nbsp;&nbsp;&nbsp;<b>" + orderID +"</b></p></br><br><br>"+
                          "<input type='button' name='verifyPushServer' value='Retrive Result' onclick='tbxTCenter.insertSSEAsyncronousResponse(\""+toolboxUrl+"\",\""+service+"\",\""+operation+"\",\""+toolboxUrl+"/manager?cmd=getPortalLikeResponseFromPushServer&messageId="+messageID+"&serviceName="+service+"&operationName="+operation+"\",\"AsyncOutput"+toolboxUrl+service+operation+"\");'></input><br><br>"+
                          "<div id='AsyncOutput"+toolboxUrl+service+operation+"'>";
                          tbxTCenter.addToolboxOperationOutputTab(toolboxUrl, service, operation, {html: htmlAsync});          
                       }else{
                         tbxTCenter.addToolboxOperationOutputTab(toolboxUrl, service, operation, {html: "<div id=\"responseContainer_"+toolboxUrl+service+operation+"\">"+response+"</div>"}); 
                         var objContainerResponse=document.getElementById("responseContainer_"+toolboxUrl+service+operation);
                         var listDiv=objContainerResponse.getElementsByTagName("div");
                         var scriptDivURLObj,scriptDivValueObj;
                         for(var z=0; z<listDiv.length; z++){
                             if(listDiv[z].getAttribute("name") == "renderingURL"){
                                 scriptDivURLObj=listDiv[z];
                             }else
                                if(listDiv[z].getAttribute("name") == "renderingEmbedded"){
                                 scriptDivValueObj=listDiv[z];
                             } 
                                 
                         }
                         var renderingURLInstructions,scriptsURL;
                         if(scriptDivURLObj){
                            scriptsURL=scriptDivURLObj.getElementsByTagName("script");
                            renderingURLInstructions=scriptsURL[0].firstChild.nodeValue;
                         }else
                           renderingURLInstructions="";
                         var renderingValueInstructions,scriptsValue;
                         if(scriptDivValueObj){
                            scriptsValue=scriptDivValueObj.getElementsByTagName("script"); 
                            renderingValueInstructions=scriptsValue[0].firstChild.nodeValue;
                            var tmp=replaceAll(renderingValueInstructions,"\"","\\\"");
                            renderingValueInstructions=replaceAll(tmp,"\r","");
                            tmp=replaceAll(renderingValueInstructions,"\n","");
                            renderingValueInstructions=replaceAll(tmp,"\t","");
                            tmp=replaceAll(renderingValueInstructions,"\x0B","");
                            renderingValueInstructions=replaceAll(tmp,"\0","");
                         }else
                            renderingValueInstructions="";
                         
                         var intialize="toolboxUrlRendering='"+toolboxUrl+"';"+
                                      "serviceRendering='"+service+"';"+
                                      "operationRendering='"+operation+"';";
                         var renderingInstructions = intialize + renderingURLInstructions + renderingValueInstructions;
                         
                         eval(renderingInstructions);
                       }  
                     };
                     var eventSSETimeOut= function(){
                            Ext.Msg.alert('Error', 'Time OUT SSE Request');
                     };
                     sendXmlHttpRequestTimeOut("POST", 
                                proxyRedirect, 
                                true, xmlKeyValueString, 9999999, eventSSEResponse, eventSSETimeOut);
                } 
                else{
                    Ext.Msg.alert('Info', 'Please draw the Area of Interest polygon');
                }
           }         
                
             
             
             return(false);
         },
         sseRenderingReference : function(urlData, dataType){
             /* It use a Proxy */
             var proxy=proxyRedirect+"?url=";
             var proxyUrlData=proxy+urlData;
             var dataName=/*/toolboxUrlRendering+serviceRendering+*/operationRendering;
            switch(dataType) {
                case "GML": 
                      //Controllare se è già presnete 
                      map.addLayer(new OpenLayers.Layer.GML(dataName+"_GML", proxyUrlData));
                    break;
                case "SHAPE": 
                      Ext.Msg.alert('Error', 'The rendering of a SHAPE file from URL is not yet supported');
                    break;    
                case "WMSURL": 
                     Ext.Msg.alert('Error', 'The rendering of a WMS GetMap request  is not yet supported');
                    break; 
                case "WFSURL": 
                      Ext.Msg.alert('Error', 'The rendering of a WFS GetFeature request is not yet supported');
                    break;
                case "WCSURL":
                      Ext.Msg.alert('Error', 'The rendering of a WCS GetCoverage request is not yet supported');
                    break;    
                case "WMCFileURL": 
                      var formatWMC = new OpenLayers.Format.WMC(mapOptions);
                      var contextMap = Sarissa.getDomDocument();
                      contextMap.async=false;
                      contextMap.validateOnParse=false;
                      contextMap.load(proxyUrlData);
                      map = formatWMC.read(contextMap, {map: map});
                      Ext.Msg.alert('Info', 'New Web Map Context retrived... The map is reloaded');
                    break;  
                case "CoverageFileURL": 
                     Ext.Msg.alert('Error', 'The rendering of a Coverage File from URL is not yet supported');
                    break;
                case "SOSURL": 
                    Ext.Msg.alert('Error', 'The rendering of a SOS file from URL is not yet supported');
                    break;    
            }
             
         },
         sseRenderingValue : function(valueData, dataType){
            var tools=toolsServlet+"?cmd=PUTFILE&type=NOMULTIPART&modality=VIEW&editAreaPath="+editAreaPath+"&outputFormat=text/xml";
            var dataName=/*toolboxUrlRendering+serviceRendering+*/operationRendering;  
            var renderingResp=function (response){ 
              switch(dataType) {
                case "GML": 
                    map.addLayer(new OpenLayers.Layer.GML(dataName+"_EMB_GML", response));
                    break;
                case "KML": 
                    map.addLayer(new OpenLayers.Layer.GML(dataName+"_EMB_KML", response, 
                        {
                         format: OpenLayers.Format.KML, 
                         formatOptions: {
                              extractStyles: true, 
                              extractAttributes: true
                        }
                        }));
                    break;    
                case "WMC": 
                    var formatWMC = new OpenLayers.Format.WMC(mapOptions);
                    var contextMap = Sarissa.getDomDocument();
                    contextMap.async=false;
                    contextMap.validateOnParse=false;
                    contextMap.load(response);
                      map = formatWMC.read(contextMap, {map: map});
                    Ext.Msg.alert('Info', 'New Web Map Context retrived... The map is reloaded');
                    break; 
            } 
             };
             
             var renderingTimeOut= function(){
                 Ext.Msg.alert('Error', 'Time OUT Embedded Data Rendering');
             };
             sendXmlHttpRequestTimeOut("POST", 
                                tools, 
                                true, valueData, 99999999, renderingResp, renderingTimeOut);
         },
         insertSSEAsyncronousResponse: function (toolboxUrl, service, operation, responseUrl, renderObjId){
             var insertObj=document.getElementById(renderObjId);
             var loading="<table width='100%'><tr><td align='center'><img src='style/img/loader/loader1.gif'></td></tr><tr><td align='center'>Please Wait...</td></tr></table>";
             insertObj.innerHTML=loading;
             var eventSSEAsyncResponse=function(response){
                 insertObj.innerHTML=response;
                 var listDiv=insertObj.getElementsByTagName("div");
                 var scriptDivURLObj,scriptDivValueObj;
                         for(var z=0; z<listDiv.length; z++){
                             if(listDiv[z].getAttribute("name") == "renderingURL"){
                                 scriptDivURLObj=listDiv[z];
                             }else
                                if(listDiv[z].getAttribute("name") == "renderingEmbedded"){
                                 scriptDivValueObj=listDiv[z];
                             } 
                                 
                         }
                         
                         var renderingURLInstructions,scriptsURL;
                         if(scriptDivURLObj){
                            scriptsURL=scriptDivURLObj.getElementsByTagName("script");
                            renderingURLInstructions=scriptsURL[0].firstChild.nodeValue;
                         }else
                           renderingURLInstructions="";
                         var renderingValueInstructions,scriptsValue;
                         if(scriptDivValueObj){
                            scriptsValue=scriptDivValueObj.getElementsByTagName("script"); 
                            renderingValueInstructions=scriptsValue[0].firstChild.nodeValue;
                            var tmp=replaceAll(renderingValueInstructions,"\"","\\\"");
                            renderingValueInstructions=replaceAll(tmp,"\r","");
                            tmp=replaceAll(renderingValueInstructions,"\n","");
                            renderingValueInstructions=replaceAll(tmp,"\t","");
                            tmp=replaceAll(renderingValueInstructions,"\x0B","");
                            renderingValueInstructions=replaceAll(tmp,"\0","");
                         }else
                            renderingValueInstructions="";
                         
                         var intialize="toolboxUrlRendering='"+toolboxUrl+"';"+
                                      "serviceRendering='"+service+"';"+
                                      "operationRendering='"+operation+"';";
                         var renderingInstructions = intialize + renderingURLInstructions + renderingValueInstructions;
                         
                         eval(renderingInstructions);
                 
             };
             var eventSSEAsyncTimeOut=function(){
                 Ext.Msg.alert('Error', 'Time OUT SSE Asyncronohus Response');
             };
             sendXmlHttpRequestTimeOut("GET", 
                                proxyRedirect+"?url="+responseUrl, 
                                true, null, 999999999, eventSSEAsyncResponse, eventSSEAsyncTimeOut);
         },
         /*INIZIO: Da rimpiazzare con una funzione*/
         sendToolboxGenericRequest : function(messageMode,req,toolboxURL,service,soapAction,operation,messageID){
          var mode;
            
         if(messageMode instanceof NodeList){
            for(var u=0;u<messageMode.length;u++)
                if(messageMode[u].checked)
                   mode=messageMode[u].value;
          }else
           mode=messageMode; 


            var last = toolboxURL.charAt(toolboxURL.length - 1);
            if(last != '/')
               toolboxURL+="/";

          if(mode){
            if(req !=""){
                var newRequest=removeXmlDiterctive(req);
                var headers=null;
                var xmlRequest="<xmlRequest><ServiceUrl>"+toolboxURL+"services/"+service+"</ServiceUrl>";
                xmlRequest+="<SoapAction>"+soapAction+"</SoapAction>";
                xmlRequest+="<idRequest>"+messageID+"</idRequest>";
                xmlRequest+="<LogFolder>/TOOLBOXGeneric</LogFolder>";
                xmlRequest+="<Ident>true</Ident>";
             
                switch (mode){
                 case "SOAP":
                          xmlRequest+="<Protocol>HTTPPOST</Protocol>";
                          xmlRequest+="<Request>"+newRequest+"</Request>";
                     break;
                 case "AsyncPayload":
                         xmlRequest+="<Protocol>HTTPPOST</Protocol>"; 
                         headers=new Array();
                         headers.push("SOAPAction,"+soapAction);
                         var soapEnvelopeOpen="<soap-env:Envelope xmlns:soap-env=\"http://schemas.xmlsoap.org/soap/envelope/\">";
                         var soapHeader="<soapHeader>"+
                                          "<soap-env:MessageID>"+messageID+"</soap-env:MessageID>"+
                                           "<soap-env:ReplyTo>"+
                                                "<soap-env:Address>"+toolboxURL+"/Push</soap-env:Address>"+
                                                "<soap-env:PortType>ServicePortType</soap-env:PortType>"+
                                                "<soap-env:ServiceName>ServiceName</soap-env:ServiceName>"+
                                          "</soap-env:ReplyTo>"+
                                        "</soapHeader>";
                        var soapBodyOpen="<soap-env:Body>";
                        var soapBodyClose="</soap-env:Body>";
                        var soapEnvelopeClose="</soap-env:Envelope>";
                        xmlRequest+="<Request>"+soapEnvelopeOpen+soapHeader+soapBodyOpen+newRequest+soapBodyClose+soapEnvelopeClose+"</Request>";
                     break;
                 case "SyncPayload":
                     xmlRequest+="<Protocol>SOAP</Protocol>";
                     xmlRequest+="<Request>"+newRequest+"</Request>";
                     break;    
                }
                xmlRequest+="</xmlRequest>";   
                var genericToolboxResponse= function(response){
                    document.getElementById("toolboxOutputInformation").innerHTML="";
                  if(!response){
                      response="Service Exception!";
                  }
                  var htmlReposne="<br><table width='100%'><tr width='100%'rowspan='2' BGCOLOR='#325e8f'><td><br><b style='color: #ffffff;'>&nbsp;&nbsp;&nbsp;"+operation+" Response: </b></td></tr>"+  
                  "<tr align='center'><td align='center'><br><textarea id='ToolboxGeneric' style='width:"+textAreaWToolbox+"px;height:"+textAreaHToolbox+"px;'>"+response+"</textarea></td></tr></table>";
                  tbxTCenter.addToolboxOperationOutputTab(toolboxURL, service, operation, {html: htmlReposne});
                  // alert("ToolboxGeneric");
                   initEditArea('ToolboxGeneric');
                };
              var genericToolboxTimeOut= function(){
                  document.getElementById("toolboxOutputInformation").innerHTML="";
                  Ext.Msg.alert('Error', 'Request Time-Out');
              };

               var eventError= function(response){
                         document.getElementById("toolboxOutputInformation").innerHTML="";
                         Ext.Msg.alert('Request Error', response);
               }
                      toolboxOperationTabPanel.setActiveTab(0);
                      var loadingHtml="<br><br><br><br><table align='center'><tr><td align='center'><img src='style/img/loader/loader1.gif'></td></tr><tr><td align='center'>Please Wait ...</td></tr></table>";
                      document.getElementById("toolboxOutputInformation").innerHTML=loadingHtml;

              sendXmlHttpRequestTimeOut("POST", 
                                proxyRedirect, 
                                true, xmlRequest, 99999999, genericToolboxResponse, genericToolboxTimeOut,headers,null,eventError);
            }else
               Ext.Msg.alert('Error', 'Please insert a soap meassage or a soap Payload');                       
          }else
            Ext.Msg.alert('Error', 'Please select the message content type.'); 
       },
         sendGenericRequest : function(WSOperationURL,soapAction,req){
             var headers=null;
             //genericClientPanel.collapse(false);
             //var req=
             //alert(req);
             if(req!= ""){
                if(WSOperationURL!="" && WSOperationURL!="http://"){
                   if(soapAction !=""){ 
                      var newRequest=removeXmlDiterctive(req); 
                      var xmlRequest="<xmlRequest><ServiceUrl>"+WSOperationURL+"</ServiceUrl>";
                      xmlRequest+="<SoapAction>"+soapAction+"</SoapAction>";
                      xmlRequest+="<idRequest>"+WSOperationURL+soapAction+"</idRequest>";
                      xmlRequest+="<LogFolder>/WSGeneric</LogFolder>";
                      xmlRequest+="<Ident>true</Ident>";
                      xmlRequest+="<Protocol>HTTPPOST</Protocol>";
                      xmlRequest+="<Request>"+newRequest+"</Request>";
                      xmlRequest+="</xmlRequest>";   
                      var genericResponse= function(response){

                      document.getElementById("genericOutputInformation").innerHTML="";

                      if(!response){
                          response="Service Exception!";
                      }
                       var id=WSOperationURL+soapAction;
                       var title=soapAction + " Output";
                       if (genericOutputTabPanel.findById("tabWSOutput_"+id)){
                         var i=1;
                        while(genericOutputTabPanel.findById("tabWSOutput_"+id)){
                            i++;
                            id=WSOperationURL+soapAction+i;
                            title=soapAction + " Output"+"("+i+")";
                        }
                      }
                      var htmlResponse="<br><table width='100%'><tr width='100%'rowspan='2' BGCOLOR='#325e8f'><td><br><b style='color: #ffffff;'>&nbsp;&nbsp;&nbsp;"+soapAction+" Response: </b></td></tr>"+
                        "<tr align='center'><td align='center'><br><textarea id='"+id+"' style='width:"+textAreaWGenericOutput+"px;height:"+textAreaHGenericOutput+"px;'>"+response+"</textarea></td></tr></table>";
                      tbxTCenter.addWSGenericOperationOutputTab(WSOperationURL, soapAction, {html: htmlResponse}, id, title);
                      initEditArea(id);
                      };
                      var genericTimeOut= function(){
                          document.getElementById("genericOutputInformation").innerHTML="";
                          Ext.Msg.alert('Error', 'Time OUT Web Service Generic Request');
                      };
                      var eventError= function(response){
                         document.getElementById("genericOutputInformation").innerHTML="";
                         Ext.Msg.alert('Request Error', response);
                      }
                      genericOutputTabPanel.setActiveTab(0);
                      var loadingHtml="<br><br><br><br><table align='center'><tr><td align='center'><img src='style/img/loader/loader1.gif'></td></tr><tr><td align='center'>Please Wait ...</td></tr></table>";
                      document.getElementById("genericOutputInformation").innerHTML=loadingHtml;
                      //alert(xmlRequest);
                      sendXmlHttpRequestTimeOut("POST", 
                                proxyRedirect, 
                                true, xmlRequest, 9999999, genericResponse, genericTimeOut,headers, null, eventError);
                  }else
                    Ext.Msg.alert('Error', 'Please insert the SoapAction');
              } else
                Ext.Msg.alert('Error', 'Please insert a valid Web Service URL');  
            }else
              Ext.Msg.alert('Error', 'Please insert the Soap Message');
       }
        /*FINE: Da rimpiazzare con una funzione*/
      };
        
}();

var tbxTCenter=ToolboxTestCenter.Application;
// Run the application when browser is ready
Ext.onReady(tbxTCenter.init, ToolboxTestCenter.Application);

