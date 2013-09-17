
/*
 * PEP Service Instances Interface
 * author: Andrea Marongiu
 */


InstancesPEPServiceInterface=function(serviceName){

    this.restURL="rest/servicesInstances/";
    
    this.restInstanceURL="rest/instances"
    
    this.serviceName=serviceName;
    
    this.pageSize=20;

    this.renderElement=null;
      
    this.render=function (elementID){
        var htmlElement=document.getElementById(elementID);
        htmlElement.innerHTML="";
        this.renderElement=elementID;
    
       var dataRecord=new Array();
          
          dataRecord.push({name : "id", mapping: "id"});
          dataRecord.push({name : "operation", 
                           mapping: "operation"});
          dataRecord.push({name : "date", 
                           mapping: "date"});              
          dataRecord.push({name : "status", 
                           mapping: "status"});             
          /*dataRecord.push({name : "deleteDate", 
                           mapping: "deleteDate",
                           type: 'date', 
                           dateFormat: 'time'});*/
          
          
          var dataListJsonReader=new Ext.data.JsonReader({
                                    root : "instancesPage.instances",
                                    totalProperty: 'instancesPage.instancesCount'
                                    },
                                    Ext.data.Record.create(dataRecord)); 
          var pageSize=this.pageSize;                          
          var datastore=new Ext.data.Store({
                         nocache : true,
                         autoLoad: {params:{start:0, limit:pageSize}},
                         storeId: "instancesList"+this.serviceName,
                         proxy:new Ext.data.HttpProxy({
                              url : this.restURL+this.serviceName,
                              method : 'GET',
                              listeners: {
                                      "exception": function(){
                                              
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
       
       var buttonsHTMLTemplate="";
       var buttons=new Array();
       
       
       buttons.push({
           imageButton:"styles/img/sendG.png",
           imageSelButton:"styles/img/send.png",
           labelButton:"Service Request",
           javascriptMethod:"pepManager.openServiceResource('{instanceID}_"+this.serviceName+"_{requestResourceID}',"+
               "'Intsance {instanceID} of "+this.serviceName+" service - Service Request',"+
               "'manager?cmd=getXMLResource&output=text&id={requestResourceID}', "+
               "'manager?cmd=getXMLResource&output=xml&id={requestResourceID}');"
       });
       
       buttons.push({
           imageButton:"styles/img/receiveG.png",
           imageSelButton:"styles/img/receive.png",
           labelButton:"Service Response",
           javascriptMethod:"pepManager.openServiceResource('{instanceID}_"+this.serviceName+"_{responseResourceID}',"+
               "'Intsance {instanceID} of "+this.serviceName+" service - Service Response',"+
               "'manager?cmd=getXMLResource&output=text&id={responseResourceID}', "+
               "'manager?cmd=getXMLResource&output=xml&id={responseResourceID}');"
       });
       
    
     
       var imageDimMin="30";
       var imageDimMax="30";
       buttonsHTMLTemplate+="<table width=\"650\"><tr><td width=\"8%\"></td><td width=\"92%\">";
       for(var i=0; i<buttons.length;i++){
        
           buttonsHTMLTemplate+="<img src='styles/img/empty.png' width='1'  height='"+imageDimMax+"'/>";
           buttonsHTMLTemplate+="<img title='"+buttons[i].labelButton+"' "+
            "src='"+buttons[i].imageButton+"' "+
            "onmouseout=\"javascript:this.src='"+buttons[i].imageButton+"';this.width='"+imageDimMin+"';this.height='"+imageDimMin+"';\""+
            " onmouseover=\"javascript:this.src='"+buttons[i].imageSelButton+"';this.width='"+imageDimMax+"';this.height='"+imageDimMax+"';\" "+
            "width='"+imageDimMin+"'  height='"+imageDimMin+"'"+
            " onclick=\"javascript:"+buttons[i].javascriptMethod+"\"/>"+
            "<img src='styles/img/empty.png' width='1'  height='"+imageDimMax+"'/>"
       }
       
       buttonsHTMLTemplate+="</td></tr></table>";
       // row expander
        var expander = new Ext.ux.grid.RowExpander({
            expandRowURL:this.restInstanceURL, 
            restDataId:"id",
            tpl : new Ext.Template(buttonsHTMLTemplate)
        });
        
      
       
       grid = new Ext.grid.GridPanel({  
            renderTo: elementID,
            height : Ext.getCmp("bogus"+this.serviceName+"instancesService").getInnerHeight(),
            autoScroll: true,
            frame:true,
            animCollapse: true,
           // title:'Service "'+this.serviceName+'" Instances',
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
                header: "Instance ID",
                dataIndex: 'id',
                width: 200,
                sortable:true
            },{
                header: "Service Operation",
                dataIndex: 'operation',
                width: 300,
                sortable:true
            },{
                header: "Arrival Date",
                dataIndex: 'date',
                width: 300,
                sortable:true
            },{
                header: "Status",
                dataIndex: 'status',
                width: 300,
                sortable:true
            }]}),

            tbar:[{
                text:'Remove Selected Instances',
                iconCls:'removeinstances',
                disabled: true,
                renderEl: this.renderElement,
                restDeleteUrl: this.restInstanceURL,
                ref: '../removeButton',
                handler : function(){
                    
                    var record;
                    var countDeleted=0;
                    Ext.Msg.show({
                         smObj: smObj,
                         renderEl: this.renderEl,
                         restDeleteUrl: this.restDeleteUrl,
                         title:'PEP: Remove Instances',
                         msg: "Do you really want to delete the selected Instances ("+smObj.getCount()+") ?",
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
                                              opt.restDeleteUrl+"/"+record['id'],
                                              false, null, "", "", 
                                              800000, deleteResponse, deleteRequestTimeOut,null,
                                              null, null);  
                                    }

                                    Ext.Msg.show({
                                         title:'PEP: Operation Completed',
                                         msg: "Number of removed instances:" +  countDeleted,
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
		    pageSize:pageSize,
		    displayInfo:true
	    })
    });
  };
  
   this.update=function(){
         this.render(this.renderElement);
    };
};

