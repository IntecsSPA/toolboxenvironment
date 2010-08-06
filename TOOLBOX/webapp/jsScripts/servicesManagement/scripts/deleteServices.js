
/*
 * Toolbox Delete Services Interface
 * author: Andrea Marongiu
 */
var elementIDG;
DeleteServicesInterface=function(){

     this.xmlInterface="jsScripts/servicesManagement/resources/xml/deleteGroupServicesPanel.xml";

     this.elementID=null;

     this.formInterface=createPanelExjFormByXml(this.xmlInterface);

     this.render=function (elementID){
        elementIDG=elementID;
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
     };

     this.onDeleteServices=function(){
            var exportInterfaceValues= this.formInterface.getFormValues();
            
            formInterface=this.formInterface;
            var serviceArray=new Array();

            if(exportInterfaceValues['servicesDel'].indexOf(",") == -1)
                serviceArray[0]=exportInterfaceValues['servicesDel']
            else
                serviceArray=exportInterfaceValues['servicesDel'].split(",");

            alert(serviceArray);

            var deleteServiceFunction, deleteServiceTimeOut;

            for(var i=0; i<serviceArray.length-1; i++){

                deleteServiceFunction=function(response){
                     var jsonResp=JSON.parse(response);
                   
                      if(jsonResp.success){
                          alert('success');
                      }else
                        {
                          alert("ERROR: " + jsonResp.reason)
                        }
                       /* alert(this.elementID);
                        document.getElementById(elementIDG).innerHTML="";
                        this.render(elementIDG);*/
                }

                deleteServiceTimeOut=function(response){


                }

                var onSubmit=sendXmlHttpRequestTimeOut("DELETE",
                 exportInterfaceValues['restDeleteRequestURL']+serviceArray[i]+".json",
                 true, null, 800000,
                 deleteServiceFunction,
                 deleteServiceTimeOut,["Accept,text/json"]);

                
            }
           
    };

};