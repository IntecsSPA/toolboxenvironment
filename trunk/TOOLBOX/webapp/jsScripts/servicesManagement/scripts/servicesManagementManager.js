


var textAreaFrameH=(screen.height/1.9);
var textAreaFrameW=(screen.width/1.55);

/* Service Management XML Interfaces*/
var exportGroupServicesXML="jsScripts/servicesManagement/resources/xml/exportGroupServicesPanel.xml";
var importGroupServicesXML="jsScripts/servicesManagement/resources/xml/importGroupServicesPanel.xml";


/* Service Management Interface Objects */
var exportGroupServiceIO=null;
var importGroupServiceIO=null;


/* Service Management Windows*/
var importExportGroupServiceWin=null;



var spot = new Ext.ux.Spotlight({
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

