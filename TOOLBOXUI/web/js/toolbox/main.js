/*!
 * Ext JS Library 3.1.1
 * Copyright(c) 2006-2010 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.onReady(function() {
	Ext.QuickTips.init();
    
    // create some portlet tools using built in Ext tool ids
    var tools = [{
        id:'refresh',
        handler: function(e, target, panel){
             panel.refresh();
           
        }
    }];

    var viewport = new Ext.Viewport({
        layout:'fit',
        items:[{
            xtype: 'grouptabpanel',
    		tabWidth: 130,
    		activeGroup: 0,
            	items: [
                {
                     expanded: true,
                items: [{
                    xtype: 'portal',
                    title: 'Dashboard',
                    tabTip: 'Dashboard tabtip',
                    items:[{
                        columnWidth:.33,
                        style:'padding:10px 0 10px 10px',
                        items:[{
                            title: 'Deployed services',
                            layout:'fit',
                            tools: tools,
                            items: new DeployedServicesGrid([0, 1])
                        }]
                    },{
                        columnWidth:.33,
                        style:'padding:10px 0 10px 10px',
                        items:[{
                            title: 'Running instances',
                            layout:'fit',
                            tools: tools,
                            items: new RunningInstancesGrid([0, 1,2])
                        }]
                    },{
                        columnWidth:.33,
                        style:'padding:10px',
                        items:[]
                    }]
                }]},
            {
              expanded: true,
                items: [{
                    title: 'Services',
                    iconCls: 'x-icon-configuration',
                    tabTip: 'This panel contains all configuration settings',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Create',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Create a new service',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Status',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                     expanded: true,
                items: [{
                    xtype: 'portal',
                    items:[{
                        columnWidth:1,
                        columnHeight:1,
                        
                        items:[{
                            title: 'Deployed services',
                           
                            tools: tools,
                            items: new ServicesStatusGrid([0, 1,2])
                        }]
                    }]
                }]
                }, {
                    title: 'Operations',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Instances',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }

            ]
            },
            {
              expanded: true,
                items: [{
                    title: 'Servers',
                    iconCls: 'x-icon-configuration',
                    tabTip: 'This panel contains all configuration settings',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'FTP',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'PostgresSQL',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Push',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }]
            },
            {
              expanded: true,
                items: [{
                    title: 'Tools',
                    iconCls: 'x-icon-configuration',
                    tabTip: 'This panel contains some useful tools',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Test client',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Validate tscripts',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Harvest Metadata',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Display the status of each service deployed in this instance of TOOLBOX',
                    style: 'padding: 10px;',
                    html: ""
                }]
            },{
                expanded: true,
                items: [{
                    title: 'Configuration',
                    iconCls: 'x-icon-configuration',
                    tabTip: 'This panel contains all configuration settings',
                    style: 'padding: 10px;',
                    html: ""
                }, {
                    title: 'Edit',
                    iconCls: 'x-icon-templates',
                    tabTip: 'Edit configuration',
                    style: 'padding: 10px;',
                    html: ""
                }]
            }

        ]
		}]
    });
});
