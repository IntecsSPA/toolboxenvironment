


/*Import Form Interfaces -- START*/
    gcManager.loadGlobalScript("jsScripts/servicesManagement/scripts/importServices.js");
    gcManager.loadGlobalScript("jsScripts/servicesManagement/scripts/exportServices.js");
    gcManager.loadGlobalScript("jsScripts/servicesManagement/scripts/dupliacateServices.js");
    gcManager.loadGlobalScript("jsScripts/servicesManagement/scripts/deleteServices.js");
 /*Import Form Interfaces -- END*/


var servicesXMLInterface= new Array();
var arrayPlugin= new Array();


var servicesAccordionPanels=new Array();
var servicesInterfaces=new Array();

/* Service Management Interface Objects */
var duplicateServices;
var deleteServices;
var exportServices;
var importServices;


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


function managerGroupServices(){

    if(importExportGroupServiceWin == null){

        importServices=new ImportServicesInterface();
        exportServices=new ExportServicesInterface();
        duplicateServices=new DuplicateServicesInterface();
        deleteServices=new DeleteServicesInterface();
    
        var exportServicesPanel=new Ext.Panel({
                            title: 'Export Services',
                            border: true,
                            autoScroll : true,
                            id: "exportServicePanel",
                            bodyColor: '#E4E7E7',
                            listeners: {
                                "beforeexpand": function(){
                                    var checkboxGroup=Ext.getCmp("services_cont");
                                    checkboxGroup.updateValues();
                                }
                            },
                            html: "<div id='exportServicesInterface'>",
                            iconCls: 'export'
                   });

        var importServicesPanel=new Ext.Panel({
                            title: 'Import Services',
                            border: true,
                            autoScroll : true,
                            id: "importServicePanel",
                            bodyColor: '#E4E7E7',
                            html: "<div id='importServicesInterface'>",
                            iconCls: 'import'
                   });

        var duplicateServicesPanel=new Ext.Panel({
                            title: 'Duplicate Services',
                            border: true,
                            autoScroll : true,
                            id: "duplicateServicePanel",
                            bodyColor: '#E4E7E7',
                            listeners: {
                                "beforeexpand": function(){
                                    var checkboxGroup=Ext.getCmp("dupServices_cont");
                                    checkboxGroup.updateValues();
                                }
                            },
                            html: "<div id='duplicateServicesInterface'>",
                            iconCls: 'duplicate'
                   });

        var deleteServicesPanel=new Ext.Panel({
                            title: 'Delete Services',
                            border: true,
                            autoScroll : true,
                            id: "deleteServicePanel",
                            bodyColor: '#E4E7E7',
                            listeners: {
                                "beforeexpand": function(){
                                    var checkboxGroup=Ext.getCmp("servicesDel_cont");
                                    checkboxGroup.updateValues();
                                }
                            },
                            html: "<div id='deleteServicesInterface'>",
                            iconCls: 'delete'
                   });

        var accordionImportExportServicesPanel= new Ext.Panel({
                                  split:true,
                                  bodyStyle : {background: "#E4E7E7"},
                                  anchor:'100% 80%',
                                  autoScroll : true,
                                  margins:'5 0 5 5',
                                  layout:'accordion',
                                  layoutConfig: {
                                        fill : false,
                                        animate: false,
                                        sequence:false
                                    },
                                   items:[importServicesPanel,
                                         exportServicesPanel,
                                         duplicateServicesPanel,
                                         deleteServicesPanel
                                       ]
                            });

        importExportGroupServiceWin = new WebGIS.Panel.WindowInterfacePanel({
                        title: 'Manager  Services',
                        id: 'importExportGroupServiceWin',
                        border: false,
                        animCollapse : true,
                        maximizable : true,
                        autoScroll : true,
                        resizable : false,
                        draggable: false,
                        collapsible: false,
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
                              spot.hide();
                          },
                          collapse: function(){
                              spot.hide();
                          },
                          expand: function(){
                              spot.show('importExportGroupServiceWin');
                          }
                        },
                        width: screen.width/1.5,
                        height: screen.height/1.5,
                        closeAction:'hide',
                        items:[accordionImportExportServicesPanel]
			});
       importExportGroupServiceWin.show();
       importExportGroupServiceWin.insertLoadingPanel();

        accordionImportExportServicesPanel.layout.setActiveItem(0);
        importServices.render("importServicesInterface");
 
        accordionImportExportServicesPanel.layout.setActiveItem(1);
        exportServices.render("exportServicesInterface");

        accordionImportExportServicesPanel.layout.setActiveItem(2);
        duplicateServices.render("duplicateServicesInterface");

        accordionImportExportServicesPanel.layout.setActiveItem(3);
        deleteServices.render("deleteServicesInterface");

        accordionImportExportServicesPanel.layout.setActiveItem(0);
       
       
    }else
       importExportGroupServiceWin.show();

       spot.show('importExportGroupServiceWin');  
}


function createServiceInterface(){

   var i,k;
   var header;

   

   if(createServiceWin == null){

        var elementImage=document.getElementById("createServiceImage");

            elementImage.setAttribute("src", "images/loader1.gif");
        //elementImage.src="images/createServiceLoader.gif";
       // elementImage.src="images/loader1.gif";

        var getPlugins=function(response){
            var jsonResponseObj=eval('new Object(' + response + ')');
            arrayPlugin=jsonResponseObj.sections;
        };

        var getPluginsTimeOut=function(response){
            Ext.Msg.show({
                        title:'Get Plugins: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });

        };


        var onSubmit=sendXmlHttpRequestTimeOut("GET",
                             "rest/gui/creationWizardSections.json",
                             false, null, 800000, getPlugins, getPluginsTimeOut,null);


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
                             arrayPlugin[k]+".json",
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
                            bodyColor: '#E4E7E7',
                            html: "<div id='"+servicesXMLInterface[i].name+"ServiceInterface'>",
                            iconCls: servicesXMLInterface[i].name+'cls'
                   }));





        }

       var accordionCreateServicePanel=new Ext.Panel({
                          split:true,
                          autoScroll : true,
                          bodyStyle : {background: "#E4E7E7"},
                          anchor:'100% 80%',
                          margins:'5 0 5 5',
                                  layout:'accordion',
                                  layoutConfig: {
                                        titleCollapse: false,
                                        animate: false,
                                        hideCollapseTool: false,
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
                        draggable: false,
                        collapsible: false,
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
  var genValidate=false;
  for(var i=0;i<servicesInterfaces.length;i++){
     validate=true;
     for(j=0;j<servicesInterfaces[i].formsArray.length;j++)
        validate=validate && servicesInterfaces[i].formsArray[j].getForm().isValid();
     if(validate){
        genValidate=true;
        var interfaceActionMethod=servicesXMLInterface[i].actionMethod;
        interfaceActionMethod=replaceAll(interfaceActionMethod, "this", "servicesInterfaces[i]");
        
        eval(interfaceActionMethod);
        

     }

  }
    if(!genValidate)
         Ext.Msg.show({
                         title:'Create a new service: Error',
                         buttons: Ext.Msg.OK,
                         msg: 'Missing mandatory parameters!',
                         animEl: 'elId',
                         icon: Ext.MessageBox.ERROR
         });

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



    