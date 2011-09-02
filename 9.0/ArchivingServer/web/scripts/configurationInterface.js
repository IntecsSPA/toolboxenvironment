
/*
 * Toolbox Set ARMS Configuration Interface
 * @author: Andrea Marongiu
 */


ConfigurationInterface=function(){

     this.xmlInterface="resources/interfaces/configurationPanel.xml";


     this.restConfigurationURL="rest/config";
     
     this.formInterface=new ExtXmlInterface(this.xmlInterface);

     this.render=function (elementID){
        /*var maskConfiguration=new Ext.LoadMask(armsManager.workspacePanel.body,
            { msg:"Please wait..."}
        ); 
        maskConfiguration.show();*/
        armsManager.showWorkspaceLoadPanel(); 
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();

            var getConfigurationTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Configuration: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
            var confInterface=this.formInterface;
            var getConfiguration= function(response){
                var conf=JSON.parse(response);
                confInterface.setJSONValues(conf);
                //maskConfiguration.hide();
                armsManager.hideWorkspaceLoadPanel();
            };

           //var loginValues=this.formInterface.getFormValues();
        
           
            
         /*  this.user= loginValues['user'];
           this.password=loginValues['password'];*/
           
          /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
 
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restConfigurationURL,
                     true, null, interfacesManager.user, interfacesManager.password, 
                     800000, getConfiguration, getConfigurationTimeOut,null,
                     null, null);
     };

     this.onToggleFTP=function(){
     
     var ftpEn=Ext.getCmp('publish.local.ftp.enable');
     var ftpIp=Ext.getCmp('publish.local.ftp.ip');
     var ftpPort=Ext.getCmp('publish.local.ftp.port');
     var ftpRoot=Ext.getCmp('publish.local.ftp.rootdir');
        if( ftpEn && ftpIp && ftpPort && ftpRoot )
         if(ftpEn.checked){
             ftpIp.enable();
             ftpPort.enable();
             ftpRoot.enable();
         }else{
             ftpIp.disable();
             ftpPort.disable();
             ftpRoot.disable(); 
         }
          
    };
    
    this.onToggleHTTP=function(){
         //alert("HTTP");
          
    };
    
    

    
     this.onSetConfiguration=function(){
      var jsonRequest=this.formInterface.getJsonValueObject();
         
      if(jsonRequest){
         var saveConfiguration=function(response){
             var jsonResp=JSON.parse(response);
                if(jsonResp.success){
                  Ext.Msg.show({
                          title:'Save Configuration',
                          buttons: Ext.Msg.OK,
                          msg: "Configuration saved",
                          animEl: 'elId',
                         icon: Ext.MessageBox.INFO
                   });  
                }else
                    Ext.Msg.show({
                          title:'Save Configuration: Error',
                          buttons: Ext.Msg.OK,
                          msg: 'Reason: ' + jsonResp.reason,
                          animEl: 'elId',
                         icon: Ext.MessageBox.ERROR
                   });    
             
         };
         
         var saveConfigurationTimeOut=function(){
                   Ext.Msg.show({
                       title:'Save Configuration: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
         }
         sendAuthenticationXmlHttpRequestTimeOut("POST",
                     this.restConfigurationURL,
                     true, jsonRequest, 
                     interfacesManager.user, interfacesManager.password, 
                     800000, saveConfiguration, saveConfigurationTimeOut,null,
                     null, null);
  
     }else{
         Ext.Msg.show({
                    title:'Mandatory property missing',
                    msg: 'Please insert all mandatory properties',
                    buttons: Ext.Msg.OK,
                    animEl: 'elId',
                    icon: Ext.MessageBox.WARNING
         });
     }
    };

};

