
/*
 * Toolbox Create Catalogue Database Interface
 * @author: Andrea Marongiu
 */


CreateCatalogueDatabaseInterface=function(serviceName){

     this.xmlInterface="jsScripts/serviceTools/resources/xml/createCatalogueDatabasePanel.xml";

     this.serviceName= serviceName;

     this.formInterface=createPanelExjFormByXml(this.xmlInterface);

     this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
     };

    
     this.onCreate=function(){
         alert("create");
          /*var formValuesImport=this.formInterface.getFormValues(false);
            
           if(formValuesImport.metadataFile.uploadID){

               var loading=new Object();
               loading.message="Please Wait ...";
               loading.title="Harvest Metadata";

               var importServiceFunc=function(response){
                    var evalStr="var respObj= new Object("+response+");";
                    eval(evalStr);
                    if(respObj.error){
                        Ext.Msg.show({
                              title:'Harvest Metadata: Error',
                              buttons: Ext.Msg.OK,
                              msg: respObj.error,
                              minWidth: screen.width/3,
                              animEl: 'elId',
                              fn: function(){
                                window.location = 'tools.jsp?extVers=3&serviceName='+respObj.serviceName;
                              },
                              icon: Ext.MessageBox.ERROR
                        });
                    }else{
                        Ext.Msg.show({
                              title:'Harvest Metadata: Completed',
                              buttons: Ext.Msg.OK,
                              msg: respObj.info,
                              minWidth: screen.width/3,
                              animEl: 'elId',
                              fn: function(){
                                window.location = 'tools.jsp?extVers=3&serviceName='+respObj.serviceName;
                              },
                              icon: Ext.MessageBox.INFO
                        });

                    };
                    
               }


               var importServiceTimeOut=function(){
                   Ext.Msg.show({
                                    title:'Harvest Metadata: Error',
                                    buttons: Ext.Msg.OK,
                                    msg: 'Request TIME-OUT!',
                                    animEl: 'elId',
                                    icon: Ext.MessageBox.ERROR
                                });
               }

               var onSubmit=sendXmlHttpRequestTimeOut("GET",
                     "manager?cmd=ebRRHarvestFile&serviceName="+this.serviceName+"&id="+formValuesImport.metadataFile.uploadID,
                     true, null, 800000, importServiceFunc, importServiceTimeOut,null,
                     loading, null);
           }*/
    };

};

