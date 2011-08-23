

var describe = null;

var updateWindow=null;

var editArea= null;

var editAreaDoc= null;

var ioGridHeight=30;

var backgroundColor="#dfe8f6";

var ds = null;

var dsOut = null;

Ext.onReady(function(){
    describe = new DescribeObject();

    var ds = new Ext.data.ArrayStore({
        autoDestroy: true,
        fields: new Input().getRecordType()
    });

    var dsOut = new Ext.data.ArrayStore({
        autoDestroy: true,
        fields: new Output().getRecordType()
    });


    var colModel = new Ext.grid.ColumnModel([{
        header: "IDENTIFIER",
        width: BrowserDetect.getWidth(20),
        dataIndex: 'identifier'
    },{
        header: "TYPE",
        width:BrowserDetect.getWidth(20),
        dataIndex:'Type'
    },{
        header: "TITLE",
        width:  BrowserDetect.getWidth(20),
        dataIndex:'Title'
    }]);

    var win;
    var button = Ext.get('show-btn');
    button.on('click', function(){
        // Common Input
        
        var common = [{
            items:{
                xtype: 'fieldset',
                collapsible: true,
                id: "commonInputFiledSet",
                title: 'Common Information Input',
                labelAlign: 'top',
                listeners:{
                    'expand': function(){
                        var fieldSetType=Ext.getCmp("literal");
                        if(fieldSetType)
                            fieldSetType.collapse();
                        
                        fieldSetType=Ext.getCmp("complexgrid");
                        if(fieldSetType)
                            fieldSetType.collapse();
                        
                        fieldSetType=Ext.getCmp("bbox");
                        if(fieldSetType)
                            fieldSetType.collapse();
                    }
                },
                items:[{
                    layout:'column' ,
                    items:[{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            fieldLabel: 'Identifier',
                            vtype: "inoutidentifier",
                            allowBlank: false,
                            id:'identifier',
                            name: 'identifier',
                            anchor:'95%'
                        },{
                            xtype:'textarea',
                            fieldLabel: 'Abstract',
                            height: BrowserDetect.getHeight(6), 
                            id:'Abs' ,
                            name: 'abstract',
                            anchor:'95%'
                        },{
                            xtype:'numberfield',
                            fieldLabel: 'MinOccurs',
                            id:'min',
                            allowBlank: false,
                            vtype: "minoccurs",
                            name: 'minoccurs',
                            value: 0,
                            minValue: 0,
                            anchor:'95%'
                        }]
                    },{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            fieldLabel: 'Title',
                            id:'Title' ,
                            allowBlank: false,
                            name: 'title',
                            anchor: '96%'
                        },{
                            xtype:'textarea',
                            fieldLabel: "Metadata[ List serated by','' ]",
                            height: BrowserDetect.getHeight(6),  
                            id:'Meta' ,
                            name: 'metadata',
                            anchor:'96%'
                        },{
                            xtype:'numberfield',
                            fieldLabel: 'MaxOccurs',
                            id:'max' ,
                            allowBlank: false,
                            name: 'maxoccurs',
                            vtype: "maxoccurs",
                            value: 1,
                            minValue: 1,
                            anchor: '96%'
                        }]
                    }
                    ]
                }]
            }
        }];

        var commonUpdate = [{
            items:{
                xtype: 'fieldset',
                collapsible: true,
                id: "commonInputFiledSet",
                title: 'Common Information Input',
                labelAlign: 'top',
                listeners:{
                    'expand': function(){
                        var fieldSetType=Ext.getCmp("literal");
                        if(fieldSetType)
                            fieldSetType.collapse();

                        fieldSetType=Ext.getCmp("complexgrid");
                        if(fieldSetType)
                            fieldSetType.collapse();

                        fieldSetType=Ext.getCmp("bbox");
                        if(fieldSetType)
                            fieldSetType.collapse();
                    }
                },
                items:[{
                    layout:'column' ,
                    items:[{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            fieldLabel: 'Identifier',
                            allowBlank: false,
                            id:'identifier',
                            name: 'identifier',
                            anchor:'95%'
                        },{
                            xtype:'textarea',
                            fieldLabel: 'Abstract',
                            height: BrowserDetect.getHeight(6),
                            id:'Abs' ,
                            name: 'abstract',
                            anchor:'95%'
                        },{
                            xtype:'numberfield',
                            fieldLabel: 'MinOccurs',
                            id:'min',
                            vtype: "minoccurs",
                            allowBlank: false,
                            name: 'minoccurs',
                            value: 0,
                            minValue: 0,
                            anchor:'95%'
                        }]
                    },{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            fieldLabel: 'Title',
                            id:'Title' ,
                            allowBlank: false,
                            name: 'title',
                            anchor: '96%'
                        },{
                            xtype:'textarea',
                            fieldLabel: "Metadata[ List serated by','' ]",
                            height: BrowserDetect.getHeight(6),
                            id:'Meta' ,
                            name: 'metadata',
                            anchor:'96%'
                        },{
                            xtype:'numberfield',
                            fieldLabel: 'MaxOccurs',
                            id:'max' ,
                            allowBlank: false,
                            name: 'maxoccurs',
                            vtype: "maxoccurs",
                            value: 1,
                            minValue: 1,
                            anchor: '96%'
                        }]
                    }
                    ]
                }]
            }
        }];

        var fieldCombo = {
            xtype: 'fieldset',
            id:'fieldsetCombo',
            collapsible: true,
            title: 'Input Type' ,
            labelAlign: 'top',
            items:[{
                xtype: 'combo',
                id:'Type' ,
                allowBlank: false,
                fieldLabel: 'InputFormChoice',
                store: new Ext.data.SimpleStore({
                    id: 'cbox',
                    fields:['inputType'],
                    data:  [
                    ['ComplexData'],
                    ['LiteralData'],
                    ['BoundingBoxData']
                    ]
                }),
                listeners:{
                   
                    'select': function(){
                        var fp = Ext.getCmp('inputForm');
                        var combo = this.getValue() ;
                        var complex = Ext.getCmp('complexgrid');
                        if(complex){
                            fp.remove(complex);
                        }
                        var Boundingbox = Ext.getCmp('bbox');
                        if(Boundingbox){
                            fp.remove(Boundingbox);
                        }
                        var literalData = Ext.getCmp('literal');
                        if(literalData){
                            fp.remove(literalData);
                        }

                        switch(combo)
                        {
                            case  'ComplexData':
                                var Inputs = Ext.data.Record.create([{
                                    name: 'mime',
                                    type: 'string'
                                }, {
                                    name: 'encoding',
                                    type: 'string'
                                }, {
                                    name: 'schema',
                                    type: 'string'
                                }]);

                                var store = new Ext.data.Store({
                                    fields: Inputs
                                });

                                var editor = new Ext.ux.grid.RowEditor({
                                    saveText: 'Update'
                                });

                                var grid = new Ext.grid.GridPanel({
                                    id:'tab' ,
                                    store: store,
                                    frame: true,
                                    title: "Complex Data supported Format (Default Format is the selected)",
                                    autoScroll: true,
                                    height:BrowserDetect.getHeight(35),
                                    region:'center',
                                    margins: '0 5 5 5',
                                    autoExpandColumn: 'name',
                                    editable: true,
                                    plugins: [editor],
                                    tbar: [{
                                        iconCls: 'silk-add',
                                        text: 'Add ',
                                        handler: function(){
                                            var e = new Inputs({
                                                mime: 'text/xml',
                                                encoding: 'UTF-8',
                                                schema:'http://schema.xsd'
                                            });
                                            editor.stopEditing();
                                            store.insert(0, e);
                                            grid.getView().refresh();
                                            grid.getSelectionModel().selectRow(0);
                                            editor.startEditing(0);
                                        }
                                    },{
                                        ref: '../removeBtn',
                                        iconCls: 'silk-delete' ,
                                        text: 'Remove',
                                        disabled: true,
                                        handler: function(){
                                            editor.stopEditing();
                                            var s = grid.getSelectionModel().getSelections();
                                            for(var i = 0, r; r = s[i]; i++){
                                                store.remove(r);
                                            }
                                        }
                                    }],
                                    columns: [
                                    new Ext.grid.RowNumberer(),{
                                        id: 'name',
                                        header: 'Mime Type',
                                        dataIndex: 'mime',
                                        width: BrowserDetect.getWidth(15),
                                        sortable: true,
                                        editable: true,
                                        editor:{
                                            xtype: 'textfield',
                                            allowBlank: false
                                        }
                                    },{
                                        header: 'Encoding',
                                        dataIndex: 'encoding',
                                        width: BrowserDetect.getWidth(15),
                                        sortable: true,
                                        editable: true,
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: true
                                        }
                                    },{
                                        header: 'Schema',
                                        dataIndex: 'schema',
                                        width: BrowserDetect.getWidth(35),
                                        sortable: true,
                                        editable: true,
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: true
                                        }
                                    }]
                                });
                                grid.getSelectionModel().on('selectionchange', function(sm){
                                    grid.removeBtn.setDisabled(sm.getCount() < 1);
                                });

                                var maxMb = {
                                    xtype:'numberfield',
                                    id:'maxMb' ,
                                    fieldLabel: 'Maximum Megabytes',
                                    name: 'maxMegabytes',
                                    minValue: 0
                                };
                                var ComplexGrid = new Ext.form.FieldSet({
                                    id:'complexgrid',
                                    listeners:{
                                        'expand': function(){
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        },
                                        'beforerender': function(){
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        }
                                    }, 
                                    labelAlign: 'left',
                                    autoScroll: true,
                                    collapsible: true ,
                                    title: 'Complex Data' ,
                                    items:[maxMb,grid]
                                });
                                fp.add(ComplexGrid);
                                fp.doLayout();
                                break;

                            case  'BoundingBoxData' :
                                var Bbox = new Ext.form.FieldSet({
                                    id: 'bbox',
                                    listeners:{
                                        'expand': function(){
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        },
                                        'beforerender': function(){
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        }
                                    }, 
                                    labelAlign: 'top',
                                    collapsible: true ,
                                    height: 120,
                                    title: 'BBOX Default/Supported CRS' ,
                                    items:[{
                                        xtype:'textfield',
                                        id:'crs' ,
                                        allowBlank: false,
                                        fieldLabel: "CRS  [ List separated by ',' Supported CRS (Default CRS is the first)]",
                                        value:'EPSG:4326' ,
                                        name: 'title',
                                        anchor:'48%'
                                    }]
                                });
                                fp.add(Bbox);
                                fp.doLayout();
                                break;

                            case  'LiteralData':
                                var Allowdata = Ext.data.Record.create([{
                                    name: 'Allowed',
                                    type:'string'
                                }])

                                var store2 = new Ext.data.Store({
                                    fields: Allowdata
                                });

                                var editor2 = new Ext.ux.grid.RowEditor({
                                    saveText: 'Update'
                                });

                                var grid2 = new Ext.grid.GridPanel({
                                    id:'Lgrid',
                                    frame: true,
                                    anchor:'98%',
                                    title: "Allowed Values (Default Value is the selected)",
                                    store: store2,
                                    autoScroll: true,
                                    height:BrowserDetect.getHeight(35),
                                    region:'center',
                                    margins: '0 5 5 5',
                                    autoExpandColumn: 'name',
                                    plugins: [editor2],
                                    tbar: [{
                                        iconCls: 'silk-add',
                                        text: 'Add ',
                                        st: store2,
                                        handler: function(){
                                            var e = new Allowdata({
                                                Allowed: "VALUE_"+this.st.getCount()
                                                });
                                            editor2.stopEditing();
                                            store2.insert(0, e);
                                            grid2.getView().refresh();
                                            grid2.getSelectionModel().selectRow(0);
                                            editor2.startEditing(0);
                                        }
                                    },{
                                        ref: '../removeBtn',
                                        iconCls: 'silk-delete' ,
                                        text: 'Remove',
                                        disabled: true,
                                        handler: function(){
                                            editor2.stopEditing();
                                            var s = grid2.getSelectionModel().getSelections();
                                            for(var i = 0, r; r = s[i]; i++){
                                                store2.remove(r);
                                            }
                                        }
                                    }],

                                    columns: [
                                    new Ext.grid.RowNumberer(),
                                    {
                                        id: 'name',
                                        header: 'Value',
                                        dataIndex: 'Allowed',
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: false
                                        }
                                    }]
                                });

                                grid2.getSelectionModel().on('selectionchange', function(sm){
                                    grid2.removeBtn.setDisabled(sm.getCount() < 1);
                                });

                                var UOMs = Ext.data.Record.create([{
                                    name:'uom',
                                    type:'string'
                                }]);

                                var store3 = new Ext.data.Store({
                                    fields: UOMs
                                });

                                var editor3 = new Ext.ux.grid.RowEditor({
                                    saveText: 'Update'
                                });

                                var grid3 = new Ext.grid.GridPanel({
                                    id:'Uomgrid',
                                    anchor:'98%',
                                    title: "Supported UOMs (Default UOM is the selected)",
                                    frame: true,
                                    store: store3,
                                    autoScroll: true,
                                    height:BrowserDetect.getHeight(35),
                                    region:'center',
                                    margins: '0 5 5 5',
                                    autoExpandColumn: 'name',
                                    plugins: [editor3],
                                    tbar: [{
                                        iconCls: 'silk-add',
                                        text: 'Add ',
                                        handler: function(){
                                            var e = new UOMs({
                                                uom: 'meters'
                                            });
                                            editor3.stopEditing();
                                            store3.insert(0, e);
                                            grid3.getView().refresh();
                                            grid3.getSelectionModel().selectRow(0);
                                            editor3.startEditing(0);
                                        }
                                    },{
                                        ref: '../removeBtn',
                                        iconCls: 'silk-delete' ,
                                        text: 'Remove',
                                        disabled: true,
                                        handler: function(){
                                            editor3.stopEditing();
                                            var s = grid3.getSelectionModel().getSelections();
                                            for(var i = 0, r; r = s[i]; i++){
                                                store3.remove(r);
                                            }
                                        }
                                    }],
                                    columns: [
                                    new Ext.grid.RowNumberer(),
                                    {
                                        id: 'name',
                                        header: 'UOM',
                                        dataIndex: 'uom',
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: false
                                        }
                                    }]
                                });


                                grid3.getSelectionModel().on('selectionchange', function(sm){
                                    grid3.removeBtn.setDisabled(sm.getCount() < 1);
                                });

                                var Literal = new Ext.form.FieldSet({
                                    id: 'literal',
                                    title:'LiteralData',
                                    listeners:{
                                        'expand': function(){
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse(); 
                                        },
                                        'beforerender': function(){
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        }
                                    }, 
                                    collapsible: true ,
                                    autoScroll: true,
                                    items:[{
                                        layout:'column' ,
                                        items:[{
                                            columnWidth:.3,
                                            layout: 'form',
                                            items:[{

                                                xtype:'combo',
                                                id:'datype',
                                                fieldLabel: 'DataType',
                                                store: new Ext.data.SimpleStore({
                                                    id: 'Ldatatype',
                                                    fields:['datatype'],
                                                    data:  [
                                                    ['short'],
                                                    ['int'],
                                                    ['double'],
                                                    ['float'],
                                                    ['String'],
                                                    ['boolean'],
                                                    ['long'],
                                                    ['byte']
                                                    ]
                                                }),
                                                editable: true,
                                                displayField:'datatype',
                                                triggerAction: 'all',
                                                mode: 'local',
                                                emptyText:'Select a type...',
                                                name: 'dataType',
                                                anchor: '96%'  
                                            }
                                            ]
                                        },{
                                            columnWidth:.35,  
                                            layout: 'form',
                                            items:[{
                                                xtype: 'checkbox',
                                                id: 'any',
                                                fieldLabel: 'AnyValue',
                                                name: 'anyValue',
                                                handler: function(){
                                                    var chek = this.getValue();
                                                    var defaultVal = Ext.getCmp('defValue');
                                                    var allowValues = Ext.getCmp('Lgrid');
                                                    if(chek){
                                                        defaultVal.enable();
                                                        allowValues.disable();
                                                    } else {
                                                        allowValues.enable();
                                                        defaultVal.disable();
                                                    }
                                                }                                       
                                            } , 
                                            grid2]
                                        },{
                                            columnWidth:.35,  
                                            layout:'form',
                                            items:[{
                                                xtype:'textfield',
                                                fieldLabel:'DefaultValue',
                                                id: 'defValue' ,
                                                name:'defaultValue',
                                                anchor:'98%'
                                            }, 
                                            grid3
                                            ]
                                        }

                                        ]
                                    }
                                    ]
                                });
                                var defaultVal = Ext.getCmp('defValue');
                                defaultVal.disable();
                                fp.add(Literal);
                                fp.doLayout();
                                break;
                            default:
                                alert('select input');
                        }
                    }

                },
                editable: true,
                displayField:'inputType',
                triggerAction: 'all',
                mode: 'local',
                emptyText:'Select a type...',
                name: 'inputform',
                anchor:'48%'
            }]
        };


        var commonOutput =[ {
            items:{
                xtype: 'fieldset',
                collapsible: true,
                id:"commonOutputFiledSet",
                listeners:{
                    'expand': function(){
                        
                        var fieldSetType=Ext.getCmp("literal");
                        if(fieldSetType)
                            fieldSetType.collapse();
                        
                        fieldSetType=Ext.getCmp("complexgrid");
                        if(fieldSetType)
                            fieldSetType.collapse();
                        
                        fieldSetType=Ext.getCmp("bbox");
                        if(fieldSetType)
                            fieldSetType.collapse();
                    }
                },
                title: 'Common Information Output',
                labelAlign: 'top',
                items:[{
                    layout:'column' ,
                    items:[{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            id:'identifier',
                            allowBlank: false,
                            fieldLabel: 'Identifier',
                            vtype: "inoutidentifier",
                            name: 'identifier',
                            anchor:'95%'
                        },{
                        },{
                            xtype:'textarea',
                            id:'absOut',
                            fieldLabel: 'Abstract',
                            height: BrowserDetect.getHeight(6),
                            name: 'abstract',
                            anchor:'95%'
                        }]
                    },{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            id:'Title',
                            allowBlank: false,
                            fieldLabel: 'Title',
                            name: 'title',
                            anchor:'96%'
                        },{
                            xtype:'textarea',
                            id:'metaOut',
                            fieldLabel: "Metadata [List serated by','']",
                            height: BrowserDetect.getHeight(6),
                            name: 'metadata',
                            anchor:'96%'
                        }]
                    }
                    ]
                }]
            }

        }];

        var commonOutputUpdate =[ {
            items:{
                xtype: 'fieldset',
                collapsible: true,
                id:"commonOutputFiledSet",
                listeners:{
                    'expand': function(){

                        var fieldSetType=Ext.getCmp("literal");
                        if(fieldSetType)
                            fieldSetType.collapse();

                        fieldSetType=Ext.getCmp("complexgrid");
                        if(fieldSetType)
                            fieldSetType.collapse();

                        fieldSetType=Ext.getCmp("bbox");
                        if(fieldSetType)
                            fieldSetType.collapse();
                    }
                },
                title: 'Common Information Output',
                labelAlign: 'top',
                items:[{
                    layout:'column' ,
                    items:[{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            id:'identifier',
                            allowBlank: false,
                            fieldLabel: 'Identifier',
                            name: 'identifier',
                            anchor:'95%'
                        },{
                        },{
                            xtype:'textarea',
                            id:'absOut',
                            fieldLabel: 'Abstract',
                            height: BrowserDetect.getHeight(6),
                            name: 'abstract',
                            anchor:'95%'
                        }]
                    },{
                        columnWidth:.5,
                        layout: 'form',
                        items:[{
                            xtype:'textfield',
                            id:'Title',
                            allowBlank: false,
                            fieldLabel: 'Title',
                            name: 'title',
                            anchor:'96%'
                        },{
                            xtype:'textarea',
                            id:'metaOut',
                            fieldLabel: "Metadata [List serated by','']",
                            height: BrowserDetect.getHeight(6),
                            name: 'metadata',
                            anchor:'96%'
                        }]
                    }
                    ]
                }]
            }

        }];


        // Output type
        var outputType = {
            xtype: 'fieldset',
            collapsible: true,
            height: 80 ,
            title: 'Output Type' ,
            labelAlign: 'top',
            items:[{
                xtype: 'combo',
                id: 'Type',
                allowBlank: false,
                fieldLabel: 'OutputFormChoice',
                store: new Ext.data.SimpleStore({
                    fields:['outputType'],
                    data:  [
                    ['ComplexOutput'],
                    ['LiteralOutput'],
                    ['BoundingBoxOutput']]
                }),
                listeners:{
                    'select': function(){

                        var fp = Ext.getCmp('outputForm');
                        var comboOut = this.getValue() ;

                        var complexOut = Ext.getCmp('complexgrid');

                        if(complexOut){
                            fp.remove(complexOut);
                        }
                        var BoundingboxOut = Ext.getCmp('bbox');
                        if(BoundingboxOut){
                            fp.remove(BoundingboxOut);
                        }
                        var literalDataOut = Ext.getCmp('literal');
                        if(literalDataOut){
                            fp.remove(literalDataOut);
                        }

                        switch(comboOut)
                        {
                            case  'ComplexOutput'     :
                                var Inputs = Ext.data.Record.create([{
                                    name: 'mime',
                                    type: 'string'
                                }, {
                                    name: 'encoding',
                                    type: 'string'
                                }, {
                                    name: 'schema',
                                    type: 'string'
                                }]);

                                var store4 = new Ext.data.Store({
                                    fields: Inputs
                                });

                                var editor4 = new Ext.ux.grid.RowEditor({
                                    saveText: 'Update'
                                });


                                var grid4 = new Ext.grid.GridPanel({
                                    id:'tab4' ,
                                    store: store4,
                                    autoScroll: true,
                                    frame: true,
                                    title: "Complex Output supported Format (Default Format is the selected)",
                                    height:BrowserDetect.getHeight(35),
                                    region:'center',
                                    margins: '0 5 5 5',
                                    autoExpandColumn: 'name',
                                    plugins: [editor4],
                                    tbar: [{
                                        iconCls: 'silk-add',
                                        text: 'Add ',
                                        handler: function(){
                                            var i = new Inputs({
                                                mime: 'text/xml',
                                                encoding: 'UTF-8',
                                                schema:'http://foo.bar/gml/polygon.xsd'
                                            });
                                            editor4.stopEditing();
                                            store4.insert(0, i);
                                            grid4.getView().refresh();
                                            grid4.getSelectionModel().selectRow(0);
                                            editor4.startEditing(0);
                                        }
                                    },{
                                        ref: '../removeBtn',
                                        iconCls: 'silk-delete' ,
                                        text: 'Remove',
                                        disabled: true,
                                        handler: function(){
                                            editor4.stopEditing();
                                            var s = grid4.getSelectionModel().getSelections();
                                            for(var i = 0, r; r = s[i]; i++){
                                                store4.remove(r);
                                            }
                                        }
                                    }],

                                    columns: [
                                    new Ext.grid.RowNumberer(),
                                    {
                                        id: 'name',
                                        header: 'Mime Type',
                                        dataIndex: 'mime',
                                        width: BrowserDetect.getWidth(15),
                                        sortable: true,
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: false
                                        }
                                    },{
                                        header: 'Encoding',
                                        dataIndex: 'encoding',
                                        sortable: true,
                                        width: BrowserDetect.getWidth(15),
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: true
                                        }
                                    },
                                    {
                                        header: 'Schema',
                                        dataIndex: 'schema',
                                        sortable: true,
                                        width: BrowserDetect.getWidth(35),
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: true
                                        }
                                    }]
                                });
                                grid4.getSelectionModel().on('selectionchange', function(sm){
                                    grid4.removeBtn.setDisabled(sm.getCount() < 1);
                                });

                                /*var maxMb = {
                                    xtype:'numberfield',
                                    id:'maxMbOut',
                                    fieldLabel: 'Maximum Megabytes',
                                    name: 'maxMegabytes',
                                    minValue: 0

                                };*/
                                var ComplexGridOut = new Ext.form.FieldSet({
                                    id:'complexgrid',
                                    labelAlign: 'left',
                                    autoScroll: true,
                                    collapsible: true,
                                    listeners:{
                                        'expand': function(){
                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse(); 
                                        },
                                        'beforerender': function(){
                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        }
                                    }, 
                                    title: 'Complex Output' ,
                                    items:[grid4]

                                });
                                fp.add(ComplexGridOut);
                                fp.doLayout();
                                break;

                            case  'BoundingBoxOutput' :
                                var BboxOut = new Ext.form.FieldSet({
                                    id: 'bbox',
                                    labelAlign: 'top',
                                    listeners:{
                                        'expand': function(){
                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse(); 
                                        },
                                        'beforerender': function(){
                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        }
                                    }, 
                                    collapsible: true ,
                                    title: 'BBOX Output Default/Supported CRS' ,
                                    items:[{
                                        xtype:'textfield',
                                        id:'crsOut',
                                        allowBlank: false,
                                        fieldLabel: "CRS  [ List separated by ',' (Default CRS is the first)]",
                                        value:' EPSG:4326' ,
                                        name: 'title',
                                        anchor:'48%' 
                                    }]
                                });
                                fp.add(BboxOut);
                                fp.doLayout();
                                break;

                            case  'LiteralOutput'     :

                                var UOMs = Ext.data.Record.create([{
                                    name:'uom',
                                    type:'string'
                                }]);

                                var store5 = new Ext.data.Store({
                                    fields: UOMs
                                });
                                
                                var editor5 = new Ext.ux.grid.RowEditor({
                                    saveText: 'Update'
                                });

                                var grid5 = new Ext.grid.GridPanel({
                                    id:'UomgridOut',
                                    anchor:'96%', 
                                    store: store5,
                                    frame: true,
                                    autoScroll: true,
                                    title: "UOMs Supported (Default UOM is the selected)",
                                    height:BrowserDetect.getHeight(35),
                                    region:'center',
                                    margins: '0 5 5 5',
                                    autoExpandColumn: 'name',
                                    plugins: [editor5],
                                    tbar: [{
                                        iconCls: 'silk-add',
                                        text: 'Add ',
                                        handler: function(){
                                            var uOut = new UOMs({
                                                uom: 'meters'
                                            });
                                            editor5.stopEditing();
                                            store5.insert(0, uOut);
                                            grid5.getView().refresh();
                                            grid5.getSelectionModel().selectRow(0);
                                            editor5.startEditing(0);
                                        }
                                    },{
                                        ref: '../removeBtn',
                                        iconCls: 'silk-delete' ,
                                        text: 'Remove',
                                        disabled: true,
                                        handler: function(){
                                            editor5.stopEditing();
                                            var s = grid5.getSelectionModel().getSelections();
                                            for(var i = 0, r; r = s[i]; i++){
                                                store5.remove(r);
                                            }
                                        }
                                    }],
                                    columns: [
                                    new Ext.grid.RowNumberer(),
                                    {
                                        id: 'name',
                                        header: 'UOM',
                                        dataIndex: 'uom',
                                        editor: {
                                            xtype: 'textfield',
                                            allowBlank: false
                                        }
                                    }]
                                });

                                grid5.getSelectionModel().on('selectionchange', function(sm){
                                    grid5.removeBtn.setDisabled(sm.getCount() < 1);
                                });


                                var LiteralOut = new Ext.form.FieldSet({
                                    id: 'literal',
                                    title:'LiteralOutput',
                                    collapsible: true ,
                                    autoScroll: true,
                                    listeners:{
                                        'expand': function(){
                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse(); 
                                        },
                                        'beforerender': function(){
                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.collapse();
                                        }
                                    }, 
                                    items:[{
                                        layout:'column' ,
                                        items:[{
                                            columnWidth:.5,
                                            layout: 'form',
                                            items:[{
                                                xtype:'combo',
                                                id:'datacomboOut' ,
                                                fieldLabel: 'DataType',
                                                store: new Ext.data.SimpleStore({
                                                    id: 'Ldatatype',
                                                    fields:['datatype'],
                                                    data:  [
                                                    ['short'],
                                                    ['int'],
                                                    ['double'],
                                                    ['float'],
                                                    ['String'],
                                                    ['boolean'],
                                                    ['long'],
                                                    ['byte']
                                                    ]
                                                }),
                                                editable: true,
                                                displayField:'datatype',
                                                triggerAction: 'all',
                                                mode: 'local',
                                                emptyText:'Select a type...',
                                                name: 'dataType',
                                                anchor: '69%'
                                            }]
                                        }
                                        ,{
                                            columnWidth: .5,
                                            layout: 'form',
                                            items:[grid5]
                                        }]
                                    }]
                                });
                                fp.add(LiteralOut);
                                fp.doLayout();
                                break;

                            default                 :
                                alert('select output');
                        }
                    }
                },
                displayField:'outputType',
                triggerAction: 'all',
                mode: 'local',
                emptyText:'Select a type...',
                name: 'outputform',
                anchor:'48%'

            }]
        };

        var InputGrid = new Ext.grid.GridPanel({
            id:'inputGrid',
            anchor:'98%',
            ds: ds,
            title: "Processing Inputs",
            cm: colModel,
            frame: true,
            enableRowBody: true,
            listeners: {
                'afterrender': function(){
                    this.setHeight((describeProcessingEditAreaWindow.getInnerHeight()/100)*ioGridHeight);
                    this.doLayout();
                },
                'rowdblclick': function(grid, rowIndex, columnIndex, e){
                    var record = this.getStore().getAt(rowIndex);
                    if(!updateWindow)
                        updateWindow = new Ext.Window({
                            title:'Processing Input Update',
                            layout: 'fit',
                            height:  BrowserDetect.getHeight(98),
                            width: BrowserDetect.getWidth(85),
                            frame: true ,
                            closeAction: "destroy",
                            listeners: {
                                "destory": function(){
                                    updateWindow=null;
                                }
                            },
                            setDefaultValue: function(defaultDataObj){

                                var formPanel= Ext.getCmp('updateForm');
                           
                                formPanel.getForm().items.each(function(item,i) {

                                    item.setValue(defaultDataObj[item.getId()]);

                                    var disableId = Ext.getCmp('identifier');
                                    disableId.disable();

                                    if(defaultDataObj[item.getId()]== 'ComplexData'){
                                        
                                        if(! Ext.getCmp('complexgrid')){
                                            var Inputs = Ext.data.Record.create([{
                                                name: 'mime',
                                                type: 'string'
                                            }, {
                                                name: 'encoding',
                                                type: 'string'
                                            }, {
                                                name: 'schema',
                                                type: 'string'
                                            }]);


                                            var store = new Ext.data.Store({
                                                fields: Inputs
                                            });

                                            var editor = new Ext.ux.grid.RowEditor({
                                                saveText: 'Update'
                                            });

                                            var grid = new Ext.grid.GridPanel({
                                                id:'tabUp' ,
                                                store: store,
                                                frame: true,
                                                title: "Complex Data supported Format (Default Format is the selected)",
                                                autoScroll: true,
                                                height: BrowserDetect.getHeight(35),
                                                region:'center',
                                                margins: '0 5 5 5',
                                                autoExpandColumn: 'nameUp',
                                                editable: true,
                                                plugins: [editor],
                                                tbar: [{
                                                    iconCls: 'silk-add',
                                                    text: 'Add ',
                                                    handler: function(){
                                                        var e = new Inputs({
                                                            mime: 'text/xml',
                                                            encoding: 'UTF-8',
                                                            schema:'http://schema.xsd'
                                                        });
                                                        editor.stopEditing();
                                                        store.insert(0, e);
                                                        grid.getView().refresh();
                                                        grid.getSelectionModel().selectRow(0);
                                                        editor.startEditing(0);
                                                    }
                                                },{
                                                    ref: '../removeBtn',
                                                    iconCls: 'silk-delete' ,
                                                    text: 'Remove',
                                                    disabled: true,
                                                    handler: function(){
                                                        editor.stopEditing();
                                                        var s = grid.getSelectionModel().getSelections();
                                                        for(var i = 0, r; r = s[i]; i++){
                                                            store.remove(r);
                                                        }
                                                    }
                                                }],

                                                columns: [
                                                new Ext.grid.RowNumberer(),
                                                {
                                                    id: 'nameUp',
                                                    header: 'Mime Type',
                                                    dataIndex: 'mime',
                                                    sortable: true,
                                                    editable: true,
                                                    width: BrowserDetect.getWidth(15),
                                                    editor:{
                                                        xtype: 'textfield',

                                                        allowBlank: false
                                                    }
                                                },{
                                                    header: 'Encoding',
                                                    dataIndex: 'encoding', 
                                                    sortable: true,
                                                    editable: true,
                                                    width: BrowserDetect.getWidth(15),
                                                    editor: {
                                                        xtype: 'textfield',
                                                        allowBlank: true

                                                    }
                                                },
                                                {
                                                    header: 'Schema',
                                                    dataIndex: 'schema',
                                                    sortable: true,
                                                    editable: true,
                                                    width: BrowserDetect.getWidth(35),
                                                    editor: {
                                                        xtype: 'textfield',
                                                        allowBlank: true
                                                    }
                                                }]
                                            });
                                            grid.getSelectionModel().on('selectionchange', function(sm){
                                                grid.removeBtn.setDisabled(sm.getCount() < 1);
                                            });

                                            var maxMb = {
                                                xtype:'numberfield',
                                                id:'maxMbUp' ,
                                                fieldLabel: 'Maximum Megabytes',
                                                name: 'maxMegabytes',
                                                minValue: 0
                                    
                                            };
                                            var Cdata = new Ext.form.FieldSet({
                                                id:'complexgrid',
                                                labelAlign: 'left',
                                                autoScroll: true,
                                                listeners:{
                                                    'expand': function(){
                                                        var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                        if(fieldSetType)
                                                            fieldSetType.collapse();
                                                    },
                                                    'beforerender': function(){
                                                        var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                        if(fieldSetType)
                                                            fieldSetType.collapse();
                                                    }
                                                }, 
                                                collapsible: true ,
                                                title: 'Complex Data' ,
                                                items:[maxMb,grid]

                                            });

                                            formPanel.add(Cdata);

                                            var newrecordtype = Ext.data.Record.create(
                                                new Input().getRecordType());
                                            var selectedInput=describe.getInputByIdentifier(record.data.identifier); 
                                    
                                            for(i=0; i<selectedInput.TypeInformation.formatSupported.length; i++){
                                                var newRecord= new newrecordtype({


                                                    mime:selectedInput.TypeInformation.formatSupported[i].mimeType ,
                                                    encoding:selectedInput.TypeInformation.formatSupported[i].encoding ,
                                                    schema:selectedInput.TypeInformation.formatSupported[i].schema

                                                });
                                                var itemMb= Ext.getCmp('maxMbUp');

                                                itemMb.setValue(selectedInput.TypeInformation.MaximumMegabytes);

                                                store.add([newRecord]);
                                            }

                                            formPanel.doLayout();
                                    
                                            var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.expand();
                                     
                                        }
                                    }
                                    else if(defaultDataObj[item.getId()]== 'BoundingBoxData'){
                                        if(!Ext.getCmp('bbox')){
                                            var Bbox = new Ext.form.FieldSet({
                                                id: 'bbox',
                                                labelAlign: 'top',
                                                collapsible: true ,
                                                listeners:{
                                                    'expand': function(){
                                                        var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                        if(fieldSetType)
                                                            fieldSetType.collapse();
                                                    },
                                                    'beforerender': function(){
                                                        var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                        if(fieldSetType)
                                                            fieldSetType.collapse();
                                                    }
                                                }, 
                                                title: 'BBOX Default/Supported CRS' ,
                                                items:[{
                                                    xtype:'textfield',
                                                    id:'crsUp' ,
                                                    allowBlank: false,
                                                    fieldLabel: "CRS  [ List separated by ',' Supported CRS (Default CRS is the first)]",
                                                    value:'EPSG' ,
                                                    name: 'title',
                                                    anchor:'48%' 

                                                }]
                                            });

                                            formPanel.add(Bbox);
                                   
                                            var itemBox= Ext.getCmp('crsUp');
                                            var labelBox=describe.getInputByIdentifier(record.data.identifier);
                                            itemBox.setValue(labelBox.TypeInformation.CRSSupported);
                                            formPanel.doLayout();
                                    
                                            fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.expand();
                                        }
                                    }
                                    else if(defaultDataObj[item.getId()]== 'LiteralData'){
                                        if(!Ext.getCmp('literal')){
                                            var Allowdata = Ext.data.Record.create([{
                                                name: 'Allowed',
                                                type:'string'
                                            }])

                                            var store2 = new Ext.data.Store({

                                                fields: Allowdata

                                            });

                                            var editor2 = new Ext.ux.grid.RowEditor({
                                                saveText: 'Update'
                                            });

                                            var grid2 = new Ext.grid.GridPanel({

                                                id:'Lgrid',
                                                anchor:'96%',
                                                store: store2,
                                                title: "Allowed Values (Default Value is the selected)",
                                                frame: true,
                                                autoScroll: true,
                                                height:BrowserDetect.getHeight(35),
                                                region:'center',
                                                margins: '0 5 5 5',
                                                autoExpandColumn: 'name',
                                                plugins: [editor2],
                                                tbar: [{
                                                    iconCls: 'silk-add',
                                                    text: 'Add ',
                                                    handler: function(){
                                                        var e = new Allowdata({
                                                   
                                                            });

                                                        editor2.stopEditing();
                                                        store2.insert(0, e);
                                                        grid2.getView().refresh();
                                                        grid2.getSelectionModel().selectRow(0);
                                                        editor2.startEditing(0);
                                                    }
                                                },{
                                                    ref: '../removeBtn',
                                                    iconCls: 'silk-delete' ,
                                                    text: 'Remove',
                                                    disabled: true,
                                                    handler: function(){
                                                        editor2.stopEditing();
                                                        var s = grid2.getSelectionModel().getSelections();
                                                        for(var i = 0, r; r = s[i]; i++){
                                                            store2.remove(r);
                                                        }
                                                    }
                                                }],

                                                columns: [
                                                new Ext.grid.RowNumberer(),
                                                {
                                                    id: 'name',
                                                    header: 'AllowedValues',
                                                    dataIndex: 'Allowed',
                                                    editor: {
                                                        xtype: 'textfield',
                                                        allowBlank: false
                                                    }
                                                }

                                                ]
                                            });

                                            grid2.getSelectionModel().on('selectionchange', function(sm){
                                                grid2.removeBtn.setDisabled(sm.getCount() < 1);
                                            });

                                            var UOMs = Ext.data.Record.create([{
                                                name:'uom',
                                                type:'string'
                                            }]);


                                            var store3 = new Ext.data.Store({

                                                fields: UOMs

                                            });

                                            var editor3 = new Ext.ux.grid.RowEditor({
                                                saveText: 'Update'
                                            });


                                            var grid3 = new Ext.grid.GridPanel({

                                                id:'Uomgrid',
                                                anchor:'96%',
                                                store: store3,
                                                title: "Supported UOMs (Default UOM is the selected)",
                                                frame: true,
                                                autoScroll: true,
                                                height:BrowserDetect.getHeight(35),
                                                region:'center',
                                                margins: '0 5 5 5',
                                                autoExpandColumn: 'name',
                                                plugins: [editor3],
                                                tbar: [{
                                                    iconCls: 'silk-add',
                                                    text: 'Add ',
                                                    handler: function(){
                                                        var e = new UOMs({
                                                            uom: 'meters'
                                                        });
                                                        editor3.stopEditing();
                                                        store3.insert(0, e);
                                                        grid3.getView().refresh();
                                                        grid3.getSelectionModel().selectRow(0);
                                                        editor3.startEditing(0);
                                                    }
                                                },{
                                                    ref: '../removeBtn',
                                                    iconCls: 'silk-delete' ,
                                                    text: 'Remove',
                                                    disabled: true,
                                                    handler: function(){
                                                        editor3.stopEditing();
                                                        var s = grid3.getSelectionModel().getSelections();
                                                        for(var i = 0, r; r = s[i]; i++){
                                                            store3.remove(r);
                                                        }
                                                    }
                                                }],

                                                columns: [
                                                new Ext.grid.RowNumberer(),
                                                {
                                                    id: 'name',
                                                    header: 'UOM',
                                                    dataIndex: 'uom',
                                                    editor: {
                                                        xtype: 'textfield',
                                                        allowBlank: false
                                                    }
                                                }
                                                ]

                                            });


                                            grid3.getSelectionModel().on('selectionchange', function(sm){
                                                grid3.removeBtn.setDisabled(sm.getCount() < 1);
                                            });

                                            var Literal = new Ext.form.FieldSet({
                                                id: 'literal',
                                                title:'LiteralData',
                                                collapsible: true ,
                                                autoScroll: true,
                                                listeners:{
                                                    'expand': function(){
                                                        var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                        if(fieldSetType)
                                                            fieldSetType.collapse();
                                                    },
                                                    'beforerender': function(){
                                                        var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                        if(fieldSetType)
                                                            fieldSetType.collapse();
                                                    }
                                                }, 
                                                items:[{
                                                    layout:'column' ,
                                                    items:[{
                                                        columnWidth:.3,
                                                        layout: 'form',
                                                        items:[{

                                                            xtype:'combo',
                                                            id:'datype',
                                                            fieldLabel: 'DataType',
                                                            store: new Ext.data.SimpleStore({
                                                                id: 'Ldatatype',
                                                                fields:['datatype'],
                                                                data:  [
                                                                ['short'],
                                                                ['int'],
                                                                ['double'],
                                                                ['float'],
                                                                ['String'],
                                                                ['boolean'],
                                                                ['long'],
                                                                ['byte']
                                                                ]
                                                            }),
                                                            editable: true,
                                                            displayField:'datatype',
                                                            triggerAction: 'all',
                                                            mode: 'local',
                                                            emptyText:'Select a type...',
                                                            name: 'dataType',
                                                            anchor: '96%'
                                                        }
                                                        ]
                                                    },{
                                                        columnWidth:.35,
                                                        layout: 'form',
                                                        items:[{
                                                            xtype: 'checkbox',
                                                            id: 'any',
                                                            fieldLabel: 'AnyValue',
                                                            name: 'anyValue',
                                                            handler: function(){
                                                                var chek = this.getValue();
                                                                var defaultVal = Ext.getCmp('defValue');
                                                                var allowValues = Ext.getCmp('Lgrid');
                                                                if(chek){
                                                                    defaultVal.enable();
                                                                    allowValues.disable();
                                                                } else {
                                                                    allowValues.enable();
                                                                    defaultVal.disable();
                                                                }
                                                            }
                                                        } ,
                                                        grid2]
                                                    },{
                                                        columnWidth:.35,
                                                        layout:'form',
                                                        items:[{
                                                            xtype:'textfield',
                                                            fieldLabel:'DefaultValue',
                                                            id: 'defValue' ,
                                                            name:'defaultValue',
                                                            anchor:'98%'
                                                        },
                                                        grid3
                                                        ]
                                                    }

                                                    ]
                                                }
                                                ]
                                            });

                                            // disabilita di default la TextField DefaultValue
                                            var defaultVal = Ext.getCmp('defValue');
                                            defaultVal.disable();

                                            var comboDatatype= Ext.getCmp('datype');
                                            var itemDatatype=describe.getInputByIdentifier(record.data.identifier)
                                            comboDatatype.setValue(itemDatatype.TypeInformation.DataType);

                                            formPanel.add(Literal);

                                            var itemDefVal=describe.getInputByIdentifier(record.data.identifier)
                                            defaultVal.setValue(itemDefVal.TypeInformation.DefaultValue);

                                            var newrectype = Ext.data.Record.create(
                                                new Input().getRecordType());
                                            var itemAllow=describe.getInputByIdentifier(record.data.identifier);  //

                                            for(i=0; i<itemAllow.TypeInformation.AllowedValues.length; i++){
                                                var newRec= new newrectype({

                                                    Allowed:itemAllow.TypeInformation.AllowedValues[i]

                                                });

                                                store2.add([newRec]);
                                            }

                                            var recordUom = Ext.data.Record.create(
                                                new Input().getRecordType());

                                            var itemUom=describe.getInputByIdentifier(record.data.identifier);

                                            for(i=0; i<itemUom.TypeInformation.UOMsSupported.length; i++){
                                                var newUom= new recordUom({

                                                    uom:itemUom.TypeInformation.UOMsSupported[i] // dataIndex: recordType

                                                });
                                                store3.add([newUom]);
                                            }

                                            formPanel.doLayout();
                                    
                                            fieldSetType=Ext.getCmp("commonInputFiledSet");
                                            if(fieldSetType)
                                                fieldSetType.expand();
                                        }
                                    }
                                }, this);

                                var labelValue=describe.getInputByIdentifier(record.data.identifier);
                            
                                var labelAbs= Ext.getCmp('Abs');
                                labelAbs.setValue(labelValue.Abstract);
                           
                                var labelMeta= Ext.getCmp('Meta');
                                labelMeta.setValue(labelValue.Metadata);

                                var labelMin= Ext.getCmp('min');
                                labelMin.setValue(labelValue.MinOccurs);

                                var labelMax= Ext.getCmp('max');
                                labelMax.setValue(labelValue.MaxOccurs);

                            },
                            items: new Ext.FormPanel({
                                id: 'updateForm' ,
                                labelWidth: 75,
                                border: false,
                                frame:true,
                                bodyStyle: 'padding: 5px 5px 0' ,
                                fbar: [{
                                    iconCls: 'silk-accept',
                                    text: 'Update',
                                    handler: function(){
                                        // modifica eventualmente i campi del GRIDinput
                                        // e distruggi la window

                                        var formValidation = Ext.getCmp('updateForm').getForm();
                                        var comboType = Ext.getCmp('Type');
                                        var inputType = comboType.getValue();
                                        var typeCheck=true;
                                        var validateTypeMsg="";
                                        switch (inputType){
                                            case "ComplexData":
                                                var gridFormat=Ext.getCmp('tabUp');
                                                typeCheck=gridFormat.getStore().getCount() > 0;
                                                if(!typeCheck)
                                                
                                                    validateTypeMsg="Please insert a Complex Data format";
                                                break;
                                            case "LiteralData":
                                                var anyCheckBox=Ext.getCmp('any');
                                                var gridAllowValues=Ext.getCmp('Lgrid');
                                                typeCheck=anyCheckBox.getValue() ||
                                                gridAllowValues.getStore().getCount() > 0;
                                                if(!typeCheck)
                                                    validateTypeMsg="Please select Any Value or insert a allowed value for the Literal Data.";
                                                break;
                                        }

                                        if(formValidation.isValid() && typeCheck){

                                            var  inputObject= new Input(inputType);

                                            var listForm=new Array('identifier','Title','min','max','Abs','Meta');
                                            var listObjectForm=new Array('Identifier','Title','MinOccurs','MaxOccurs','Abstract','Metadata');
                                            for(var i=0; i<listForm.length; i++){
                                                inputObject[listObjectForm[i]]=Ext.getCmp(listForm[i]).getValue();
                                            }

                                            if(inputType=='ComplexData'){
                                                var complexDataGridFormat = Ext.getCmp('tabUp');
                                                var formatStore=complexDataGridFormat.getStore();
                                                var currentRecord=null;

                                                inputObject.TypeInformation.setMaximumMegabytes(Ext.getCmp('maxMbUp').getValue());

                                                for(i=0; i<formatStore.getCount(); i++){
                                                    currentRecord=formatStore.getAt(i);
                                                    inputObject.TypeInformation.addFormat(currentRecord.get('mime'),
                                                        currentRecord.get('encoding'),  // 'encoding'
                                                        currentRecord.get('schema'));


                                                }
                                        
                                                var selectionModel= complexDataGridFormat.getSelectionModel();
                                                var recordSelected = selectionModel.getSelected();

                                                if(recordSelected ==null){
                                                    inputObject.TypeInformation.setDefault(formatStore.getAt(0).get('mime'));
                                                }
                                                else{
                                                    inputObject.TypeInformation.setDefault(recordSelected.get('mime'));
                                                }
                                            }
                                            else if(inputType=='LiteralData'){
                                                if(Ext.getCmp('datype').getValue())
                                                    inputObject.TypeInformation.setDataType(Ext.getCmp('datype').getValue());

                                                if(Ext.getCmp('defValue').getValue())
                                                    inputObject.TypeInformation.setDefaultValue(Ext.getCmp('defValue').getValue());


                                                inputObject.TypeInformation.setDefaultValue(Ext.getCmp('defValue').getValue());


                                                var UOMgrid = Ext.getCmp('Uomgrid');

                                                var uomStore= UOMgrid.getStore();

                                                var currentUOM=null;

                                                if(uomStore.getCount() >0){
                                                    for(i=0; i<uomStore.getCount(); i++){
                                                        currentUOM=uomStore.getAt(i);
                                                        inputObject.TypeInformation.addUOM(currentUOM.get('uom'));

                                                    }

                                                    var uomSelectionModel = UOMgrid.getSelectionModel();
                                                    var uomSelected = uomSelectionModel.getSelected();


                                                    if(uomSelected ==null){
                                                        inputObject.TypeInformation.setDefaultUOM(uomStore.getAt(0).get('uom'));
                                                    }
                                                    else{
                                                        inputObject.TypeInformation.setDefaultUOM(uomSelected.get('uom'));
                                                    }
                                                }


                                                var LiteralAValuesGrid = Ext.getCmp('Lgrid');
                                                var valuesStore= LiteralAValuesGrid.getStore();

                                                var currentValue=null;

                                                if(valuesStore.getCount() >0){
                                                    for(i=0; i<valuesStore.getCount(); i++){
                                                        currentValue=valuesStore.getAt(i);
                                                        inputObject.TypeInformation.addAllowedValue(currentValue.get('Allowed'));
                                                    }
                                                    var allowedValuesSelectionModel = LiteralAValuesGrid.getSelectionModel();
                                                    var valueSelected = allowedValuesSelectionModel.getSelected();

                                                    if(valueSelected ==null){
                                                        inputObject.TypeInformation.setDefaultValue(valuesStore.getAt(0).get('Allowed'));
                                                    }
                                                    else{
                                                        inputObject.TypeInformation.setDefaultValue(valueSelected.get('Allowed'));
                                                    }
                                                }

                                            }
                                            else if(inputType=='BoundingBoxData'){

                                                inputObject.TypeInformation.setCRSList(Ext.getCmp('crsUp').getValue());

                                            }
                                            else{
                                                alert('select Input Type');
                                            }

                                   
                                            describe.updateInput(inputObject);

                                            //   inviare nel grid i valori in corrispondenza dei valori
                                            //  IDENT TITLE TYPE

                                   
                                            var inputGrid= Ext.getCmp('inputGrid');
                                            var inputSelectionModel = inputGrid.getSelectionModel();
                                            var inputSelected = inputSelectionModel.getSelected();


                                            inputSelected.set('Title',inputObject.Title);
                                            inputSelected.set('Type',inputObject.Type);
                                            updateWindow.destroy(true);
                                            updateWindow=null;
                                            setDescribeValueOnEditArea();

                                        }else{
                                            if(typeCheck){
                                                Ext.Msg.show({
                                                    title:'Validate Processing Input',
                                                    msg: "Please insert all mandatory parameters.",
                                                    buttons: Ext.Msg.OK,
                                                    icon: Ext.MessageBox.WARNING
                                                });
                                                var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                if(fieldSetType)
                                                    fieldSetType.expand();
                                            }else
                                                Ext.Msg.show({
                                                    title:'Validate Processing Input',
                                                    msg:validateTypeMsg,
                                                    buttons: Ext.Msg.OK,
                                                    icon: Ext.MessageBox.WARNING
                                                });
                                           
                                        }
                                    }

                                },'->',{
                                    text: 'Cancel',
                                    iconCls: 'silk-delete',
                                    handler: function(){
                                        updateWindow.destroy(true);
                                        updateWindow=null;
                                    }
                                }]
                                ,
                                items:[{},
                                [commonUpdate],

                                {
                                    xtype: 'fieldset',
                                    id:'fieldsetCombo',
                                    height: 80 ,
                                    collapsible: true,
                                    title: 'Input Type' ,
                                    labelAlign: 'top',
                                    items:[{
                                        xtype: 'combo',
                                        id:'Type' ,     
                                        fieldLabel: 'InputFormChoice',
                                        store: new Ext.data.SimpleStore({
                                            id: 'cbox',
                                            fields:['inputType'],
                                            data:  [
                                            ['ComplexData'],
                                            ['LiteralData'],
                                            ['BoundingBoxData']
                                            ]
                                        }),
                                        listeners:{

                                            'select': function(){
                                                var fp = Ext.getCmp('updateForm');
                                                var combo = this.getValue() ;

                                                var complex = Ext.getCmp('complexgrid');
                                                if(complex){

                                                    fp.remove(complex); 
                                                }
                                                var Boundingbox = Ext.getCmp('bbox');
                                                if(Boundingbox){

                                                    fp.remove(Boundingbox);
                                                }
                                                var literalData = Ext.getCmp('literal');
                                                if(literalData){
                                                    fp.remove(literalData);
                                                }
                                                if(combo=='BoundingBoxData'){
                                                   
                                                    if(! Ext.getCmp('bbox')){
                                                        var Bbox = new Ext.form.FieldSet({
                                                            id: 'bbox',
                                                            labelAlign: 'top',
                                                            collapsible: true ,
                                                            height: 120,
                                                            title: 'BBOX Default/Supported CRS' ,
                                                            items:[{
                                                                xtype:'textfield',
                                                                id:'crsUp' ,
                                                                allowBlank: false,
                                                                fieldLabel: "CRS  [ List separated by ',' Supported CRS (Default CRS is the first)]",
                                                                value:'EPSG:4326' ,
                                                                name: 'title',
                                                                anchor:'48%'
                                                            }]
                                                        });


                                                        fp.add(Bbox);
                                                        fp.doLayout();
                                                    }
                                                    
                                                }
                                                else if(combo=='ComplexData'){
                                                    if(! Ext.getCmp('complexgrid')){
                                                        var Inputs = Ext.data.Record.create([{
                                                            name: 'mime',
                                                            type: 'string'
                                                        }, {
                                                            name: 'encoding',
                                                            type: 'string'
                                                        }, {
                                                            name: 'schema',
                                                            type: 'string'
                                                        }]);


                                                        var store = new Ext.data.Store({
                                                            fields: Inputs
                                                        });

                                                        var editor = new Ext.ux.grid.RowEditor({
                                                            saveText: 'Update'
                                                        });

                                                        var grid = new Ext.grid.GridPanel({
                                                            id:'tabUp' ,
                                                            store: store,
                                                            frame: true,
                                                            autoScroll: true,
                                                            height: BrowserDetect.getHeight(35),
                                                            region:'center',
                                                            margins: '0 5 5 5',
                                                            autoExpandColumn: 'nameUp',
                                                            editable: true,
                                                            plugins: [editor],
                                                            tbar: [{
                                                                iconCls: 'silk-add',
                                                                text: 'Add ',
                                                                handler: function(){
                                                                    var e = new Inputs({
                                                                        mime: 'text/xml',
                                                                        encoding: 'UTF-8',
                                                                        schema:'http://schema.xsd'
                                                                    });
                                                                    editor.stopEditing();
                                                                    store.insert(0, e);
                                                                    grid.getView().refresh();
                                                                    grid.getSelectionModel().selectRow(0);
                                                                    editor.startEditing(0);
                                                                }
                                                            },{
                                                                ref: '../removeBtn',
                                                                iconCls: 'silk-delete' ,
                                                                text: 'Remove',
                                                                disabled: true,
                                                                handler: function(){
                                                                    editor.stopEditing();
                                                                    var s = grid.getSelectionModel().getSelections();
                                                                    for(var i = 0, r; r = s[i]; i++){
                                                                        store.remove(r);
                                                                    }
                                                                }
                                                            }],

                                                            columns: [
                                                            new Ext.grid.RowNumberer(),
                                                            {
                                                                id: 'nameUp',
                                                                header: 'Mime Type',
                                                                dataIndex: 'mime',
                                                                sortable: true,
                                                                width: BrowserDetect.getWidth(15),
                                                                editable: true,
                                                                editor:{
                                                                    xtype: 'textfield',
                                                                    allowBlank: false
                                                                }


                                                            },{
                                                                header: 'Encoding',
                                                                dataIndex: 'encoding', 
                                                                sortable: true,
                                                                width: BrowserDetect.getWidth(15),
                                                                editable: true,
                                                                editor: {
                                                                    xtype: 'textfield',
                                                                    allowBlank: true
                                                                }
                                                            },
                                                            {
                                                                header: 'Schema',
                                                                dataIndex: 'schema',
                                                                sortable: true,
                                                                width: BrowserDetect.getWidth(35),
                                                                editable: true,
                                                                editor: {
                                                                    xtype: 'textfield',
                                                                    allowBlank: true
                                                                }
                                                            }]
                                                        });
                                                        grid.getSelectionModel().on('selectionchange', function(sm){
                                                            grid.removeBtn.setDisabled(sm.getCount() < 1);
                                                        });

                                                        var maxMb = {
                                                            xtype:'numberfield',
                                                            id:'maxMbUp' ,
                                                            fieldLabel: 'Maximum Megabytes',
                                                            name: 'maxMegabytes',
                                                            minValue: 0
                                               
                                                        };
                                                        var Cdata = new Ext.form.FieldSet({
                                                            id:'complexgrid',
                                                            labelAlign: 'left',
                                                            autoScroll: true,
                                                            collapsible: true ,
                                                            listeners:{
                                                                'expand': function(){
                                                                    var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                                    if(fieldSetType)
                                                                        fieldSetType.collapse();
                                                                },
                                                                'beforerender': function(){
                                                                    var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                                    if(fieldSetType)
                                                                        fieldSetType.collapse();
                                                                }
                                                            }, 
                                                            title: 'Complex Data' ,
                                                            items:[maxMb,grid]

                                                        });

                                                        fp.add(Cdata);
                                                        fp.doLayout(); 
                                                    } 
                                                    

                                                }

                                                else if(combo=='LiteralData'){
                                                    if(! Ext.getCmp('literal')){
                                                        var Allowdata = Ext.data.Record.create([{
                                                            name: 'Allowed',
                                                            type:'string'
                                                        }])

                                                        var store2 = new Ext.data.Store({
                                                            fields: Allowdata
                                                        });

                                                        var editor2 = new Ext.ux.grid.RowEditor({
                                                            saveText: 'Update'
                                                        });

                                                        var grid2 = new Ext.grid.GridPanel({
                                                            id:'Lgrid',
                                                            frame: true,
                                                            anchor:'98%',
                                                            title: "Allowed Values (Default Value is the selected)",
                                                            store: store2,
                                                            autoScroll: true,
                                                            height:BrowserDetect.getHeight(35),
                                                            region:'center',
                                                            margins: '0 5 5 5',
                                                            autoExpandColumn: 'name',
                                                            plugins: [editor2],
                                                            tbar: [{
                                                                iconCls: 'silk-add',
                                                                text: 'Add ',
                                                                handler: function(){
                                                                    var e = new Allowdata({
                                                                        });
                                                                    editor2.stopEditing();
                                                                    store2.insert(0, e);
                                                                    grid2.getView().refresh();
                                                                    grid2.getSelectionModel().selectRow(0);
                                                                    editor2.startEditing(0);
                                                                }
                                                            },{
                                                                ref: '../removeBtn',
                                                                iconCls: 'silk-delete' ,
                                                                text: 'Remove',
                                                                disabled: true,
                                                                handler: function(){
                                                                    editor2.stopEditing();
                                                                    var s = grid2.getSelectionModel().getSelections();
                                                                    for(var i = 0, r; r = s[i]; i++){
                                                                        store2.remove(r);
                                                                    }
                                                                }
                                                            }],

                                                            columns: [
                                                            new Ext.grid.RowNumberer(),
                                                            {
                                                                id: 'name',
                                                                header: 'Value',
                                                                dataIndex: 'Allowed',
                                                                editor: {
                                                                    xtype: 'textfield',
                                                                    allowBlank: false
                                                                }
                                                            }]
                                                        });

                                                        grid2.getSelectionModel().on('selectionchange', function(sm){
                                                            grid2.removeBtn.setDisabled(sm.getCount() < 1);
                                                        });

                                                        var UOMs = Ext.data.Record.create([{
                                                            name:'uom',
                                                            type:'string'
                                                        }]);

                                                        var store3 = new Ext.data.Store({
                                                            fields: UOMs
                                                        });

                                                        var editor3 = new Ext.ux.grid.RowEditor({
                                                            saveText: 'Update'
                                                        });

                                                        var grid3 = new Ext.grid.GridPanel({
                                                            id:'Uomgrid',
                                                            anchor:'98%',
                                                            title: "Supported UOMs (Default UOM is the selected)",
                                                            frame: true,
                                                            store: store3,
                                                            autoScroll: true,
                                                            height:BrowserDetect.getHeight(35),
                                                            region:'center',
                                                            margins: '0 5 5 5',
                                                            autoExpandColumn: 'name',
                                                            plugins: [editor3],
                                                            tbar: [{
                                                                iconCls: 'silk-add',
                                                                text: 'Add ',
                                                                handler: function(){
                                                                    var e = new UOMs({
                                                                        uom: 'meters'
                                                                    });
                                                                    editor3.stopEditing();
                                                                    store3.insert(0, e);
                                                                    grid3.getView().refresh();
                                                                    grid3.getSelectionModel().selectRow(0);
                                                                    editor3.startEditing(0);
                                                                }
                                                            },{
                                                                ref: '../removeBtn',
                                                                iconCls: 'silk-delete' ,
                                                                text: 'Remove',
                                                                disabled: true,
                                                                handler: function(){
                                                                    editor3.stopEditing();
                                                                    var s = grid3.getSelectionModel().getSelections();
                                                                    for(var i = 0, r; r = s[i]; i++){
                                                                        store3.remove(r);
                                                                    }
                                                                }
                                                            }],
                                                            columns: [
                                                            new Ext.grid.RowNumberer(),
                                                            {
                                                                id: 'name',
                                                                header: 'UOM',
                                                                dataIndex: 'uom',
                                                                editor: {
                                                                    xtype: 'textfield',
                                                                    allowBlank: false
                                                                }
                                                            }]
                                                        });


                                                        grid3.getSelectionModel().on('selectionchange', function(sm){
                                                            grid3.removeBtn.setDisabled(sm.getCount() < 1);
                                                        });

                                                        var Literal = new Ext.form.FieldSet({
                                                            id: 'literal',
                                                            title:'LiteralData',
                                                            collapsible: true ,
                                                            autoScroll: true,
                                                            listeners:{
                                                                'expand': function(){
                                                                    var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                                    if(fieldSetType)
                                                                        fieldSetType.collapse();
                                                                },
                                                                'beforerender': function(){
                                                                    var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                                    if(fieldSetType)
                                                                        fieldSetType.collapse();
                                                                }
                                                            }, 
                                                            items:[{
                                                                layout:'column' ,
                                                                items:[{
                                                                    columnWidth:.3,
                                                                    layout: 'form',
                                                                    items:[{

                                                                        xtype:'combo',
                                                                        id:'datype',
                                                                        fieldLabel: 'DataType',
                                                                        store: new Ext.data.SimpleStore({
                                                                            id: 'Ldatatype',
                                                                            fields:['datatype'],
                                                                            data:  [
                                                                            ['short'],
                                                                            ['int'],
                                                                            ['double'],
                                                                            ['float'],
                                                                            ['String'],
                                                                            ['boolean'],
                                                                            ['long'],
                                                                            ['byte']
                                                                            ]
                                                                        }),
                                                                        editable: true,
                                                                        displayField:'datatype',
                                                                        triggerAction: 'all',
                                                                        mode: 'local',
                                                                        emptyText:'Select a type...',
                                                                        name: 'dataType',
                                                                        anchor: '96%'
                                                                    }
                                                                    ]
                                                                },{
                                                                    columnWidth:.35,
                                                                    layout: 'form',
                                                                    items:[{
                                                                        xtype: 'checkbox',
                                                                        id: 'any',
                                                                        fieldLabel: 'AnyValue',
                                                                        name: 'anyValue',
                                                                        handler: function(){
                                                                            var chek = this.getValue();
                                                                            var defaultVal = Ext.getCmp('defValue');
                                                                            var allowValues = Ext.getCmp('Lgrid');
                                                                            if(chek){
                                                                                defaultVal.enable();
                                                                                allowValues.disable();
                                                                            } else {
                                                                                allowValues.enable();
                                                                                defaultVal.disable();
                                                                            }
                                                                        }
                                                                    } ,
                                                                    grid2]
                                                                },{
                                                                    columnWidth:.35,
                                                                    layout:'form',
                                                                    items:[{
                                                                        xtype:'textfield',
                                                                        fieldLabel:'DefaultValue',
                                                                        id: 'defValue' ,
                                                                        name:'defaultValue',
                                                                        anchor:'98%'
                                                                    },
                                                                    grid3
                                                                    ]
                                                                }

                                                                ]
                                                            }
                                                            ]
                                                        });
                                                        var defaultVal = Ext.getCmp('defValue');
                                                        defaultVal.disable();
                                                        fp.add(Literal);
                                                        fp.doLayout();
                                                    }
                                                    
                                                    

                                                }
                                            }
                                        },
                                        editable: true,
                                        displayField:'inputType',
                                        triggerAction: 'all',
                                        mode: 'local',
                                        emptyText:'Select a type...',
                                        name: 'inputform',
                                        anchor:'48%'
                                    }]
                                }]

                            })
                        });

                    updateWindow.show();
                    updateWindow.setDefaultValue(record.data);
                }
            },

            tbar:[{
                text:'Add Input',
                iconCls: 'silk-add',
                handler: function(){
                    if(!updateWindow)
                        updateWindow = new Ext.Window({

                            id:'win',
                            layout: 'fit',
                            title: "New Processing Input",
                            height:BrowserDetect.getHeight(98),
                            width: BrowserDetect.getWidth(85),
                            frame: true ,
                            closeAction: "destroy",
                            listeners:{
                                "destroy": function(){
                                    updateWindow=null;
                                }
                            },
                            items: new Ext.FormPanel({
                                id: 'inputForm' ,
                                labelWidth: 75,
                                frame:true,
                                bodyStyle: 'padding: 5px 5px 0' ,
                                fbar: [{
                                    iconCls: 'icon-save',
                                    text: 'Save',
                                    handler: function(){

                                        var formValidation = Ext.getCmp('inputForm').getForm();
                                        var comboType = Ext.getCmp('Type');
                                        var inputType = comboType.getValue();
                                        var typeCheck=true;
                                        var validateTypeMsg="";
                                        switch (inputType){
                                            case "ComplexData":
                                                var gridFormat=Ext.getCmp('tab');
                                                typeCheck=gridFormat.getStore().getCount() > 0;
                                                if(!typeCheck)
                                                    validateTypeMsg="Please insert a Complex Data format";
                                                break;
                                            case "LiteralData":
                                                var anyCheckBox=Ext.getCmp('any');
                                                var gridAllowValues=Ext.getCmp('Lgrid');
                                                typeCheck=anyCheckBox.getValue() ||
                                                gridAllowValues.getStore().getCount() > 0;
                                                if(!typeCheck)
                                                    validateTypeMsg="Please select Any Value or insert a allowed value for the Literal Data.";
                                                break;                                        
                                        }
                                    
                                        if(formValidation.isValid() && typeCheck){
                                            // istanzio l' oggetto Input presente in DescribeObject.js
                                     
                                    
                                            var  inputObject= new Input(inputType);

                                            var listForm=new Array('identifier','Title','min','max','Abs','Meta');
                                            var listObjectForm=new Array('Identifier','Title','MinOccurs','MaxOccurs','Abstract','Metadata');
                                            for(var i=0; i<listForm.length; i++){
                                                inputObject[listObjectForm[i]]=Ext.getCmp(listForm[i]).getValue();

                                            }

                                            if(inputType=='ComplexData'){
                                                var complexDataGridFormat = Ext.getCmp('tab');
                                                var formatStore=complexDataGridFormat.getStore();
                                                var currentRecord=null;

                                                inputObject.TypeInformation.setMaximumMegabytes(Ext.getCmp('maxMb').getValue());

                                                for(i=0; i<formatStore.getCount(); i++){
                                                    currentRecord=formatStore.getAt(i);
                                                    inputObject.TypeInformation.addFormat(currentRecord.get('mime'),
                                                        currentRecord.get('encoding'),
                                                        currentRecord.get('schema'));


                                                }
                                            
                                                var selectionModel= complexDataGridFormat.getSelectionModel();
                                                var recordSelected = selectionModel.getSelected();

                                                if(recordSelected ==null){
                                                    inputObject.TypeInformation.setDefault(formatStore.getAt(0).get('mime'));
                                                }
                                                else{
                                                    inputObject.TypeInformation.setDefault(recordSelected.get('mime'));
                                                }
                                            }
                                            else if(inputType=='LiteralData'){
                                                if(Ext.getCmp('datype').getValue())
                                                    inputObject.TypeInformation.setDataType(Ext.getCmp('datype').getValue());

                                                if(Ext.getCmp('defValue').getValue())
                                                    inputObject.TypeInformation.setDefaultValue(Ext.getCmp('defValue').getValue());


                                                inputObject.TypeInformation.setDefaultValue(Ext.getCmp('defValue').getValue());


                                                var UOMgrid = Ext.getCmp('Uomgrid');

                                                var uomStore= UOMgrid.getStore();

                                                var currentUOM=null;

                                                if(uomStore.getCount() >0){
                                                    for(i=0; i<uomStore.getCount(); i++){
                                                        currentUOM=uomStore.getAt(i);
                                                        inputObject.TypeInformation.addUOM(currentUOM.get('uom'));

                                                    }

                                                    var uomSelectionModel = UOMgrid.getSelectionModel();
                                                    var uomSelected = uomSelectionModel.getSelected();


                                                    if(uomSelected ==null){
                                                        inputObject.TypeInformation.setDefaultUOM(uomStore.getAt(0).get('uom'));
                                                    }
                                                    else{
                                                        inputObject.TypeInformation.setDefaultUOM(uomSelected.get('uom'));
                                                    }
                                                }


                                                var LiteralAValuesGrid = Ext.getCmp('Lgrid');
                                                var valuesStore= LiteralAValuesGrid.getStore();

                                                var currentValue=null;

                                                if(valuesStore.getCount() >0){
                                                    for(i=0; i<valuesStore.getCount(); i++){
                                                        currentValue=valuesStore.getAt(i);
                                                        inputObject.TypeInformation.addAllowedValue(currentValue.get('Allowed'));
                                                    }
                                                    var allowedValuesSelectionModel = LiteralAValuesGrid.getSelectionModel();
                                                    var valueSelected = allowedValuesSelectionModel.getSelected();

                                                    if(valueSelected ==null){
                                                        inputObject.TypeInformation.setDefaultValue(valuesStore.getAt(0).get('Allowed'));
                                                    }
                                                    else{
                                                        inputObject.TypeInformation.setDefaultValue(valueSelected.get('Allowed'));
                                                    }
                                                }

                                            }
                                            else if(inputType=='BoundingBoxData'){

                                                inputObject.TypeInformation.setCRSList(Ext.getCmp('crs').getValue());

                                            }
                                            else{
                                                alert('select Input Type');
                                            }

                                            describe.insertInput(inputObject);

                                            //   inviare nel grid i valori in corrispondenza dei valori
                                            //  IDENT TITLE TYPE

                                        
                                            var newrecordtype = Ext.data.Record.create(
                                                new Input().getRecordType());
                                            var newRecord= new newrecordtype({
                                                identifier:inputObject.Identifier,
                                                Type:inputObject.Type,
                                                Title:inputObject.Title

                                            });
                                    
                                            ds.add([newRecord]);

                                            updateWindow.destroy();
                                            updateWindow=null;
                                            setDescribeValueOnEditArea();
                                        }else{
                                            if(typeCheck){
                                                Ext.Msg.show({
                                                    title:'Validate Processing Input',
                                                    msg: "Please insert all mandatory parameters.",
                                                    buttons: Ext.Msg.OK,
                                                    icon: Ext.MessageBox.WARNING
                                                });
                                                var fieldSetType=Ext.getCmp("commonInputFiledSet");
                                                if(fieldSetType)
                                                    fieldSetType.expand();
                                            }else
                                                Ext.Msg.show({
                                                    title:'Validate Processing Input',
                                                    msg:validateTypeMsg,
                                                    buttons: Ext.Msg.OK,
                                                    icon: Ext.MessageBox.WARNING
                                                });
                                        }
                                    }
                                },'->',{
                                    text: 'Cancel',
                                    iconCls: 'silk-delete',
                                    handler: function(){
                                        updateWindow.destroy();
                                        updateWindow=null;
                                    }
                                }]
                                ,
                                items:[
                                common,
                                fieldCombo

                                ]
                            })
                        })

                    updateWindow.show();

                }
            },'-',{
                text: 'Remove Input',
                iconCls: 'silk-delete',
                handler: function(){

                    InputGrid= Ext.getCmp('inputGrid');
                    var inputSelectionModel = InputGrid.getSelectionModel();
                    var inputSelected = inputSelectionModel.getSelected();

                    if(inputSelected== null){
                        Ext.Msg.show({
                            title:'Remove Processing Input',
                            msg: "No input selected",
                            buttons: Ext.Msg.OK,
                            icon: Ext.MessageBox.WARNING
                        });

                    }else{
                        Ext.Msg.show({
                            title:'Remove Processing Input',
                            msg: "Do you really want to delete the selected Processing Input?",
                            buttons: Ext.Msg.YESNOCANCEL,
                            icon: Ext.MessageBox.QUESTION,
                            fn: function(buttonId,text,opt){
                                if(buttonId == "yes"){
                                    ds.remove(inputSelected);
                                    describe.removeInput(inputSelected.data.identifier);
                                    setDescribeValueOnEditArea();
                                }
                            }
                        });
                    }
                }
            }
            ]
        });

        newProcessingFormPanel = new Ext.FormPanel({
            id:'form',
            //labelAlign: 'top',
            frame:true,
            border: false,
            bodyStyle:'padding:5px 5px 0',
            width: BrowserDetect.getWidth(70),
            listeners:{
                "resize": function(){
                    this.setHeight(newProcessingPanel.getInnerHeight());
                }
            },
            items:[{
                layout:'column' ,
                labelAlign: 'top',
                items:[{
                    columnWidth:.5,
                    layout: 'form',
                    items:[{
                        xtype:'textfield',
                        fieldLabel: 'Name',
                        allowBlank: false,
                        name: 'name',
                        listeners: {
                            "change": function(){
                                describe.Identifier=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        anchor:'96%'
                    }]
                },{
                    columnWidth:.5,
                    layout: 'form',
                    items:[{
                        xtype:'textfield',
                        fieldLabel: 'Title',
                        allowBlank: false,
                        name: 'title',
                        listeners: {
                            "change": function(){
                                describe.Title=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        anchor:'96%'
                    }]
                },{
                    columnWidth:.5,
                    //  layout:'ux.center',
                    labelAlign: "left",
                    // items:[{
                    layout: 'form',    
                    items:[ {
                        xtype:'textfield',
                        fieldLabel: 'Process Version',
                        allowBlank: false,
                        name: 'processVersion',
                        listeners: {
                            "change": function(){
                                describe.processVersion=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        anchor:'96%'
                    }]
                //  }  
                //  ]
                },{
                    columnWidth:.25,
                    layout: 'form',
                    labelAlign: "rigth",
                    items:[{
                        xtype:'checkbox',
                        fieldLabel: 'Store Supported',
                        allowBlank: false,
                        name: 'storeSupported',
                        listeners: {
                            "check": function(){
                                describe.storeSupported=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        anchor:'96%'
                    }]
                },{
                    columnWidth:.25,
                    layout: 'form',
                    labelAlign: "left",
                    items:[{
                        xtype:'checkbox',
                        fieldLabel: 'Status Supported',
                        allowBlank: false,
                        name: 'statusSupported',
                        listeners: {
                            "check": function(){
                                describe.statusSupported=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        anchor:'96%'
                    }]
                },{
                    columnWidth:1,
                    layout: 'form',
                    items:[{
                        xtype:'textarea',
                        fieldLabel: 'Abstract',
                        name: 'abstract',
                        height: BrowserDetect.getHeight(6),
                        listeners: {
                            "change": function(){
                                describe.Abstract=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        anchor:'98%'
                    },{
                        xtype:'textfield',
                        fieldLabel: "Metadata [ List separated by ',' ]",
                        listeners: {
                            "change": function(){
                                describe.Metadata=this.getValue();
                                setDescribeValueOnEditArea();
                            }
                        },
                        name: 'metadata',
                        anchor:'98%'
                    }]
                } ]
            }
            ,[InputGrid]
            ,{
                xtype:'grid',
                id:'outputGrid',
                frame: true,
                title: "Processing Outputs",
                enableRowBody: true,
                listeners: {
                    'afterrender': function(){
                        this.setHeight((describeProcessingEditAreaWindow.getInnerHeight()/100)*ioGridHeight);
                        this.doLayout();
                    },
                    'rowdblclick': function(grid, rowIndex, columnIndex, e){

                        OutputGrid= Ext.getCmp('outputGrid');
                        var record = this.getStore().getAt(rowIndex);

                        if(!updateWindow)
                            updateWindow = new Ext.Window({
                                title:'Processing Output Update',
                                layout: 'fit',
                                height:   BrowserDetect.getHeight(98),
                                width: BrowserDetect.getWidth(85),
                                closeAction: "destroy",
                                frame: true ,
                                listeners:{
                                    "destroy": function(){
                                        updateWindow=null;
                                    }
                                },
                                setOutDefaultValue: function(defaultDataObj){

                                    var  formPanelOut = Ext.getCmp('updateOutForm');
                                    formPanelOut.getForm().items.each(function(item,i) {

                                        item.setValue(defaultDataObj[item.getId()]);

                                        var disableIdout = Ext.getCmp('identifier');
                                        disableIdout.disable();

                                        if(defaultDataObj[item.getId()]== 'ComplexOutput'){
                                            if(!Ext.getCmp('complexgrid')){
                                                var Inputs = Ext.data.Record.create([{
                                                    name: 'mime',
                                                    type: 'string'
                                                }, {
                                                    name: 'encoding',
                                                    type: 'string'
                                                }, {
                                                    name: 'schema',
                                                    type: 'string'
                                                }]);

                                                var store4 = new Ext.data.Store({

                                                    fields: Inputs

                                                });

                                                var editor4 = new Ext.ux.grid.RowEditor({
                                                    saveText: 'Update'
                                                });


                                                var grid4 = new Ext.grid.GridPanel({

                                                    id:'tabOutUp' ,
                                                    store: store4,
                                                    autoScroll: true,
                                                    frame: true,
                                                    title: "Complex Output supported Format (Default Format is the selected)",
                                                    height:BrowserDetect.getHeight(35),
                                                    region:'center',
                                                    margins: '0 5 5 5',
                                                    autoExpandColumn: 'name',
                                                    plugins: [editor4],
                                                    tbar: [{
                                                        iconCls: 'silk-add',
                                                        text: 'Add ',
                                                        handler: function(){
                                                            var i = new Inputs({
                                                                mime: 'text/xml',
                                                                encoding: 'UTF-8',
                                                                schema:'http://foo.bar/gml/polygon.xsd'
                                                            });
                                                            editor4.stopEditing();
                                                            store4.insert(0, i);
                                                            grid4.getView().refresh();
                                                            grid4.getSelectionModel().selectRow(0);
                                                            editor4.startEditing(0);
                                                        }
                                                    },{
                                                        ref: '../removeBtn',
                                                        iconCls: 'silk-delete' ,
                                                        text: 'Remove',
                                                        disabled: true,
                                                        handler: function(){
                                                            editor4.stopEditing();
                                                            var s = grid4.getSelectionModel().getSelections();
                                                            for(var i = 0, r; r = s[i]; i++){
                                                                store4.remove(r);
                                                            }
                                                        }
                                                    }],

                                                    columns: [
                                                    new Ext.grid.RowNumberer(),
                                                    {
                                                        id: 'name',
                                                        header: 'Mime Type',
                                                        dataIndex: 'mime',
                                                        width: BrowserDetect.getWidth(15),
                                                        sortable: true,
                                                        editor: {
                                                            xtype: 'textfield',
                                                            allowBlank: false
                                                        }
                                                    },{
                                                        header: 'Encoding',
                                                        dataIndex: 'encoding',
                                                        width: BrowserDetect.getWidth(15),
                                                        sortable: true,
                                                        editor: {
                                                            xtype: 'textfield',
                                                            allowBlank: true
                                                        }
                                                    },
                                                    {
                                                        header: 'Schema',
                                                        dataIndex: 'schema',
                                                        width: BrowserDetect.getWidth(35),
                                                        sortable: true,
                                                        editor: {
                                                            xtype: 'textfield',
                                                            allowBlank: true
                                                        }
                                                    }]
                                                });
                                                grid4.getSelectionModel().on('selectionchange', function(sm){
                                                    grid4.removeBtn.setDisabled(sm.getCount() < 1);
                                                });

                                                /*var maxMb = {
                                                    xtype:'numberfield',
                                                    id:'maxMbOutUp',
                                                    fieldLabel: 'Maximum Megabytes',
                                                    name: 'maxMegabytes',
                                                    minValue: 0

                                                };*/
                                                var ComplexGridOutUp = new Ext.form.FieldSet({
                                                    id:'complexgrid',
                                                    labelAlign: 'left',
                                                    autoScroll: true,
                                                    collapsible: true,
                                                    listeners:{
                                                        'expand': function(){
                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                            if(fieldSetType)
                                                                fieldSetType.collapse();
                                                        },
                                                        'beforerender': function(){
                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                            if(fieldSetType)
                                                                fieldSetType.collapse();
                                                        }
                                                    }, 
                                                    title: 'Complex Default/Supported Format' ,
                                                    items:[/*maxMb,*/grid4]

                                                });
                                                formPanelOut.add(ComplexGridOutUp);

                                                var newrecordtype = Ext.data.Record.create(
                                                    new Output().getRecordType());
                                                var selectedOutput=describe.getOutputByIdentifier(record.data.identifier);  //

                                                for(i=0; i<selectedOutput.TypeInformation.formatSupported.length; i++){
                                                    var newRecord= new newrecordtype({

                                                        mime:selectedOutput.TypeInformation.formatSupported[i].mimeType ,
                                                        encoding:selectedOutput.TypeInformation.formatSupported[i].encoding ,
                                                        schema:selectedOutput.TypeInformation.formatSupported[i].schema

                                                    });
                                                   /* var itemMb= Ext.getCmp('maxMbOutUp');

                                                    itemMb.setValue(selectedOutput.TypeInformation.MaximumMegabytes);*/

                                                    store4.add([newRecord]);
                                                }
                                        
                                            }
                                            
                                        }

                                        else if(defaultDataObj[item.getId()]== 'LiteralOutput'){
                                            if(!Ext.getCmp('literal')){
                                                var UOMs = Ext.data.Record.create([{
                                                    name:'uom',
                                                    type:'string'
                                                }]);

                                                var store5 = new Ext.data.Store({
                                                    fields: UOMs
                                                });

                                                var editor5 = new Ext.ux.grid.RowEditor({
                                                    saveText: 'Update'
                                                });

                                                var grid5 = new Ext.grid.GridPanel({
                                                    id:'UomgridOut',
                                                    anchor:'96%', 
                                                    store: store5,
                                                    frame: true,
                                                    title: "UOMs Supported (Default UOM is the selected)",
                                                    autoScroll: true,
                                                    height:BrowserDetect.getHeight(35),
                                                    region:'center',
                                                    margins: '0 5 5 5',
                                                    autoExpandColumn: 'name',
                                                    plugins: [editor5],
                                                    tbar: [{
                                                        iconCls: 'silk-add',
                                                        text: 'Add ',
                                                        handler: function(){
                                                            var uOut = new UOMs({
                                                                uom: 'meters'
                                                            });
                                                            editor5.stopEditing();
                                                            store5.insert(0, uOut);
                                                            grid5.getView().refresh();
                                                            grid5.getSelectionModel().selectRow(0);
                                                            editor5.startEditing(0);
                                                        }
                                                    },{
                                                        ref: '../removeBtn',
                                                        iconCls: 'silk-delete' ,
                                                        text: 'Remove',
                                                        disabled: true,
                                                        handler: function(){
                                                            editor5.stopEditing();
                                                            var s = grid5.getSelectionModel().getSelections();
                                                            for(var i = 0, r; r = s[i]; i++){
                                                                store5.remove(r);
                                                            }
                                                        }
                                                    }],

                                                    columns: [
                                                    new Ext.grid.RowNumberer(),
                                                    {
                                                        id: 'name',
                                                        header: 'UOM',
                                                        dataIndex: 'uom',
                                                        editor: {
                                                            xtype: 'textfield',
                                                            allowBlank: false
                                                        }
                                                    }
                                                    ]

                                                });


                                                grid5.getSelectionModel().on('selectionchange', function(sm){
                                                    grid5.removeBtn.setDisabled(sm.getCount() < 1);
                                                });


                                                var LiteralOut = new Ext.form.FieldSet({
                                                    id: 'literal',
                                                    title:'LiteralOutput',
                                                    collapsible: true ,
                                                    autoScroll: true,
                                                    listeners:{
                                                        'expand': function(){
                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                            if(fieldSetType)
                                                                fieldSetType.collapse();
                                                        },
                                                        'beforerender': function(){
                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                            if(fieldSetType)
                                                                fieldSetType.collapse();
                                                        }
                                                    }, 
                                                    items:[{
                                                        layout:'column' ,
                                                        items:[{
                                                            columnWidth:.5,
                                                            layout: 'form',
                                                            items:[{
                                                                xtype:'combo',
                                                                id:'datacomboOut' ,
                                                                fieldLabel: 'DataType',
                                                                store: new Ext.data.SimpleStore({
                                                                    id: 'Ldatatype',
                                                                    fields:['datatype'],
                                                                    data:  [
                                                                    ['short'],
                                                                    ['int'],
                                                                    ['double'],
                                                                    ['float'],
                                                                    ['String'],
                                                                    ['boolean'],
                                                                    ['long'],
                                                                    ['byte']
                                                                    ]
                                                                }),
                                                                editable: true,
                                                                displayField:'datatype',
                                                                triggerAction: 'all',
                                                                mode: 'local',
                                                                emptyText:'Select a type...',
                                                                name: 'dataType',
                                                                anchor: '69%'  
                                                            }]
                                                        }
                                                        ,{
                                                            columnWidth: .5,
                                                            layout: 'form',
                                                            items:[grid5]
                                                        }

                                                        ]
                                                    }

                                                    ]
                                                });

                                                formPanelOut.add(LiteralOut);

                                                var comboDatatype= Ext.getCmp('datacomboOut');
                                                var itemDatatype=describe.getOutputByIdentifier(record.data.identifier)
                                                comboDatatype.setValue(itemDatatype.TypeInformation.DataType);


                                                var recordUom = Ext.data.Record.create(
                                                    new Output().getRecordType());

                                                var itemUom=describe.getOutputByIdentifier(record.data.identifier);

                                                for(i=0; i<itemUom.TypeInformation.UOMsSupported.length; i++){
                                                    var newUom= new recordUom({

                                                        uom:itemUom.TypeInformation.UOMsSupported[i] // dataIndex: recordType

                                                    });
                                                    store5.add([newUom]);
                                                }
                                            
                                            }

                                        }

                                        else if(defaultDataObj[item.getId()]== 'BoundingBoxOutput'){
                                            if(!Ext.getCmp('bbox')){
                                                var BboxUpOut = new Ext.form.FieldSet({
                                                    id: 'bbox',
                                                    labelAlign: 'top',
                                                    collapsible: true ,
                                                    listeners:{
                                                        'expand': function(){
                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                            if(fieldSetType)
                                                                fieldSetType.collapse();
                                                        },
                                                        'beforerender': function(){
                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                            if(fieldSetType)
                                                                fieldSetType.collapse();
                                                        }
                                                    }, 
                                                    title: 'BBOX Output Default/Supported CRS' ,
                                                    items:[{
                                                        xtype:'textfield',
                                                        id:'crsUpOut' ,
                                                        allowBlank: false,
                                                        fieldLabel: "CRS  [ List separated by ','   (Default CRS is the first) ]",
                                                        value:'EPSG' ,
                                                        name: 'title',
                                                        anchor:'48%'

                                                    }]
                                                });

                                                formPanelOut.add(BboxUpOut);
                                      
                                                var itemBox= Ext.getCmp('crsUpOut');
                                                var labelBox=describe.getOutputByIdentifier(record.data.identifier);
                                                itemBox.setValue(labelBox.TypeInformation.CRSSupported);
                                            
                                            }
                                        }

                                   
                                    }, this);

                                    var labelValue=describe.getOutputByIdentifier(record.data.identifier);
                                
                                    var labelAbs= Ext.getCmp('absOut');
                                    labelAbs.setValue(labelValue.Abstract);

                                    var labelMeta= Ext.getCmp('metaOut');
                                    labelMeta.setValue(labelValue.Metadata);
                                
                                    formPanelOut.doLayout();
                                
                                    var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                    if(fieldSetType)
                                        fieldSetType.expand();

                                },
                                items: new Ext.FormPanel({

                                    id: 'updateOutForm',
                                    labelWidth: 75,
                                    frame:true,
                                    bodyStyle: 'padding: 5px 5px 0' ,
                                    fbar: [{
                                        iconCls: 'silk-accept',
                                        text: 'Update',
                                        handler: function(){

                                            var formValidation = Ext.getCmp('updateOutForm').getForm();
                                            var comboTypeOut = Ext.getCmp('Type');
                                            var outputType = comboTypeOut.getValue();
                                            var typeCheck=true;
                                            var validateTypeMsg="";
                                            switch (outputType){
                                                case "ComplexOutput":
                                                    var gridFormat=Ext.getCmp('tabOutUp');
                                                    typeCheck=gridFormat.getStore().getCount() > 0;
                                                    if(!typeCheck)
                                                        validateTypeMsg="Please insert a Complex Output format";
                                                    break;
                                            }

                                            if(formValidation.isValid() && typeCheck){

                                                var  outputObject= new Output(outputType);

                                                var listForm=new Array('identifier','Title','metaOut','absOut');
                                                var listObjectForm=new Array('Identifier','Title','Metadata','Abstract');
                                                for(var i=0; i<listForm.length; i++){
                                                    outputObject[listObjectForm[i]]=Ext.getCmp(listForm[i]).getValue();
                                           
                                                }

                                                if(outputType=='ComplexOutput'){
                                                    var complexDataGridFormat = Ext.getCmp('tabOutUp');
                                                    var formatStore=complexDataGridFormat.getStore();
                                                    var currentRecord=null;

                                                    //outputObject.TypeInformation.setMaximumMegabytes(Ext.getCmp('maxMbOutUp').getValue());

                                                    for(i=0; i<formatStore.getCount(); i++){
                                                        currentRecord=formatStore.getAt(i);
                                                        outputObject.TypeInformation.addFormat(currentRecord.get('mime'),
                                                            currentRecord.get('encoding'),
                                                            currentRecord.get('schema'));
                                                    }

                                                    var selectionModel= complexDataGridFormat.getSelectionModel();
                                                    var recordSelected = selectionModel.getSelected();

                                                    if(recordSelected ==null){
                                                        outputObject.TypeInformation.setDefault(formatStore.getAt(0).get('mime'));
                                                    }
                                                    else{
                                                        outputObject.TypeInformation.setDefault(recordSelected.get('mime'));
                                                    }
                                                }

                                                else if(outputType==  'LiteralOutput'){
                                                    if(Ext.getCmp('datacomboOut').getValue())
                                                        outputObject.TypeInformation.setDataType(Ext.getCmp('datacomboOut').getValue());
                                           
                                                    var UOMgrid = Ext.getCmp('UomgridOut');

                                                    var uomStore= UOMgrid.getStore();

                                                    var currentUOM=null;

                                                    if(uomStore.getCount() >0){
                                                        for(i=0; i<uomStore.getCount(); i++){
                                                            currentUOM=uomStore.getAt(i);
                                                            outputObject.TypeInformation.addUOM(currentUOM.get('uom'));

                                                        }

                                                        var uomSelectionModel = UOMgrid.getSelectionModel();
                                                        var uomSelected = uomSelectionModel.getSelected();


                                                        if(uomSelected ==null){
                                                            outputObject.TypeInformation.setDefaultUOM(uomStore.getAt(0).get('uom'));
                                                        }
                                                        else{
                                                            outputObject.TypeInformation.setDefaultUOM(uomSelected.get('uom'));
                                                        }
                                                    }
                                                }

                                                else if(outputType=='BoundingBoxOutput'){

                                                    outputObject.TypeInformation.setCRSList(Ext.getCmp('crsUpOut').getValue());

                                                }
                                                else{
                                                    alert('select Output Type');
                                                }

                                                describe.updateOutput(outputObject);
                                                var outputGrid= Ext.getCmp('outputGrid');
                                                var outputSelectionModel = outputGrid.getSelectionModel();
                                                var outputSelected = outputSelectionModel.getSelected();

                                                outputSelected.set('Title',outputObject.Title);
                                                outputSelected.set('Type',outputObject.Type);

                                                updateWindow.destroy(true);
                                                updateWindow=null;
                            
                                                setDescribeValueOnEditArea();
                                        
                                            }else{
                                                if(typeCheck){
                                                    Ext.Msg.show({
                                                        title:'Validate Processing Output',
                                                        msg: "Please insert all mandatory parameters.",
                                                        buttons: Ext.Msg.OK,
                                                        icon: Ext.MessageBox.WARNING
                                                    });
                                                    var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                    if(fieldSetType)
                                                        fieldSetType.expand();  
                                                }else
                                                    Ext.Msg.show({
                                                        title:'Validate Processing Output',
                                                        msg:validateTypeMsg,
                                                        buttons: Ext.Msg.OK,
                                                        icon: Ext.MessageBox.WARNING
                                                    });
                                            }

                                        }
                                    },'->',{
                                        text: 'Cancel',
                                        iconCls: 'silk-delete',
                                        handler: function(){
                                            updateWindow.destroy(true);                   
                                            updateWindow=null;

                                        }
                                    }]
                                    ,
                                    items:[{},
                                    [commonOutputUpdate],
                                    {
                                        xtype: 'fieldset',
                                        collapsible: true,
                                        height: 80 ,
                                        title: 'Output Type' ,
                                        labelAlign: 'top',
                                        items:[{
                                            xtype: 'combo',
                                            id: 'Type',
                                            fieldLabel: 'OutputFormChoice',
                                            store: new Ext.data.SimpleStore({
                                                fields:['outputType'],
                                                data:  [
                                                ['ComplexOutput'],
                                                ['LiteralOutput'],
                                                ['BoundingBoxOutput']
                                                ]
                                            }),
                                            listeners:{

                                                'select': function(){

                                                    var fp = Ext.getCmp('updateOutForm');
                                                    var comboOut = this.getValue() ;

                                                    var complexOut = Ext.getCmp('complexgrid');

                                                    if(complexOut){
                                                        fp.remove(complexOut);
                                                    }
                                                    var BoundingboxOut = Ext.getCmp('bbox');
                                                    if(BoundingboxOut){
                                                        fp.remove(BoundingboxOut);
                                                    }
                                                    var literalDataOut = Ext.getCmp('literal');
                                                    if(literalDataOut){
                                                        fp.remove(literalDataOut);
                                                    }

                                                    switch(comboOut)
                                                    {
                                                        case  'ComplexOutput'     :
                                                            
                                                            if(! Ext.getCmp('complexgrid')){
                                                                var Inputs = Ext.data.Record.create([{
                                                                    name: 'mime',
                                                                    type: 'string'
                                                                }, {
                                                                    name: 'encoding',
                                                                    type: 'string'
                                                                }, {
                                                                    name: 'schema',
                                                                    type: 'string'
                                                                }]);

                                                                var store4 = new Ext.data.Store({
                                                                    fields: Inputs
                                                                });

                                                                var editor4 = new Ext.ux.grid.RowEditor({
                                                                    saveText: 'Update'
                                                                });


                                                                var grid4 = new Ext.grid.GridPanel({
                                                                    id: 'tabOutUp',
                                                                    store: store4,
                                                                    autoScroll: true,
                                                                    frame: true,
                                                                    title: "Complex Output supported Format (Default Format is the selected)",
                                                                    height:BrowserDetect.getHeight(35),
                                                                    region:'center',
                                                                    margins: '0 5 5 5',
                                                                    autoExpandColumn: 'name',
                                                                    plugins: [editor4],
                                                                    tbar: [{
                                                                        iconCls: 'silk-add',
                                                                        text: 'Add ',
                                                                        handler: function(){
                                                                            var i = new Inputs({
                                                                                mime: 'text/xml',
                                                                                encoding: 'UTF-8',
                                                                                schema:'http://foo.bar/gml/polygon.xsd'
                                                                            });
                                                                            editor4.stopEditing();
                                                                            store4.insert(0, i);
                                                                            grid4.getView().refresh();
                                                                            grid4.getSelectionModel().selectRow(0);
                                                                            editor4.startEditing(0);
                                                                        }
                                                                    },{
                                                                        ref: '../removeBtn',
                                                                        iconCls: 'silk-delete' ,
                                                                        text: 'Remove',
                                                                        disabled: true,
                                                                        handler: function(){
                                                                            editor4.stopEditing();
                                                                            var s = grid4.getSelectionModel().getSelections();
                                                                            for(var i = 0, r; r = s[i]; i++){
                                                                                store4.remove(r);
                                                                            }
                                                                        }
                                                                    }],

                                                                    columns: [
                                                                    new Ext.grid.RowNumberer(),
                                                                    {
                                                                        id: 'name',
                                                                        header: 'Mime Type',
                                                                        dataIndex: 'mime',
                                                                        width: BrowserDetect.getWidth(15),
                                                                        sortable: true,
                                                                        editor: {
                                                                            xtype: 'textfield',
                                                                            allowBlank: false
                                                                        }
                                                                    },{
                                                                        header: 'Encoding',
                                                                        dataIndex: 'encoding',
                                                                        width: BrowserDetect.getWidth(15),
                                                                        sortable: true,
                                                                        editor: {
                                                                            xtype: 'textfield',
                                                                            allowBlank: true

                                                                        }
                                                                    },{
                                                                        header: 'Schema',
                                                                        dataIndex: 'schema',
                                                                        width: BrowserDetect.getWidth(35),
                                                                        sortable: true,
                                                                        editor: {
                                                                            xtype: 'textfield',
                                                                            allowBlank: true
                                                                        }
                                                                    }]
                                                                });
                                                                grid4.getSelectionModel().on('selectionchange', function(sm){
                                                                    grid4.removeBtn.setDisabled(sm.getCount() < 1);
                                                                });

                                                                /*var maxMb = {
                                                                    xtype:'numberfield',
                                                                    id:'maxMbOutUp',
                                                                    fieldLabel: 'Maximum Megabytes',
                                                                    name: 'maxMegabytes',
                                                                    minValue: 0

                                                                };*/
                                                                var ComplexGridOut = new Ext.form.FieldSet({
                                                                    id:'complexgrid',
                                                                    labelAlign: 'left',
                                                                    autoScroll: true,
                                                                    collapsible: true ,
                                                                    listeners:{
                                                                        'expand': function(){
                                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                                            if(fieldSetType)
                                                                                fieldSetType.collapse();
                                                                        },
                                                                        'beforerender': function(){
                                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                                            if(fieldSetType)
                                                                                fieldSetType.collapse();
                                                                        }
                                                                    }, 
                                                                    title: 'Complex Output' ,
                                                                    items:[/*maxMb*/,grid4]

                                                                });
                                                                fp.add(ComplexGridOut);
                                                                fp.doLayout();
                                                            }
                                                            
                                                            break;

                                                        case  'BoundingBoxOutput' :
                                                            if(! Ext.getCmp('bbox')){
                                                                var BboxOut = new Ext.form.FieldSet({
                                                                    id: 'bbox',
                                                                    labelAlign: 'top',
                                                                    collapsible: true ,
                                                                    listeners:{
                                                                        'expand': function(){
                                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                                            if(fieldSetType)
                                                                                fieldSetType.collapse();
                                                                        },
                                                                        'beforerender': function(){
                                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                                            if(fieldSetType)
                                                                                fieldSetType.collapse();
                                                                        }
                                                                    }, 
                                                                    title: 'BBOX Output Default/Supported CRS' ,
                                                                    items:[{
                                                                        xtype:'textfield',
                                                                        id:'crsUpOut',
                                                                        allowBlank: false,
                                                                        fieldLabel: "CRS  [ List separated by ','   (Default CRS is the first) ]",
                                                                        value:' EPSG:4326' ,
                                                                        name: 'title',
                                                                        anchor:'48%'
                                                                    }]
                                                                });
                                                                fp.add(BboxOut);
                                                                fp.doLayout();
                                                            }
                                                            break;

                                                        case  'LiteralOutput'     :
                                                            if(! Ext.getCmp('literal')){
                                                                var UOMs = Ext.data.Record.create([{
                                                                    name:'uom',
                                                                    type:'string'
                                                                }]);

                                                                var store5 = new Ext.data.Store({

                                                                    fields: UOMs

                                                                });

                                                                var editor5 = new Ext.ux.grid.RowEditor({
                                                                    saveText: 'Update'
                                                                });

                                                                var grid5 = new Ext.grid.GridPanel({
                                                                    id:'UomgridOut',
                                                                    anchor:'96%',
                                                                    store: store5,
                                                                    frame: true,
                                                                    autoScroll: true,
                                                                    height:BrowserDetect.getHeight(35),
                                                                    title: "UOMs supported (Default UOM is the selected)",
                                                                    region:'center',
                                                                    margins: '0 5 5 5',
                                                                    autoExpandColumn: 'name',
                                                                    plugins: [editor5],
                                                                    tbar: [{
                                                                        iconCls: 'silk-add',
                                                                        text: 'Add ',
                                                                        handler: function(){
                                                                            var uOut = new UOMs({
                                                                                uom: 'meters'
                                                                            });
                                                                            editor5.stopEditing();
                                                                            store5.insert(0, uOut);
                                                                            grid5.getView().refresh();
                                                                            grid5.getSelectionModel().selectRow(0);
                                                                            editor5.startEditing(0);
                                                                        }
                                                                    },{
                                                                        ref: '../removeBtn',
                                                                        iconCls: 'silk-delete' ,
                                                                        text: 'Remove',
                                                                        disabled: true,
                                                                        handler: function(){
                                                                            editor5.stopEditing();
                                                                            var s = grid5.getSelectionModel().getSelections();
                                                                            for(var i = 0, r; r = s[i]; i++){
                                                                                store5.remove(r);
                                                                            }
                                                                        }
                                                                    }],

                                                                    columns: [
                                                                    new Ext.grid.RowNumberer(),
                                                                    {
                                                                        id: 'name',
                                                                        header: 'UOM',
                                                                        dataIndex: 'uom',
                                                                        editor: {
                                                                            xtype: 'textfield',
                                                                            allowBlank: false
                                                                        }
                                                                    }
                                                                    ]

                                                                });


                                                                grid5.getSelectionModel().on('selectionchange', function(sm){
                                                                    grid5.removeBtn.setDisabled(sm.getCount() < 1);
                                                                });


                                                                var LiteralOut = new Ext.form.FieldSet({
                                                                    id: 'literal',
                                                                    title:'LiteralOutput',
                                                                    collapsible: true ,
                                                                    autoScroll: true,
                                                                    listeners:{
                                                                        'expand': function(){
                                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                                            if(fieldSetType)
                                                                                fieldSetType.collapse();
                                                                        },
                                                                        'beforerender': function(){
                                                                            var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                                            if(fieldSetType)
                                                                                fieldSetType.collapse();
                                                                        }
                                                                    }, 
                                                                    items:[{
                                                                        layout:'column' ,
                                                                        items:[{
                                                                            columnWidth:.5,
                                                                            layout: 'form',
                                                                            items:[{
                                                                                xtype:'combo',
                                                                                id:'datacomboOut' ,
                                                                                fieldLabel: 'DataType',
                                                                                store: new Ext.data.SimpleStore({
                                                                                    id: 'Ldatatype',
                                                                                    fields:['datatype'],
                                                                                    data:  [
                                                                                    ['short'],
                                                                                    ['int'],
                                                                                    ['double'],
                                                                                    ['float'],
                                                                                    ['String'],
                                                                                    ['boolean'],
                                                                                    ['long'],
                                                                                    ['byte']
                                                                                    ]
                                                                                }),
                                                                                editable: true,
                                                                                displayField:'datatype',
                                                                                triggerAction: 'all',
                                                                                mode: 'local',
                                                                                emptyText:'Select a type...',
                                                                                name: 'dataType',
                                                                                anchor: '69%'  
                                                                            }]
                                                                        }
                                                                        ,{
                                                                            columnWidth: .5,
                                                                            layout: 'form',
                                                                            items:[grid5]
                                                                        }

                                                                        ]
                                                                    }

                                                                    ]
                                                                });
                                                                fp.add(LiteralOut);
                                                                fp.doLayout();
                                                                break;
                                                            }  
                                                        default                 :
                                                            alert('select output');
                                                    }

                                                }
                                            },

                                            displayField:'outputType',
                                            triggerAction: 'all',
                                            mode: 'local',
                                            emptyText:'Select a type...',
                                            name: 'outputform',
                                            anchor:'48%'

                                        }]
                                    }] //,commonOutput]
                                })


                            })
                        updateWindow.show();
                        updateWindow.setOutDefaultValue(record.data);
                    }
                },
                tbar:[{
                    text:'Add Output',
                    iconCls: 'silk-add',
                    handler: function(){
                        if(!updateWindow)
                            updateWindow = new Ext.Window({
                                layout: 'fit',
                                title: "New Processing Output",
                                height:BrowserDetect.getHeight(98),
                                width: BrowserDetect.getWidth(85),
                                frame: true ,
                                listeners:{
                                    "destroy": function(){
                                        updateWindow=null;
                                    }
                                },
                                closeAction: "destroy",
                                items: new Ext.FormPanel({
                                    id:'outputForm' ,
                                    labelWidth: 75,
                                    frame:true,
                                    bodyStyle:'padding:5px 5px 0' ,
                                    fbar: [{
                                        iconCls: 'icon-save',
                                        text: 'Save',
                                        handler: function(){

                                            var formValidation = Ext.getCmp('outputForm').getForm();
                                            var comboTypeOut = Ext.getCmp('Type');
                                            var outputType = comboTypeOut.getValue();
                                            var typeCheck=true;
                                            var validateTypeMsg="";
                                            switch (outputType){
                                                case "ComplexOutput":
                                                    var gridFormat=Ext.getCmp('tab4');
                                                    typeCheck=gridFormat.getStore().getCount() > 0;
                                                    if(!typeCheck)
                                                        validateTypeMsg="Please insert a Complex Output format";
                                                    break;

                                            }

                                            if(formValidation.isValid() && typeCheck){
                                                //  qui creo un' istanza di OutputObject
                                                var  outputObject= new Output(outputType);

                                                var listForm=new Array('identifier','Title','metaOut','absOut');
                                                var listObjectForm=new Array('Identifier','Title','Metadata','Abstract');
                                                for(var i=0; i<listForm.length; i++){
                                                    outputObject[listObjectForm[i]]=Ext.getCmp(listForm[i]).getValue();
                                        
                                                }

                                                if(outputType=='ComplexOutput'){
                                                    var complexDataGridFormat = Ext.getCmp('tab4');
                                                    var formatStore=complexDataGridFormat.getStore();
                                                    var currentRecord=null;

                                                   // outputObject.TypeInformation.setMaximumMegabytes(Ext.getCmp('maxMbOut').getValue());

                                                    for(i=0; i<formatStore.getCount(); i++){
                                                        currentRecord=formatStore.getAt(i);
                                                        outputObject.TypeInformation.addFormat(currentRecord.get('mime'),
                                                            currentRecord.get('encoding'),
                                                            currentRecord.get('schema'));
                                                    }

                                                    var selectionModel= complexDataGridFormat.getSelectionModel();
                                                    var recordSelected = selectionModel.getSelected();

                                                    if(recordSelected ==null){
                                                        outputObject.TypeInformation.setDefault(formatStore.getAt(0).get('mime'));
                                                    }
                                                    else{
                                                        outputObject.TypeInformation.setDefault(recordSelected.get('mime'));
                                                    }
                                                }

                                                else if(outputType==  'LiteralOutput'){
                                                    if(Ext.getCmp('datacomboOut').getValue())
                                                        outputObject.TypeInformation.setDataType(Ext.getCmp('datacomboOut').getValue());

                                                    var UOMgrid = Ext.getCmp('UomgridOut');

                                                    var uomStore= UOMgrid.getStore();

                                                    var currentUOM=null;

                                                    if(uomStore.getCount() >0){
                                                        for(i=0; i<uomStore.getCount(); i++){
                                                            currentUOM=uomStore.getAt(i);
                                                            outputObject.TypeInformation.addUOM(currentUOM.get('uom'));

                                                        }

                                                        var uomSelectionModel = UOMgrid.getSelectionModel();
                                                        var uomSelected = uomSelectionModel.getSelected();


                                                        if(uomSelected ==null){
                                                            outputObject.TypeInformation.setDefaultUOM(uomStore.getAt(0).get('uom'));
                                                        }
                                                        else{
                                                            outputObject.TypeInformation.setDefaultUOM(uomSelected.get('uom'));
                                                        }
                                                    }
                                                }

                                                else if(outputType=='BoundingBoxOutput'){

                                                    outputObject.TypeInformation.setCRSList(Ext.getCmp('crsOut').getValue());

                                                }
                                                else{
                                                    alert('select Output Type');
                                                }

                                                describe.insertOutput(outputObject);
                                                var newrecordtypeOut = Ext.data.Record.create(
                                                    new Output().getRecordType());
                                                var newRecord= new newrecordtypeOut({
                                                    identifier:outputObject.Identifier,
                                                    Type:outputObject.Type,
                                                    Title:outputObject.Title

                                                });
                                                dsOut.add([newRecord]);
                                                updateWindow.destroy();
                                                setDescribeValueOnEditArea();

                                            }else{
                                                if(typeCheck){
                                                    Ext.Msg.show({
                                                        title:'Validate Processing Output',
                                                        msg: "Please insert all mandatory parameters.",
                                                        buttons: Ext.Msg.OK,
                                                        icon: Ext.MessageBox.WARNING
                                                    });
                                                    var fieldSetType=Ext.getCmp("commonOutputFiledSet");
                                                    if(fieldSetType)
                                                        fieldSetType.expand();
                                                }else
                                                    Ext.Msg.show({
                                                        title:'Validate Processing Output',
                                                        msg:validateTypeMsg,
                                                        buttons: Ext.Msg.OK,
                                                        icon: Ext.MessageBox.WARNING
                                                    });
                                            }
                                        }
                                    },'->',{
                                        text: 'Cancel',
                                        handler: function(){
                                            updateWindow.destroy();
                                            updateWindow=null;
                                        },
                                        iconCls: 'silk-delete'
                                    }],
                                    items:[
                                    commonOutput,
                                    outputType
                                    ]


                                })
                            })
                        updateWindow.show();
                    }
                },'-',{
                    text: 'Remove Output',
                    iconCls: 'silk-delete',
                    handler: function(){
                        OutputGrid= Ext.getCmp('outputGrid');
                        var outputSelectionModel = OutputGrid.getSelectionModel();
                        var outputSelected = outputSelectionModel.getSelected();

                        if(outputSelected== null){
                            Ext.Msg.show({
                                title:'Remove Processing Output',
                                msg: "No output selected",
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.WARNING
                            }); 
                        }else{
                            Ext.Msg.show({
                                title:'Remove Processing Output',
                                msg: "Do you really want to delete the selected Processing Output?",
                                buttons: Ext.Msg.YESNOCANCEL,
                                icon: Ext.MessageBox.QUESTION,
                                fn: function(buttonId,text,opt){
                                    if(buttonId == "yes"){
                                        dsOut.remove(outputSelected);
                                        describe.removeOutput(outputSelected.data.identifier);
                                        setDescribeValueOnEditArea();
                                    }
                                }    
                            });  
                        } 
                    }
                }
                ],
                anchor:'98%',
                ds: dsOut,
                cm: colModel,
                height:BrowserDetect.getHeight(37),
                border: true
            }],
            fbar: [{
                iconCls: 'silk-accept',
                text: 'Create',
                handler: function(){
                    var checkMandatary=true;
                    checkMandatary=checkMandatary && describe.inputs.length > 0;
                    checkMandatary=checkMandatary && describe.outputs.length > 0;
                    checkMandatary= checkMandatary && 
                    newProcessingFormPanel.getForm().isValid();
                    
                    if(checkMandatary)   
                        parseWPSDescribeProcessingRequest(); 
                    else{
                        if(describe.inputs.length <=0){
                            Ext.Msg.show({
                                title:'Create new WPS Processing ',
                                msg: "Please insert at least one Processing Input.",
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.WARNING
                            });   
                        }else
                        if(describe.outputs.length <=0){
                            Ext.Msg.show({
                                title:'Create new WPS Processing ',
                                msg: "Please insert at least one Processing Output.",
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.WARNING
                            });
                        }else
                            Ext.Msg.show({
                                title:'Create new WPS Processing ',
                                msg: "Please insert all mandatory Processing parameters.",
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.WARNING
                            });
                    }  
                }
            },'->',{
                iconCls: 'silk-delete',
                text: /*'Reset'*/'Close',
                handler: function(){
                    // Ext.getCmp("form").getForm().reset();
                    describeProcessingEditAreaWindow.destroy();
                }

            }]
        });

        newProcessingPanel = new Ext.Panel({
            id:'west' ,
            title: 'WPS Processing',
            region: 'west',
            split: true,
            layout: 'fit',
            height:BrowserDetect.getHeight(90),
            width: BrowserDetect.getWidth(70),
            listeners:{
                "resize": function(){
                    newProcessingFormPanel.setSize(this.getInnerWidth(),
                        this.getInnerHeight());
                    newProcessingFormPanel.doLayout();
                }
            },
            bodyStyle : {
                background: backgroundColor
            },
            collapsible: true,
            items:[newProcessingFormPanel]
        });


        var describeProcessEditAreaInterface=new DescribeProcessEditAreaInterface();
        
        createWPSProccesingIO=describeProcessEditAreaInterface.formInterface;

        describeProcessingEditAreaWindow = new WebGIS.Panel.WindowInterfacePanel({
            title: 'Create a new WPS Processing',
            border: false,
            id: 'insertDescribeWizardWindow',
            animCollapse : true,
            autoScroll : true,
            resizable : false,
            collapsible: false,
            maximizable: false,
            loadingBarImg: "images/loader1.gif",
            loadingBarImgPadding: 90,
            loadingMessage: "Loading... Please Wait...",
            loadingMessagePadding: 60,
            loadingMessageColor: "black",
            loadingPanelColor: "#d9dce0",
            loadingPanelDuration: 1000,
            layout: 'border',
            maximized: true,
            listeners:{
                hide: function(){
                    this.destroy(true);
                }
            },
            width: BrowserDetect.getWidth(90),
            height: BrowserDetect.getHeight(90),
            closeAction:'close',
            items: [newProcessingPanel,
            new Ext.Panel({
                title: 'Describe Process',
                region: 'center',
                split: true,
                listeners:{
                    "resize": function(){
                        var iframe=Ext.getCmp("processDescriptionContent");
                        var size=this.getInnerWidth();
                        if(iframe){
                            iframe.setSize(size/1.07, eval(iframe.heigthAuto));
                            editArea = document.getElementById('processDescriptionContentTextAreaIframe');
                            if(editArea){
                                editAreaDoc = editArea.contentWindow.document;
                                setTimeout("editAreaDoc.execute('resync_highlight', true)", 500);
                            }  
                        }
                        
                    }
                },
                height:BrowserDetect.getHeight(90),
                width: BrowserDetect.getWidth(30),
                html: "<div id='describeProcessEditArea'/>"
            })]

        });
        describeProcessingEditAreaWindow.show();
        describeProcessEditAreaInterface.render("describeProcessEditArea");
        
    //test();
    })
})


function setDescribeValueOnEditArea(){
    var serializer = new XMLSerializer();
    var editArea = document.getElementById("processDescriptionContentTextAreaIframe");
    var editAreaDoc = editArea.contentWindow.document;
    
    var describeProcessText=XML(serializer.serializeToString(describe.getDescribeProcess())).toXMLString();
    editAreaDoc.setValue(replaceAll(describeProcessText, "ProcessDescriptions","wps:ProcessDescriptions"));
}


function setDescribeWizardFromXML(xmlTextValue){
    var newRecord, newRecordOutputType,newRecordInputType;
    describe=new DescribeObject(xmlTextValue);
    
    var idArray=["identifier","Abs","Title","Meta"];
    var describeObjectID=["Identifier","Title","Abstract","Metadata"];
    var cmp;
    
    alert(describe.Identifier);
    for(var i=0; i<idArray.length;i++){
        cmp=Ext.getCmp(idArray[i]);

        cmp.setValue(describe[describeObjectID[i]]);
    }
    
    ds.removeAll();
    
    dsOut.removeAll();
    newRecordInputType = Ext.data.Record.create(new Input().getRecordType());
    for(i=0; i<describe.inputs.length;i++){
        
        newRecord= new newRecordInputType({
            identifier:describe.inputs[i].Identifier,     
            Type:describe.inputs[i].Type,
            Title:describe.inputs[i].Title
        });
                                    
        ds.add([newRecord]);
    }
    
    newRecordOutputType = Ext.data.Record.create(new Output().getRecordType());
    for(i=0; i<describe.outputs.length;i++){
        newRecord= new newRecordOutputType({
            identifier:describe.outputs[i].Identifier,     
            Type:describe.outputs[i].Type,
            Title:describe.outputs[i].Title
        });
                                    
        dsOut.add([newRecord]);
    }
}


function loadDescribeWizard(){

    /*  var editArea = document.getElementById("processDescriptionContentTextAreaIframe");
    var editAreaDoc = editArea.contentWindow.document;
    setDescribeWizardFromXML(editAreaDoc.getValue());*/
    if(newProcessingPanel)
        newProcessingPanel.collapse();

}