var address, mess, win, menu, tabs;
var pageWindowFrame=new Object();

function XMLHTTPObject() {
    var xmlhttp;
    if (window.ActiveXObject) {
        if (_XML_ActiveX) {
            xmlhttp = new ActiveXObject(_XML_ActiveX);
        } else {
            var versions = ["MSXML2.XMLHTTP", "Microsoft.XMLHTTP", "Msxml2.XMLHTTP.7.0", 
            "Msxml2.XMLHTTP.6.0", "Msxml2.XMLHTTP.5.0", "Msxml2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0"];
            for (var i = 0; i < versions.length ; i++) {
                try {
                    xmlhttp = new ActiveXObject(versions[i]);
                    if (xmlhttp) {
                        var _XML_ActiveX = versions[i];
                        break;
                    }
                }
                catch (e) {
                } ;
            };
        }
    } // then the browser isn't Explorer
    if (!xmlhttp && typeof XMLHttpRequest != 'undefined') {
        try {
            xmlhttp = new XMLHttpRequest();
        } catch (e) {
            xmlhttp = false;
        }
    }
    return xmlhttp;
}

/*Show a Yes/No Dialog box
 * if the answer is 'Yes', this code load
 * the page in 'add' */

function confirm(add, text, title, msg){
    address = add;
    mess=msg;
    Ext.Msg.show({
        title:title,
        minWidth:200,
        msg: text,
        buttons: Ext.Msg.YESNO,
        fn: goPage,
        animEl: 'elId',
        icon: Ext.MessageBox.QUESTION
    });
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

function goPage(btn){
  var http = XMLHTTPObject();
    if (btn == 'yes'){
        if (mess=='delete'){
            http.open("GET", address, true);
            http.onreadystatechange = function(){
                if (http.readyState == 4) { 
                    if(http.status == 200){
                        Ext.MessageBox.wait("Deleting service...","Please Wait"); 
                        setTimeout("location.replace('main.jsp');",4000);
                    } else 
                        Ext.Msg.alert('TOOLBOX Error ', 'Deleting service: Internally Error'); 
                }
            };
            if ( !callInProgress(http) ) {
                http.send(null);
            } else {
                alert("I'm busy. Wait a moment");
            }
        }else{
            location.replace(address);
        }
    }
}


function ResponseClear() {
    if (http.readyState == 4) { 
        if(http.status == 200){
        } else {
            alert("Error"); 
        }
    }else{
    }
}

function translateDelete(strg){
    var text;
    switch (strg){
        case ("en"):
            text="Delete";
            break;
        case ("it"):
            text="Cancella";
            break;
        default:
            text="Delete";
            break;
    }
    return text;
}

/*Create container for Tab and TreeTab
 *Add a tab to Ext.Window
 */
function openTab(type, titleCont, page, titleTab){
    if((!win)){
        tabs = new Ext.TabPanel({
            renderTo: Ext.getBody(),
            enableTabScroll:true
        });
        win = new Ext.Window({
            renderTo: Ext.getBody(),
            layout:'fit',
            width:600,
            height:500,
            maximizable:true,
            collapsible:true,
            closeAction:'hide',
            plain: true,
            items: tabs,
            buttons: [{
                text: 'Close',
                handler: function(){
                    win.hide();
                }
            }]
        });
        win.setTitle(titleCont);
    }
    //if the document is an xml file
    if (type=="xml"){
        addTab(page, titleTab);
    }
    //if the document is a tree
    if (type=="tree"){
        addTree(page, titleTab);
    }
    win.show();
}

/*Open a new Tab with page inside*/
function addTab(url, titleTab){
    if (!tabs.findById(titleTab)){
        tabs.add({
            id:titleTab,
            title: titleTab,
            html: "<iframe scrolling='auto' src='"+ url +"' name='xmltab' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>",
            closable:true
        }).show();
        tabs.setActiveTab(titleTab);
    }else{
        tabs.setActiveTab(titleTab);
        win.show();
    }
}

/*Add a treePanel to tabs
 * treePanel is load from an xml document
 */
function addTree(url, titleTab){
    if (!tabs.findById(titleTab)){     
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
                handler: function (){tree.expandAll();},
                tooltip:'Expand All'
            },'-',{
                id:'remove',
                text:'Collapse All',
                BLANK_IMAGE_URL : /*/"http:/"+"/extjs.com/s.gif"*/"jsScripts/ext-2.0.1/resources/images/default/s.gif",
                handler: function (){tree.collapseAll();},
                tooltip:'Collapse All'
            }
            ],
            closable:true});
            tabs.add(tree).show();
            tabs.setActiveTab(titleTab);
    }else{
        tabs.setActiveTab(titleTab);
        win.show();
    }
}

function createXmlTree(url, callback) {
    var nodo = new Ext.tree.TreeNode();
    var p = new Ext.data.HttpProxy({url:url});
    p.on("loadexception", function(o, response, e) {
        try {
            if (e) throw e;
        }catch (e){
            window.alert("Error: "+e.message);
        }
    });
    p.load(null, {
        read: function(response) {
            var doc = response.responseXML;
            nodo.appendChild(treeNodeFromXml(doc.documentElement || doc));
        }
    }, callback || nodo.getDepth, null);
    return nodo;
}

/**
 Create a TreeNode from an XML node
 */
function treeNodeFromXml(XmlEl) {
    //	Text is nodeValue to text node, otherwise it's the tag name
    var t = ((XmlEl.nodeType == 3) ? XmlEl.nodeValue : XmlEl.tagName);
    
    //	No text, no node.
    if (t.replace(/\s/g,'').length == 0) {
        return null;
    }
    var result = new Ext.tree.TreeNode({
        //iconCls: 'treecss',
        text : t
    });
    
    //	For Elements, process attributes and children
    if (XmlEl.nodeType == 1) {
	
        Ext.each(XmlEl.attributes, function(a) {
            var newHref="";
            var nodename;
            var imageLink;
                       
            nodename=a.nodeName + ': ' + a.nodeValue;
            imageLink='jsScripts/import/gis-client-library/import/ext/resources/images/default/tree/attribute.png';
                 
	    if(a.nodeName == 'schemaLocation'  || a.nodeName == 'xsi:schemaLocation' )
              {
               var tableName=a.nodeValue.substr(a.nodeValue.lastIndexOf('/')+1,a.nodeValue.length);  
               var index=a.nodeValue.indexOf(' ');
               var url=a.nodeValue.substr(index+1,a.nodeValue.length);
	       newHref="javascript: openTab('tree','Tab', 'manager?cmd=proxyRedirect&method=GET&url="+url+"', 'Tree: "+tableName+"');";
              }  
               if(a.nodeName == 'resourceKey')
              {
               var tableName=a.nodeValue.substr(a.nodeValue.lastIndexOf('/')+1,a.nodeValue.length);  
	       newHref="javascript: openTab('tree','Tab', 'manager?cmd=getResource&serviceName="+serviceName+"&instanceType="+instanceType+"&instanceId="+instanceID+"&resourceKey="+a.nodeValue+"', 'Tree: "+a.nodeValue+"');";
            }  
              if(a.nodeName == 'resourceLink')
              {
               var tableName=a.nodeValue.substr(a.nodeValue.lastIndexOf('/')+1,a.nodeValue.length);  
	       newHref="javascript: openTab('tree','Tab', 'manager?cmd=getResource&serviceName="+serviceName+"&instanceType="+instanceType+"&instanceId="+instanceID+"&resourceKey="+a.nodeValue+"', 'Tree: "+a.nodeValue+"');";
               
                 nodename='Resource overview available. Click here';
                 imageLink='jsScripts/ext-2.0.1/resources/images/default/tree/page_white_text.png';
            } 
            
              result.appendChild(new Ext.tree.TreeNode({
                text: nodename,
                icon: imageLink,
		href: newHref,
                BLANK_IMAGE_URL : /*/"http:/"+"/extjs.com/s.gif"*/"jsScripts/ext-2.0.1/resources/images/default/s.gif",
		width: '10',
		height: '10'
            }));
            
           
            
        });
        Ext.each(XmlEl.childNodes, function(el) {
            //		Only process Elements and TextNodes
            if ((el.nodeType == 1) || (el.nodeType == 3)) {
                var c = treeNodeFromXml(el);
                if (c) {
                    result.appendChild(c);
                }
            }
        });
    }
    return result;
}

function view(file, title){
    if (!win){        
        cm = new Ext.grid.ColumnModel([{
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
        
        tabs = new Ext.TabPanel({
            renderTo: Ext.getBody(),
            activeTab:0,
            height:500,
            autoWidth:true
        });
        win = new Ext.Window({
            renderTo: Ext.getBody(),
            title:'Log Table',
            closeAction:'hide',
            collapsible:true,
            layout:'fit',
            maximizable:true,
            autoHeight:true,
            width:750,
            buttons: [{
                text: 'Close',
                handler: function(){
                    win.hide();
                }
            }],
            items:tabs
        })
    }    
    addGrid(file, title);
    win.show();
}

var http = new XMLHttpRequest();
var barPaging=null;
function addGrid(file, titleTab){
    
    if (!tabs.findById(titleTab)){
        
        var store = new Ext.data.Store({
            url: file,
            reader: new Ext.data.XmlReader({
                root: 'logs',
                totalRecords: '@pagesNumber',
                record: "log"
            }, [ 
            {name : 'id', mapping:'@level'},
            {name : 'data', mapping:'@date'},
            {name : 'thread', mapping:'@thread'},
            {name : '', mapping: ''}
            ])
        });
        
        var start;
        
        store.on('beforeload', function(store, options) {
            start = options.params.start;
        });
        
        // trigger the data store load
        store.load({params:{start:0}});
        menu =  new Ext.Toolbar.MenuButton({
            text: 'Log Level',
            menu : {items: [ 
            {text: 'FATAL', handler: function(){store.filterBy(function (record){
                 if(record.get('id')=="FATAL")
                     return true;})}},
                     {text: 'ERROR', handler: function(){store.filterBy(function (record){
                          if(record.get('id')=="ERROR" || record.get('id')=="FATAL")
                              return true;})}},
                              {text: 'WARN', handler: function(){store.filterBy(function (record){
                                   if(record.get('id')!="INFO" && record.get('id')!="DEBUG")
                                       return true;})}},
                                       {text: 'INFO', handler: function(){store.filterBy(function (record){
                                            if(record.get('id')!="DEBUG")
                                                return true;})}},
                                                {text: 'DEBUG', handler: function(){store.clearFilter();}}
                                                ]}
        });
        barPaging = new Ext.PagingToolbar({
            store: store,
            pageSize:1
        });
        var but = new Ext.Button({
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
                if (tabs.findById(titleTab+" - Text")){
                    tabs.add({
                        title:titleTab+" - Text",
                        html: "<iframe scrolling='auto' src='"+ file+"&start="+start+"' name='xmltab' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>", 
                        id:titleTab+" - Text",
                        closable:true
                    });
                }
                //store.reload();
                store.load({params:{start:0}});

                /*alert(tabs.items.items.length);
                tabs.items.items[0].remove();*/
               

                //addGrid(file, titleTab);
                //tabs.setActiveTab(titleTab);
            }
        });
        
        var pan; 
        
        var but1 = new Ext.Button({
            text:'Text',
            handler: function(){
                pan  = new Ext.Panel({
                    title: titleTab+" - Text",
                    id: titleTab+" - Text",
                    html: "<iframe scrolling='auto' src='"+ file+"&start="+start+"' name='xmltab' width='100%' height='100%' marginwidth='0' marginheight='0'></iframe>",
                    closable: true
                });
                tabs.add(pan);
                tabs.setActiveTab(titleTab+" - Text");
            }
        });
        
        var grid = new Ext.grid.GridPanel({
            id:titleTab,
            title:titleTab,
            width:730,
            height:500,
            store: store,
            cm: cm,
            closable:true,
            autoExpandColumn:'text',
            enableColumnMove: true,
            trackMouseOver:false,
            loadMask: true,
            sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
            frame:true,
            viewConfig: {
                forceFit:true,
                enableRowBody:true,
                showPreview:false
            },
            bbar: [barPaging, menu, but, but1]
        });
        
        tabs.add(grid);
        tabs.setActiveTab(titleTab);
    }else{
        tabs.setActiveTab(titleTab);
    }
}


function openWindowFrame (id,title,url,perc_width,perc_height){
  var width,height;  
  if(pageWindowFrame[id]) {
     pageWindowFrame[id].close();  
     pageWindowFrame[id].destroy();
     pageWindowFrame[id]=null;
  } 
  if(perc_width)
     width=Math.floor((screen.width/100)*perc_width); 
  else
     width=Math.floor((screen.width/100)*50); 
  if(perc_height)
     height=Math.floor((screen.height/100)*perc_height); 
  else
     height=Math.floor((screen.height)/100*50); 
  pageWindowFrame[id]=new Ext.Window({
	title: title,
	border: false,
        animCollapse : true,
        autoScroll : true,
        maximizable: true,
        collapsible: true,
	layout: 'fit',
	width: width,
	height: height,
        closeAction:'hide',
        html:"<iframe scrolling='auto' src='"+url+"' name='windowFrame_"+id+"' width='98%' height='98%' marginwidth='0' marginheight='0'></iframe>"
   });  
   pageWindowFrame[id].show();
}

function printError (errorType){
    if(errorType == 'serviceexist'){
         Ext.Msg.show({
            title:'Create a new service: Error',
            buttons: Ext.Msg.OK,
            msg: 'Service already defined. Please choose another name',
            animEl: 'elId',
            icon: Ext.MessageBox.ERROR
     });  
    }
}


function printInfo (infoType){
    if(infoType == 'servicecreated'){
         Ext.Msg.show({
            title:'New service creation: Completed',
            buttons: Ext.Msg.OK,
            msg: 'The new service \"'+currentService+'\" was created. <br> Please complete the service configuration.',
            animEl: 'elId',
            icon: Ext.MessageBox.INFO
     });
    }
}

