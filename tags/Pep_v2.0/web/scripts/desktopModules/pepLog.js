
/*
 * PEP LOG Desktop Module
 * author: Andrea Marongiu
 */


PEPLog = Ext.extend(Ext.app.Module, {
    windowPepLog: null,
    id:'peplog-win',
    text: "PEP Log",
    requestGetLog: "manager?cmd=getTbxLog&serviceName=",
    init : function(){
        this.launcher = {
            text: "PEP Log",
            iconCls: 'log',
            handler: this.createWindow,
            scope: this
        }
    },
    createWindow: function() {
        var logTabGridPanel=new pepManager.desktopTabPanelResource({
            idTabPanel: "pepLogTabPanel",
            tabPanelTitle: "PEP Log",
            width: BrowserDetect.getWidth(70),
            height: BrowserDetect.getHeight(70)
        });
                            
        logTabGridPanel.openTab("grid", 
            this.requestGetLog,
            "Grid Log");
    /*  var desktop = pepManager.desktop.getDesktop();
        if (!pepManager.pepLog.windowPepLog){        
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
            pepManager.pepLog.windowPepLog = desktop.createWindow({
                renderTo: Ext.getBody(),
                title:'Log Window',
                closeAction:'close',
                collapsible:true,
                layout:'fit',
                iconCls: 'bogus',
                maximizable:true,
                autoHeight:true,
                width: BrowserDetect.getWidth(60),
                listeners: {
                    "close": function(){
                        pepManager.pepLog.windowPepLog=null;
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
                items:tabs
            })
        }    
        addGrid(this.requestGetLog, this.text);
        pepManager.pepLog.windowPepLog.show();*/
    }

});



