
/*
 * PEP Create Service Interface
 * author: Andrea Marongiu
 */


CreatePEPServiceInterface=function(){

    this.xmlInterface="resources/interfaces/createPEPService.xml";
    
    this.restURL="rest/services/pepservices/";

    this.formInterface=new Object();
    
    this.configuredSteps=null;
    
    this.xacmlFileLocation = null;
          
    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang);
        this.getConfiguredSteps();
        this.populateSteps("multiInputAuthentication", this.configuredSteps.authentication.commands);
        this.populateSteps("multiInputAuthorization", this.configuredSteps.authorization.commands);
    },

    this.render=function (elementID){      
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();       
    };


    
    this.onCreate=function(){
       
        var chosenSteps = JSON.parse(JSON.stringify(this.configuredSteps));
        
        this.getStepsValue(chosenSteps.authentication.commands);
        this.getStepsValue(chosenSteps.authorization.commands);
        //console.log(JSON.stringify(chosenSteps.authentication.commands));
        
        var jsonRequest=JSON.parse(this.formInterface.getJsonValueObject());
        
        if(jsonRequest){
            jsonRequest.xacmlFileLocation = this.xacmlFileLocation;
            jsonRequest.chosenCommands = chosenSteps;
          
            var myMask = new Ext.LoadMask(Ext.getBody(), {
                msg:"Please wait..."
            });
            // var myMask = new Ext.LoadMask(Ext.getCmp('bogus'+"createPEPServicefromWSDL").getEl(),
            //    {msg:"Please wait..."});
            myMask.show();
            var createPEPServiceFunc=function(response){
                myMask.hide();
                var jsonResponse=JSON.parse(response);
      
                if(jsonResponse.success){
                    myMask.hide();
                    
                    var operations=jsonResponse.operations;
                    pepManager.createPEPOperationsInterface=new CreatePEPOperationsInterface(operations, jsonRequest['serviceName']);
                    var winCreateService=Ext.getCmp('bogus'+"createPEPServicefromWSDL"); 
        
                    winCreateService.close();
                    var renderOp="pepManager.createPEPOperationsInterface.render(\"extInterfaceSelectPEPOperations\")"; 
                    winCreateService = pepManager.desktop.getDesktop().createWindow({
                        id: "interfaceSelectPEPOperationsWin",
                        title:"Select PEP Operations",
                        renderOp: renderOp,
                        width: BrowserDetect.getWidth(60),
                        height : BrowserDetect.getHeight(60),
                        serviceName: jsonRequest['serviceName'],
                        iconCls: 'bogus',
                        layout:'fit',
                        listeners: {
                            "resize":function(){
                                setTimeout(this.renderOp,500);
                              
                            },
                            "close":function(){
                                if(pepManager.createPEPOperationsInterface.operationsDefined){
                                    pepManager.serviceMenu.addService(this.serviceName);
                                }else{
                            //TODO: Delete the service
                            } 
                            }  
                        },
                        shim:false,
                        closable: false,
                        animCollapse:false,
                        constrainHeader:true,
                        html: "<div id='extInterfaceSelectPEPOperations'/>"    
                    });
                    winCreateService.show(); 
                    
                    setTimeout(renderOp,500);
                    var multiInputOperations=Ext.getCmp("operationMultiInput");
                    var fieldSetName="operationsFieldSetSet_";
                    multiInputOperations.addFieldSet(fieldSetName, 
                        "Select PEP Operations", null, false, null);
                    for(var i=0; i<operations.length;i++){
                        multiInputOperations.addCheckBox(operations[i].operation, 
                            operations[i].operation, function(){}, fieldSetName, null, null, false);
                        
                    }
                    multiInputOperations.doLayout();
                             
                }else
                    Ext.Msg.show({
                        title:'Create Service: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Details: ' + jsonResponse.details,
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });  
             
            }

            var createPEPServiceError=function(){
                myMask.hide();
                Ext.Msg.show({
                    title:'Create Service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Create Service Error.',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
            }

            var createPEPServiceTimeOut=function(){
                myMask.hide();
                Ext.Msg.show({
                    title:'Create Service: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Create Service: Error Request TIME-OUT!',
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });
            }

            var headers=new Array();
            headers.push("Content-Type,application/json");
            var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("PUT",
                this.restURL+jsonRequest['serviceName'],
                false, JSON.stringify(jsonRequest), 
                interfacesManager.user, interfacesManager.password, 
                800000, createPEPServiceFunc, createPEPServiceTimeOut,headers,
                null, createPEPServiceError);

        }else
            Ext.Msg.show({
                title:'Create Service',
                buttons: Ext.Msg.OK,
                msg: 'Please set all mandatory inputs.',
                animEl: 'elId',
                icon: Ext.MessageBox.WARN
            });
           
    };
    
    this.getConfiguredSteps=function(){
        var configuredStepsTemp=null;
        var getPepConfStepsFunc=function(response){
            var jsonResponse=JSON.parse(response);
            if(jsonResponse.success){
                configuredStepsTemp=jsonResponse.configuredCommands;      
            }            
        }

        var getPepConfStepsError=function(){
            Ext.Msg.show({
                title:'Configuration Error',
                buttons: Ext.Msg.OK,
                msg: 'PEP Steps Configuration Error',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var getPepConfStepsTimeOut=function(){
            Ext.Msg.show({
                title:'Configuration Error',
                buttons: Ext.Msg.OK,
                msg: 'PEP Steps Configuration Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("GET",
            "rest/ConfiguredCommands",
            false, null, interfacesManager.user, interfacesManager.password,
            800000, getPepConfStepsFunc, getPepConfStepsTimeOut,null,
            null, getPepConfStepsError);  
        
        this.configuredSteps=configuredStepsTemp;
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
                    commandFun, fieldSetName, null, commandIdArray, false);
            for (var j = 0; j < commandProperties.length; j++) {
                if (commandProperties[j].type == "text") {
                    multiInputAuth.addTextField(commandProperties[j].id, commandProperties[j].description,
                            commandProperties[j].value, 50, fieldSetName, 1, null, true);
                }
                if (commandProperties[j].type == "file") {
                    multiInputAuth.addFileField(commandProperties[j].id, commandProperties[j].description, 50, "rest/manager/storefile",
                            null, "upload-icon", "styles/img/loaderFile.gif", "styles/img/fail.png", "styles/img/success.png", fieldSetName, 50, true);
                }
            }
        }
        multiInputAuth.doLayout();
    };
    
    this.getStepsValue = function(commands){    
        for(var i=0; i < commands.length;i++){
            commands[i].selected = Ext.getCmp(commands[i].id).getValue();
            var commandProperties = commands[i].properties;
            for (var j=0; j < commandProperties.length; j++){  
                if (commandProperties[j].type == "text"){
                    commandProperties[j].value = Ext.getCmp(commandProperties[j].id).getValue();
                } 
                if (commandProperties[j].type == "file"){
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

