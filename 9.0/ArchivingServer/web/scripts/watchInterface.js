
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
       /* var maskWatch=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskWatch.show(); */
        armsManager.showWorkspaceLoadPanel();
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
                    if(! watchCollection.watchList[i].hidden){
                      watchInterface.onAddWatch(watchCollection.watchList[i], true);
                      //Ext.getCmp("watchFieldSetSet_"+i).collapse();
                    }
                      
                        
                }
                Ext.getCmp('watchMultiType').doLayout();
                for(i=0; i<watchCollection.watchNumber;i++){
                    if(! watchCollection.watchList[i].hidden){
                      Ext.getCmp("watchFieldSetSet_"+i).collapse();
                    }
                }    
                armsManager.hideWorkspaceLoadPanel();
              
            };

         //var loginValues=this.formInterface.getFormValues();
        /* this.user= loginValues['user'];
           this.password=loginValues['password'];*/
        /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restWatchListURL,
                     true, null, interfacesManager.user, interfacesManager.password, 
                     800000, getWatchList, getWatchListTimeOut,null,
                     null, null);
     };

     this.onAddWatch=function(defaultValues,notRefresh){
         
         
         var multiType=Ext.getCmp('watchMultiType');
         
         var setDefaultValues=null;
         
         var watchName="New Watch "/*("+this.watchNumber+")"/*/;
         
         if(defaultValues){
           setDefaultValues="armsManager.watchInterface.loadWatchDefaultValues("+this.watchNumber+");";
           watchName="Watch ( "+this.watchListLoaded.watchList[this.watchNumber].watchFolder+" )";
         }  
             
         
         //Add watch FieldSet
         multiType.addFieldSet("watchFieldSetSet_"+this.watchNumber, watchName, null, false, setDefaultValues);

         //Add Watch Type Combo
         var watchTypeStoreFields=['value'];
         var watchTypeStoreData=armsManager.getTypesList();
         multiType.addComboField("watchType_"+this.watchNumber,'Chain Type',10,
                                watchTypeStoreFields,watchTypeStoreData, null,
                                "watchFieldSetSet_"+this.watchNumber);
                                
         multiType.addSpace("watchSpace1_"+this.watchNumber,4,
                            "watchFieldSetSet_"+this.watchNumber);
         
         
         // Add Watch Server Folder Path Text
         if(defaultValues)
            multiType.addTextField("watchFolder_"+this.watchNumber, 
            "Watch Folder", "", 60, "watchFieldSetSet_"+this.watchNumber,null,null,true);
         else
            multiType.addTextField("watchFolder_"+this.watchNumber, 
            "Watch Folder", "", 60, "watchFieldSetSet_"+this.watchNumber,null, "newwatch");   
         
         multiType.addSpace("watchSpace2_"+this.watchNumber,4, "watchFieldSetSet_"+this.watchNumber);
         
         
         //Add Remove Watch Button
         multiType.addButtonField("watchDeleteButton_"+this.watchNumber,'Remove',
                        "armsManager.watchInterface.onDeleteWatch("+this.watchNumber+")",null, "watchFieldSetSet_"+this.watchNumber);
                        
         this.addDeleteAfterOptions(multiType);
                        
         multiType.addCheckBox("watchHTTPEnable_"+this.watchNumber, 
                                "Internal HTTP Publish", function(){},
                                        "watchFieldSetSet_"+this.watchNumber,5,'x-check-group-alt');
                                        
         multiType.addCheckBox("watchInternalFTPEnable_"+this.watchNumber, 
                                "Internal FTP Publish", function(){},
                                        "watchFieldSetSet_"+this.watchNumber,5,'x-check-group-alt');                               
                                                                     
         //Add Fieldset Section  
         
         this.addFTPOptions(multiType);
         this.addEbRIMCatalogueOptions(multiType);
         this.addOpenSearchCatalogueOptions(multiType);
         this.addGeoserverOptions(multiType);
         this.addSOSOptions(multiType);
         
       
         if(!notRefresh){
            multiType.doLayout();
             Ext.getCmp("watchHTTPEnable_"+this.watchNumber).setValue(false);
             Ext.getCmp("watchFTPFieldSet_"+this.watchNumber).collapse();
             Ext.getCmp("watchGeoserverFieldSet_"+this.watchNumber).collapse();
             Ext.getCmp("watchCatalogueFieldSet_"+this.watchNumber).collapse();
             Ext.getCmp("watchOpenSearchCatalogueFieldSet_"+this.watchNumber).collapse();
             Ext.getCmp("watchSOSFieldSet_"+this.watchNumber).collapse(); 
             Ext.getCmp("watchDeleteAfterFieldSet_"+this.watchNumber).collapse();
         }
          

        /* if(defaultValues)
            this.loadWatchDefaultValues(this.watchNumber, defaultValues);*/
         
         this.watchNumber++;    
     

    };
     this.addDeleteAfterOptions= function(multiType){

         multiType.addFieldSet("watchDeleteAfterFieldSet_"+this.watchNumber, 
             "Automatic Deletion", "watchFieldSetSet_"+this.watchNumber, true);
             
             
         multiType.addNumberField("deleteAfter_"+this.watchNumber, "Delete After", "",3,
                                       ".",0,"watchDeleteAfterFieldSet_"+this.watchNumber);
         
         multiType.addSpace("watchSpace12_"+this.watchNumber,0, "watchDeleteAfterFieldSet_"+this.watchNumber);
         
         var watchDeleteAfterUOMStoreFields=['name','value'];
         var watchDeleteAfterUOMStoreData=[['seconds', 's'],['minutes','m'], ['hours','h'], ['days','D'], ['weeks','W']];
         multiType.addComboField("deleteAfterUOM_"+this.watchNumber,'',10,
                                watchDeleteAfterUOMStoreFields,watchDeleteAfterUOMStoreData, null,
                                "watchDeleteAfterFieldSet_"+this.watchNumber);
     };
     
     this.addFTPOptions= function (multiType){
         // Add FTP input Text
         
         multiType.addFieldSet("watchFTPFieldSet_"+this.watchNumber, 
             "FTP", "watchFieldSetSet_"+this.watchNumber, true);
         

         multiType.addTextField("ftpUrl_"+this.watchNumber, 
            "FTP URL", "", 80, "watchFTPFieldSet_"+this.watchNumber,5);
            
         
         multiType.addTextField("ftpUser_"+this.watchNumber, 
            "FTP Username", "", 20, "watchFTPFieldSet_"+this.watchNumber);
         multiType.addSpace("ftpwatchSpace3_"+this.watchNumber,4, "watchFTPFieldSet_"+this.watchNumber);
         
         multiType.addTextField("ftpPassword_"+this.watchNumber, 
            "FTP Password", "", 20, "watchFTPFieldSet_"+this.watchNumber);
         multiType.addSpace("ftpwatchSpace4_"+this.watchNumber,4, "watchFTPFieldSet_"+this.watchNumber);      
     };
     
     this.addEbRIMCatalogueOptions= function (multiType){
         // Add EbRIM Catalogue input Text
         
         multiType.addFieldSet("watchCatalogueFieldSet_"+this.watchNumber, 
             "Catalogue", "watchFieldSetSet_"+this.watchNumber, true);
         

         multiType.addTextField("catalogueUrl_"+this.watchNumber, 
            "Catalogue URL", "", 80, "watchCatalogueFieldSet_"+this.watchNumber);
     };
     
     
     this.addOpenSearchCatalogueOptions= function (multiType){
         // Add OpenSearch Catalogue input Text
         
         multiType.addFieldSet("watchOpenSearchCatalogueFieldSet_"+this.watchNumber, 
             "OpenSearch Catalogue", "watchFieldSetSet_"+this.watchNumber, true);
         
         multiType.addTextField("catalogueOpenSearchUrl_"+this.watchNumber, 
            "OpenSearch Catalogue URL", "", 80, "watchOpenSearchCatalogueFieldSet_"+this.watchNumber);
            
         multiType.addTextField("catalogueOpenSearchIngestUrl_"+this.watchNumber, 
            "OpenSearch Catalogue Ingestion URL", "", 80, "watchOpenSearchCatalogueFieldSet_"+this.watchNumber);
            
            
     };
     
     this.addGeoserverOptions= function (multiType){
         // Add Geoserver input Text
         
         multiType.addFieldSet("watchGeoserverFieldSet_"+this.watchNumber, 
             "Geoserver", "watchFieldSetSet_"+this.watchNumber, true);
         
         var geoserverTypeStoreFields=['value'];
         var geoserverTypeStoreData=[['shp'],['geotiff'],['imagemosaic']];
         
         multiType.addComboField("geoserverDataType_"+this.watchNumber,'Data Type',8,
                                geoserverTypeStoreFields,geoserverTypeStoreData,
                                null,"watchGeoserverFieldSet_"+this.watchNumber);    
                                
         multiType.addSpace("geoserverwatchSpace1_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);
         
         multiType.addTextField("geoserverUrl_"+this.watchNumber, 
            "Geoserver URL", "", 57, "watchGeoserverFieldSet_"+this.watchNumber,3);
            
         //multiType.addSpace("geoserverwatchSpace2_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber,2);
         
         multiType.addTextField("geoserverUser_"+this.watchNumber, 
            "Geoserver User", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
         multiType.addSpace("geoserverwatchSpace3_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);
         
         multiType.addTextField("geoserverPassword_"+this.watchNumber, 
            "Geoserver Password", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
         multiType.addSpace("geoserverwatchSpace4_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);   

         multiType.addTextField("geoserverWorkspace_"+this.watchNumber, 
            "Geoserver Workspace", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
            
        // multiType.addSpace("geoserverwatchSpace5_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);     
         multiType.addTextField("geoserverFileNameTemplate_"+this.watchNumber, 
            "File Name Template", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
         
         multiType.addSpace("geoserverwatchSpace6_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);
         multiType.addTextField("geoserverStyle_"+this.watchNumber, 
            "Layer Style", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);
         
         multiType.addSpace("geoserverwatchSpace7_"+this.watchNumber,4, "watchGeoserverFieldSet_"+this.watchNumber);
         multiType.addTextField("geoserverDimensions_"+this.watchNumber, 
            "Layer Dimensions", "", 20, "watchGeoserverFieldSet_"+this.watchNumber);   
            
     };
     
     this.addSOSOptions= function (multiType){
         // Add SOS input Text
         
         multiType.addFieldSet("watchSOSFieldSet_"+this.watchNumber, 
             "SOS", "watchFieldSetSet_"+this.watchNumber, true);

         multiType.addTextField("SOSUrl_"+this.watchNumber, 
            "SOS URL", "", 80, "watchSOSFieldSet_"+this.watchNumber);

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
    
    this.loadWatchDefaultValues=function(watchIndex){

     if(Ext.getCmp("watchType_"+watchIndex).getValue()==""){
        
        var defaultValues=this.watchListLoaded.watchList[watchIndex];
        if(defaultValues.type)
           Ext.getCmp("watchType_"+watchIndex).setValue(defaultValues.type);
       
        if(defaultValues.watchFolder)
           Ext.getCmp("watchFolder_"+watchIndex).setValue(defaultValues.watchFolder);

        if(defaultValues.deleteAfter)
            if(defaultValues.deleteAfter== "-1")
               Ext.getCmp("watchDeleteAfterFieldSet_"+watchIndex).collapse();
            else{
               Ext.getCmp("watchDeleteAfterFieldSet_"+watchIndex).expand(); 
               var ns=defaultValues.deleteAfter.substring(
               defaultValues.deleteAfter.length-1,0)
               var uom=defaultValues.deleteAfter.substring(
               defaultValues.deleteAfter.length-1,defaultValues.deleteAfter.length);
               Ext.getCmp("deleteAfter_"+watchIndex).setValue(parseFloat(ns));
               Ext.getCmp("deleteAfterUOM_"+watchIndex).setValue(
               Ext.getCmp("deleteAfterUOM_"+watchIndex).getLabelForValueInformation("value",uom));
                
            }   
            
            
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
                 if(defaultValues.Ftp[1].indexOf("@") !=-1){
                    var urlFTP=replaceAll(defaultValues.Ftp[1], "ftp://","");
                     var tmp=urlFTP.split("@");
                     Ext.getCmp("ftpUrl_"+watchIndex).setValue(tmp[1]);
                     var tmp2=tmp[0].split(":");
                     Ext.getCmp("ftpUser_"+watchIndex).setValue(tmp2[0]);
                     Ext.getCmp("ftpPassword_"+watchIndex).setValue(tmp2[1]); 
                 }else    
                  Ext.getCmp("ftpUrl_"+watchIndex).setValue(defaultValues.Ftp[1]); 
              }else
                 Ext.getCmp("watchFTPFieldSet_"+watchIndex).collapse(); 
              
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
                tmp=urlGeoserver.split("@");
               Ext.getCmp("geoserverUrl_"+watchIndex).setValue(tmp[1]);
                tmp2=tmp[0].split(":");
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
           if(defaultValues.geoserverFileNameTemplate)
               Ext.getCmp("geoserverFileNameTemplate_"+watchIndex).setValue(defaultValues.geoserverFileNameTemplate);
           if(defaultValues.geoserverDimensions)
               Ext.getCmp("geoserverDimensions_"+watchIndex).setValue(defaultValues.geoserverDimensions);
           if(defaultValues.geoserverStyle)
               Ext.getCmp("geoserverStyle_"+watchIndex).setValue(defaultValues.geoserverStyle);
           
        }   
        else
           Ext.getCmp("watchGeoserverFieldSet_"+watchIndex).collapse();
       
        if(defaultValues.ebRIMCatalogue.length>0){
           Ext.getCmp("watchCatalogueFieldSet_"+watchIndex).expand(); 
           Ext.getCmp("catalogueUrl_"+watchIndex).setValue(defaultValues.ebRIMCatalogue[0]);
        }else
           Ext.getCmp("watchCatalogueFieldSet_"+watchIndex).collapse();
       
       if(defaultValues.openSearchCatalogueIngestion.length>0){
           Ext.getCmp("watchOpenSearchCatalogueFieldSet_"+watchIndex).expand(); 
           Ext.getCmp("catalogueOpenSearchUrl_"+watchIndex).setValue(defaultValues.openSearchCatalogue[0]);
           Ext.getCmp("catalogueOpenSearchIngestUrl_"+watchIndex).setValue(defaultValues.openSearchCatalogueIngestion[0]);
           Ext.getCmp("catalogueOpenSearchUrl_"+watchIndex).setValue(defaultValues.openSearchCatalogue[0]);
        }else
           Ext.getCmp("watchOpenSearchCatalogueFieldSet_"+watchIndex).collapse();

         if(defaultValues.SOS.length>0){
           Ext.getCmp("watchSOSFieldSet_"+watchIndex).expand();
           Ext.getCmp("SOSUrl_"+watchIndex).setValue(defaultValues.SOS[0]);
         }else
           Ext.getCmp("watchSOSFieldSet_"+watchIndex).collapse();
       }
    };
    
    
    this.getWatchData=function(watchIndex){
        var protocol;
        if(Ext.getCmp("watchType_"+watchIndex)){
            if(Ext.getCmp("watchType_"+watchIndex).getValue()=="")
                return this.watchListLoaded.watchList[watchIndex];
        }
    
    
        var watchData=new Watch();
        
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
    
        if(Ext.getCmp("watchFolder_"+watchIndex) && Ext.getCmp("watchFolder_"+watchIndex).validate()){
           value=Ext.getCmp("watchFolder_"+watchIndex).getValue();
           if(value){
               watchData.watchFolder=value;
               watchData.idString+=value;
           }else
              return null; 
        }else
           return null; 
    
        if(Ext.getCmp("watchDeleteAfterFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchDeleteAfterFieldSet_"+watchIndex).collapsed;
           if(!value){
               var deleteAfter=Ext.getCmp("deleteAfter_"+watchIndex).getValue();
               var deleteAfterUOM=Ext.getCmp("deleteAfterUOM_"+watchIndex).getValueInformation("value");
               if(deleteAfter && deleteAfterUOM)
                   if(deleteAfter!='' && deleteAfterUOM!=''){
                       watchData.deleteAfter=deleteAfter+deleteAfterUOM; 
                       watchData.idString+=deleteAfter+deleteAfterUOM;
                   }
                     
           }else
            watchData.idString+="false";    
        } 

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
                    protocol = value.substring(6,0);
                    if(protocol == "ftp://")
                       watchData.Ftp.push(value); 
                    else
                       watchData.Ftp.push("ftp://"+value); 
                   watchData.idString+=value;
                }
                  
             }   
           }else
            watchData.idString+="false";  
        }
        var ftpUP="";
        if(Ext.getCmp("ftpUser_"+watchIndex)){
           value=Ext.getCmp("ftpUser_"+watchIndex).getValue(); 
           if(value){
               watchData.idString+=value;
               ftpUP+=value+":";
           }
        }
        if(Ext.getCmp("ftpPassword_"+watchIndex)){
          value=Ext.getCmp("ftpPassword_"+watchIndex).getValue(); 
          if(value){
             ftpUP+=value;
             watchData.idString+=value;
          }
        }
        if(ftpUP!=""){
           var urlFtp=replaceAll(watchData.Ftp[watchData.Ftp.length-1], "ftp://","");
           watchData.Ftp[watchData.Ftp.length-1]="ftp://"+ftpUP+"@"+urlFtp;
        }
             
             
    
        if(Ext.getCmp("watchGeoserverFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchGeoserverFieldSet_"+watchIndex).collapsed;
           if(!value){
             watchData.GeoServer=new Array();
             if(Ext.getCmp("geoserverUrl_"+watchIndex)){
                value=Ext.getCmp("geoserverUrl_"+watchIndex).getValue(); 
                if(value){
                     protocol = value.substring(7,0);
                    if(protocol == "http://")
                        watchData.GeoServer.push(value); 
                    else
                        watchData.GeoServer.push("http://"+value);
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
             if(Ext.getCmp("geoserverFileNameTemplate_"+watchIndex)){
                value=Ext.getCmp("geoserverFileNameTemplate_"+watchIndex).getValue(); 
                if(value){
                    watchData.geoserverFileNameTemplate=value;
                    watchData.idString+=value;
                }
                  
             }
             if(Ext.getCmp("geoserverStyle_"+watchIndex)){
                value=Ext.getCmp("geoserverStyle_"+watchIndex).getValue(); 
                if(value){
                    watchData.geoserverStyle=value;
                    watchData.idString+=value;
                }
                  
             }
             if(Ext.getCmp("geoserverDimensions_"+watchIndex)){
                value=Ext.getCmp("geoserverDimensions_"+watchIndex).getValue(); 
                if(value){
                    watchData.geoserverDimensions=value;
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
                    protocol = value.substring(7,0);
                    if(protocol == "http://")
                        watchData.ebRIMCatalogue.push(value); 
                    else
                        watchData.ebRIMCatalogue.push("http://"+value);
                    watchData.idString+=value;
                }
             }   
           }else
            watchData.idString+="false";    
        }
        
        if(Ext.getCmp("watchOpenSearchCatalogueFieldSet_"+watchIndex)){
           value=Ext.getCmp("watchOpenSearchCatalogueFieldSet_"+watchIndex).collapsed;
           if(!value){
             watchData.openSearchCatalogueIngestion=new Array();
             watchData.openSearchCatalogue=new Array();
             
             if(Ext.getCmp("catalogueOpenSearchUrl_"+watchIndex)){
                value=Ext.getCmp("catalogueOpenSearchUrl_"+watchIndex).getValue(); 
                if(value){
                    protocol = value.substring(7,0);
                    if(protocol == "http://")
                        watchData.openSearchCatalogue.push(value); 
                    else
                        watchData.openSearchCatalogue.push("http://"+value);
                    watchData.idString+=value;
                }
             }
             
             if(Ext.getCmp("catalogueOpenSearchIngestUrl_"+watchIndex)){
                value=Ext.getCmp("catalogueOpenSearchIngestUrl_"+watchIndex).getValue(); 
                if(value){
                    protocol = value.substring(7,0);
                    if(protocol == "http://")
                        watchData.openSearchCatalogueIngestion.push(value); 
                    else
                        watchData.openSearchCatalogueIngestion.push("http://"+value);
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
                    protocol = value.substring(7,0);
                    if(protocol == "http://")
                        watchData.SOS.push(value); 
                    else
                        watchData.SOS.push("http://"+value);
                    watchData.idString+=value;
                }
             }   
           }else
            watchData.idString+="false";    
        }
        
       return watchData;  
    };
    
     this.onSave=function(){
        /* var maskWatch=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskWatch.show(); */
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
                //maskWatch.hide(); 
                if(jsonResponse.success){
                    Ext.Msg.show({
                    title:'Changes Saved',
                    msg: 'Number of watch saved: ' + watchSaved,
                    buttons: Ext.Msg.OK,
                    fn: function(){
                       armsManager.workspacePanel.cleanPanel();
                       armsManager.loadXmlInterface('WatchARMS',
                               "armsManager.watchInterface",
                              armsManager.watchInterfaceObj
                              ); 
                    },
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
                     interfacesManager.user, interfacesManager.password, 
                     800000, postWatchList, postWatchListTimeOut,null,
                     null, null);
        
    };

};


Watch=function(){
    this.idString="";
    this.watchFolder= null;  
    this.deleteAfter= "-1";
    this.hidden= false;
    this.type= null;
    this.downloadUrl= "";
    this.metadataUrl= "";
    this.Ftp= new Array();
    this.GeoServer= new Array();
    this.geoserverType= "";
    this.geoserverWorkspace= "";
    
    this.geoserverDimensions= "";
    this.geoserverFileNameTemplate= "";
    this.geoserverStyle= "";
   /* this.geoserverUser= "";
    this.geoserverPassword= "";*/
    this.ebRIMCatalogue= new Array();
    this.openSearchCatalogueIngestion= new Array();
    this.SOS= new Array();
    this.Http= null;
}

