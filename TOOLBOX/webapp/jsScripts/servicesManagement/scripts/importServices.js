
/*
 * Toolbox Import Services Interface
 * author: Andrea Marongiu
 */


ImportServicesInterface=function(){

     this.xmlInterface="jsScripts/servicesManagement/resources/xml/importGroupServicesPanel.xml";

     this.formInterface=createPanelExjFormByXml(this.xmlInterface);

     this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
     };

     this.onImportServices=function(){
           var formValuesImport=this.formInterface.getFormValues(false);
           if(formValuesImport.zipServices.uploadID){

               var loading=new Object();
               loading.message="Please Wait ...";
               loading.title="Import Services";

               var importServiceFunc=function(response){

                    Ext.Msg.show({
                                  title: 'Import Services',
                                  buttons: Ext.Msg.OK,
                                  width: Math.floor((screen.width/100)*50),
                                  msg: "<pre>"+response+"</pre>",
                                  fn: function(){
                                    window.location = 'main.jsp';
                                  },
                                  icon: Ext.MessageBox.INFO
                     });
               }


               var importServiceTimeOut=function(){
                   Ext.Msg.show({
                                    title:'Import group services: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Request TIME-OUT!',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });
               }

                var onSubmit=sendXmlHttpRequestTimeOut("GET",
                     "manager?cmd=importGroupServices&id="+formValuesImport.zipServices.uploadID,
                     true, null, 800000, importServiceFunc, importServiceTimeOut,null,
                                            loading, null);
           }
    };

};

