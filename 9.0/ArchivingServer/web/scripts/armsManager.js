

/*ARMS Application version 2.0*/


/*Import XML Interfaces -- START*/
    interfacesManager.loadGlobalScript("scripts/configurationInterface.js");
    interfacesManager.loadGlobalScript("scripts/watchInterface.js");
    interfacesManager.loadGlobalScript("scripts/chainTypeInterface.js");
    interfacesManager.loadGlobalScript("scripts/dataGridInterface.js");
    interfacesManager.loadGlobalScript("scripts/loggingInterface.js");
   
 /*Import XML Interfaces -- END*/

armsManager = {
     restInfoURL: "service/info",
    /*ARMS PAGES*/
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
     logManagerInterface: null,
     dataListInterfaceObj: "DataGridInterface",
     dataListInterface: null,
     pagingSize: 20,
     preProcessingDefaultValues: ["geotiff", "shp"],
     metadataProcessingDefaultValues: ["geotiff", "shp"],
     watchDefautValues:[{type:"geotiff", folder:""},
                        {type:"shp", folder:""},
                       ],
    init:function(){
           armsManager.configurationInterface=new ConfigurationInterface();
           armsManager.watchInterface=new WatchInterface();
           armsManager.chainTypesInterface=new ChainTypesInterface();
           
           armsManager.workspacePanel=new Ext.Panel({
                 region: 'center',
                 margins:'5 0 5 5',
                 layout:'anchor',
                 id:'workspacePanel',
                 autoScroll: true,
                 border: true,
                 html: "<div id='loadingPanel'></div><div id='"+armsManager.workspacePanelDIV+"'/>",
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
                          iconCls: 'manager',
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
                                                 iconCls: 'configuration',
                                                 xmlInterface: armsManager.configurationInterfaceObj,
                                                 interfacePanel: "armsManager.configurationInterface",
                                                 leaf: true
                                                },{
                                                 text: "Chain Types",
                                                 type: 'ChainTypesARMS',
                                                 iconCls: 'chaintypes',
                                                 xmlInterface: armsManager.chainTypesInterfaceObj,
                                                 interfacePanel: "armsManager.chainTypesInterface",
                                                 leaf: true
                                                },{
                                                 text: "Watches",
                                                 type: 'WatchARMS',
                                                 iconCls: 'watches',
                                                 xmlInterface: armsManager.watchInterfaceObj,
                                                 interfacePanel: "armsManager.watchInterface",
                                                 leaf: true
                                                },{
                                                 text: "Data List",
                                                 type: 'DataListARMS',
                                                 iconCls: 'datalist',
                                                 xmlInterface: armsManager.dataListInterfaceObj,
                                                 interfacePanel: "armsManager.dataListInterface",
                                                 leaf: true
                                                }
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
                                                             
                                                         }
                                                     },
                                                     html: "<div id='"+n.attributes.type+"'></div><iframe src='"+n.attributes.page+"' name='"+n.attributes.type+"_frame' id='"+n.attributes.type+"_frame' scrolling='no' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>"
                                                  }).show();
                                              }else{
                                                if(n.attributes.xmlInterface){  
                                                    armsManager.loadXmlInterface(n.attributes.type,
                                                    n.attributes.interfacePanel, n.attributes.xmlInterface);
                                                 
                                                 
                                                }  
                                              }   
                                            armsManager.workspacePanel.doLayout();
                                         }
                                      }
                               })
                            ]
                        },{
                          title: "Log",
                          iconCls: 'log',
                          autoScroll:true,
                          border:false,
                          html: "<div id='logManagerDiv'/>",
                          listeners: {
                            "collapse": function(){


                            },
                            "expand": function(){


                               if(!armsManager.logManagerInterface){

                                  armsManager.logManagerInterface=new LoggingInterface();

                               }

                               armsManager.logManagerInterface.render('logManagerDiv');

                               armsManager.logManagerInterface.updadeLogView();
                            }
                          }
                        },{
                          title: "Documentation",
                          border:false,
                          iconCls: 'documentation',
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
                                      maxWidth: 350,
                                      bodyStyle : {background: armsManager.bodyColor},
                                      width: 350,
                                      title: armsManager.getArmsTitleAndVersion(),
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


              Ext.apply(Ext.form.VTypes, {
                  newwatch: function(val, field){
                      var watchesList=armsManager.getWatchesList();
                      for(var i=0; i<watchesList.length; i++)
                          if(watchesList[i][0]== val)
                              return false;

                      return true;

                  },
                  newwatchText: 'Watch folder already defined',

                  newchaintype: function (val, field){
                    var chainTypesList=armsManager.getTypesList();
                      for(var i=0; i<chainTypesList.length; i++)
                          if(chainTypesList[i][0]== val)
                              return false;

                      return true;
                  },
                  newchaintypeText: 'Chain Type already defined'
                  
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
        
        showWorkspaceLoadPanel: function(msg){
            var workEl=document.getElementById("loadingPanel");
            var message="Loading: Please Wait...";
            if(msg)
                if(msg!='')
                    message=msg; 
            
            var loadingPanel="<div id=\"loading-mask-workspace\" style=\"\" ></div>"+
            "<div id=\"loading\">"+
            //    "<div class=\"x-mask-loading\"><br /><span id=\"loading-msg\">Please Wait...<br><span align=\"center\"><img src=\"resources/images/init_Load.gif\"/></span></div>"+
            "<table class=\"loadingTable\">"+
                "<tr><td align=\"center\"><span id=\"loading-msg\">"+message+"</span></td></tr>"+
                "<tr><td align=\"center\"><img src=\"resources/images/workspaceLoad.gif\"/></td></tr>"+
            "</table>"+    
            "</div>";
        
           workEl.innerHTML=loadingPanel; 
        },
        
        hideWorkspaceLoadPanel: function(){
            Ext.get('loading').remove();
            Ext.fly('loading-mask-workspace').fadeOut({
                   remove:true  
            });
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
        
        getTypesList: function(){
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
            
        },
        
        getWatchesList: function(){
            var storeCombo=new Array();
            
            
            var getWatchesListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Watches List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
            var getWatchesList= function(response){
                var watchesCollection=JSON.parse(response);
                var tmp=null;
                tmp=new Array();
                tmp.push("No Output Watch");
                storeCombo.push(tmp);
                for(var i=0; i<watchesCollection.watchNumber;i++){
                    if(! watchesCollection.watchList[i].hidden){
                        tmp=new Array();
                        tmp.push(watchesCollection.watchList[i].watchFolder);
                        storeCombo.push(tmp);
                    }
                    
                }
            };

           
           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.watchInterface.restWatchListURL,
                     false, null, "loginValues['user']", "loginValues['password']", 
                     800000, getWatchesList, getWatchesListTimeOut,null,
                     null, null);
            
            return storeCombo;
            
        },
        
        getArmsTitleAndVersion: function(){
            var titleAndVersion;
            var getARMSInfoTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get ARMS Information: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
            var getARMSInfo= function(response){    
                var armsInfo=JSON.parse(response);
                var tmp=null;
                titleAndVersion=armsInfo.title+" version: "+armsInfo.version;
            };
            
            sendAuthenticationXmlHttpRequestTimeOut("GET",
                     armsManager.restInfoURL,
                     false, null, "loginValues['user']", "loginValues['password']", 
                     800000, getARMSInfo, getARMSInfoTimeOut,null,
                     null, null);
            
            return titleAndVersion;
        },     
        
        
        newWindow: function(url,type){
            var winURL=url;
            if(type=='geoserver'){
                if(url.indexOf("@")!=-1){
                   winURL="http://"; 
                   var urlSplit=url.split("@"); 
                   winURL+=urlSplit[1];
                } 
            }
            /*"toolbar=yes,
            location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,copyhistory=yes,
            resizable=yes"*/
            window.open(winURL);
        },



        loadXmlInterface: function(type, interfacePanel, xmlInterface){
            var panelHeight=Ext.getCmp("workspacePanel").getHeight()-2;
            new Ext.Panel({
               renderTo: armsManager.workspacePanelDIV,
               height: panelHeight,
               autoScroll: true,
               listeners: {
                 "resize" : function(){
                     this.doLayout();
                 }
               },
               html: "<div id='loadingPanel'></div><div id='"+type+"'></div>"
            }).show();  
            eval(interfacePanel+"=new "+xmlInterface+"();");
            eval(interfacePanel).render(type); 
        }
};


interfacesManager.setXmlClientLibPath("import/xmlInterfaces");
interfacesManager.setLanguage("eng");
interfacesManager.onReady(armsManager.init);



