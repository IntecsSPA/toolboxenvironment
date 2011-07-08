
/*
 * PEP Service Desktop Module
 * author: Andrea Marongiu
 */

PEPServiceMenu=function(serviceName, pepServicesModule){
    return {
        items:[{
            text: "Service Information",
            iconCls: 'serviceinfo',
            serviceName: serviceName,
            pepServicesModule: pepServicesModule,
            restURLServiceInstances: "rest/services/pepServices",
            requestGetLog: "manager?cmd=getSrvLog&serviceName=",
            handler : function(src){
               var windID= src.serviceName+"informationService"; 
               pepManager.informationPEPServicesInterfaces[src.serviceName]= new InfoPEPServiceInterface(src.serviceName);
               var renderOp="pepManager.informationPEPServicesInterfaces[\""+src.serviceName+"\"].render(\"extInterface"+windID+"\")"; 
               var win = pepManager.desktop.getDesktop().createWindow({
                        id: windID,
                        title:src.serviceName+" Information",
                        renderOp: renderOp,
                        width: BrowserDetect.getWidth(93),
                        height : BrowserDetect.getHeight(90),
                        iconCls: 'serviceinfo',
                        layout:'fit',
                        listeners: {
                          "resize":function(){
                              setTimeout(this.renderOp,500);
                          },
                          "close": function(){
                              this.destroy(true);
                                           
                          } 
                        },
                        shim:false,
                        closeAction:'close',
                        animCollapse:false,
                        constrainHeader:true,
                        html: "<div id='extInterface"+windID+"'/>"    
                    });
                    win.show(); 
                    setTimeout(renderOp,500);   
            },
            scope: this
        },{
            text: "Service Log",
            iconCls: 'log',
            serviceName: serviceName,
            pepServicesModule: pepServicesModule,
            restURLServiceInstances: "rest/services/pepServices",
            requestGetLog: "manager?cmd=getSrvLog&serviceName=",
            handler : function(src){
                var serviceTabGridPanel=new pepManager.desktopTabPanelResource({
                    idTabPanel: "serviceLogTabPanel_"+src.serviceName,
                    tabPanelTitle: src.serviceName+" Log",
                    width: BrowserDetect.getWidth(70),
                    height: BrowserDetect.getHeight(70)
                });
                            
                serviceTabGridPanel.openTab("grid", 
                    src.requestGetLog+src.serviceName,
                    "Grid Log");
               /* if (!pepManager.windowServicesPepLog[src.serviceName]){        
                    pepManager.servicesLogcm[src.serviceName] = new Ext.grid.ColumnModel([{
                            header:"Level",
                            dataIndex: 'id',
                            sortable:true,
                            width:30
                        }, {
                            header: "Thread ID",
                            dataIndex: 'thread',
                            sortable:true,
                            width: 100
                        }, {
                            id:'text',
                            header:"Message",
                            dataIndex:'',
                            sortable:true,
                            width: 200
                        },{
                            header:"Date",
                            dataIndex:'data',
                            sortable:true,
                            width:70
                        }]);
        
                    pepManager.servicesLogtabs[src.serviceName] = new Ext.TabPanel({
                        renderTo: Ext.getBody(),
                        activeTab:0,
                        height:500,
                        autoWidth:true
                    });
                    pepManager.windowServicesPepLog[src.serviceName] = pepManager.desktop.getDesktop().createWindow({
                        renderTo: Ext.getBody(),
                        title:'Service "'+src.serviceName+'" Log',
                        closeAction:'close',
                        collapsible:true,
                        layout:'fit',
                        serviceName:src.serviceName,
                        iconCls: 'bogus',
                        maximizable:true,
                        autoHeight:true,
                        width: BrowserDetect.getWidth(60),
                        listeners: {
                          "close": function(){
                              pepManager.windowServicesPepLog[this.serviceName]=null;
                              this.destroy(true);
                          }  
                        },
                        buttons: [{
                                text: 'Close',
                                handler: function(){
                                    var win=this.findParentByType("window");
                                    win.close();
                                }
                            }],
                        items:pepManager.servicesLogtabs[src.serviceName]
                    })
                }    
                tabs=pepManager.servicesLogtabs[src.serviceName];
                cm=pepManager.servicesLogcm[src.serviceName];
                addGrid(src.requestGetLog+src.serviceName, 'Service "'+src.serviceName+'" Log');
                pepManager.windowServicesPepLog[src.serviceName].show();   */
            },
            scope: this
        },{
            text: "Configure Service",
            iconCls: 'editservice',
            serviceName: serviceName,
            pepServicesModule: pepServicesModule,
            restURLServiceInstances: "rest/services/pepServices",
            restMethod: "POST",
            handler : function(src){
               var windID="bogus"+src.serviceName+"configureService"; 
               pepManager.configurePEPServicesInterfaces[src.serviceName]= new ConfigurePEPServiceInterface(src.serviceName);
               var renderOp="pepManager.configurePEPServicesInterfaces[\""+src.serviceName+"\"].render(\"extInterface"+windID+"\")"; 
               var win = pepManager.desktop.getDesktop().createWindow({
                        id: windID,
                        title:"Configure "+src.serviceName+" service",
                        renderOp: renderOp,
                        width: BrowserDetect.getWidth(90),
                        height : BrowserDetect.getHeight(90),
                        iconCls: 'bogus',
                        layout:'fit',
                        listeners: {
                          "resize":function(){
                              setTimeout(this.renderOp,500);
                              
                          },
                          "close": function(){
                              this.destroy(true);
                                           
                          } 
                        },
                        shim:false,
                        closeAction:'close',
                        animCollapse:false,
                        constrainHeader:true,
                        html: "<div id='extInterface"+windID+"'/>"    
                    });
                    win.show(); 
                    setTimeout(renderOp,500);   
            },
            scope: this
        },{
            text: "Service Instances",
            iconCls: 'instances',
            serviceName: serviceName,
            pepServicesModule: pepServicesModule,
            restGetServiceInstances: "rest/services/pepServices/<serviceName>/instances",
            handler : function(src){
               var windID="bogus"+src.serviceName+"instancesService"; 
               pepManager.instancesPEPServiceInterfaces[src.serviceName]= new InstancesPEPServiceInterface(src.serviceName);
               var renderOp="pepManager.instancesPEPServiceInterfaces[\""+src.serviceName+"\"].render(\"extInterface"+windID+"\")"; 
               var win = pepManager.desktop.getDesktop().createWindow({
                        id: windID,
                        title:'"'+src.serviceName+'" Service Instances',
                        renderOp: renderOp,
                        width: BrowserDetect.getWidth(70),
                       // height : BrowserDetect.getHeight(70),.
                        height: BrowserDetect.getHeight(71),
                        iconCls: 'bogus',
                        layout:'fit',
                        listeners: {
                          "resize":function(){
                              setTimeout(this.renderOp,500);
                              
                          },
                          "close": function(){
                              this.destroy(true);
                                           
                          } 
                        },
                        shim:false,
                        closeAction:'close',
                        animCollapse:false,
                        constrainHeader:true,
                        html: "<div id='extInterface"+windID+"'/>"    
                    });
                    win.show();
                    //win.setPosition(BrowserDetect.getWidth(15),BrowserDetect.getHeight(15));
                    
                    setTimeout(renderOp,500);        
                        
            },
            scope: this
        },{
            text: "Delete Service",
            iconCls: 'deleteservice',
            serviceName: serviceName,
            pepServicesModule: pepServicesModule,
            restGetServiceInstances: "rest/services/pepservices/"+serviceName,
            handler : function(src){
                var delService=src.serviceName;
                var mod=src.pepServicesModule;
                var deletePEPServiceFunc=function(response){
                    
                    var jsonResponse=JSON.parse(response);
                    if(jsonResponse.success){
                        mod.deleteService(delService);
                        Ext.Msg.show({
                            title:'Delete PEP Service',
                            buttons: Ext.Msg.OK,
                            msg: "PEP Service Deleted",
                            animEl: 'elId',
                            icon: Ext.MessageBox.INFO
                        });      
                    }else{
                        Ext.Msg.show({
                            title:'Delete PEP Service Error',
                            buttons: Ext.Msg.OK,
                            msg: 'Details: ' + jsonResponse.details ,
                            animEl: 'elId',
                            icon: Ext.MessageBox.ERROR
                        }); 
                
                    }         
                }

                var deletePEPServiceError=function(){
                    Ext.Msg.show({
                        title:'Delete PEP Service',
                        buttons: Ext.Msg.OK,
                        msg: 'Delete PEP Service Error',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                }

                var deletePEPServiceTimeOut=function(){
                    Ext.Msg.show({
                        title:'Delete PEP Service Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Delete PEP Service Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
                }
                var restURL=src.restGetServiceInstances;
                
                
                Ext.Msg.show({
                    msg:'Delete Service '+ delService+' ?',
                    buttons: Ext.Msg.YESNO,
                    title: 'Delete Service',
                    animEl: 'elId',
                    fn: function(btn){
                        if(btn=='yes'){
                            var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("DELETE",
                                restURL,
                                false, null, interfacesManager.user, 
                                interfacesManager.password, 800000, 
                                deletePEPServiceFunc, deletePEPServiceTimeOut,
                                null,
                                null, deletePEPServiceError); 
                        }        
                    },
                    icon: Ext.MessageBox.QUESTION
                                
                });
               
                       
                        
            },
            scope: this
        }
        ]
    }
};
        
PEPServices = Ext.extend(DesktopModule, {
    restGetServicesURL: "rest/services/pepservices",
    init : function(){
        
        var menuServices={
            id: "pepServicesMenu",
            items: new Array()
        };
        var modServices=this;
        var getPEPServicesFunc=function(response){
            var jsonResponse=JSON.parse(response);
            
            if(jsonResponse.success){
                var services=jsonResponse.pepServices;
                for(var i=0; i<services.length; i++){
                  
                    menuServices.items.push({
                        text: services[i].serviceName,
                        id: services[i].serviceName+"_pepService",
                        iconCls: 'service',
                        handler: function() {
                            return false;
                        },
                        menu: new PEPServiceMenu(services[i].serviceName,modServices)
                    });
                }
           
                           
            }else{
                Ext.Msg.show({
                    title:'Get PEP Services Error',
                    buttons: Ext.Msg.OK,
                    msg: 'Details: ' + jsonResponse.details ,
                    animEl: 'elId',
                    icon: Ext.MessageBox.ERROR
                }); 
                
            }         
        }

        var getPepServicesError=function(){
            Ext.Msg.show({
                title:'Get PEP Services Error',
                buttons: Ext.Msg.OK,
                msg: 'Get PEP Services List Error',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var getPepServicesTimeOut=function(){
            Ext.Msg.show({
                title:'Get PEP Services Error',
                buttons: Ext.Msg.OK,
                msg: 'Get PEP Services List Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("GET",
            this.restGetServicesURL,
            false, null, interfacesManager.user, interfacesManager.password, 800000, getPEPServicesFunc, getPepServicesTimeOut,null,
            null, getPepServicesError);  
        
        this.launcher = {
            text: "PEP Services",
            iconCls: 'services',
            handler: function() {
                return false;
            },
            menu: menuServices
        }
    },
    addService : function(serviceName){
        
        var menuServices=Ext.getCmp("pepServicesMenu");
        menuServices.doLayout();
        menuServices.add({
            text: serviceName,
            id: serviceName+"_pepService",            
            iconCls: 'newpepService',
            handler: function() {
                return false;
            },
            menu: new PEPServiceMenu(serviceName, this)
        });
        menuServices.doLayout();
        
    },
    
    deleteService: function(serviceName){
        var delService=Ext.getCmp(serviceName+"_pepService");
        if(delService){
            delService.destroy();
        }
    }
});

