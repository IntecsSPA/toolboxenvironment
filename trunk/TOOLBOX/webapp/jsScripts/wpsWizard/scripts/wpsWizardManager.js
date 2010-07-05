/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var formCrateWpsService;
var wpsWizardWindow=null;
var insertDescribeWizardWindow=null;
var editAreaPath="jsScripts/import/edit_area/edit_area_full.js";
var textAreaFrameH=(screen.height/1.9);
var textAreaFrameW=(screen.width/1.55);
var toolsServlet="Tools";
var grassTemplate;
var shellTemplate;
var toolboxTemplate;
var insertGrassScriptWizardWindow=null;

var spot = new Ext.ux.Spotlight({
        //easing: 'easeOut',
        duration: .3
    });

function wpsWizardManager(){

    var xmlDocumentUrl="jsScripts/wpsWizard/resources/xml/createWPSServicePanel.xml";
    formCrateWpsService=createPanelExjFormByXml(xmlDocumentUrl);
    if(wpsWizardWindow == null){
        wpsWizardWindow = new Ext.Window({
                        title: 'Create a new Web Processing Service',
                        border: false,
                        id: 'wpsWizardWindow',
                        animCollapse : true,
                        autoScroll : true,
                                resizable : false,
                                collapsible: true,
                        layout: 'fit',
                        listeners:{
                          hide: function(){
                              spot.hide();
                          },
                          collapse: function(){
                              spot.hide();
                          },
                          expand: function(){
                              spot.show('wpsWizardWindow');
                          }
                        },
                        width: screen.width/2.1,
                        height: screen.height/2.1,
                        closeAction:'close',
                        //onEsc : function(){wpsWizardWindow=null},
                        items:[formCrateWpsService.formsPanel]
			});
        wpsWizardWindow.on('close', function(){/*wpsWizardWindow=null*/window.location.reload();});
        wpsWizardWindow.show();
        formCrateWpsService.render();
        formCrateWpsService.formsTab.setActiveTab(0);
    }
  spot.show('wpsWizardWindow');
}

function wpsProcessingWizardManager(){
    var id="InsertDescribeInterface";
    var insertDescribeWizard_html="<table width='100%'></br><tr rowspan='2' BGCOLOR='#325e8f'><td align='center'><b style='color: #ffffff;'>Insert WPS Process Description: </b><br></td></tr><tr><td align='left' width='100%'>"+
                                     "<form name='formFile_"+id+"' action='"+toolsServlet+"?cmd=putFile&type=multipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/15+"' method='POST' enctype='multipart/form-data' target='iframeRequest_"+id+"'>"+
                                        "<input type='file' id='FILE' name='FILE' value='' width='"+textAreaFrameW/1.4+"' />"+
                                        "<input type='submit' name='buttonSubmit_"+id+"' value='Load File'/>"+
                                    "</form></td></tr><tr align='center'><td>"+
                                    "<iframe scrolling='no' FRAMEBORDER='0' src='"+toolsServlet+"?cmd=putFile&type=nomultipart&editAreaPath="+editAreaPath+"&modality=edit&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/15+"' name='iframeRequest_"+id+"' width='"+textAreaFrameW+"' height='"+(textAreaFrameH+(textAreaFrameH/50))+"' marginwidth='0' marginheight='0'></iframe>"+
                                    "<tr><td><input type='button' name='buttonSendDescribe_"+id+"' value='Parse WPS Processing Describe Information' onclick='parseWPSDescribeProcessingRequest();'/>"+
                                    "</td></tr></table>";

   if(insertDescribeWizardWindow == null){

           insertDescribeWizardWindow = new WebGIS.Panel.WindowInterfacePanel({
                                title: 'Create a new WPS Processing',
                                border: false,
                                id: 'insertDescribeWizardWindow',
                                animCollapse : true,
                                autoScroll : true,
                                resizable : false,
                                collapsible: true,
                                layout: 'fit',
                                loadingBarImg: "images/loader1.gif",
                                loadingBarImgPadding: 90,
                                loadingMessage: "Loading... Please Wait...",
                                loadingMessagePadding: 60,
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
                                      spot.show('insertDescribeWizardWindow');
                                  }
                                },
                                width: screen.width/1.5,
                                height: screen.height/1.5,
                                closeAction:'close',
                                html: "<div id='insertDescribeWizardWindow_div'/>"
                    });
           insertDescribeWizardWindow.on('close', function(){/*insertDescribeWizardWindow=null*/window.location.reload();});
           
           insertDescribeWizardWindow.show();
           insertDescribeWizardWindow.insertLoadingPanel();
           document.getElementById("insertDescribeWizardWindow_div").innerHTML=insertDescribeWizard_html;
           spot.show('insertDescribeWizardWindow');
   }
}

function createWPSRequest(){
    var xmlRequest=formCrateWpsService.getXmlKeyValueDocument('String', false);
    var newServiceName=formCrateWpsService.getFormValues()["serviceName"];
    
    if(controlNewService(newServiceName)){
        var wpsServiceCreateControl=function(response){
                     if(!response){
                              Ext.Msg.show({
                                    title:'Create a new WPS service: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Service Exception!',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                              });
                          }else
                            window.location="serviceConfiguration.jsp?serviceName="+newServiceName;

                 };
                 var wpsServiceCreateControlTimeOut=function(){
                     Ext.Msg.show({
                        title:'Create a new WPS service: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                 };

                 var globalControl=true;
                 var onSubmit=sendXmlHttpRequestTimeOut("POST",
                             "manager?cmd=wpsServiceCreate",
                             true, xmlRequest, 800000, wpsServiceCreateControl, wpsServiceCreateControlTimeOut,null);
  }
}

function parseWPSDescribeProcessingRequest(){
   var describeProcess=parent.iframeRequest_InsertDescribeInterface.window.getEditAreaValue();
   if(describeProcess != ''){
        var wpsProcessingDescribeParseControl=function(response){
                 if(!response){
                          Ext.Msg.show({
                                title:'Create a new WPS Processing: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                      }else{
                        
                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var ValidateErrors=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/ErrorValidation");
                        if(ValidateErrors.length >0){
                            Ext.Msg.show({
                                title:'Create a new WPS Processing: Describe Process Validation Error',
                                buttons: Ext.Msg.OK,
                                msg: ValidateErrors[0].firstChild.nodeValue,
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                        }else{
                                var grassTemplateElements=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/GrassTemplate");
                                var shellTemplateElements=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/ShellTemplate");
                                var toolboxTemplateElements=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/ToolboxTemplate");
                                var serviceNameElements=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/ServiceName");
                                var processNameElements=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/ProcessingName");
                                var asynchronousElements=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/Asynchronous");

                                grassTemplate=grassTemplateElements[0].firstChild.nodeValue;
                                shellTemplate=shellTemplateElements[0].firstChild.nodeValue;
                                toolboxTemplate=toolboxTemplateElements[0].firstChild.nodeValue;

                                /*Ext.Msg.show({
                                title:'Grass Template',
                                buttons: Ext.Msg.OK,
                                msg: grassTemplate,
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                                  });*/
                                var serviceName=serviceNameElements[0].firstChild.nodeValue;
                                var processingName=processNameElements[0].firstChild.nodeValue;
                                var asynchronous=asynchronousElements[0].firstChild.nodeValue;
                                if(controlNewWPSOperation(serviceName,"ExecuteProcess_"+processingName,"ExecuteProcess_"+processingName)){
                                    insertDescribeWizardWindow.destroy();
                                     var id="engineEditArea";
                                     var  insertGrassScriptWizard_html="<table width='100%'></br><tr rowspan='2' BGCOLOR='#325e8f'><td align='center'><b style='color: #ffffff;'>Insert a Engine modality for the WPS Process: </b><br></td></tr>"+
                                                 "<tr><td align='left' width='100%'>"+
                                                    "<form id='formEngine'><table width='100%'><tr><td colspan='3' align='center'><b>Select a WPS Processing Type:</b></td></tr>"+
                                                    "<tr><td width='20%'><input type='radio' name='WPSType' value='GrassEngine' onchange='javascript:getElementById(\"engineMessageInformation\").innerHTML=\"<b>Insert a GRASS Shell Script :</b>\";parent.iframeRequest_engineEditArea.window.setEditAreaValue(grassTemplate);'/><h8>Grass Engine</h8></td>"+
                                                    "<td width='20%'><input type='radio' name='WPSType' value='ShellEngine' onchange='javascript:getElementById(\"engineMessageInformation\").innerHTML=\"<b>Insert a Shell Script :</b>\";parent.iframeRequest_engineEditArea.window.setEditAreaValue(shellTemplate);'/><h8>Shell Script</h8></td>"+
                                                    "<td width='20%'><input type='radio' name='WPSType' value='JavaEngine'  onchange='javascript:getElementById(\"engineMessageInformation\").innerHTML=\"<b>Insert a TOOLBOX Script :</b>\";parent.iframeRequest_engineEditArea.window.setEditAreaValue(toolboxTemplate);'/><h8>Toolbox Script</h8></td>"+
                                                    "<td align='left'><div id='engineMessageInformation'><b>-</b></div></td></tr>"+
                                                    "</table></form>"+
                                                 "</td></tr>"+
                                                 "<tr><td align='left' width='100%'>"+
                                                 "<form name='formFile_"+id+"' action='"+toolsServlet+"?cmd=putFile&type=multipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/17+"' method='POST' enctype='multipart/form-data' target='iframeRequest_"+id+"'>"+
                                                    "<input type='file' id='FILE' name='FILE' value='' width='"+textAreaFrameW/1.4+"' />"+
                                                    "<input type='submit' name='buttonSubmit_"+id+"' value='Load File'/>"+
                                                "</form></td></tr><tr align='center'><td>"+
                                                "<iframe scrolling='no' FRAMEBORDER='0' src='"+toolsServlet+"?cmd=putFile&type=nomultipart&editAreaPath="+editAreaPath+"&modality=edit&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/17+"' name='iframeRequest_"+id+"' width='"+textAreaFrameW+"' height='"+(textAreaFrameH/1.1+(textAreaFrameH/50))+"' marginwidth='0' marginheight='0'></iframe>"+
                                                "<tr><td><input type='button' name='buttonSendDescribe_"+id+"' value='Create WPS Processing' onclick='createWPSProcessingRequest(\""+serviceName+"\",\""+processingName+"\",\""+asynchronous+"\");'/>"+
                                                "</td></tr></table>";

                                        if(insertGrassScriptWizardWindow==null){
                                            insertGrassScriptWizardWindow = new Ext.Window({
                                                            title: 'Create a new WPS Processing',
                                                            border: false,
                                                            animCollapse : true,
                                                            id: 'insertGrassScriptWizardWindow',
                                                            autoScroll : true,
                                                            resizable : false,
                                                            collapsible: true,
                                                            layout: 'fit',
                                                            listeners:{
                                                              hide: function(){
                                                                  spot.hide();
                                                              },
                                                              collapse: function(){
                                                                  spot.hide();
                                                              },
                                                              expand: function(){
                                                                  spot.show('insertGrassScriptWizardWindow');
                                                              }
                                },
                                                            width: screen.width/1.5,
                                                            height: screen.height/1.5,
                                                            closeAction:'close',
                                                            html: insertGrassScriptWizard_html
                                                            //items:[formCrateWpsService.formsPanel]
                                                });

                                            insertGrassScriptWizardWindow.on('close', function(){/*insertGrassScriptWizardWindow=null*/ window.location.reload();});
                                            insertGrassScriptWizardWindow.show();
                                            spot.show('insertGrassScriptWizardWindow');
                                           // parent.iframeRequest_engineEditArea.window.setEditAreaValue(toolboxTemplate);
                                        }
                                }
                              }
                      }
             };
             var wpsProcessingDescribeParseControlTimeOut=function(){
                 Ext.Msg.show({
                    title:'Create a new WPS Processing: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
             };

             var globalControl=true;
             var onSubmit=sendXmlHttpRequestTimeOut("POST",
                         "manager?cmd=wpsProcessingCreate&step=parseDescribe&serviceName="+currentService,
                         true, describeProcess, 800000, wpsProcessingDescribeParseControl, wpsProcessingDescribeParseControlTimeOut,null);
   }else{
      Ext.Msg.show({
                    title:'Create a new WPS Processing Operation: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Please insert the Describe Process file in the Edit Area.',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
      });

   }
}

function createWPSProcessingRequest(serviceName,processingName,asynchronous){
    var engineScript=parent.iframeRequest_engineEditArea.window.getEditAreaValue();

    var engineTypeElements=document.getElementById("formEngine").WPSType;
    var engineType="";


    for(var i=0; engineTypeElements.length; i++){
        if(engineTypeElements[i].checked){
            engineType=engineTypeElements[i].value;
            break;
        }
    }
    if(engineScript!=""){
        var wpsProcessingCreateOperationControl=function(response){
                    if(!response){
                          Ext.Msg.show({
                                title:'Create a new WPS Processing: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                      }else{
                          var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                           var ValidateErrors=xmlResponse.selectNodes("createWPSOperationResult/ErrorValidation");
                          if(ValidateErrors.length >0){
                            Ext.Msg.show({
                                title:'Create a new WPS Processing: Toolbox Script Validation Error',
                                buttons: Ext.Msg.OK,
                                msg: ValidateErrors[0].firstChild.nodeValue,
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                            });
                        }else
                           window.location="manageOperations.jsp?serviceName="+serviceName;

                      }
             };
             var wpsProcessingCreateOperationControlTimeOut=function(){
                 Ext.Msg.show({
                    title:'Create a new WPS Processing: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
             };

             var onSubmit=sendXmlHttpRequestTimeOut("POST",
                         "manager?cmd=wpsProcessingCreate&step=generateOperation&engineType="+engineType+"&serviceName="+serviceName+"&processingName="+processingName+"&asynchronous="+asynchronous,
                         true, engineScript, 800000, wpsProcessingCreateOperationControl, wpsProcessingCreateOperationControlTimeOut,null);
    }else{
        switch (engineType){
            case "JavaEngine":
                    Ext.Msg.show({
                            title:'Create a new WPS Processing Operation: Error',
                            buttons: Ext.Msg.OK,
                            msg: 'Please insert the Toolbox script file in the Edit Area.',
                            animEl: 'elId',
                            icon: Ext.MessageBox.ERROR
                    });
                break;
            case "GrassEngine":
                    Ext.Msg.show({
                            title:'Create a new WPS Processing Operation: Error',
                            buttons: Ext.Msg.OK,
                            msg: 'Please insert the GRASS shell script file in the Edit Area.',
                            animEl: 'elId',
                            icon: Ext.MessageBox.ERROR
                    });
                break;

        }

    }

}


function controlNewWPSOperation(serviceName,newOperationName,newOperationSoapAction){
          var globalControl=true;
          if(newOperationName.length<=0 || newOperationSoapAction.length<=0){
               Ext.Msg.show({
                    title:'Create a new WPS Processing: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Please change a new WPS Processing Operation Name in the Describe Document',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
               return false;
          }else{
             try{
               var controlOperationName=function(response){
                 if(!response){
                          Ext.Msg.show({
                                title:'Create a new operation: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                          globalControl=false;
                      }else{
                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var isValid=xmlResponse.selectNodes("response")[0].getAttribute("value");
                            if(isValid !='true'){
                                Ext.Msg.show({
                                    title:'Create a new WPS Processing Operation: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Operation already defined. Please change a new WPS Processing Operation Name in the Describe Document',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });
                                globalControl=false;
                            }else{

                                var controlSoapAction=function(response){
                                    if(!response){
                                          Ext.Msg.show({
                                                title:'Soap Action Control: Error',
                                                buttons: Ext.Msg.OK,
                                                msg: 'Service Exception!',
                                                animEl: 'elId',
                                                icon: Ext.MessageBox.ERROR
                                          });
                                       globalControl=false;
                                    }else{
                                        var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                                        var isValid=xmlResponse.selectNodes("response")[0].getAttribute("value");
                                        if(isValid !='true'){
                                            Ext.Msg.show({
                                                title:'Create a new WPS Processing Operationn: Error',
                                                buttons: Ext.Msg.OK,
                                                msg: 'Operation already defined. Please change a new WPS Processing Operation Name in the Describe Document',
                                                animEl: 'elId',
                                                icon: Ext.MessageBox.ERROR
                                            });
                                            globalControl=false;
                                        }
                                 }
                              };
                             var controlSoapActionTimeOut=function(){
                                 Ext.Msg.show({
                                    title:'Soap Action Control: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Request TIME-OUT!',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });
                             };

                            var result=sendXmlHttpRequestTimeOut("GET",
                                "manager?cmd=isSoapActionAvailable&serviceName="+serviceName+"&soapAction="+newOperationSoapAction,
                                false, null, 8000, controlSoapAction, controlSoapActionTimeOut,null);
                            }
                            globalControl= globalControl && result;

                      }
             };
             var controlOperationTimeOut=function(){
                 Ext.Msg.show({
                    title:'Create a new WPS Processing Operation: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
             };


             var onsubmit=sendXmlHttpRequestTimeOut("GET",
                         "manager?cmd=isOperationNameAvailable&serviceName="+serviceName+"&operationName="+newOperationName,
                         false, null, 8000, controlOperationName, controlOperationTimeOut,null);

              return globalControl && onsubmit;
              }catch(e){
                    return false;
              }
            }
         

       return true;
    }

var describeResponse;
var wpsDescribeEditorWindow=null;
function editDescribeProcess(serviceName, processName, async, engineType){
    var id="editDescribeProcess";
    var describePath="AdditionalResources/WPS/DescribeProcess/DescribeInformation_"+processName+".xml";
    var currentDescribe=getWPSResource(serviceName, describePath);
    var ind=currentDescribe.indexOf("?>")+2;

    var describe=currentDescribe.substr(ind, currentDescribe.length);
    describeResponse="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<wps:ProcessDescriptions xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" service=\"WPS\" version=\"1.0.0\" xml:lang=\"en-UK\">\n";
    describeResponse+=describe;
    describeResponse+="\n</wps:ProcessDescriptions>";

    var insertDescribeWizard_html="<table width='100%'></br><tr rowspan='2' BGCOLOR='#325e8f'><td align='center'><b style='color: #ffffff;'>Insert WPS Process Description: </b><br></td></tr><tr><td align='left' width='100%'>"+
                                     "<form name='formFile_"+id+"' action='"+toolsServlet+"?cmd=putFile&type=multipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/15+"' method='POST' enctype='multipart/form-data' target='iframeRequest_"+id+"'>"+
                                        "<input type='file' id='FILE' name='FILE' value='' width='"+textAreaFrameW/1.4+"' />"+
                                        "<input type='submit' name='buttonSubmit_"+id+"' value='Load File'/>"+
                                    "</form></td></tr><tr align='center'><td>"+
                                    "<iframe scrolling='no' FRAMEBORDER='0' src='"+toolsServlet+"?cmd=putFile&type=nomultipart&editAreaPath="+editAreaPath+"&modality=edit&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/15+"' name='iframeRequest_"+id+"' width='"+textAreaFrameW+"' height='"+(textAreaFrameH+(textAreaFrameH/50))+"' marginwidth='0' marginheight='0'></iframe>"+
                                    "<tr><td><input type='button' name='buttonSendDescribe_"+id+"' value='Change WPS Processing Describe' onclick=\"changeWPSDescribeProcessingRequest('"+serviceName+"', '"+processName+"', '"+async+"', '"+engineType+"');\">"+
                                    "</td></tr></table>";

    wpsDescribeEditorWindow = new WebGIS.Panel.WindowInterfacePanel({
                                title: 'Edit Describe Process',
                                border: false,
                                id: 'editDescribeWindow',
                                animCollapse : true,
                                autoScroll : true,
                                resizable : false,
                                collapsible: true,
                                layout: 'fit',
                                loadingBarImg: "images/loader1.gif",
                                loadingBarImgPadding: 90,
                                loadingMessage: "Loading... Please Wait...",
                                loadingMessagePadding: 60,
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
                                      spot.show('editDescribeWindow');
                                  }
                                },
                                width: screen.width/1.5,
                                height: screen.height/1.5,
                                closeAction:'close',
                                html: "<div id='editDescribeWindow_div'/>"
                    });
           wpsDescribeEditorWindow.on('close', function(){window.location.reload();});

           wpsDescribeEditorWindow.show();
           wpsDescribeEditorWindow.insertLoadingPanel();
           document.getElementById("editDescribeWindow_div").innerHTML=insertDescribeWizard_html;
           spot.show('editDescribeWindow');

           //parent.iframeRequest_editDescribeProcess.window.setEditAreaValue(describeResponse);

           setTimeout("parent.iframeRequest_editDescribeProcess.window.setEditAreaValue(describeResponse)",1250);
}

var currentEngineScript;
var wpsEngineScriptEditorWindow;
function editEngineScript(serviceName, processName, async, engineType, scriptPath){
    var id="editEngineScript";

    currentEngineScript=getWPSResource(serviceName, scriptPath);


    var insertDescribeWizard_html="<table width='100%'></br><tr rowspan='2' BGCOLOR='#325e8f'><td align='center'><b style='color: #ffffff;'>Insert WPS Process "+engineType+" Script: </b><br></td></tr><tr><td align='left' width='100%'>"+
                                     "<form name='formFile_"+id+"' action='"+toolsServlet+"?cmd=putFile&type=multipart&modality=edit&editAreaPath="+editAreaPath+"&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/15+"' method='POST' enctype='multipart/form-data' target='iframeRequest_"+id+"'>"+
                                        "<input type='file' id='FILE' name='FILE' value='' width='"+textAreaFrameW/1.4+"' />"+
                                        "<input type='submit' name='buttonSubmit_"+id+"' value='Load File'/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
                                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' name='buttonTemplate_"+id+"' value='Load "+engineType+" Processing Template' onclick=\"loadEngineTemplate('"+serviceName+"', '"+processName+"', '"+engineType+"')\"/>"+
                                    "</form></td></tr><tr align='center'><td>"+
                                    "<iframe scrolling='no' FRAMEBORDER='0' src='"+toolsServlet+"?cmd=putFile&type=nomultipart&editAreaPath="+editAreaPath+"&modality=edit&cols="+textAreaFrameW/7.2+"&rows="+textAreaFrameH/15+"' name='iframeRequest_"+id+"' width='"+textAreaFrameW+"' height='"+(textAreaFrameH+(textAreaFrameH/50))+"' marginwidth='0' marginheight='0'></iframe>"+
                                    "<tr><td><input type='button' name='buttonSendEngineScript_"+id+"' value='Change WPS Processing Engine Script' onclick=\"changeWPSEngineScriptRequest('"+serviceName+"', '"+processName+"', '"+async+"', '"+engineType+"');\">"+
                                    "</td></tr></table>";

    wpsEngineScriptEditorWindow = new WebGIS.Panel.WindowInterfacePanel({
                                title: 'Edit '+engineType+' Script',
                                border: false,
                                id: 'edit'+engineType+'SciptWindow',
                                animCollapse : true,
                                autoScroll : true,
                                resizable : false,
                                collapsible: true,
                                layout: 'fit',
                                loadingBarImg: "images/loader1.gif",
                                loadingBarImgPadding: 90,
                                loadingMessage: "Loading... Please Wait...",
                                loadingMessagePadding: 60,
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
                                      spot.show('edit'+engineType+'SciptWindow');
                                  }
                                },
                                width: screen.width/1.5,
                                height: screen.height/1.5,
                                closeAction:'close',
                                html: "<div id='edit"+engineType+"SciptWindow_div'/>"
                    });
           wpsEngineScriptEditorWindow.on('close', function(){window.location.reload();});

           wpsEngineScriptEditorWindow.show();
           wpsEngineScriptEditorWindow.insertLoadingPanel();
           document.getElementById("edit"+engineType+"SciptWindow_div").innerHTML=insertDescribeWizard_html;
           spot.show("edit"+engineType+"SciptWindow_div");

           //parent.iframeRequest_editDescribeProcess.window.setEditAreaValue(describeResponse);

           setTimeout("parent.iframeRequest_"+id+".window.setEditAreaValue(currentEngineScript)",1250);
}


function changeWPSDescribeProcessingRequest(serviceName, processingName, async, engineType){
   var describeProcess=parent.iframeRequest_editDescribeProcess.window.getEditAreaValue();

   var wpsProcessingDescribeParseControl=function(response){
       var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var ValidateErrors=xmlResponse.selectNodes("parseWPSDescribrProcessingResult/ErrorValidation");
                        if(ValidateErrors.length >0){
                            Ext.Msg.show({
                                title:'Update WPS Processing: Error',
                                buttons: Ext.Msg.OK,
                                msg: ValidateErrors[0].firstChild.nodeValue,
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                        }else{
                           Ext.Msg.show({
                                title:'Update WPS Processing',
                                buttons: Ext.Msg.OK,
                                msg: 'Processing Udpated',
                                animEl: 'elId',
                                fn: function(){
                                    wpsDescribeEditorWindow.close();
                                },
                                icon: Ext.MessageBox.INFO
                          });
                          
                        }
   };

   var wpsProcessingDescribeParseControlTimeOut=function(){
                 Ext.Msg.show({
                    title:'Update WPS Processing: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
   };
   sendXmlHttpRequestTimeOut("POST",
       "manager?cmd=wpsProcessingUpdate&step=updateDescribe&serviceName="+serviceName+"&processingName="+processingName+"&engineType="+engineType+"&async="+async,
        true, describeProcess, 800000, wpsProcessingDescribeParseControl, wpsProcessingDescribeParseControlTimeOut,null);

    
}

function changeWPSEngineScriptRequest(serviceName, processingName, async, engineType){
   var scriptEngine=parent.iframeRequest_editEngineScript.window.getEditAreaValue();

   var wpsUpdateScriptEngine=function(response){
    var xmlResponse = (new DOMParser()).parseFromString(response, "text/xml");
                        var result=xmlResponse.selectNodes("createWPSOperationResult");
                        if(result.length == 0){
                            Ext.Msg.show({
                                title:'Update WPS Processing Engine Script : Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Script Engine not updated',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                        }else{
                         var control=eval(result[0].firstChild.nodeValue);
                         if(control){
                           Ext.Msg.show({
                                title:'Update WPS Processing Script Engine',
                                buttons: Ext.Msg.OK,
                                msg: 'Script Engine updated',
                                animEl: 'elId',
                                fn: function(){
                                    wpsEngineScriptEditorWindow.close();
                                },
                                icon: Ext.MessageBox.INFO
                          });
                        }else
                          Ext.Msg.show({
                                title:'Update WPS Processing Engine Script : Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Script Engine not updated',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
                     }
   };

   var wpsUpdateScriptEngineTimeOut=function(){
                 Ext.Msg.show({
                    title:'Update WPS Processing Script Engine: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
   };
   sendXmlHttpRequestTimeOut("POST",
       "manager?cmd=wpsProcessingUpdate&step=updateScriptEngine&serviceName="+serviceName+"&processingName="+processingName+"&engineType="+engineType+"&async="+async,
        true, scriptEngine, 800000, wpsUpdateScriptEngine, wpsUpdateScriptEngineTimeOut,null);


}

function getWPSResource (serviceName, resourcePath){

    var getResourceCommandURL="manager?cmd=getServiceResource&serviceName="+serviceName+"&relativePath="+resourcePath;
    var resource;
    var wpsGetResource=function(response){
            
             if(!response){
                          Ext.Msg.show({
                                title:'WPS Get Resource: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
              }else{
                  resource=response;

        }
     };
     var wpsProcessingCreateOperationControlTimeOut=function(){
                 Ext.Msg.show({
                    title:'CWPS Get Resource: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
     };

     var onSubmit=sendXmlHttpRequestTimeOut("GET",
            getResourceCommandURL,
            false, null, 800000, wpsGetResource, wpsProcessingCreateOperationControlTimeOut,null);


    return resource;

}


var windowProcessResources=new Object();

function getProcessingResourceWindow(serviceName, processingName, processingType, async){

    if(windowProcessResources[processingName])
        windowProcessResources[processingName].show();
    else{
       var procRes=getProcessingResources(serviceName, processingName,processingType);

        function resource(val){
           return '<span style="color:#617992; font-size:14px;">' + val + '</span>';
           
        }

        function download(val){
            if(val!='')
               return  "<a href=\"javascript:downloadPopup('"+val+"')\"><img id='ext-gen11' title='View' src='images/download-icon.gif' alt='arrow'></a>";
            else
              return val;
        }

        function edit(val){
            if(val!='')
               return  "<a href=\"javascript:editWPSResource('"+val+"', '"+serviceName+"', '"+processingName+"', '"+processingType+"', '"+async+"')\"><img id='ext-gen11' title='Edit' src='images/edit.png' alt='arrow'></a>";
            else
              return val;
        }
        
        var store = new Ext.data.ArrayStore({
            fields: [
               {name: 'resource'},
               {name: 'url'},
               {name: 'edit'}
            ]
        });
        store.loadData(procRes);

        // create the Grid
        var grid = new Ext.grid.GridPanel({
            store: store,
            columns: [
                {id:'resource',header: "Resource", sortable: true, renderer: resource, dataIndex: 'resource'},
                {header: "View", width: 75, sortable: true, renderer: download, dataIndex: 'url'},
                {header: "Edit", width: 75, sortable: true, renderer: edit, dataIndex: 'edit'},
            ],
            stripeRows: true,
            autoExpandColumn: 'resource',
            autoHeight: true
        //    height:screen.height/2.55,
          //  width:screen.width/2.55
        });

       windowProcessResources[processingName]=new Ext.Window({
                                title: 'Processing '+processingName+' Resources' ,
                                border: false,
                                id: 'windowProcess'+processingName+'Resources',
                                animCollapse : true,
                                autoScroll : true,
                                resizable : false,
                                collapsible: true,
                                layout: 'fit',
                                width: screen.width/2.5,
                               // height: screen.height/2.5,
                                autoHeight: true,
                                closeAction:'hide',
                                html: "<div id='windowProcess"+processingName+"Resources_div'/>"
                    });


      windowProcessResources[processingName].show();
      grid.render('windowProcess'+processingName+'Resources_div');


    }

    
}

function getProcessingResources(serviceName, processingName, processingType){
    var processingResources=new Array();

    var getResourceServiceURL="manager?cmd=getServiceResource&serviceName="+serviceName+"&relativePath=";


    // Describe Document
    processingResources[0]=["Describe Process",
                            getResourceServiceURL+"AdditionalResources/WPS/DescribeProcess/DescribeInformation_"+processingName+".xml","Describe Process"];

    // Engine Script
    processingResources[1]=[processingType+" Script",
            getResourceServiceURL+"Resources/execute_"+processingName+"_script", "Script"];

    // SSE Interface
    processingResources[2]=["Portal Interface Order",
                    getResourceServiceURL+"Resources/Interface/"+processingName+"/order.xhtml",""];
    processingResources[3]=["Portal Interface Transformation",
                getResourceServiceURL+"Resources/Interface/"+processingName+"/transformation.xsl",""];

    return processingResources;

    
}

function editWPSResource (resourceType, serviceName, processingName, processingType, async){
    switch (resourceType){
        case "Describe Process":
            editDescribeProcess(serviceName, processingName, async, processingType);
           break;
        case "Script":
                switch (processingType){
                case "GrassEngine":
                    editEngineScript(serviceName, processingName, async, processingType, "Resources/GrassScripts/execute_"+processingName+"_original.sh");
                   break;
                case "ShellEngine":
                    editEngineScript(serviceName, processingName, async, processingType, "Resources/ShellScripts/execute_"+processingName+"_original.sh");
                   break;
              }
            
           break;
        
    }

    
}

function loadEngineTemplate(serviceName, processingName, engineType){
    var resourcePath="";
    switch(engineType){
        case "GrassEngine":
            resourcePath="Resources/GrassScripts/execute_"+processingName+"_template.sh";
           break;
        case "ShellEngine":
            resourcePath="Resources/ShellScripts/execute_"+processingName+"_template.sh";
           break;
       /* case "ShellEngine Script":
            resourcePath="Resources/ShellScripts/execute_"+processingName+"_template.sh";
           break;*/
    }
    var engineScriptTemplate=getWPSResource (serviceName, resourcePath);
    parent.iframeRequest_editEngineScript.window.setEditAreaValue(engineScriptTemplate);
}


function removeProcessing(serviceName, processName, processType, async){

var removeProcessingCommandURL="manager?cmd=wpsProcessingDelete&serviceName="+
    serviceName+"&processingName="+processName+"&async="+async+
    "&engineType="+processType;

    var wpsRemoveProcessing=function(response){
             if(!response)
                          Ext.Msg.show({
                                title:'Remove WPS Processing: Error',
                                buttons: Ext.Msg.OK,
                                msg: 'Service Exception!',
                                animEl: 'elId',
                                icon: Ext.MessageBox.ERROR
                          });
              else
                  Ext.Msg.show({
                                title:'Remove WPS Processing',
                                buttons: Ext.Msg.OK,
                                msg: 'WPS Processing Removed',
                                animEl: 'elId',
                                fn: function(){window.location.reload();},
                                icon: Ext.MessageBox.INFO
                          });
     };

     var wpsProcessingCreateOperationControlTimeOut=function(){
                 Ext.Msg.show({
                    title:'Remove WPS Processing: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
     };

     var onSubmit=sendXmlHttpRequestTimeOut("GET",
            removeProcessingCommandURL,
            false, null, 800000, wpsRemoveProcessing, wpsProcessingCreateOperationControlTimeOut,null);

    
}
