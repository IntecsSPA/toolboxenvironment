/**
 *
 */

DeployedServicesGrid = function(limitColumns){

    function italic(value){
        return '<i>' + value + '</i>';
    }

    function change(val){
        if(val > 0){
            return '<span style="color:green;">' + val + '</span>';
        }else if(val < 0){
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    }

    function pctChange(val){
        if(val > 0){
            return '<span style="color:green;">' + val + '%</span>';
        }else if(val < 0){
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    }


    var columns = [
        {id:'service_name',header: "Service", width: 160, sortable: true, dataIndex: 'service_name'},
        {header: "Status", width: 75, sortable: true, renderer: Ext.util.Format.trim, dataIndex: 'service_status'},
        
    ];

    // allow samples to limit columns
    if(limitColumns){
        var cs = [];
        for(var i = 0, len = limitColumns.length; i < len; i++){
            cs.push(columns[limitColumns[i]]);
        }
        columns = cs;
    }

    DeployedServicesGrid.superclass.constructor.call(this, {
        store: new Ext.data.Store({
            reader: new Ext.data.ArrayReader({}, [
                   {name: 'service_name'},
                   {name: 'service_status', type: 'string'}
              ]),
            data: [
                ['testService','Running']
            ]
        }),
        columns: columns,
        autoExpandColumn: 'service_name',
        height:250,
        width:600
    });


}

Ext.extend(DeployedServicesGrid, Ext.grid.GridPanel);

RunningInstancesGrid = function(limitColumns){

    function italic(value){
        return '<i>' + value + '</i>';
    }

    function change(val){
        if(val > 0){
            return '<span style="color:green;">' + val + '</span>';
        }else if(val < 0){
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    }

    function pctChange(val){
        if(val > 0){
            return '<span style="color:green;">' + val + '%</span>';
        }else if(val < 0){
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    }


    var columns = [
        {id:'instance_id',header: "Instance", width: 160, sortable: true, dataIndex: 'instance_id'},
        {header: "Service", width: 75, sortable: true, renderer: Ext.util.Format.trim, dataIndex: 'service_name'},
        {header: "Operation", width: 75, sortable: true, renderer: Ext.util.Format.trim, dataIndex: 'operation_name'},
    ];

    // allow samples to limit columns
    if(limitColumns){
        var cs = [];
        for(var i = 0, len = limitColumns.length; i < len; i++){
            cs.push(columns[limitColumns[i]]);
        }
        columns = cs;
    }

    RunningInstancesGrid.superclass.constructor.call(this, {
        store: new Ext.data.Store({
            reader: new Ext.data.ArrayReader({}, [
                   {name: 'instance_id'},
                   {name: 'service_name', type: 'string'},
                   {name: 'operation_name', type: 'string'}
              ]),
            data: [
                ['tbx00001','testService','test']
            ]
        }),
        columns: columns,   
        height:250,
        width:600
    });


}

Ext.extend(RunningInstancesGrid, Ext.grid.GridPanel);

ServicesStatusGrid = function(limitColumns){

    function italic(value){
        return '<i>' + value + '</i>';
    }

    function change(val){
        if(val > 0){
            return '<span style="color:green;">' + val + '</span>';
        }else if(val < 0){
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    }

    function pctChange(val){
        if(val > 0){
            return '<span style="color:green;">' + val + '%</span>';
        }else if(val < 0){
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    }


    var columns = [
        {id:'instance_id',header: "Instance", width: 160, sortable: true, dataIndex: 'instance_id'},
        {header: "Service", width: 75, sortable: true, renderer: Ext.util.Format.trim, dataIndex: 'service_name'},
        {header: "Operation", width: 75, sortable: true, renderer: Ext.util.Format.trim, dataIndex: 'operation_name'},
    ];

    // allow samples to limit columns
    if(limitColumns){
        var cs = [];
        for(var i = 0, len = limitColumns.length; i < len; i++){
            cs.push(columns[limitColumns[i]]);
        }
        columns = cs;
    }

    ServicesStatusGrid.superclass.constructor.call(this, {
        store: new Ext.data.Store({
            reader: new Ext.data.ArrayReader({}, [
                   {name: 'instance_id'},
                   {name: 'service_name', type: 'string'},
                   {name: 'operation_name', type: 'string'}
              ]),
            data: [
                ['tbx00001','testService','test']
            ]
        }),
        columns: columns,
        height:250,
        width:600
    });


}

Ext.extend(ServicesStatusGrid, Ext.grid.GridPanel);
