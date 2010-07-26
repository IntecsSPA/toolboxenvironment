


var textAreaFrameH=(screen.height/1.9);
var textAreaFrameW=(screen.width/1.55);

/* Service Management XML Interfaces*/
var exportGroupServicesXML="jsScripts/servicesManagement/resources/xml/exportGroupServicesPanel.xml";
var importGroupServicesXML="jsScripts/servicesManagement/resources/xml/importGroupServicesPanel.xml";

var servicesXMLInterface= new Array();
//var createWPSXML="jsScripts/wpsWizard/resources/xml/createWPSServicePanel.xml";
//var createToolboxServiceXML="jsScripts/servicesManagement/resources/xml/createToolboxServicePanel.xml";
/*servicesXMLInterface.push({xmlUrl:"jsScripts/servicesManagement/resources/xml/createOrderingServicePanel.xml",
                           icon:"images/order_blk.png", title:"Create Ordering Service", name:"orderingService",
                            actionMethod: "createToolboxService(this)"});
servicesXMLInterface.push({xmlUrl:"jsScripts/servicesManagement/resources/xml/createCatalogueServicePanel.xml",
                            icon:"images/catalogue_blk.png", title:"Create Catalogue Service", name:"catalogueService",
                            actionMethod: "createToolboxService(this)"});
servicesXMLInterface.push({xmlUrl:"jsScripts/servicesManagement/resources/xml/createArchivingServicePanel.xml",
                        icon:"images/archiving_blk.png", title:"Create Archiving Service", name:"archivingService",
                            actionMethod: "createToolboxService(this)"});
servicesXMLInterface.push({xmlUrl:"jsScripts/wpsWizard/resources/xml/createWPSServicePanel.xml",
                            icon:"images/wps_blk.png", title:"Create Web Processing Service (WPS)", name:"wpsService",
                            actionMethod: "createWPSRequest(this)"});*/

 var arrayPlugin= new Array();
 arrayPlugin.push("rest/gui/creationWizard/Ordering.json");
 arrayPlugin.push("rest/gui/creationWizard/Archiving.json");
 arrayPlugin.push("rest/gui/creationWizard/Catalogue.json");
 arrayPlugin.push("rest/gui/creationWizardWPS.json");






var servicesAccordionPanels=new Array();
var servicesInterfaces=new Array();

/* Service Management Interface Objects */
var exportGroupServiceIO=null;
var importGroupServiceIO=null;


/* Service Management Windows*/
var importExportGroupServiceWin=null;
var createServiceWin=null;


var spot = new Ext.ux.Spotlight({
        easing: 'easeOut',
        duration: .3
    });

var spot2 = new Ext.ux.Spotlight({
        easing: 'easeOut',
        duration: .3
    });

/*Show Import Export Group Services Interface*/
function importExportGroupServices(){

    if(importExportGroupServiceWin == null){

        exportGroupServiceIO=createPanelExjFormByXml(exportGroupServicesXML);
   
        importGroupServiceIO=createPanelExjFormByXml(importGroupServicesXML);

        var exportServicesPanel=new Ext.Panel({
                            title: 'Export Services',
                            border: true,
                            autoScroll : true,
                            id: "exportServicePanel",
                            bodyColor: '#79a3cb',
                            html: "<div id='exportServicesInterface'>",
                            iconCls: 'export'
                   });

        var importServicesPanel=new Ext.Panel({
                            title: 'Import Services',
                            border: true,
                            autoScroll : true,
                            id: "importServicePanel",
                            bodyColor: '#79a3cb',
                            html: "<div id='importServicesInterface'>",
                            iconCls: 'import'
                   });

        var accordionImportExportServicesPanel= new Ext.Panel({
                                  split:true,
                                  bodyStyle : {background: "#79a3cb"},
                                  anchor:'100% 80%',
                                  margins:'5 0 5 5',
                                  //autoScroll : true,
                                  layout:'accordion',
                                  layoutConfig: {
                                        titleCollapse: false,
                                        animate: false,
                                        hideCollapseTool: false,
                                        titleCollapse: true,
                                        fill : false
                                    },
                                  

                                  items:[importServicesPanel,
                                         exportServicesPanel//,
                                         /*cat.advancedSearchPanel,
                                         cat.modelInstanceSearchPanel,
                                         cat.remoteSensingSearchPanel,
                                         cat.observationsSearchPanel,
                                         cat.settingsPanel*/]
                            });

  
        importExportGroupServiceWin = new WebGIS.Panel.WindowInterfacePanel({
                        title: 'Import / Export  Services',
                        id: 'importExportGroupServiceWin',
                        border: false,
                        animCollapse : true,
                        maximizable : true,
                       // autoScroll : true,
                                resizable : false,
                                collapsible: true,
                        layout: 'fit',
                        loadingBarImg: "images/loader1.gif",
                        loadingBarImgPadding: 60,
                        loadingMessage: "Loading... Please Wait...",
                        loadingMessagePadding: 30,
                        loadingMessageColor: "black",
                        loadingPanelColor: "#d9dce0",
                        loadingPanelDuration: 1000,
                        listeners:{
                          hide: function(){
                             // alert("close");
                              spot.hide();
                          },
                          collapse: function(){
                              spot.hide();
                          },
                          expand: function(){
                              spot.show('importExportGroupServiceWin');
                          }
                        },
                        
                        width: screen.width/2.1,
                        height: screen.height/2.1,
                        closeAction:'hide',
                        items:[accordionImportExportServicesPanel]
			});
       importExportGroupServiceWin.show();
       importExportGroupServiceWin.insertLoadingPanel();
       
       //accordionImportExportServicesPanel.render(document.getElementById("accordionImportExportServicesPanel"));
        accordionImportExportServicesPanel.layout.setActiveItem(0);
        importGroupServiceIO.formsPanel.render(document.getElementById("importServicesInterface"));
        importGroupServiceIO.render();

        accordionImportExportServicesPanel.layout.setActiveItem(1);
        exportGroupServiceIO.formsPanel.render(document.getElementById("exportServicesInterface"));
        exportGroupServiceIO.render();

        accordionImportExportServicesPanel.layout.setActiveItem(0);
     //   importExportGroupServiceIO.formsTab.setActiveTab(0);
    }else
       importExportGroupServiceWin.show();

      
       spot.show('importExportGroupServiceWin');
    
    
    // importExportGroupServiceWin.on('close', function(){/*wpsWizardWindow=null*/window.location.reload();});
}

function exportServices(){
    var exportInterfaceValues= exportGroupServiceIO.getFormValues();

    var httpGetRequest=exportInterfaceValues['ServiceUrl']+"&services="+exportInterfaceValues['services'];
  
    window.location.href = httpGetRequest;
   /* var exportResponse= function(response){
          alert(response);
          var responseDocument= Sarissa.getDomDocument();
          responseDocument=(new DOMParser()).parseFromString(response, "text/xml");
          responseDocument.setProperty("SelectionLanguage", "XPath");
                 
    };*/

   /* var exportResponseTimeOut= function(){
         Ext.Msg.alert('Error', 'Export Services Group Request: TIME OUT');
    };
    var exportResponseError= function(response){
         Ext.Msg.alert('Error', 'Export Services Group Request: Internal Error ');
    };

    sendXmlHttpRequestTimeOut("GET",
                              httpGetRequest,
                              false, null, 999,
                              exportResponse,
                              exportResponseTimeOut,
                              null, null, exportResponseError);*/


}


var importServices=function(){
  
     if(importGroupServiceIO.formsArray[0].getForm().isValid()){
        // alert(importGroupServiceIO.formsArray[0].getForm().findField(this.filePathID));
        // if(importGroupServiceIO.formsArray[0].getForm().findField(this.filePathID))

	  importGroupServiceIO.formsArray[0].getForm().submit({
	        url: 'manager?cmd=importGroupServices',
	        waitMsg: 'Loading services Group...',
	        success: function(form, action){

	           Ext.Msg.show({
                          title: 'Import Services',
                          buttons: Ext.Msg.OK,
                          width: Math.floor((screen.width/100)*50),
                          msg: action.response.responseText,
                          fn: function(){
                            window.location = 'main.jsp';
                          },
                          icon: Ext.MessageBox.INFO
                      });
                   
	         },
                failure: function(form, action) {
                   Ext.Msg.show({
                          title: 'Import Services: Internal Error ',
                          buttons: Ext.Msg.OK,
                          width: Math.floor((screen.width/100)*50),
                          msg: 'Internal Error:  '+action.response.responseText,
                          icon: Ext.MessageBox.ERROR
                      });
                
                }
	     });
         }
}



function createServiceInterface(){

   var i,k;
   var header;

   

   if(createServiceWin == null){

        var elementImage=document.getElementById("createServiceImage");

        elementImage.src="images/createServiceLoader.gif";
        for(var k=0; k<arrayPlugin.length; k++){
            var getPluginInfo=function(response){
                     if(!response){
                             Ext.Msg.show({
                                 title:'Get Plugin: Error',
                                 buttons: Ext.Msg.OK,
                                 msg: 'Service Exception!',
                                 animEl: 'elId',
                                 icon: Ext.MessageBox.ERROR
                              });
                       }else{
                            var jsonResponseObj=eval('new Object(' + response + ')');
                            
                            servicesXMLInterface.push(jsonResponseObj);
                       }

                   };

                 var getPluginTimeOut=function(){
                     Ext.Msg.show({
                        title:'Get Plugin: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                 };

                 var globalControl=true;
                
                 var onSubmit=sendXmlHttpRequestTimeOut("GET",
                             arrayPlugin[k],
                             false, null, 800000, getPluginInfo, getPluginTimeOut,null);

        }
       
        


        for(i=0; i<servicesXMLInterface.length;i++){
           servicesInterfaces.push(createPanelExjFormByXml(servicesXMLInterface[i].xmlUrl));
           header=document.getElementById("body");
           header.innerHTML+="\n<style type=\"text/css\">\n"+
                                "."+servicesXMLInterface[i].name+"cls {\n"+
                                    "background-image:url("+servicesXMLInterface[i].icon+");\n"+
                                "}\n"+
                            "</style>\n";
           servicesAccordionPanels.push(new Ext.Panel({
                            title: servicesXMLInterface[i].title,
                            border: true,
                            autoScroll : true,
                            id: servicesXMLInterface[i].name+"ServicePanel",
                            bodyColor: '#79a3cb',
                            html: "<div id='"+servicesXMLInterface[i].name+"ServiceInterface'>",
                            iconCls: servicesXMLInterface[i].name+'cls'
                   }));





        }

       var accordionCreateServicePanel=new Ext.Panel({
                          split:true,
                          bodyStyle : {background: "#79a3cb"},
                          anchor:'100% 80%',
                          margins:'5 0 5 5',
                          //autoScroll : true,
                                  layout:'accordion',
                                  layoutConfig: {
                                        titleCollapse: false,
                                        animate: false,
                                        hideCollapseTool: false,
                                        titleCollapse: true,
                                        fill : false
                                    },
                                  items:servicesAccordionPanels
                            });
;

         createServiceWin = new WebGIS.Panel.WindowInterfacePanel({
                        title: 'Create new Toolbox Service',
                        id: 'createServiceWin',
                        border: false,
                        animCollapse : true,
                        maximizable : true,
                        autoScroll : true,
                        resizable : false,
                        collapsible: true,
                        layout: 'fit',
                        loadingBarImg: "images/loader1.gif",
                        loadingBarImgPadding: 60,
                        loadingMessage: "Loading... Please Wait...",
                        loadingMessagePadding: 30,
                        loadingMessageColor: "black",
                        loadingPanelColor: "#d9dce0",
                        loadingPanelDuration: 2000,
                        listeners:{
                          hide: function(){
                             // alert("close");
                              spot2.hide();
                          },
                          collapse: function(){
                              spot2.hide();
                          },
                          expand: function(){
                              spot2.show('createServiceWin');
                          }
                        },

                        width: screen.width/2.1,
                        height: screen.height/2.1,
                        closeAction:'hide',
                       // html: "<div id='newServiceAggordionInterface'>"
                        items:[accordionCreateServicePanel]
			});
        createServiceWin.show();
        createServiceWin.insertLoadingPanel();

        var elementImage=document.getElementById("createServiceImage");

        elementImage.src="images/createService.png";

        // accordionCreateServicePanel.render(document.getElementById("newServiceAggordionInterface"));
        for(i=0; i<servicesXMLInterface.length;i++){
            accordionCreateServicePanel.layout.setActiveItem(i);
            servicesInterfaces[i].formsPanel.render(document.getElementById(servicesXMLInterface[i].name+"ServiceInterface"));
            servicesInterfaces[i].render();

        }
    

        accordionCreateServicePanel.layout.setActiveItem(0);
        spot2.show('createServiceWin');

        var elementImage=document.getElementById("createServiceImage");

        elementImage.src="images/createService.png";
       
    }else{
       createServiceWin.show();
       spot2.show('createServiceWin');
    }
       
    
}


///TOOLBOX/rest/gui/getJs/<ID>
/*var restGetJsURL="/TOOLBOX/rest/gui/getJs/";"/TOOLBOX/jsScripts/";*/
var restGetJsURL="ProxyRedirect?url=http://192.168.24.121:8080/TOOLBOX/rest/gui/getJs/";
function executeRemoteMethod(methodID){;
    var restRequest=restGetJsURL+methodID;
    var getJSMethod=function(response){
             if(!response)
                          Ext.Msg.show({
                                title:'getJSMethod: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
              else{
                  alert(response);
                  var jsMethod=eval(response);
                  alert(jsMethod);
                  jsMethod.call("10");
                  jsMethod('newTest');

              }

     };

     var wpsProcessingCreateOperationControlTimeOut=function(){
                 Ext.Msg.show({
                    title:'getJSMethod: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
     };

     var onSubmit=sendXmlHttpRequestTimeOut("GET",
            restRequest,
            false, null, 800000, getJSMethod, wpsProcessingCreateOperationControlTimeOut,null);


}

function serviceInterfaceModeChange(){
    var formPanel=this.findParentByType("form");
    var interfaceModeText=formPanel.form.items.items[3];
    interfaceModeText.setValue(this.getValueInformation("interfaceMode"));
     //alert(interfaceModeText.value);
}

function serviceInterfaceTypeChange(){

    var restModeRequest=this.getValueInformation("interfaceModeUrlRequest");
    var formPanel=this.findParentByType("form");
    var interfaceTypeText=formPanel.form.items.items[0];
    interfaceTypeText.setValue(this.getValueInformation("interfaceType"));
    //alert(interfaceTypeText.value);
    var interfaceNameText=formPanel.form.items.items[1];
    interfaceNameText.setValue(this.getValueInformation("interfaceName"));
    //alert(interfaceNameText.value);
    var interfaceVersionText=formPanel.form.items.items[2];
    interfaceVersionText.setValue(this.getValueInformation("interfaceVersion"));
    //alert(interfaceVersionText.value);
    var modeCombo=formPanel.form.items.items[5];
    
    var getInterfaceModeMethod=function(response){
             if(!response)
                          Ext.Msg.show({
                                title:'Get Interface Mode: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
              else{
                  var jsonResponseObj=eval('(' + response + ')');
                  var newStrore= new Array();
                  for(var u=0; u<jsonResponseObj.types.length; u++)
                      newStrore.push([jsonResponseObj.types[u]]);
                  modeCombo.setStore(newStrore,
                    ['interfaceMode'],
                    'interfaceMode');

              }

     };

     var getInterfaceModeTimeOut=function(){
                 Ext.Msg.show({
                    title:'Get Interface Mode: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
     };

     var onSubmit=sendXmlHttpRequestTimeOut("GET",
            restModeRequest,
            false, null, 800000, getInterfaceModeMethod, getInterfaceModeTimeOut,null);

}

function createServiceRequest(){
  var j=0;
  for(var i=0;i<servicesInterfaces.length;i++){
     validate=true;
     for(j=0;j<servicesInterfaces[i].formsArray.length;j++)
        validate=validate && servicesInterfaces[i].formsArray[j].getForm().isValid();
     if(validate){

        var interfaceActionMethod=servicesXMLInterface[i].actionMethod;
        interfaceActionMethod=replaceAll(interfaceActionMethod, "this", "servicesInterfaces[i]");
        
        eval(interfaceActionMethod);
        /*var xmlRequest=servicesInterfaces[i].getXmlKeyValueDocument("String",false);
        alert(xmlRequest);
        newServiceName=servicesInterfaces[i].formsArray[0].getForm().items.items[0].value;
        alert(serviceName);
        httpGetRequest="configureService.jsp?serviceName="+newServiceName;
        window.location.href = httpGetRequest;*/

     }

  }  
}


function createToolboxService(formCrateService){
    var xmlRequest=formCrateService.getXmlKeyValueDocument('String', false);

    //alert(xmlRequest);
    var serviceCreateControl=function(response){
             if(!response){
                     Ext.Msg.show({
                         title:'Create a new service: Error',
                         buttons: Ext.Msg.OK,
                         msg: 'Service Exception!',
                         animEl: 'elId',
                         icon: Ext.MessageBox.ERROR
                      });
               }else{
                var responseDocument= Sarissa.getDomDocument();
                responseDocument=(new DOMParser()).parseFromString(response, "text/xml");
                responseDocument.setProperty("SelectionLanguage", "XPath");

                responseDocument=new XmlDoc(responseDocument);

                var newServiceName=responseDocument.selectNodes("response/serviceName")[0].childNodes[0].nodeValue;
                window.location="configureService.jsp?serviceName="+newServiceName+"&info=servicecreated";
               }

           };
                 var serviceCreateControlTimeOut=function(){
                     Ext.Msg.show({
                        title:'Create a new service: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                 };

                 var globalControl=true;
                 var onSubmit=sendXmlHttpRequestTimeOut("PUT",
                             "rest/gui/createService.xml",
                             true, xmlRequest, 800000, serviceCreateControl, serviceCreateControlTimeOut,null);
}



function controlNewService(serviceName){
       var newServiceName;
     if(!serviceName){
       if(document.newService.importOrCreate[1].checked)
          newServiceName=document.newService.newServiceName.value;
     }else
         newServiceName=serviceName;
          if(newServiceName.length<=0){

               Ext.Msg.show({
                    title:'Create a new service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Please insert a new Service Name',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
               return false;
          }else{
             try{
             var controlServiceName=function(response){
                 if(!response){
                          Ext.Msg.show({
                                title:'Create a new service: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                      }else{
                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var isValid=xmlResponse.selectNodes("response")[0].getAttribute("value");
                        if(isValid !='true'){
                            Ext.Msg.show({
                                title:'Create a new service: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service already defined. Please choose another name',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                            });
                            globalControl=false;
                        }
                      }
             };
             var controlServiceTimeOut=function(){
                 Ext.Msg.show({
                    title:'Create a new service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
             };

             var globalControl=true;
             var onSubmit=sendXmlHttpRequestTimeOut("GET",
                         "manager?cmd=isServiceNameAvailable&serviceName="+newServiceName,
                         false, null, 8000, controlServiceName, controlServiceTimeOut,null);
            return globalControl && onSubmit;
              }catch(e){
                    return false;
              }
         }

       return true;
    }