
/*
 * Toolbox Export Services Interface
 * author: Andrea Marongiu
 */

ExportServicesInterface=function(){

     this.xmlInterface="jsScripts/servicesManagement/resources/xml/exportGroupServicesPanel.xml";

     this.formInterface=createPanelExjFormByXml(this.xmlInterface);

     this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
     };

     this.onExportServices=function(){
            var exportInterfaceValues= this.formInterface.getFormValues();
            if(exportInterfaceValues!=''){
               var httpGetRequest=exportInterfaceValues['ServiceUrl']+"&services="+exportInterfaceValues['services'];
               window.location.href = httpGetRequest;
            }
            
    };

};