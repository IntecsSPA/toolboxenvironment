
/*
 * Toolbox ARMS Data Interface
 * @author: Andrea Marongiu
 */


DataGridInterface=function(){

     this.restDataUrl="rest/data/";
     
     this.renderElement=null;

     this.render=function (elementID){
        var htmlElement=document.getElementById(elementID);
        htmlElement.innerHTML="";
        this.renderElement=elementID;
        var maskWatch=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskWatch.show(); 
       /* this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();*/
        
       /* var getDataListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Watch List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }*/
            
         //var watchInterface=this;
         
         
        /* var getDataList= function(response){
                var watchCollection=JSON.parse(response);
                watchInterface.watchListLoaded=watchCollection;
                for(var i=0; i<watchCollection.watchNumber;i++){
                    if(! watchCollection.watchList[i].hidden)
                        watchInterface.onAddWatch(watchCollection.watchList[i]);
                }
                maskWatch.hide();
            };*/

         //var loginValues=this.formInterface.getFormValues();
        /* this.user= loginValues['user'];
           this.password=loginValues['password'];*/
        /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
           /*sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restWatchListURL,
                     true, null, "loginValues['user']", "loginValues['password']", 
                     800000, getWatchList, getWatchListTimeOut,null,
                     null, null);*/
                     
                     
          var datastore = new Ext.data.Store({
                 remoteSort: false,
                 baseParams: {lightWeight:true,ext: 'js'},
                 sortInfo: {field:'publishDate', direction:'DESC'},
                 autoLoad: {params:{start:0, limit:500}},

                 proxy: new Ext.data.ScriptTagProxy({
                    url: this.restDataUrl
                 }),

                reader: new Ext.data.JsonReader({
                    root: 'dataList',
                    totalProperty: 'totalCount',
                    idProperty: 'dataId',
                    fields: [
                        'title', 'forumtitle', 'forumid', 'author',
                        {name: 'replycount', type: 'int'},
                        {name: 'lastpost', mapping: 'lastpost', type: 'date', dateFormat: 'timestamp'},
                        'lastposter', 'excerpt'
                    ]
                })
            });

        var grid = new Ext.grid.GridPanel({  
            renderTo: elementID,
            width:700,
            height:500,
            frame:true,
            title:'ExtJS.com - Browse Forums',
            trackMouseOver:false,
            autoExpandColumn: 'topic',
            store: store,

            columns: [new Ext.grid.RowNumberer({width: 30}),{
                id: 'topic',
                header: "Topic",
                dataIndex: 'title',
                width: 420,
                renderer: renderTopic,
                sortable:true
            },{
                header: "Replies",
                dataIndex: 'replycount',
                width: 70,
                align: 'right',
                sortable:true
            },{
                id: 'last',
                header: "Last Post",
                dataIndex: 'lastpost',
                width: 150,
                renderer: renderLast,
                sortable:true
            }],

	    bbar: new Ext.PagingToolbar({
		    store: store,
		    pageSize:500,
		    displayInfo:true
	    }),

	    view: new Ext.ux.grid.BufferView({
		    // custom row height
		    rowHeight: 34,
		    // render rows as they come into viewable area.
		    scrollDelay: false
	    })
    });


        // render functions
        function renderTopic(value, p, record){
            return String.format(
                    '<b><a href="http://extjs.com/forum/showthread.php?t={2}" target="_blank">{0}</a></b><a href="http://extjs.com/forum/forumdisplay.php?f={3}" target="_blank">{1} Forum</a>',
                    value, record.data.forumtitle, record.id, record.data.forumid);
        }
        function renderLast(value, p, r){
            return String.format('{0}<br/>by {1}', value.dateFormat('M j, Y, g:i a'), r.data['lastposter']);
        }       
        
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



