
/*
 * PEP Configure Service Interface
 * author: Andrea Marongiu
 */


ConfigurePEPServiceInterface=function(serviceName){

    this.xmlInterface="resources/interfaces/configurePEPService.xml";
    
    this.restURL="rest/services/pepservices/";

    this.formInterface=new Object();
    
    this.serviceName=serviceName;
    
    this.currentConfiguration=null;
    
    this.jksFileID=null;
    
    this.xacmlFileID=null;
      
    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang, this.serviceName);
    },

    this.render=function (elementID){
        this.updateServiceCurrentConfiguration();
        this.formInterface.setJSONValues(this.currentConfiguration);
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
    };
    
    this.onSave=function(){
        var newJksFileID=null;
        var newXacmlFileID=null;
        if(this.formInterface.getFormValues().jksFileLocation)
            newJksFileID=this.formInterface.getFormValues().jksFileLocation.uploadID;
        if(this.formInterface.getFormValues().xacmlFileLocation)
            newXacmlFileID=this.formInterface.getFormValues().xacmlFileLocation.uploadID;
        var jsonRequest=JSON.parse(this.formInterface.getJsonValueObject());
        
        if(jsonRequest){
          
            var updatePEPServiceFunc=function(response){
          
                var jsonResponse=JSON.parse(response);
                
      
                if(jsonResponse.success){
                    Ext.Msg.show({
                        title:'Update Service',
                        buttons: Ext.Msg.OK,
                        msg: "Service Configuration saved",
                        animEl: 'elId',
                        icon: Ext.MessageBox.INFO
                    });
                             
                }else
                    Ext.Msg.show({
                        title:'Service Update: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Details: ' + jsonResponse.details,
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });  
             
            }

            var updatePEPServiceError=function(){
                Ext.Msg.show({
                    title:'Update Service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Service Update: Error.',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
            }

            var updatePEPServiceTimeOut=function(){
                Ext.Msg.show({
                    title:'Update Service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Service Update: Error Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
            }

            if(this.jksFileID)
                if(this.jksFileID == newJksFileID)  
                    Ext.getCmp("jksFileLocation"+this.serviceName+"_file").setValue('');
            this.jksFileID = newJksFileID;
            
            if(this.xacmlFileID)
                if(this.xacmlFileID == newXacmlFileID)  
                    Ext.getCmp("xacmlFileLocation"+this.serviceName+"_file").setValue('');
            this.xacmlFileID = newXacmlFileID;
            
            var headers=new Array();
            headers.push("Content-Type,application/json");
            var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("PUT",
                this.restURL+this.serviceName,
                false, this.formInterface.getJsonValueObject(), 
                interfacesManager.user, interfacesManager.password, 
                800000, updatePEPServiceFunc, updatePEPServiceTimeOut,headers,
                null, updatePEPServiceError);

        }else
            Ext.Msg.show({
                title:'Update Service',
                buttons: Ext.Msg.OK,
                msg: 'Please set all mandatory inputs.',
                animEl: 'elId',
                icon: Ext.MessageBox.WARN
            });
 
    };
    
    this.updateServiceCurrentConfiguration=function(){
        var configuration=null;
        var getPepServiceConfFunc=function(response){
            var jsonResponse=JSON.parse(response);
            if(jsonResponse.success){
                configuration=jsonResponse.serviceConfiguration;         
             }            
        }

        var getPepServiceConfError=function(){
            Ext.Msg.show({
                title:'Configuration Error',
                buttons: Ext.Msg.OK,
                msg: 'PEP Configuration Error',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var getPepServiceConfTimeOut=function(){
            Ext.Msg.show({
                title:'Configuration Error',
                buttons: Ext.Msg.OK,
                msg: 'PEP Configuration Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("GET",
            this.restURL+this.serviceName,
            false, null, interfacesManager.user, interfacesManager.password,
            800000, getPepServiceConfFunc, getPepServiceConfTimeOut,null,
            null, getPepServiceConfError);  
        
        this.currentConfiguration=configuration;
    }
    
    this.init();
};

