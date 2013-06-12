
/*
 * PEP Information Service Interface
 * author: Andrea Marongiu
 */


InfoPEPServiceInterface=function(serviceName){

    this.restURL="rest/services/pepservices/";

    this.formInterface=new Object();
    
    this.serviceName=serviceName;   
      
    this.init=function(){
       
    },

    this.render=function (elementID){
        var service=this.serviceName;
        var getInfoPEPServiceFunc=function(response){
            var jsonResponse=JSON.parse(response);
            if(jsonResponse.success){
                var htmlTemplate="<table width='100%'>" 
                htmlTemplate+="<tr><td colspan='2'><div id='operationGrid"+service+"'/><td></tr>"+    
                "<tr><td colspan='2'><div class='cellsm_t'>Other Information</div><td></tr>"+
                
                "<tr><td width='10%' class='cellsx_t'><b>WSDL:</b></td><td><div id='wsdlToolbar"+service+"'/></td></tr>"+
                "<tr><td width='10%' class='cellsx_t'><b>Schema:</b></td><td><div id='schemaToolbar"+service+"'/></td></tr>"+
                "<tr><td colspan='2'><table>"+
                "<tr rowspan='2'><td width='35%' class='cellsx_t' BGCOLOR='#FFFFFF'><b>URL of the SOAP endpoint:</b></td><td BGCOLOR='#FFFFFF'>"+jsonResponse.serviceConfiguration.remoteUrl+"</td></tr>"+
                "<tr><td width='35%' class='cellsx_t' BGCOLOR='#FFFFFF'><b>Forward message with security token decrypted:</b></td><td BGCOLOR='#FFFFFF'>"+jsonResponse.serviceConfiguration.forwardMessageWithClearToken+"</td></tr>"+
                "<tr><td width='35%' class='cellsx_t' BGCOLOR='#FFFFFF'><b>Forward message with security token re-encrypted (this will override all forwarding options):</b></td><td BGCOLOR='#FFFFFF'>"+jsonResponse.serviceConfiguration.forwardMessageWithCryptedToken+"</td></tr>"+
                "<tr><td width='35%' class='cellsx_t' BGCOLOR='#FFFFFF'><b>Forward message with incoming security token:</b></td><td BGCOLOR='#FFFFFF'>"+jsonResponse.serviceConfiguration.forwardMessageWithIncomingToken+"</td></tr>"+
                "</table></td></tr>"+
                "</table>";     
                    
                var windowDiv=document.getElementById(elementID);
                windowDiv.innerHTML=htmlTemplate;
                    
                var store = new Ext.data.ArrayStore({
                    fields: [
                    {
                        name: 'name'
                    },

                    {
                        name: 'soapAction'
                    },

                    {
                        name: 'inputType'
                    },

                    {
                        name: 'inputTypeNamespace'
                    },

                    {
                        name: 'outputType'
                    },

                    {
                        name: 'outputTypeNamespace'
                    }
                    ]
                });

                var storeDataArray=new Array();
                var tmp;
            
                for(var i=0; i<jsonResponse.operations.length; i++){
                    tmp=new Array();
                    tmp.push(jsonResponse.operations[i].name);
                    tmp.push(jsonResponse.operations[i].soapAction);
                    tmp.push(jsonResponse.operations[i].inputType);
                    tmp.push(jsonResponse.operations[i].inputTypeNamespace);
                    tmp.push(jsonResponse.operations[i].outputType);
                    tmp.push(jsonResponse.operations[i].outputTypeNamespace);
                    storeDataArray.push(tmp);
                }
                store.loadData(storeDataArray);   
                
                function operationName(val){
                  
                        return '<b>' + val + '</b>';
                    
                    return val;
                }
                var grid = new Ext.grid.GridPanel({
                    store: store,
                    columns: [
                    {
                        header: "Operation Name", 
                        sortable: true, 
                        dataIndex: 'name',
                        renderer: operationName,
                        width: BrowserDetect.getWidth(10)
                    },

                    {
                        header: "Soap Action", 
                        sortable: true, 
                        dataIndex: 'soapAction',
                        width: BrowserDetect.getWidth(22)
                    },

                    {
                        header: "Input Type Element", 
                        sortable: true, 
                        dataIndex: 'inputType',
                        width: BrowserDetect.getWidth(12)
                    },

                    {
                        header: "Input Type Namepsace", 
                        sortable: true, 
                        dataIndex: 'inputTypeNamespace',
                        width: BrowserDetect.getWidth(17)
                    },

                    {
                        header: "Output Type Element", 
                        sortable: true, 
                        dataIndex: 'outputType',
                        width: BrowserDetect.getWidth(12)
                    },
                    {
                        header: "Output Type Namepsace", 
                        sortable: true, 
                        dataIndex: 'outputTypeNamespace',
                        width: BrowserDetect.getWidth(17)
                    }
                    ],
                    //    stripeRows: true,
                    autoHeight: true,
                    title:'Service Operations'
                });
                grid.render('operationGrid'+service);
                
                
                new Ext.Toolbar({
                    renderTo: "wsdlToolbar"+service,
                    height: 30,
                    items: [{
                        text: 'Navigate',
                        serviceName: service,
                        iconCls:'resourcenavigate',
                        handler: function(){
                            var navigationPanel=new pepManager.desktopTabPanelResource({
                                idTabPanel: this.serviceName+"_WSDLNavigate",
                                tabPanelTitle: this.serviceName+" WSDL Navigator",
                                width: BrowserDetect.getWidth(40),
                                height: BrowserDetect.getHeight(40)
                            });
                            
                            navigationPanel.openTab("xml", 
                                "manager?output=text&cmd=getSrvWSDL&panelResource="+
                                this.serviceName+"_WSDLNavigate"+"&serviceName="+this.serviceName,
                                "WSDL");
                        /* pepManager.openServiceResource(this.serviceName+"_WSDLNavigate",
                            this.serviceName+" WSDL Navigator",
                            "manager?output=text&cmd=getSrvWSDL&serviceName="+this.serviceName,
                            "manager?output=xml&cmd=getSrvWSDL&serviceName="+this.serviceName);*/
                        }
                    },{
                        text: 'Download',
                        iconCls:'download',
                        serviceName: service,
                        handler: function(){
                            window.open("manager?output=xml&cmd=getSrvWSDL&serviceName="+this.serviceName,
                                'download','width=600, height=450, scrollbars=yes, menubar=yes');
                           
                        }
                    }]
                });
                
                new Ext.Toolbar({
                    renderTo: "schemaToolbar"+service,
                    height: 30,
                    items: [{
                        text: 'Navigate',
                        serviceName: service,
                        iconCls:'resourcenavigate',
                        handler: function(){
                            var navigationPanel=new pepManager.desktopTabPanelResource({
                                idTabPanel: this.serviceName+"_SchemaNavigate",
                                tabPanelTitle: this.serviceName+" Schema Navigator",
                                width: BrowserDetect.getWidth(40),
                                height: BrowserDetect.getHeight(40)
                            });
                            
                            navigationPanel.openTab("xml", 
                                "manager?output=text&cmd=getSrvSchema&panelResource="+
                                this.serviceName+"_SchemaNavigate"+"&serviceName="+this.serviceName,
                                "Schema");
                        }
                    },{
                        text: 'Download',
                        iconCls:'download',
                        serviceName: service,
                        handler: function(){
                            window.open("manager?output=xml&cmd=getSrvSchema&serviceName="+this.serviceName,
                                'download','width=600, height=450, scrollbars=yes, menubar=yes');
                        }
                    }]
                });
                
                
            }else
                Ext.Msg.show({
                    title:'Get Service Info: Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Details: ' + jsonResponse.details,
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                });  
             
        }

        var getInfoPEPServiceError=function(){
            Ext.Msg.show({
                title:'Get Service Info: Error',
                buttons: Ext.Msg.OK,
                msg: 'Get Service Info: Error.',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var getInfoPEPServiceTimeOut=function(){
            Ext.Msg.show({
                title:'Get Service Info: Error',
                buttons: Ext.Msg.OK,
                msg: 'Get Service Info: Error Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("GET",
            this.restURL+this.serviceName,
            false, null, 
            interfacesManager.user, interfacesManager.password, 
            800000, getInfoPEPServiceFunc, getInfoPEPServiceTimeOut,null,
            null, getInfoPEPServiceError);  
        
    };
    

    this.init();
};

