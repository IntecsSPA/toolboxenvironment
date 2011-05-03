
/*
 * Toolbox Set ARMS Watch Interface
 * @author: Andrea Marongiu
 */


WatchInterface=function(){

     this.xmlInterface="resources/interfaces/watchPanel.xml";

     this.watchNumber=0;
     this.formInterface=new ExtXmlInterface(this.xmlInterface);
     this.restWatchListURL="rest/config/watchlist";
     this.watchListLoaded=null;
     
     
     this.render=function (elementID){
        var maskWatch=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskWatch.show(); 
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
        
        var getWatchListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Watch List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var watchInterface=this;
         var getWatchList= function(response){
                var watchCollection=JSON.parse(response);
                watchInterface.watchListLoaded=watchCollection;
                for(var i=0; i<watchCollection.watchNumber;i++){
                    if(! watchCollection.watchList[i].hidden)
                        watchInterface.onAddWatch(watchCollection.watchList[i]);
                }
                maskWatch.hide();
            };

         //var loginValues=this.formInterface.getFormValues();
        /* this.user= loginValues['user'];
           this.password=loginValues['password'];*/
        /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restWatchListURL,
                     true, null, "loginValues['user']", "loginValues['password']", 
                     800000, getWatchList, getWatchListTimeOut,null,
                     null, null);
     };

     this.onAddWatch=function(defaultValues){
         
         
         var multiType=Ext.getCmp('watchMultiType');
         

         //Add watch FieldSet
         multiType.addFieldSet("watchFieldSetSet_"+this.watchNumber, "watch "+this.watchNumber);

         //Add Watch Type Combo
         var watchTypeStoreFields=['value'];
         var watchTypeStoreData=armsManager.getTypesList();
         multiType.addComboField("watchType_"+this.watchNumber,'Watch Type',10,
                                watchTypeStoreFields,watchTypeStoreData, null,
                                "watchFieldSetSet_"+this.watchNumber);
                                
         multiType.addSpace("watchSpace1_"+this.watchNumber,4,
                            "watchFieldSetSet_"+this.watchNumber);
         
         
         // Add Watch Server Folder Path Text
         multiType.addTextField("watchFolder_"+this.watchNumber, 
            "Watch Folder", "", 40, "watchFieldSetSet_"+this.watchNumber);
         
         multiType.addSpace("watchSpace2_"+this.watchNumber,4, "watchFieldSetSet_"+this.watchNumber);
         
         
         //Add Remove Watch Button
         multiType.addButtonField("watchDeleteButton_"+this.watchNumber,'Remove',
                        "armsManager.watchInterface.onDeleteWatch("+this.watchNumber+")",null, "watchFieldSetSet_"+this.watchNumber);
                        
                        
         multiType.addCheckBox("watchHTTPEnable_"+this.watchNumber, 
                                "Internal HTTP Publish", function(){},
                                        "watchFieldSetSet_"+this.watchNumber,5,'x-check-group-alt');
                                        
         multiType.addCheckBox("watchInternalFTPEnable_"+this.watchNumber, 
                                "Internal FTP Publish", function(){},
                                        "watchFieldSetSet_"+this.watchNumber,5,'x-check-group-alt');                               
                                                                     
         //Add Fieldset Section    
         this.addFTPOptions(multiType);
         this.addCatalogueOptions(multiType);
         this.addGeoserverOptions(multiType);
         this.addSOSOptions(multiType);
         
         multiType.doLayout();
         
         Ext.getCmp("watchHTTPEnable_"+this.watchNumber).setValue(false);
         Ext.getCmp("watchFTPFieldSet_"+this.watchNumber).collapse();
         Ext.getCmp("watchGeoserverFieldSet_"+this.watchNumber).collapse();
         Ext.getCmp("watchCatalogueFieldSet_"+this.watchNumber).collapse();
         Ext.getCmp("watchSOSFieldSet_"+this.watchNumber).collapse();

         if(defaultValues)
             this.loadWatchDefaultValues(this.watchNumber, defaultValues);
         
         this.watchNumber++;               

    };
    
     this.addFTPOptions= function (multiType){
         // Add FTP input Text
         
         multiType.addFieldSet("watchFTPFieldSet_"+this.watchNumber, 
             "FTP", "watchFieldSetSet_"+this.watchNumber, true);
         

         multiType.addTextField("ftpUrl_"+this.watchNumber, 
            "FTP URL", "", 40, "watchFTPFieldSet_"+this.watchNumber);
     };
     
     this.addCatalogueOptions= function (multiType){
         // Add Catalogue input Text
         
         multiType.addFieldSet("watchCatalogueFieldSet_"+this.watchNumber, 
             "Catalogue", "watchFieldSetSet_"+this.watchNumber, true);
         

         multiType.addTextField("catalogueUrl_"+this.watchNumber, 
            "Catalogue URL", "", 40, "watchCatalogueFieldSet_"+this.watchNumber);
     };
     
     this.addGeoserverOptions= function (multiType){
         // Add Geoserver input Text
         
         multiType.addFieldSet("watchGeoserverFieldSet_"+this.watchNumber, 
             "Geoserver", "watchFieldSetSet_"+this.watchNumber, true);
         
         var geoserverTypeStoreFields=['value'];
         var geoserverTypeStoreData=[['shp'],['geotiff']];
         
         multiType.addComboField("geoserverDataType_"+this.watchNumber,'Data Type',8,
                                geoserverTypeStoreFields,geoserverTypeStoreData,
                                null,"watchGeoserverFieldSet_"+this.watchNumber);    
                                
         multiType.addSpace("geoserverwatchSpace1_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);
         
         multiType.addTextField("geoserverUrl_"+this.watchNumber, 
            "Geoserver URL", "", 40, "watchGeoserverFieldSet_"+this.watchNumber);
            
         multiType.addSpace("geoserverwatchSpace2_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber,2);
         
         multiType.addTextField("geoserverUser_"+this.watchNumber, 
            "Geoserver User", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
         multiType.addSpace("geoserverwatchSpace3_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);
         
         multiType.addTextField("geoserverPassword_"+this.watchNumber, 
            "Geoserver Password", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
         multiType.addSpace("geoserverwatchSpace4_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);   

         multiType.addTextField("geoserverWorkspace_"+this.watchNumber, 
            "Geoserver Workspace", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
            
     };
     
     this.addSOSOptions= function (multiType){
         // Add SOS input Text
         
         multiType.addFieldSet("watchSOSFieldSet_"+this.watchNumber, 
             "SOS", "watchFieldSetSet_"+this.watchNumber, true);

         multiType.addTextField("SOSUrl_"+this.watchNumber, 
            "SOS URL", "", 40, "watchSOSFieldSet_"+this.watchNumber);

     };
     
     this.onDeleteAllWatch=function(){

         var multiType=Ext.getCmp('watchMultiType');
         
         multiType.removeAll(true);
          
        multiType.doLayout();  
        
    };
    
     this.onDeleteWatch=function(watchIndex){

       var multiType=Ext.getCmp('watchMultiType');
         
       var cmp=Ext.getCmp("watchFieldSetSet_"+watchIndex);
       if(cmp)
         cmp.destroy();
          
       multiType.doLayout();  
    };
    
    this.loadWatchDefaultValues=function(watchIndex, defaultValues){
        if(defaultValues.type)
           Ext.getCmp("watchType_"+watchIndex).setValue(defaultValues.type);
       
        if(defaultValues.watchFolder)
           Ext.getCmp("watchFolder_"+watchIndex).setValue(defaultValues.watchFolder);
       
        if(defaultValues.Http)
            Ext.getCmp("watchHTTPEnable_"+watchIndex).setValue(true);
        else
            Ext.getCmp("watchHTTPEnable_"+watchIndex).setValue(false);
        
        
        
        if(defaultValues.Ftp.length>0){
            var ftpInternal=false;
            for(var i=0; i<defaultValues.Ftp.length;i++)
                   if(defaultValues.Ftp[i]=='ftp:') 
                      ftpInternal=true; 
          Ext.getCmp("watchInternalFTPEnable_"+watchIndex).setValue(ftpInternal);
          if(ftpInternal){
              if(defaultValues.Ftp.length>1){
                 Ext.getCmp("watchFTPFieldSet_"+watchIndex).expand();
                 Ext.getCmp("ftpUrl_"+watchIndex).setValue(defaultValues.Ftp[1]); 
              }
          }else{
             Ext.getCmp("watchFTPFieldSet_"+watchIndex).expand();
             Ext.getCmp("ftpUrl_"+watchIndex).setValue(defaultValues.Ftp[0]);   
          }   
        }else
           Ext.getCmp("watchFTPFieldSet_"+watchIndex).collapse();
       
        if(defaultValues.GeoServer.length>0){
           Ext.getCmp("watchGeoserverFieldSet_"+watchIndex).expand();
           if(defaultValues.GeoServer[0].indexOf("@") !=-1){
               var urlGeoserver=replaceAll(defaultValues.GeoServer[0], "http://","");
               var tmp=urlGeoserver.split("@");
               Ext.getCmp("geoserverUrl_"+watchIndex).setValue(tmp[1]);
               var tmp2=tmp[0].split(":");
               Ext.getCmp("geoserverUser_"+watchIndex).setValue(tmp2[0]);
               Ext.getCmp("geoserverPassword_"+watchIndex).setValue(tmp2[1]);
           }else    
            Ext.getCmp("geoserverUrl_"+watchIndex).setValue(defaultValues.GeoServer[0]);  
           if(defaultValues.geoserverType)
               Ext.getCmp("geoserverDataType_"+watchIndex).setValue(defaultValues.geoserverType);
          /* if(defaultValues.geoserverUser)
               Ext.getCmp("geoserverUser_"+watchIndex).setValue(defaultValues.geoserverUser);
           if(defaultValues.geoserverPassword)
               Ext.getCmp("geoserverPassword_"+watchIndex).setValue(defaultValues.geoserverPassword);*/
           if(defaultValues.geoserverWorkspace)
               Ext.getCmp("geoserverWorkspace_"+watchIndex).setValue(defaultValues.geoserverWorkspace);
        }   
        else
           Ext.getCmp("watchGeoserverFieldSet_"+watchIndex).collapse();
       
        if(defaultValues.ebRIMCatalogue.length>0){
           Ext.getCmp("watchCatalogueFieldSet_"+watchIndex).expand(); 
           Ext.getCmp("catalogueUrl_"+watchIndex).setValue(defaultValues.ebRIMCatalogue[0]);
        }else
           Ext.getCmp("watchCatalogueFieldSet_"+watchIndex).collapse();

         if(defaultValues.SOS.length>0){
           Ext.getCmp("watchSOSFieldSet_"+watchIndex).expand();
           Ext.getCmp("SOSUrl_"+watchIndex).setValue(defaultValues.SOS[0]);
         }else
           Ext.getCmp("watchSOSFieldSet_"+watchIndex).collapse();
  
    };
    
    
    this.getWatchData=function(watchIndex){
        var watchData=new Watch();
        //alert(JSON.stringify(watchData, null));
        
        var value;
   
        if(Ext.getCmp("watchType_"+watchIndex)){
           value=Ext.getCmp("watchType_"+watchIndex).getValue();
           if(value){
               watchData.type=value;
               watchData.idString+=value;
           }else
              return null; 
        }else
           return null; 
    
        if(Ext.getCmp("watchFolder_"+watchIndex)){
           value=Ext.getCmp("watchFolder_"+watchIndex).getValue();
           if(value){
               watchData.watchFolder=value;
               watchData.idString+=value;
           }else
              return null; 
        }else
           return null; 
         
        if(Ext.getCmp("watchHTTPEnable_"+watchIndex)){
           value=Ext.getCmp("watchHTTPEnable_"+watchIndex).checked;
           watchData.Http=value; 
           watchData.idString+=value;
        }
        
        if(Ext.getCmp("watchInternalFTPEnable_"+watchIndex)){
           value=Ext.getCmp("watchInternalFTPEnable_"+watchIndex).checked;
           if(value)
              watchData.Ftp.push("ftp:");  
           watchData.idString+=value;
        }
        
        if(Ext.getCmp("watchFTPFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchFTPFieldSet_"+watchIndex).collapsed;
           if(!value){
             
             if(Ext.getCmp("ftpUrl_"+watchIndex)){
                value=Ext.getCmp("ftpUrl_"+watchIndex).getValue(); 
                if(value){
                   watchData.Ftp.push(value); 
                   watchData.idString+=value;
                }
                  
             }   
           }else
            watchData.idString+="false";  
        }
    
        if(Ext.getCmp("watchGeoserverFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchGeoserverFieldSet_"+watchIndex).collapsed;
           if(!value){
             watchData.GeoServer=new Array();
             if(Ext.getCmp("geoserverUrl_"+watchIndex)){
                value=Ext.getCmp("geoserverUrl_"+watchIndex).getValue(); 
                if(value){
                    watchData.GeoServer.push(value); 
                    watchData.idString+=value;    
                }
                
             } 
             if(Ext.getCmp("geoserverDataType_"+watchIndex)){
                value=Ext.getCmp("geoserverDataType_"+watchIndex).getValue(); 
                if(value){
                    watchData.geoserverType=value;
                    watchData.idString+=value;
                }
                  
             }
             if(Ext.getCmp("geoserverWorkspace_"+watchIndex)){
                value=Ext.getCmp("geoserverWorkspace_"+watchIndex).getValue(); 
                if(value){
                    watchData.geoserverWorkspace=value;
                    watchData.idString+=value;
                }
                  
             }
             var geoserverUP="";
             if(Ext.getCmp("geoserverUser_"+watchIndex)){
                value=Ext.getCmp("geoserverUser_"+watchIndex).getValue(); 
                if(value){
                    watchData.idString+=value;
                    geoserverUP+=value+":";
                }
                 
             }
             if(Ext.getCmp("geoserverPassword_"+watchIndex)){
                value=Ext.getCmp("geoserverPassword_"+watchIndex).getValue(); 
                if(value){
                    geoserverUP+=value;
                    watchData.idString+=value;
                }
             }
             if(geoserverUP!=""){
                 var urlGeoserver=replaceAll(watchData.GeoServer[0], "http://","");
                 watchData.GeoServer[0]="http://"+geoserverUP+"@"+urlGeoserver;
             }    
           }else
            watchData.idString+="false";    
        }
        
        if(Ext.getCmp("watchCatalogueFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchCatalogueFieldSet_"+watchIndex).collapsed;
           if(!value){
             watchData.ebRIMCatalogue=new Array();
             if(Ext.getCmp("catalogueUrl_"+watchIndex)){
                value=Ext.getCmp("catalogueUrl_"+watchIndex).getValue(); 
                if(value){
                    watchData.ebRIMCatalogue.push(value); 
                    watchData.idString+=value;
                }
             }   
           }else
            watchData.idString+="false";    
        }
  
        if(Ext.getCmp("watchSOSFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchSOSFieldSet_"+watchIndex).collapsed;
           if(!value){
             watchData.SOS=new Array();
             if(Ext.getCmp("SOSUrl_"+watchIndex)){
                value=Ext.getCmp("SOSUrl_"+watchIndex).getValue(); 
                if(value){
                    watchData.SOS.push(value); 
                    watchData.idString+=value;
                }
             }   
           }else
            watchData.idString+="false";    
        }
        
       return watchData;  
    };
    
     this.onSave=function(){
         var maskWatch=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskWatch.show(); 
         var watchListJSON=new Object();
         var watchSaved=0;
         watchListJSON.watchList=new Array();
         var watchData;
         for(var i=0; i<this.watchNumber; i++){
             watchData=this.getWatchData(i);
             if(watchData)
                watchListJSON.watchList.push(watchData);    
         }
         watchSaved=watchListJSON.watchList.length;
         
         for(i=0; i<this.watchListLoaded.watchNumber; i++){
             if(this.watchListLoaded.watchList[i].hidden)
              watchListJSON.watchList.push(this.watchListLoaded.watchList[i]);    
         }
         watchListJSON.watchNumber=watchListJSON.watchList.length;
        
        //alert(JSON.stringify(watchListJSON, null));
        
        var postWatchListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Save Watch List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var postWatchList= function(response){
                var jsonResponse=JSON.parse(response);
                maskWatch.hide(); 
                if(jsonResponse.success){
                    Ext.Msg.show({
                    title:'Changes Saved',
                    msg: 'Number of watch saved: ' + watchSaved,
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.INFO
                    });
                }else{
                    Ext.Msg.show({
                    title:'Changes Saved Error',
                    msg: 'Reason: ' + jsonResponse.reason,
                    buttons: Ext.Msg.OK,
                    icon: Ext.MessageBox.ERROR
                    });
                    
                }
            };


        sendAuthenticationXmlHttpRequestTimeOut("POST",
                     this.restWatchListURL,
                     true, JSON.stringify(watchListJSON, null),
                     "loginValues['user']", "loginValues['password']", 
                     800000, postWatchList, postWatchListTimeOut,null,
                     null, null);
        
    };

};


Watch=function(){
    this.idString="";
    this.watchFolder= null;  
    this.deleteAfter= 50000000000000;
    this.hidden= false;
    this.type= null;
    this.downloadUrl= "";
    this.metadataUrl= "";
    this.Ftp= new Array();
    this.GeoServer= new Array();
    this.geoserverType= "";
    this.geoserverWorkspace= "";
   /* this.geoserverUser= "";
    this.geoserverPassword= "";*/
    this.ebRIMCatalogue= new Array();
    this.SOS= new Array();
    this.Http= null;
}

