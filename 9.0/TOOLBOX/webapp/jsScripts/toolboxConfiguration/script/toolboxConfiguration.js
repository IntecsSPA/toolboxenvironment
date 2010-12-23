


//gcManager.lang

var toolboxConfigurationWindow=null;
var toolboxConfigurationIO=null;
var toolboxConfigurationInterfacePanel="jsScripts/toolboxConfiguration/resources/xml/toolboxConfigurationPanel.xml";

function toolboxConfigurationManager(){

   if(toolboxConfigurationWindow == null){

           toolboxConfigurationIO=createPanelExjFormByXml(
                                    toolboxConfigurationInterfacePanel,
                                    gcManager.lang);

           toolboxConfigurationWindow = new WebGIS.Panel.WindowInterfacePanel({
                                title: 'TOOLBOX Configuration',
                                border: false,
                                id: 'toolboxConfigurationWindow',
                                animCollapse : true,
                                autoScroll : true,
                                resizable : false,
                                collapsible: true,
                                maximizable: true,
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
                                      spot.show('toolboxConfigurationWindow');
                                  }
                                },
                                width: screen.width/1.5,
                                height: screen.height/1.5,
                                closeAction:'close',
                                html: "<div id='toolboxConfigurationWindow_div'/>"
                    });
           toolboxConfigurationWindow.on('close', function(){/*insertDescribeWizardWindow=null*/window.location.reload();});


           toolboxConfigurationWindow.show();
           toolboxConfigurationWindow.insertLoadingPanel();
           //document.getElementById("insertDescribeWizardWindow_div").innerHTML=insertDescribeWizard_html;

           toolboxConfigurationIO.formsPanel.render(document.getElementById("toolboxConfigurationWindow_div"));
           toolboxConfigurationIO.render();
           spot.show('toolboxConfigurationWindow');
   }
}

function saveTOOLBOXConfiguration(){

    alert("save Configuration");

}