
/*
 * PEP Desktop Interface
 * author: Andrea Marongiu
 */


DesktopInterface=function(config, pepConfigurationInterface){
    
    this.config=config;
    
    this.startMenu=null;
    
    this.pepConf=null;
    
    this.ConfigurationWindow=null;
     
    this.configurationInterface=pepConfigurationInterface;

    this.init=function(){
     
            
         
    };

    this.render=function (){
        
        this.configurationInterface.updateCurrentConfiguration();
        var pepConf=this.configurationInterface.currentConfiguration;
                 
        pepManager.serviceMenu=new PEPServices();    
        
        pepManager.pepLog=new PEPLog();
        
        pepManager.desktop = new Ext.app.App({
            
            init :function(){
                Ext.QuickTips.init();
            },
            ConfigurationWindow: this.ConfigurationWindow,
            configurationInterface: this.configurationInterface,
            getModules : function(){
                return [
                new PEPServicesManager(),
                pepManager.serviceMenu,
                pepManager.pepLog,
                ];
            },

            getStartConfig : function(){
                
                return {
                    
                    title: interfacesManager.user,
                    iconCls: 'user',
                    toolItems: [{
                        text:'Config',
                        iconCls:'settings',
                        scope:this,
                        handler:function(){
                            var renderOp="pepManager.configurationInterface.render('configurationInterfaceDiv')";
                     
                            if(!this.ConfigurationWindow || (this.ConfigurationWindow && this.ConfigurationWindow.create)){
                                if(this.ConfigurationWindow)
                                    this.ConfigurationWindow.destroy(true);
                                pepManager.configurationInterface=new ConfigurationInterface();
                                this.configurationInterface=pepManager.configurationInterface;
                                this.ConfigurationWindow=pepManager.desktop.getDesktop().createWindow({
                                    title: "PEP Configuration",
                                    id: "PepConfWind",
                                    width: BrowserDetect.getWidth(90),
                                    height : BrowserDetect.getHeight(90),
                                    iconCls: 'bogus',
                                    renderOp: renderOp,
                                    shim:false,
                                    create: false,
                                    animCollapse:false,
                                    constrainHeader:true,
                                    listeners: {
                                        "destroy": function(){
                                            this.ConfigurationWindow=null;   
                                        },
                                        "resize":function(){
                                            setTimeout(this.renderOp,500);
                              
                                        },
                                        "close": function(){
                                            this.create=true;
                                            var div=document.getElementById("configurationInterfaceDiv");
                                            div.innerHTML="";
                                            div.parentNode.removeChild(div); 
                                        } 
                                    },
                                    layout: 'fit',
                                    border: false,
                                    maximizable: true,
                                    resizable : true,
                                    closeAction:'close',
                                    html : "<div id='configurationInterfaceDiv'></div>"
                                }); 
                                this.ConfigurationWindow.show();
                                
                       //         this.configurationInterface.render('configurationInterfaceDiv');  
                            }else{
                                this.ConfigurationWindow.show(); 
                       //         this.configurationInterface.render('configurationInterfaceDiv'); 
                            }   
                            
                           
                             setTimeout(renderOp,500);
                        }
                    },'-',{
                        text:'Logout',
                        iconCls:'logout',
                        scope:this,
                        handler:function(){
                            Ext.Msg.show({
                                title:'Logout',
                                buttons: Ext.Msg.YESNO,
                                msg: 'Logout',
                                animEl: 'elId',
                                fn: function(btn){
                                    if(btn=='yes')
                                        window.location.reload();
                                },
                                icon: Ext.MessageBox.QUESTION
                                
                            });
                        }
                    }]
                };
            
            }
        });
       
        if(pepConf.firstTimeCheck == 'true'){
            //Show Configuration Interface
            if(!this.ConfigurationWindow){
                this.ConfigurationWindow=new Ext.Window({
                    title: "PEP Configuration",
                    id: "PepConfWind",
                    width: BrowserDetect.getWidth(90),
                    height : BrowserDetect.getHeight(90),
                    iconCls: 'icon-grid',
                    shim:false,
                    animCollapse:false,
                    constrainHeader:true,
                    listeners: {
                        "destroy": function(){
                            this.ConfigurationWindow=null;   
                        }
                    },
                    layout: 'fit',
                    border: false,
                    maximizable: true,
                    resizable : true,
                    closeAction:'hide',
                    html : "<div id='configurationInterfaceDiv'></div>"
                }); 
                this.ConfigurationWindow.show();
                this.configurationInterface.render('configurationInterfaceDiv');  
            }else{
                this.ConfigurationWindow.show(); 
                this.configurationInterface.render('configurationInterfaceDiv');
            }
        }
        

    };


   
    this.init();
};

