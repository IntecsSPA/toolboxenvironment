
/*Import XML Interfaces -- START*/
    interfacesManager.loadGlobalScript("scripts/configurationInterface.js");
    interfacesManager.loadGlobalScript("scripts/watchInterface.js");
    interfacesManager.loadGlobalScript("scripts/chainTypeInterface.js");
    /*interfacesManager.loadGlobalScript("scripts/preProcessingInterface.js");
    interfacesManager.loadGlobalScript("scripts/metadataProcessingInterface.js");*/
   
 /*Import XML Interfaces -- END*/

armsManager = {
  
    /*ARMS PAGES*/
     InputTDAPage: "index.html",
     OuputTDAPage: "results.html",
     DocumentationPage: "Documentation/docsExplorer/index.html",
    /*ARMS PAGES -- END*/

     workspacePanel: null,
     bodyColor: '#eeeeee',
     workspacePanelDIV: "iframePanel",
     armsMainMenuPanel:null,
     configurationInterfaceObj: "ConfigurationInterface",
     configurationInterface: null,
     watchInterfaceObj: "WatchInterface",
     watchInterface: null,
     preProcessingInterfaceObj:"PreProcessingInterface",
     preProcessingInterface: null,
     metadataProcessingInterfaceObj: "MetadataProcessingInterface",
     metadataProcessingInterface: null,
     chainTypesInterfaceObj: "ChainTypesInterface",
     chainTypesInterface: null,
     preProcessingDefaultValues: ["geotiff", "shp"],
     metadataProcessingDefaultValues: ["geotiff", "shp"],
     watchDefautValues:[{type:"geotiff", folder:"/home/massi/arms/publish/soscsv"},
                        {type:"shp", folder:"/home/massi/arms/publish/sossingleinstances"},
                       ],
    init:function(){
           armsManager.configurationInterface=new ConfigurationInterface();
           armsManager.watchInterface=new WatchInterface();
           armsManager.chainTypesInterface=new ChainTypesInterface();
           /*armsManager.preProcessingInterface= new PreProcessingInterface();
           armsManager.metadataProcessingInterface= new MetadataProcessingInterface();*/
           
           armsManager.workspacePanel=new Ext.Panel({
                 region: 'center',
                 margins:'5 0 5 5',
                 layout:'anchor',
                 id:'workspacePanel',
                 autoScroll: true,
                 border: true,
                 html: "<div id='"+armsManager.workspacePanelDIV+"'/>",
                 loadingBarImg: "resources/images/loader.gif",
                 loadingMessage: "Loading... Please Wait...",
                 loadingMessageColor: "black",
                 loadingPanelColor: "#dfe8f6",
                 loadingMessagePadding: 0,
                 loadingBarImgPadding: 0,
                 loadingPanelDuration: 250,
                 cleanPanel: function(){
                        document.getElementById(armsManager.workspacePanelDIV).innerHTML="";
                 },
                 loadingPanelWorkspaceEl: 'workspace'
           });


           armsManager.armsMainMenuPanel= new Ext.Panel({
                  split:true,
                  id: "armsMainMenuPanel",
                  bodyStyle : {background: armsManager.bodyColor},
                  anchor:'100% 100%',
                  margins:'5 0 5 5',
                  layout:'accordion',
                  items:[{
                          title: "Manager",
                          border:false,
                          autoScroll:true,
                          items:[
                                 new Ext.tree.TreePanel({
                                     animate:true,
                                     enableDD:false,
                                     containerScroll: true,
                                     rootVisible:false,
                                     split:true,
                                     autoScroll:true,
                                     loader: new Ext.tree.TreeLoader(),
                                     root: new Ext.tree.AsyncTreeNode({
                                     expanded: true,
                                     children: [{
                                                 text: "Configuration",
                                                 type: 'ConfigurationARMS',
                                                 xmlInterface: armsManager.configurationInterfaceObj,
                                                 interfacePanel: "armsManager.configurationInterface",
                                                 leaf: true
                                                }, {
                                                 text: "Watch",
                                                 type: 'WatchARMS',
                                                 xmlInterface: armsManager.watchInterfaceObj,
                                                 interfacePanel: "armsManager.watchInterface",
                                                 leaf: true
                                                },{
                                                 text: "Chain Types",
                                                 type: 'ChainTypesARMS',
                                                 xmlInterface: armsManager.chainTypesInterfaceObj,
                                                 interfacePanel: "armsManager.chainTypesInterface",
                                                 leaf: true
                                                }/*,{
                                                 text: "Pre Processing",
                                                 type: 'PreProcessingARMS',
                                                 xmlInterface: armsManager.preProcessingInterfaceObj,
                                                 interfacePanel: "armsManager.preProcessingInterface",
                                                 leaf: true
                                                },{    
                                                 text: "Metadata Processing",
                                                 type: 'MetadataProcessingARMS',
                                                 xmlInterface: armsManager.metadataProcessingInterfaceObj,
                                                 interfacePanel: "armsManager.metadataProcessingInterface",
                                                 leaf: true
                                                }*/
                                              ]
                                      }),
                                      listeners: {
                                          click: function(n) {
                                               armsManager.workspacePanel.cleanPanel();
                                               if(n.attributes.page){
                                                 var panelHeight=Ext.getCmp("workspacePanel").getHeight()-2;
                                                  new Ext.Panel({
                                                     renderTo: armsManager.workspacePanelDIV,
                                                     height: panelHeight,
                                                     frameId: n.attributes.type+"_frame",
                                                     listeners: {
                                                         "resize" : function(){
                                                             /*var panelHeight=Ext.getCmp("workspacePanel").getHeight()-2;
                                                             document.getElementById(frameId).setAttribute('height', panelHeight);
                                                             this.doLayout();*/
                                                         }
                                                     },
                                                     html: "<div id='"+n.attributes.type+"'></div><iframe src='"+n.attributes.page+"' name='"+n.attributes.type+"_frame' id='"+n.attributes.type+"_frame' scrolling='no' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>"
                                                  }).show();
                                              }else{
                                                if(n.attributes.xmlInterface){  
                                                 
                                                  new Ext.Panel({
                                                     renderTo: armsManager.workspacePanelDIV,
                                                     height: panelHeight,
                                                     listeners: {
                                                         "resize" : function(){
                                                             this.doLayout();
                                                         }
                                                     },
                                                     html: "<div id='"+n.attributes.type+"'></div>"
                                                  }).show();  
                                                   eval(n.attributes.interfacePanel+"=new "+n.attributes.xmlInterface+"();");
                                                   eval(n.attributes.interfacePanel).render(n.attributes.type); 
                                                   //eval(n.attributes.interfacePanel).loadDefaultValues(armsManager.getDefaultValues(n.attributes.xmlInterface));
                                                }  
                                              }   
                                            armsManager.workspacePanel.doLayout();
                                         }
                                      }
                               })
                            ]
                        },{
                          title: "Documentation",
                          border:false,
                          autoScroll:false,
                          page: armsManager.DocumentationPage,
                          html: "<div id='DocumentationARMS'></div><iframe src='"+armsManager.DocumentationPage+"' name='DocumentationARMS_frame'  scrolling='no' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>",
                          listeners: {
                                       expand: function() {
                                               armsManager.workspacePanel.cleanPanel();
                                                 var panelHeight=Ext.getCmp("workspacePanel").getHeight()-10;
                                                  new Ext.Panel({
                                                     renderTo: armsManager.workspacePanelDIV,
                                                     height: panelHeight,
                                                     html: "<iframe id=\"sampleframe\" name=\"sampleframe\" width=\"100%\" height=\"99%\" frameborder=\"0\" src=\"Documentation/docsExplorer/blank.html\" style=\"border: 0px solid #cecece;\"></iframe>"
                                                     //html: "<div id='DocumentationARMS'></div><iframe src='"+this.page+"' name='DocumentationARMS_frame'  scrolling='no' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>"
                                                  }).show();  
                                            armsManager.workspacePanel.doLayout();
                                         }
                                    }
                        },{
                          title: "ARMS Client",
                          border:false,
                          autoScroll:true  
                        }]
                  });
                                
                 var northPanel =new Ext.Panel({
                     region:'north',
                     id: 'northPanel',
                     split:true,
                     border: false,
                     height: screen.height/100*7,
                     minSize: screen.height/100*7,
                     maxSize: screen.height/100*7,
                     bodyColor: '#79a3cb',
                     collapsible: false,
                     collapsed : false,
                     autoScroll : false,
                     margins:'0 0 0 0',
                     html: "<table BGCOLOR='#a9baca' width='100%'><tr><td width='10%'><img src='resources/images/arms.png' height='"+screen.height/100*7+"'/></td>"+
                           "<td><b style='color: #eeeeee;'>Archiving and Resource Management Services</b></td></tr></table>"
                 });
               

                 var westPanel=new Ext.Panel({
                                      region:'west',
                                      id: 'westMainPanel',
                                      split:true,
                                      //minWidth: 300,
                                      maxWidth: 350,
                                      bodyStyle : {background: armsManager.bodyColor},
                                      width: 350,
                                      title: "Archiving Server",
                                      collapsible: true,
                                      collapsed : false,
                                      autoScroll : true,
                                      bodyColor: '#79a3cb',
                                      margins:'5 0 5 5',
                                      layout:'anchor',
                                      listeners: {
                                              expand: function(){
                                                  if(frames[0].tda)
                                                    frames[0].tda.mapRefresh();

                                               },
                                               collapse: function(){
                                                   if(frames[0].tda)
                                                   frames[0].tda.mapRefresh();
                                               }
                                        },
                                      items: [armsManager.armsMainMenuPanel]
               });

               var viewport = new Ext.Viewport({
                                layout:'border',
                                id: 'mainViewPort',
                                items:[northPanel,westPanel,armsManager.workspacePanel]
               });

               var firebugWarning = function () {
                            var cp = new Ext.state.CookieProvider();

                            if(window.console && window.console.firebug && ! cp.get('hideFBWarning')){
                                Ext.Msg.show({
                                   title:'Firebug Warning',
                                   msg: 'Firebug is known to cause performance issues with the Archiving Server Manager.',
                                   buttons: Ext.Msg.OK,
                                   icon: Ext.MessageBox.WARNING
                                });
                            }
                        }

                        var hideMask = function () {
                            Ext.get('loading').remove();
                            Ext.fly('loading-mask').fadeOut({
                                remove:true,
                                callback : firebugWarning
                            });
                        }

                        hideMask.defer(250);

        },
        

        getDefaultValues: function(interfaceName){
            switch(interfaceName){
                case "PreProcessingInterface":
                        return armsManager.preProcessingDefaultValues;
                    break;
                case "MetadataProcessingInterface":
                        return armsManager.metadataProcessingDefaultValues;
                    break;
                case "WatchInterface":
                        return armsManager.watchDefautValues;
                    break;    
                
            }
            
        },
        
        getTypesList: function(interfaceName){
            var storeCombo=new Array();
            
            
            var getChainTypesListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Chain Types List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
            var getChainTypesList= function(response){
                var chainTypesCollection=JSON.parse(response);
                var tmp=null;
                for(var i=0; i<chainTypesCollection.chainTypesNumber;i++){
                    if(! chainTypesCollection.chainTypesList[i].hidden){
                        tmp=new Array();
                        tmp.push(chainTypesCollection.chainTypesList[i].typeName);
                        storeCombo.push(tmp);
                    }
                    
                }
            };

           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.chainTypesInterface.restChainTypesListURL,
                     false, null, "loginValues['user']", "loginValues['password']", 
                     800000, getChainTypesList, getChainTypesListTimeOut,null,
                     null, null);
            
            return storeCombo;
            
        }


};


interfacesManager.setXmlClientLibPath("import/xmlInterfaces");
interfacesManager.setLanguage("eng");
interfacesManager.onReady(armsManager.init);



