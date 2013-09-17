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

var address, mess, win, menu, tabs;
var pageWindowFrame=new Object();



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
    menu =  new Ext.Toolbar({
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
                        pageSize:15
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
    
                function ResponseClear() {
                    if (http.readyState == 4) { 
                        if(http.status == 200){
                        } else {
                            alert("Error"); 
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



function openTab(type, titleCont, page, titleTab, windowID){

    var navigationWindow=new pepManager.desktopTabPanelResource({
        idTabPanel: windowID
    });
    navigationWindow.openTab(type, page, titleTab);
}
