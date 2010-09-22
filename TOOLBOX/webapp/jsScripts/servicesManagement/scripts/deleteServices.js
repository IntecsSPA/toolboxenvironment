
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
            var deleteAllServices=true;
            formInterface=this.formInterface;
            var serviceArray=new Array();

            if(exportInterfaceValues['servicesDel'].indexOf(",") == -1)
                serviceArray[0]=exportInterfaceValues['servicesDel']
            else
                serviceArray=exportInterfaceValues['servicesDel'].split(",");

     

            var deleteServiceFunction, deleteServiceTimeOut;

            for(var i=0; i<serviceArray.length-1; i++){

                deleteServiceFunction=function(response){
                     var jsonResp=JSON.parse(response);
                   
                      if(jsonResp.success){
                         var checkboxGroup=Ext.getCmp("servicesDel_cont");
                         checkboxGroup.updateValues();
                      }else
                        {
                          deleteAllServices=false;
                          Ext.Msg.show({
                                    title:'Delete service: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Reason: ' + jsonResp.reason,
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });

                        }
                }

                deleteServiceTimeOut=function(response){
                    Ext.Msg.show({
                                    title:'Delete service: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Request TIME-OUT!',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });

                }

                var onSubmit=sendXmlHttpRequestTimeOut("DELETE",
                 exportInterfaceValues['restDeleteRequestURL']+serviceArray[i]+".json",
                 true, null, 800000,
                 deleteServiceFunction,
                 deleteServiceTimeOut,["Accept,text/json"]);

                
            }

            if(deleteAllServices)
                Ext.Msg.show({
                     title: 'Delete Group Services',
                     buttons: Ext.Msg.OK,
                     width: Math.floor((screen.width/100)*50),
                     msg: "<pre>All the selected services have been deleted</pre>",
                     /*fn: function(){
                        window.location = 'main.jsp';
                     },*/
                     icon: Ext.MessageBox.INFO
                });
           
    };

};