
/*
 * PEP Create Service Interface
 * author: Andrea Marongiu
 */


CreatePEPServiceInterface=function(){

    this.xmlInterface="resources/interfaces/createPEPService.xml";
    
    this.restURL="rest/services/pepservices/";

    this.formInterface=new Object();
    
      
    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang);
    },

    this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
    };


    
    this.onCreate=function(){
        var myMask = new Ext.LoadMask(Ext.getBody(), {
            msg:"Please wait..."
        });
        // var myMask = new Ext.LoadMask(Ext.getCmp('bogus'+"createPEPServicefromWSDL").getEl(),
        //    {msg:"Please wait..."});
        myMask.show();
        

        var jsonRequest=JSON.parse(this.formInterface.getJsonValueObject());
        
        if(jsonRequest){
          
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
                        width: BrowserDetect.getWidth(50),
                        height : BrowserDetect.getHeight(50),
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
                            operations[i].operation, function(){}, fieldSetName, null, null);
                        
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
                false, this.formInterface.getJsonValueObject(), 
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
    
    

    this.init();
};

