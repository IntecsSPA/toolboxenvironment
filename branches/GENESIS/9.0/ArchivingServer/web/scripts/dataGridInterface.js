
/*
 * Toolbox ARMS Data Interface
 * @author: Andrea Marongiu
 */


DataGridInterface=function(){

     this.restDataUrl="service/datalist";
     
     this.restDataStatusUrl="service/getstatus";
     
     this.restDataDeleteUrl="service/delete";
     
     this.renderElement=null;
     

     this.render=function (elementID){

        var htmlElement=document.getElementById(elementID);
        htmlElement.innerHTML="";
        this.renderElement=elementID;
        var maskWatch=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskWatch.show(); 
       
           
          var dataRecord=new Array();
          
          dataRecord.push({name : "dataId", mapping: "dataId"});
          dataRecord.push({name : "creationDate", 
                           mapping: "creationDate",
                           type: 'date', 
                           dateFormat: 'time'});
          dataRecord.push({name : "deleteDate", 
                           mapping: "deleteDate",
                           type: 'date', 
                           dateFormat: 'time'});
          
          
          var dataListJsonReader=new Ext.data.JsonReader({
                                    root : "dataList",
                                    totalProperty: 'totalCount'
                                    },
                                    Ext.data.Record.create(dataRecord)); 
                                    
          var datastore=new Ext.data.Store({
                         nocache : true,
                         autoLoad: {params:{start:0, limit:armsManager.pagingSize}},
                         storeId: "storeDataList",
                         proxy:new Ext.data.HttpProxy({
                              url : this.restDataUrl,
                              method : 'GET',
                              listeners: {
                                      "exception": function(){
                                               /*Ext.Msg.show({
                                                 title:'Results WARNING',
                                                 msg: outputManager.pagingEmptyMsg,
                                                 buttons: Ext.Msg.OK,
                                                 icon: Ext.MessageBox.WARNING
                                               });*/
                                      }
                              },
                              timeout: 900000
                         }),
                         reader : dataListJsonReader,
                         remoteSort : false
          });        
          
       var grid=null;   
          
       var smObj =new Ext.grid.CheckboxSelectionModel({
           singleSelect: false,
           listeners: {
            selectionchange: function(sm) {
                if (sm.getCount()) {
                    grid.removeButton.enable();
                } else {
                    grid.removeButton.disable();
                }
            }
          }   
       });
       
       // row expander
        var expander = new Ext.ux.grid.RowExpander({
            expandRowURL:"service/getstatus", 
            restDataId:"dataId",
            tpl : new Ext.Template(
                "<table width=\"650\"><tr><td width=\"8%\"></td>"+
                "<td width=\"92%\"><table ALIGN=\"center\" width=\"100%\" border=\"1\" class=\"status\">"+
                    "<tr><td ALIGN=\"center\" width=\"25%\"><b>Status</b></td><td ALIGN=\"center\" width=\"75%\">{downloadStatus}</td></tr>"+  
                    "<tr><td ALIGN=\"center\"><b>HTTP</b></td><td><a href=\"javascript:armsManager.newWindow('{http}','http');\">{http}</a></td></tr>"+ 
                    "<tr><td ALIGN=\"center\"><b>FTP</b></td><td><a href=\"javascript:armsManager.newWindow('{ftp}','ftp');\">{ftp}</a></td></tr>"+ 
                    "<tr><td ALIGN=\"center\"><b>CATALOGUE</b></td><td><a href=\"javascript:armsManager.newWindow('{catalogues}','catalogues');\">{catalogues}</a></td></tr>"+ 
                    "<tr><td ALIGN=\"center\"><b>GEOSERVER</b></td><td><a href=\"javascript:armsManager.newWindow('{geoserver}','geoserver');\">{geoserver}</a></td></tr>"+ 
                    "<tr><td ALIGN=\"center\"><b>SOS</b></td><td><a href=\"javascript:armsManager.newWindow('{sos}','sos');\">{sos}</a></td></tr>"+ 
                 "</table></td>"+
                 "</tr></table>"
            )
        });
       
       grid = new Ext.grid.GridPanel({  
            renderTo: elementID,
            autoHeight: true,
            frame:true,
            animCollapse: true,
            title:'ARMS Data List',
            trackMouseOver:false,
            store: datastore,
            loadMask: {
                 msg:"Please wait..."
            },
            split: true,
            viewConfig: {
                 forceFit:true
            },
            sm: smObj,
            plugins: expander,
            columnLines: true,
            colModel: new Ext.grid.ColumnModel({
            defaults: {
                width: 20,
                sortable: true
            },
            columns:[smObj,expander,{
                header: "Data ID",
                dataIndex: 'dataId',
                width: 200,
                sortable:true
            },{
                header: "Creation Date",
                dataIndex: 'creationDate',
                width: 300,
                sortable:true
            },{
                header: "Delete Date",
                dataIndex: 'deleteDate',
                width: 300,
                sortable:true
            }]}),

            tbar:[{
                text:'Remove Selected Data',
                iconCls:'remove',
                disabled: true,
                renderEl: this.renderElement,
                restDeleteUrl: this.restDataDeleteUrl,
                ref: '../removeButton',
                handler : function(){
                    
                    var record;
                    var countDeleted=0;
                    Ext.Msg.show({
                         smObj: smObj,
                         renderEl: this.renderEl,
                         restDeleteUrl: this.restDeleteUrl,
                         title:'ARMS: Remove data',
                         msg: "Do you really want to delete the selected data ("+smObj.getCount()+") ?",
                         buttons: Ext.Msg.YESNOCANCEL,
                         animEl: this.renderEl,
                         fn: function(buttonId,text,opt){
                             if(buttonId == "yes"){
                                 var selections=smObj.getSelections();
                                 for(var i=0; i<selections.length; i++){
                                        record=selections[i].data;
                                        var deleteRequestTimeOut=function(){
                                              Ext.Msg.show({
                                                title:'Delete Request: Error',
                                                buttons: Ext.Msg.OK,
                                                msg: 'Request TIME-OUT!',
                                                animEl: this.renderEl,
                                                icon: Ext.MessageBox.ERROR
                                              });
                                        };
                                       var deleteResponse= function(response){
                                              var jsonData=JSON.parse(response);
                                              if(jsonData.success)
                                                  countDeleted++;
                                       };

                                        sendAuthenticationXmlHttpRequestTimeOut("DELETE",
                                              opt.restDeleteUrl+"/"+record['dataId'],
                                              false, null, "", "", 
                                              800000, deleteResponse, deleteRequestTimeOut,null,
                                              null, null);  
                                    }

                                    Ext.Msg.show({
                                         title:'ARMS: Operation Completed',
                                         msg: "Number of removed data:" +  countDeleted,
                                         buttons: Ext.Msg.OK,
                                         fn: function(){
                                             grid.getStore().reload();
                                         },
                                         animEl: opt.renderEl,
                                         icon: Ext.MessageBox.INFO
                                   });   
                                   
                            }      
                         },
                         icon: Ext.MessageBox.QUESTION
                   });
                   
                    
              }
           }],

	    bbar: new Ext.PagingToolbar({
		    store: datastore,
		    pageSize:armsManager.pagingSize,
		    displayInfo:true
	    })/*,

	    view: new Ext.ux.grid.BufferView({
		   
		    rowHeight: 34,
		  
		    scrollDelay: false
	    })*/
    });


        // render functions
       /* function renderTopic(value, p, record){
            return String.format(
                    '<b><a href="http://extjs.com/forum/showthread.php?t={2}" target="_blank">{0}</a></b><a href="http://extjs.com/forum/forumdisplay.php?f={3}" target="_blank">{1} Forum</a>',
                    value, record.data.forumtitle, record.id, record.data.forumid);
        }
        function renderLast(value, p, r){
            return String.format('{0}<br/>by {1}', value.dateFormat('M j, Y, g:i a'), r.data['lastposter']);
        }       */
        
        maskWatch.hide();
     };



     this.update=function(){
         this.render(this.renderElement);
    };
    
    
     
     this.onDeleteAllWatch=function(){

        
        
    };
    
     this.onDeleteWatch=function(watchIndex){

      
    };
    
    

};



