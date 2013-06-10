
/*
 * PEP Create Operations Interface
 * author: Andrea Marongiu
 */


CreatePEPOperationsInterface=function(operationsData, serviceName){

    this.xmlInterface="resources/interfaces/createPEPOperations.xml";
    
    this.restURL="rest/Operations/";

    this.formInterface=new Object();
    
    this.operationsDefined=false;
     
    this.operationInfo=operationsData;
    
    this.serviceName=serviceName;
    
    this.defOperations=false;

    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang);
    },

    this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
    };


    
    this.onCreateOperations=function(){
        var currCheckBox;
        var numeberChecked=0;
        var numeberCreated=0;
        for(var i=0;i<this.operationInfo.length;i++){
            currCheckBox=Ext.getCmp(this.operationInfo[i].operation);
            if(currCheckBox.getValue()){
                numeberChecked++;
                this.operationInfo[i].tokenMandatory = Ext.getCmp("tokenMandatory").getValue();
                var createOperationFunc=function(response){
                    var jsonResponse=JSON.parse(response);
            
                    if(jsonResponse.success){
                        numeberCreated++;                  
                    }else{
                        Ext.Msg.show({
                            title:'Create Operation: Error',
                            buttons: Ext.Msg.OK,
                            msg: 'Details : ' + jsonResponse.details,
                            animEl: 'elId',
                            icon: Ext.MessageBox.ERROR
                        });
                        return;
                    }            
                }

                var createOperationError=function(){
                    Ext.Msg.show({
                        title:'Create Operation: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Create Operation: Error',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                }

                var createOperationTimeOut=function(){
                    Ext.Msg.show({
                        title:'Create Operation: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                }

        
           
                this.operationInfo[i].service=this.serviceName;
                var headers=new Array();
                headers.push("Content-Type,application/json");
                var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("PUT",
                    this.restURL+this.operationInfo[i].operation,
                    false, JSON.stringify(this.operationInfo[i]), 
                    interfacesManager.user, interfacesManager.password, 
                    800000, createOperationFunc, createOperationTimeOut,headers,
                    null, createOperationError);
             
         
            }
                
            
        }
        if(numeberChecked == 0){
            Ext.Msg.show({
                title:'Create Operations',
                buttons: Ext.Msg.OK,
                msg: "Please select at least one operation",
                animEl: 'elId',
                icon: Ext.MessageBox.WARN
            });
            
        }else
        if(numeberChecked == numeberCreated){
            Ext.Msg.show({
                title:'Create Operations',
                buttons: Ext.Msg.OK,
                msg: "All operations have been created",
                animEl: 'elId',
                fn: function(btn){
                    var winInterface=Ext.getCmp("interfaceSelectPEPOperationsWin");
                    winInterface.close();
                },
                icon: Ext.MessageBox.INFO
            });
            this.operationsDefined=true;
        }
    

              
           
    };
    
    

    this.init();
};

