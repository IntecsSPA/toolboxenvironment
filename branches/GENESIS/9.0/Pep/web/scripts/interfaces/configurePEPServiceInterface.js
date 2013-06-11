
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
    
    this.xacmlFileLocation=null;
    
    this.jksFileID=null;
    
    //this.xacmlFileID=null; MRB comment
      
    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang, this.serviceName);
        this.updateServiceCurrentConfiguration();
        this.populateSteps("multiInputAuthentication" + this.serviceName, this.currentConfiguration.chosenCommands.authentication.commands);
        this.populateSteps("multiInputAuthorization" + this.serviceName, this.currentConfiguration.chosenCommands.authorization.commands);

    },

    this.render=function (elementID){   
        this.formInterface.setJSONValues(this.currentConfiguration);
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
    };
    
    this.onSave=function(){ 
       var chosenSteps = this.currentConfiguration.chosenCommands;
        
       this.getStepsValue(chosenSteps.authentication.commands);
       this.getStepsValue(chosenSteps.authorization.commands);
       //console.log(JSON.stringify(chosenSteps.authentication.commands));
        
        var newJksFileID=null;
        if(this.formInterface.getFormValues().jksFileLocation)
            newJksFileID=this.formInterface.getFormValues().jksFileLocation.uploadID;
        
        /* MRB comment
        var newXacmlFileID=null;
        if(this.formInterface.getFormValues().xacmlFileLocation)
            newXacmlFileID=this.formInterface.getFormValues().xacmlFileLocation.uploadID;
        */
       
        var jsonRequest=JSON.parse(this.formInterface.getJsonValueObject());
        
        if(jsonRequest){
            jsonRequest.xacmlFileLocation = this.xacmlFileLocation;
            jsonRequest.chosenCommands = chosenSteps;
          
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
            
            /* MRB comment
            if(this.xacmlFileID)
                if(this.xacmlFileID == newXacmlFileID)  
                    Ext.getCmp("xacmlFileLocation"+this.serviceName+"_file").setValue('');
            this.xacmlFileID = newXacmlFileID;
            */
            
            var headers=new Array();
            headers.push("Content-Type,application/json");
            var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("PUT",
                this.restURL+this.serviceName,
                false, JSON.stringify(jsonRequest), 
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
    };
    
    this.populateSteps = function(component, commands) {
        var multiInputAuth = Ext.getCmp(component); 
        for (var i = 0; i < commands.length; i++) {
            var fieldSetName = "field_" + commands[i].id;
            multiInputAuth.addFieldSet(fieldSetName, null, null, false, null);
            var commandProperties = commands[i].properties;
            var commandIdArray = new Array();
            for (var j = 0; j < commandProperties.length; j++) {
                commandIdArray[j] = commandProperties[j].id;
            }
            var commandFun = function() {
                var commandIdArray = this.dependComps;
                for (var j = 0; j < commandIdArray.length; j++) {
                    var isDisabled = Ext.getCmp(commandIdArray[j]).disabled;
                    Ext.getCmp(commandIdArray[j]).setDisabled(!isDisabled);
                }
            };         
            multiInputAuth.addCheckBox(commands[i].id, commands[i].description,
                    commandFun, fieldSetName, null, commandIdArray, commands[i].selected);
            for (var j = 0; j < commandProperties.length; j++) {
                if (commandProperties[j].type == "text") {
                    multiInputAuth.addTextField(commandProperties[j].id, commandProperties[j].description,
                            commandProperties[j].value, 30, fieldSetName, 50, null, !(commands[i].selected));
                }
                if (commandProperties[j].type == "file") {
                    multiInputAuth.addFileField(commandProperties[j].id, commandProperties[j].description, 50, "rest/manager/storefile",
                            null, "upload-icon", "styles/img/loaderFile.gif", "styles/img/fail.png", "styles/img/success.png", fieldSetName, 50, !(commands[i].selected));
                }
            }
        }
        multiInputAuth.doLayout();
    };
    
    this.getStepsValue = function(commands) {
        for (var i = 0; i < commands.length; i++) {
            commands[i].selected = Ext.getCmp(commands[i].id).getValue();
            var commandProperties = commands[i].properties;
            for (var j = 0; j < commandProperties.length; j++) {
                if (commandProperties[j].type == "text") {
                    commandProperties[j].value = Ext.getCmp(commandProperties[j].id).getValue();
                }
                if (commandProperties[j].type == "file") {
                    var fileName = Ext.getCmp(commandProperties[j].id + "_file").getValue();
                    var filePath = Ext.getCmp(commandProperties[j].id + "UploadID").getValue();
                    if (filePath != "") {
                        commandProperties[j].value = {
                            "fileName": fileName,
                            "uploadID": filePath
                        }
                    }
                    else {
                        commandProperties[j].value = null;
                    }
                    this.xacmlFileLocation=commandProperties[j].value;              
                }
            }
        }
    };
    
    this.init();
};

