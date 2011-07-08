
/*Policy Enforcement Point Application version 0.1*/

/*Import XML Interfaces -- START*/
interfacesManager.loadGlobalScript("scripts/interfaces/loginInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/desktopInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/configurationInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/createPEPServiceInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/createPEPOperationsInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/configurePEPServiceInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/instancesPEPServiceInterface.js");
interfacesManager.loadGlobalScript("scripts/interfaces/infoPEPServiceInterface.js");
/*Import XML Interfaces -- END*/

pepManager = {
  
    /*PEP PAGES*/
    DocumentationPage: "Documentation/docsExplorer/index.html",
    /*PEP PAGES -- END*/

    /*Interfaces Object*/
    loginInterface: null,
    desktopInterface: null,
    configurationInterface: null,
    createPEPServiceInterface: null,
    createPEPOperationsInterface: null,
    configurePEPServicesInterfaces: new Object(),
    instancesPEPServiceInterfaces: new Object(),
    informationPEPServicesInterfaces: new Object(),
    /*Interfaces Object -- END*/
    
    /*Log Services Object  -- START*/
    windowServicesPepLog: new Object(),
    servicesLogtabs: new Object(),
    servicesLogcm: new Object(),
    /*Log Services Object  -- END*/
    
    /*Desktop application*/
    desktop: null, 
    
    loginWindow: null,
    serviceMenu: null,
    
    firebugWarning: null,
    
    user: null,
    password: null,
    
    init:function(){
        pepManager.configurationInterface=new ConfigurationInterface();
        pepManager.desktopInterface=new DesktopInterface(null,pepManager.configurationInterface);
        pepManager.loginInterface=new LoginInterface(pepManager.desktopInterface);
        // pepManager.createPEPServiceInterface=new CreatePEPServiceInterface();
        
       /* var widthLoginWindow=350;
        var heightLoginWindow=210;
        pepManager.loginWindow = new Ext.Window({
            title: "Policy Enforcement Point Login",
            border: false,
            animCollapse : false,
            autoScroll : true,
            resizable : false,
            collapsible: false,
            closable : false,
            draggable: false,
            layout: 'fit',
            width: widthLoginWindow,
            height : heightLoginWindow,
            closeAction:'hide',
            renderTo: "loginPanel",
            html: "<div id='loginInterfaceDiv'/>"
        });*/
        
       /* var currentWidth=widthLoginWindow/(BrowserDetect.getWidth(100)/100);
        var currentHeight=heightLoginWindow/(BrowserDetect.getHeight(100)/100);
        pepManager.loginWindow.setPosition(BrowserDetect.getWidth((100-currentWidth)/2),
            BrowserDetect.getHeight((100-currentHeight)/2));
        pepManager.loginWindow.show();
        pepManager.loginInterface.render('loginInterfaceDiv'); */
                
                
        

        pepManager.firebugWarning = function () {
            var cp = new Ext.state.CookieProvider();

            if(window.console && window.console.firebug && ! cp.get('hideFBWarning')){
                Ext.Msg.show({
                    title:'Firebug Warning',
                    msg: 'Firebug is known to cause performance issues with the Policy Enforcement Point Application.',
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.WARNING
                });
            }
        }

        pepManager.hideMask = function () {
            Ext.get('loginlogo').remove();
            Ext.get('loginfields').remove();
            Ext.fly('loading-mask').fadeOut({
                remove:true,
                callback : pepManager.firebugWarning
            });
            
           
            
        }
        
        var enableLogging = function(){
           document.getElementById("user").removeAttribute("disabled");
           document.getElementById("password").removeAttribute("disabled");
        };
        
        enableLogging.defer(1);

    },
    
    
    configurePEPServicesSave: function(serviceName){
        pepManager.configurePEPServicesInterfaces[serviceName].onSave();
        
    },
    
    openServiceResource: function(idResource, windowTitle, resourceURL, dowloadURL){
        var winResource=Ext.getCmp(idResource);
        if(!winResource)
            winResource = pepManager.desktop.getDesktop().createWindow({
                renderTo: Ext.getBody(),
                id: idResource,
                title: windowTitle,
                closeAction:'close',
                collapsible:true,
                layout:'fit',
                //  iconCls: 'bogus',
                listners:{
                    "resize" : function(){
                        
                    }
                       
                },
                maximizable:true,
                //autoHeight:true,
                width: BrowserDetect.getWidth(40),
                height: BrowserDetect.getHeight(40),
                tbar:[{
                    text:'Download',
                    iconCls:'download',
                    url: dowloadURL,
                    handler : function(){
                        window.open(this.url,'download','width=600, height=450, scrollbars=yes, menubar=yes');
                    }
                }],
                buttons: [{
                    text: 'Close',
                    handler: function(){
                        var win=this.findParentByType("window");
                        win.close();
                    }
                }],
                html : "<div id='frame"+idResource+"'></div><iframe src='"+resourceURL+"' name='"+idResource+"_frame'  scrolling='yes' width='99%' height='95%' marginwidth='0' marginheight='0'></iframe>"
            }).show();
        else
            winResource.show();
    },
    
    desktopTabPanelResource: function(conf){
        this.id=conf.idTabPanel;
        
        this.title=conf.tabPanelTitle;
        
        this.desktopWindow=null;
        
        this.width=conf.width;
        
        this.height=conf.height;
        
        this.tabs=null;
        
        this.menu=null;
        
        this.cm=null,
        
        this.barPaging= null;
        
        this.openTab=function(type, page, titleTab){
            this.desktopWindow=Ext.getCmp(this.id);
            if((!this.desktopWindow)){
                this.tabs = new Ext.TabPanel({
                    renderTo: Ext.getBody(),
                    enableTabScroll:true
                });
                var winId=this.id;
                this.desktopWindow = pepManager.desktop.getDesktop().createWindow({
                    renderTo: Ext.getBody(),
                    layout:'fit',
                    title: this.title,
                    id: this.id,
                    width:this.width,
                    height:this.height,
                    maximizable:true,
                    collapsible:true,
                    closeAction:'close',
                    plain: true,
                    tabs: this.tabs,
                    items: this.tabs,
                    buttons: [{
                        windowID: winId,    
                        text: 'Close',
                        handler: function(){
                            Ext.getCmp(this.windowID).close();
                        }
                    }]
                });
            }else{
                this.tabs= this.desktopWindow.tabs;
       
            }
            //if the document is an xml file
            if (type=="xml"){
                this.addTab(page, titleTab);
            }
            //if the document is a tree
            if (type=="tree"){
                this.addTree(page, titleTab);
            }
            
            if (type=="grid"){
                this.addGrid(page, titleTab);
            }
            this.desktopWindow.show();
        };
      
        this.addTab= function(url, titleTab){
            if (!this.tabs.findById(titleTab)){
                this.tabs.add({
                    id:titleTab,
                    title: titleTab,
                    html: "<iframe scrolling='auto' src='"+ url +"' name='xmltab' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>",
                    closable:true
                }).show();
                this.tabs.setActiveTab(titleTab);
            }else{
                this.tabs.setActiveTab(titleTab);
                this.desktopWindow.show();
            }
        };
        
        this.addTree= function (url, titleTab){
            if (!this.tabs.findById(titleTab)){     
                var tree = new Ext.tree.TreePanel({
                    id:titleTab,
                    root:createXmlTree(url),
                    autoScroll: true,
                    rootVisible: false,
                    border: false,
                    title:titleTab,
                    tbar: [{
                        id:'option',
                        text:'Expand All',
                        handler: function (){
                            tree.expandAll();
                        },
                        tooltip:'Expand All'
                    },'-',{
                        id:'remove',
                        text:'Collapse All',
                        BLANK_IMAGE_URL : /*/"http:/"+"/extjs.com/s.gif"*/"import/xmlInterfaces/import/extresources/images/default/s.gif",
                        handler: function (){
                            tree.collapseAll();
                        },
                        tooltip:'Collapse All'
                    }
                    ],
                    closable:true
                });
                this.tabs.add(tree).show();
                this.tabs.setActiveTab(titleTab);
            }else{
                this.tabs.setActiveTab(titleTab);
                this.desktopWindow.show();
            }
        }
    
        this.addGrid= function(file, titleTab){
            if (!this.tabs.findById(titleTab)){
        
                var dataRecord=new Array();
          
                dataRecord.push({
                    name : "id", 
                    mapping: "level"
                });
                dataRecord.push({
                    name : "date", 
                    mapping: "date"
                });
                dataRecord.push({
                    name : "thread", 
                    mapping: "thread"
                });              
                dataRecord.push({
                    name : "message", 
                    mapping: "text"
                });  
                var store = new Ext.data.Store({
                    nocache : true,
                    url: file,
                    reader: new Ext.data.JsonReader({
                        root : "logs",
                        totalProperty: 'totalFileNumber'
                    },
                    Ext.data.Record.create(dataRecord))
                /*reader: new Ext.data.XmlReader({
                        root: 'logs',
                        totalRecords: '@pagesNumber',
                        record: "log"
                    }, [ 
                    {
                        name : 'id', 
                        mapping:'@level'
                    },

                    {
                        name : 'data', 
                        mapping:'@date'
                    },

                    {
                        name : 'thread', 
                        mapping:'@thread'
                    },

                    {
                        name : '', 
                        mapping: ''
                    }
                    ])*/
                });
        
                var start;
        
                store.on('beforeload', function(store, options) {
                    start = options.params.start;
                });
        
                // trigger the data store load
                store.load({
                    params:{
                        start:0
                    }
                });
                
                
                this.menu =  {xtype:'splitbutton',
                    text: 'Log Level',
                    menu : {
                        items: [ 

                        {
                            text: 'FATAL', 
                            handler: function(){
                                store.filterBy(function (record){
                                    if(record.get('id')=="FATAL")
                                        return true;
                                })
                            }
                        },

                        {
                            text: 'ERROR', 
                            handler: function(){
                                store.filterBy(function (record){
                                    if(record.get('id')=="ERROR" || record.get('id')=="FATAL")
                                        return true;
                                })
                            }
                        },

                        {
                            text: 'WARN', 
                            handler: function(){
                                store.filterBy(function (record){
                                    if(record.get('id')!="INFO" && record.get('id')!="DEBUG")
                                        return true;
                                })
                            }
                        },

                        {
                            text: 'INFO', 
                            handler: function(){
                                store.filterBy(function (record){
                                    if(record.get('id')!="DEBUG")
                                        return true;
                                })
                            }
                        },

                        {
                            text: 'DEBUG', 
                            handler: function(){
                                store.clearFilter();
                            }
                        }
                        ]
                    }
                };
                this.barPaging = new Ext.PagingToolbar({
                    store: store,
                    pageSize:1
                });
                var but = new Ext.Button({
                    tabs: this.tabs,
                    text:'Clear',
                    handler:function(){
                        // barPaging.changePage(1);
                        var c = file.replace("get","clear");
               
                        http.open("GET", c, true);
                        http.onreadystatechange = ResponseClear;
                        if ( !callInProgress(http) ) {
                            http.send(null);
                        } else {
                            alert("I'm busy. Wait a moment");
                        }
                        if (this.tabs.findById(titleTab+" - Text")){
                            this.tabs.add({
                                title:titleTab+" - Text",
                                html: "<iframe scrolling='auto' src='"+ file+"&start="+start+"' name='xmltab' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>", 
                                id:titleTab+" - Text",
                                closable:true
                            });
                        }
                        //store.reload({add : false});
                       /* store.load({
                            params:{
                                start:0
                            }
                        });*/

                    }
                });
        
                var pan; 
        
                var but1 = new Ext.Button({
                    text:'Text',
                    tabs: this.tabs,
                    handler: function(){
                        pan  = new Ext.Panel({
                            title: titleTab+" - Text",
                            id: titleTab+" - Text",
                            html: "<iframe scrolling='auto' src='"+ file+"&start="+start+"' name='xmltab' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>",
                            closable: true
                        });
                        this.tabs.add(pan);
                        this.tabs.setActiveTab(titleTab+" - Text");
                    }
                });

                this.cm=new Ext.grid.ColumnModel([{
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
                    dataIndex:'message',
                    sortable:true,
                    width: 200
                },{
                    header:"Date",
                    dataIndex:'date',
                    sortable:true,
                    width:70
                }]);

                var grid = new Ext.grid.GridPanel({
                    id:titleTab,
                    title:titleTab,
                    width:730,
                    height:500,
                    store: store,
                    cm: this.cm,
                    closable:true,
                    autoExpandColumn:'text',
                    enableColumnMove: true,
                    trackMouseOver:false,
                    loadMask: true,
                    sm: new Ext.grid.RowSelectionModel({
                        singleSelect:true
                    }),
                    frame:true,
                    viewConfig: {
                        forceFit:true,
                        enableRowBody:true,
                        showPreview:false
                    },
                    bbar: [this.barPaging, this.menu, but, but1]
                });
        
                this.tabs.add(grid);
                this.tabs.setActiveTab(titleTab);
            }else{
                this.tabs.setActiveTab(titleTab);
            }
    
            function ResponseClear() {
                if (http.readyState == 4) { 
                    if(http.status == 200){
                        store.reload({add : false});
                    
                    } else {
                       
                    }
                }else{
            }
            }

            function callInProgress(xmlhttp) {
                switch ( xmlhttp.readyState ) {
                    case 1, 2, 3:
                        return true;
                        break;
                    // Case 4 and 0
                    default:
                        return false;
                        break;
                }
            }
        }
    }
    
    
};


interfacesManager.setXmlClientLibPath("import/xmlInterfaces");
interfacesManager.setLanguage("eng");
interfacesManager.onReady(pepManager.init);



