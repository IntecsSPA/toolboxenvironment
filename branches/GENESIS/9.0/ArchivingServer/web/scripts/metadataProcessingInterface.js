
/*
 * Toolbox Set Metadata Processing Interface
 * @author: Andrea Marongiu
 */


MetadataProcessingInterface=function(){

     this.xmlInterface="resources/interfaces/metadataProcessingPanel.xml";

     this.metadataProcessingNumber=0;
     
     this.metadataProcessingListLoaded=null;
     
     this.restMetadataProcessingListURL="rest/config/metadata/processing";
     
     this.formInterface=new ExtXmlInterface(this.xmlInterface);

     this.render=function (elementID){
         
        var maskMetadataProcessing=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskMetadataProcessing.show();
        
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
        
        
        var getMetadataProcessingListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Metadata Processing List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var metadataProcessingInterface=this;
         var getMetadataProcessingList= function(response){
                var metadataProcessingCollection=JSON.parse(response);
                metadataProcessingInterface.metadataProcessingListLoaded=metadataProcessingCollection;
                for(var i=0; i<metadataProcessingCollection.metadataProcessingNumber;i++){
                    if(! metadataProcessingCollection.metadataProcessingList[i].hidden)
                        metadataProcessingInterface.onAddMetadataProcessing(metadataProcessingCollection.metadataProcessingList[i]);
                }
                maskMetadataProcessing.hide();
            };

         //var loginValues=this.formInterface.getFormValues();
        /* this.user= loginValues['user'];
           this.password=loginValues['password'];*/
        /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restMetadataProcessingListURL,
                     true, null, "loginValues['user']", "loginValues['password']", 
                     800000, getMetadataProcessingList, getMetadataProcessingListTimeOut,null,
                     null, null);
     };

     this.onAddMetadataProcessing=function(defaultValues){
         
         this.metadataProcessingNumber++;
         var metadataProcessingMulti=Ext.getCmp('metadataProcessingMultiInput');
         
         // Add Metadata Processing Field Set
         metadataProcessingMulti.addFieldSet("metadataProcessingFieldSet_"+this.metadataProcessingNumber, 
                                    "Metadata processing "+this.metadataProcessingNumber);
         
         // Add Metadata Processing Type Text
         metadataProcessingMulti.addTextField("metadataProcessingType_"+this.metadataProcessingNumber, 
         "Type", "", 20, "metadataProcessingFieldSet_"+this.metadataProcessingNumber);
         
         metadataProcessingMulti.addSpace("metadataProcessingSpace2_"+this.metadataProcessingNumber,
                  4,"metadataProcessingFieldSet_"+this.metadataProcessingNumber);
                  
                  
         // Add Metadata Processing Engine Type Combo
         var preProcessingEngineStoreFields=['value'];
         var preProcessingEngineStoreData=[['shell']];
         
         metadataProcessingMulti.addComboField("metadataProcessingEngine_"+this.metadataProcessingNumber,
                                'Engine',8,preProcessingEngineStoreFields,
                                preProcessingEngineStoreData,null,
                                "metadataProcessingFieldSet_"+this.metadataProcessingNumber); 
                                
         metadataProcessingMulti.addSpace("metadataProcessingSpace3_"+this.metadataProcessingNumber,4, 
                                "metadataProcessingFieldSet_"+this.metadataProcessingNumber,2); 
                                
          // Add Metadata Processing Output Type Combo                      
         /*var outputTypeStoreFields=['value'];
         var outputTypeStoreData=armsManager.getTypesList();
         metadataProcessingMulti.addComboField("metadataProcessingOutputType_"+this.metadataProcessingNumber,
                         'Output Type',10,outputTypeStoreFields,outputTypeStoreData,
                         null,"metadataProcessingFieldSet_"+this.metadataProcessingNumber); */
                         
                         
         // Add Metadata Processing Script Local File
         metadataProcessingMulti.addFileField("metadataProcessingScript_"+this.metadataProcessingNumber,
                                        "Processing Script", 50, "rest/storeddata", 
                                        null,
                                        "upload-icon",
                                        "resources/images/loaderFile.gif",
                                        "resources/images/fail.png",
                                        "resources/images/success.png",
                                        "metadataProcessingFieldSet_"+this.metadataProcessingNumber,3);
         
         metadataProcessingMulti.addSpace("metadataProcessingSpace4_"+this.metadataProcessingNumber,
                                           4,"metadataProcessingFieldSet_"+this.metadataProcessingNumber);
         
         // Add Metadata Processing Remove Button
         metadataProcessingMulti.addButtonField("preProcessingDeleteButton_"+this.metadataProcessingNumber,'Remove',
                        "armsManager.metadataProcessingInterface.onDeleteMetadataProcessing("+this.metadataProcessingNumber+")",
                        null,"metadataProcessingFieldSet_"+this.metadataProcessingNumber);
                                        
         metadataProcessingMulti.doLayout();
         
         if(defaultValues)
             this.loadMetadataProcessingDefaultValues(this.metadataProcessingNumber, defaultValues);    
         
         this.metadataProcessingNumber++; 
   
    };
     
     
     this.onDeleteAllMetadataProcessing=function(){
        var metadataProcessingMulti=Ext.getCmp('metadataProcessingMultiInput');
        metadataProcessingMulti.removeAll(true);
        metadataProcessingMulti.doLayout();  
    };
    
     this.onDeleteMetadataProcessing=function(metadataProcessingIndex){
         var metadataProcessingMulti=Ext.getCmp('metadataProcessingMultiInput');
         
         /*var cmp=Ext.getCmp("metadataProcessingType_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy();

         cmp=Ext.getCmp("metadataProcessingSpace1_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy()*/
        
         cmp=Ext.getCmp("metadataProcessingName_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy()
        
        cmp=Ext.getCmp("metadataProcessingSpace2_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy();
         
        cmp=Ext.getCmp("metadataProcessingScript_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy()
        
        cmp=Ext.getCmp("metadataProcessingSpace3_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy(); 
        
        cmp=Ext.getCmp("preProcessingDeleteButton_"+metadataProcessingIndex);
         if(cmp)
             cmp.destroy()
          
        metadataProcessingMulti.doLayout();  
        
    };
    
    this.loadMetadataProcessingDefaultValues=function(metadataProcessingIndex, defaultValues){
        
       if(defaultValues.type)
          Ext.getCmp("metadataProcessingType_"+metadataProcessingIndex).setValue(defaultValues.type);
       
        if(defaultValues.engineType)
          Ext.getCmp("metadataProcessingEngine_"+metadataProcessingIndex).setValue(defaultValues.engineType);
      
      
        if(defaultValues.idScriptFileStored)
           Ext.getCmp("metadataProcessingScript_"+metadataProcessingIndex+"UploadID").setValue(defaultValues.idScriptFileStored); 
        
        
    },
    
    this.getMetadataProcessingData=function(metadataProcessingIndex){
        var metadataProcessingData=new MetadataProcessing();
        var value;
   
        if(Ext.getCmp("metadataProcessingType_"+metadataProcessingIndex)){
           value=Ext.getCmp("metadataProcessingType_"+metadataProcessingIndex).getValue();
           if(value){
               metadataProcessingData.type=value;
               metadataProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null; 
       
       
       if(Ext.getCmp("metadataProcessingEngine_"+metadataProcessingIndex)){
           value=Ext.getCmp("metadataProcessingEngine_"+metadataProcessingIndex).getValue();
           if(value){
               metadataProcessingData.engineType=value;
               metadataProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null;

       if(Ext.getCmp("metadataProcessingScript_"+metadataProcessingIndex+"UploadID")){
           value=Ext.getCmp("metadataProcessingScript_"+metadataProcessingIndex+"UploadID").getValue();
           if(value){
               metadataProcessingData.idScriptFileStored=value;
               metadataProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null;
       

       return metadataProcessingData;  
    };
    
    
     this.onSave=function(){
        var maskMetadataProcessing=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskMetadataProcessing.show(); 
         var metadataProcessingListJSON=new Object();
         var metadataProcessingSaved=0;
         metadataProcessingListJSON.metadataProcessingList=new Array();
         var metadataProcessingData;

         for(var i=0; i<this.metadataProcessingNumber; i++){
             metadataProcessingData=this.getMetadataProcessingData(i);
             if(metadataProcessingData)
                metadataProcessingListJSON.metadataProcessingList.push(metadataProcessingData);    
         }
         metadataProcessingSaved=metadataProcessingListJSON.metadataProcessingList.length;
         
         for(i=0; i<this.metadataProcessingListLoaded.metadataProcessingNumber; i++){
             if(this.metadataProcessingListLoaded.metadataProcessingList[i].hidden)
              metadataProcessingListJSON.metadataProcessingList.push(this.metadataProcessingListLoaded.metadataProcessingList[i]);    
         }
         metadataProcessingListJSON.metadataProcessingNumber=metadataProcessingListJSON.metadataProcessingList.length;
        
      
        
        var postMetadataProcessingListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Save Metadata Processing List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var postMetadataProcessingList= function(response){
                var jsonResponse=JSON.parse(response);
                maskMetadataProcessing.hide(); 
                if(jsonResponse.success){
                    Ext.Msg.show({
                    title:'Changes Saved',
                    msg: 'Number of metadata processing saved: ' + metadataProcessingSaved,
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
                     this.restMetadataProcessingListURL,
                     true, JSON.stringify(metadataProcessingListJSON, null),
                     "loginValues['user']", "loginValues['password']", 
                     800000, postMetadataProcessingList, postMetadataProcessingListTimeOut,null,
                     null, null); 
   
          
    };

};

MetadataProcessing=function(){
    this.idString="";
    this.hidden= false;
    this.type= null;
    this.engineType=null;
    this.idScriptFileStored=null;
    

}