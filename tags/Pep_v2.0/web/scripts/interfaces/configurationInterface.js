
/*
 * PEP Configuration Interface
 * author: Andrea Marongiu
 */


ConfigurationInterface=function(){

    this.xmlInterface="resources/interfaces/configuration.xml";
    
    this.restConfURL="rest/configuration";

    this.formInterface=new Object();
     
    this.currentConfiguration=null;
    
    this.fileKeystroreID=null;

    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang);
    },

    this.render=function (elementID){
        this.updateCurrentConfiguration();
        this.formInterface.setJSONValues(this.currentConfiguration);
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
        
    };


    
    this.onSave=function(){
   
        var newKeystoreID=null;
        if(this.formInterface.getFormValues().tbxLevelKeystore)
            newKeystoreID=this.formInterface.getFormValues().tbxLevelKeystore.uploadID;

        var saveConfigurationFunc=function(response){
            var jsonResponse=JSON.parse(response);

            if(jsonResponse.success){
                Ext.Msg.show({
                    title:'Save Configuration',
                    buttons: Ext.Msg.OK,
                    msg: "Configuration Saved",
                    animEl: 'elId',
                    icon: Ext.MessageBox.INFO
                });  
                  
            } else
                Ext.Msg.show({
                    title:'Save Configuration: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Error Details:' + jsonResponse.details ,
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });   
        }

        var saveConfigurationError=function(){
            Ext.Msg.show({
                title:'Save Configuration: Error',
                buttons: Ext.Msg.OK,
                msg: 'Save PEP Configuration Error',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var saveConfigurationTimeOut=function(){
            Ext.Msg.show({
                title:'Save Configuration: Error',
                buttons: Ext.Msg.OK,
                msg: 'Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }
        
        if(this.fileKeystroreID)
            if(this.fileKeystroreID == newKeystoreID)  
                Ext.getCmp("tbxLevelKeystore_file").setValue('');
        this.fileKeystroreID = newKeystoreID;
        var configurationJson=this.formInterface.getJsonValueObject();
        var headers=new Array();
        headers.push("Content-Type,application/json");
        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("PUT",
            this.restConfURL,
            true, configurationJson, interfacesManager.user, interfacesManager.password, 
            800000, saveConfigurationFunc, saveConfigurationTimeOut,
            headers,
            null, saveConfigurationError);

    };
    
    this.updateCurrentConfiguration=function(){
        var configuration=null;
        var getPepConfFunc=function(response){
            var jsonResponse=JSON.parse(response);
            if(jsonResponse.success){
                configuration=jsonResponse.configuration;      
            }            
        }

        var getPepConfError=function(){
            Ext.Msg.show({
                title:'Configuration Error',
                buttons: Ext.Msg.OK,
                msg: 'PEP Configuration Error',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var getPepConfTimeOut=function(){
            Ext.Msg.show({
                title:'Configuration Error',
                buttons: Ext.Msg.OK,
                msg: 'PEP Configuration Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("GET",
            "rest/configuration",
            false, null, interfacesManager.user, interfacesManager.password,
            800000, getPepConfFunc, getPepConfTimeOut,null,
            null, getPepConfError);  
        
        this.currentConfiguration=configuration;
    }
    

    this.init();
};

