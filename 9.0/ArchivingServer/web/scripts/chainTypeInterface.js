
/*
 * ARMS Chain Types Manager Interface
 * @author: Andrea Marongiu
 */


ChainTypesInterface=function(){

     this.xmlInterface="resources/interfaces/chainTypePanel.xml";

     this.chainTypesNumber=0;
     this.formInterface=new ExtXmlInterface(this.xmlInterface);
     
     this.restChainTypesListURL="rest/config/chaintypes";
     
     this.chainTypesListLoaded=null;
     
     this.render=function (elementID){
       /* var maskChainTypes=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."
           }
        ); 
            
        maskChainTypes.show(); */
        armsManager.showWorkspaceLoadPanel(); 
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
        
        var getChainTypesListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Chain Types List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var chainTypesInterface=this;
         var getChainTypesList= function(response){
                var chainTypesCollection=JSON.parse(response);
                chainTypesInterface.chainTypesListLoaded=chainTypesCollection;
                for(var i=0; i<chainTypesCollection.chainTypesNumber;i++){
                    if(! chainTypesCollection.chainTypesList[i].hidden){
                      chainTypesInterface.onAddChainType(chainTypesCollection.chainTypesList[i],true);
                      
                    }
                      
                }

                Ext.getCmp('chainTypesMultiInput').doLayout();
               
                for(var i=0; i<chainTypesCollection.chainTypesNumber;i++){
                    if(! chainTypesCollection.chainTypesList[i].hidden){
                      Ext.getCmp("chainTypeFieldSetSet_"+i).collapse();
                    }
                }   
               // maskChainTypes.hide();
                armsManager.hideWorkspaceLoadPanel();
            };

         //var loginValues=this.formInterface.getFormValues();
        /* this.user= loginValues['user'];
           this.password=loginValues['password'];*/
        /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restChainTypesListURL,
                     true, null, "loginValues['user']", "loginValues['password']", 
                     800000, getChainTypesList, getChainTypesListTimeOut,null,
                     null, null);
        
        
     };

     this.onAddChainType=function(defaultValues, notRefresh){
         var chainTypesMulti=Ext.getCmp('chainTypesMultiInput');
         
         var setDefaultValues=null;
         
         var chainTypeName="New Chain Type "/*("+this.chainTypesNumber+")"*/;
         
         if(defaultValues){
             setDefaultValues="armsManager.chainTypesInterface.loadChainTypeDefaultValues("+this.chainTypesNumber+");";
             chainTypeName="Chain Type ( "+this.chainTypesListLoaded.chainTypesList[this.chainTypesNumber].typeName+" )";
         }
           
         
         // Add Chain Type Field Set
         chainTypesMulti.addFieldSet("chainTypeFieldSetSet_"+this.chainTypesNumber, 
                                    chainTypeName, null, false, setDefaultValues);
         
         // Add Chain Type Name
         if(defaultValues)
            chainTypesMulti.addTextField("chainTypeName_"+this.chainTypesNumber, 
            "Name", "", 20, "chainTypeFieldSetSet_"+this.chainTypesNumber,null,null,true);
         else
            chainTypesMulti.addTextField("chainTypeName_"+this.chainTypesNumber, 
            "Name", "", 20, "chainTypeFieldSetSet_"+this.chainTypesNumber,null, "newchaintype");
            
         chainTypesMulti.addSpace("chainTypeSpace2_"+this.chainTypesNumber,4, 
                          "chainTypeFieldSetSet_"+this.chainTypesNumber);            
         
         // Add Chain Type Remove Button
         chainTypesMulti.addButtonField("chainTypeDeleteButton_"+this.chainTypesNumber,'Remove',
                        "armsManager.chainTypesInterface.onDeleteChainType("+this.chainTypesNumber+")",
                        null, "chainTypeFieldSetSet_"+this.chainTypesNumber);                
         
         chainTypesMulti.addSpace("chainTypeSpace4_"+this.chainTypesNumber,4, 
                              "chainTypeFieldSetSet_"+this.chainTypesNumber,2);
                                
         //Add FieldSet Section    
         this.addPreProcessingOptions(chainTypesMulti);
         this.addMetadataProcessingOptions(chainTypesMulti);     
         this.addNotificationsOptions(chainTypesMulti);
         
         if(!notRefresh){
             chainTypesMulti.doLayout();    
             Ext.getCmp("preProcessingFieldSetSet_"+this.chainTypesNumber).collapse();
             Ext.getCmp("metadataProcessingFieldSet_"+this.chainTypesNumber).collapse();
             Ext.getCmp("notificationOptionsFieldSet_"+this.chainTypesNumber).collapse();
         }    
             
         /*if(defaultValues)
             this.loadChainTypeDefaultValues(this.chainTypesNumber, defaultValues);  */  
         this.chainTypesNumber++;    
    };
    
    this.addPreProcessingOptions=function(multiType){
        
        
        multiType.addFieldSet("preProcessingFieldSetSet_"+this.chainTypesNumber, 
             "Data pre processing", "chainTypeFieldSetSet_"+this.chainTypesNumber, true);
             
        
        // Add Pre Processing Engine Type Combo
         var preProcessingEngineStoreFields=['value'];
         var preProcessingEngineStoreData=[['shell']];
         
         multiType.addComboField("preProcessingEngine_"+this.chainTypesNumber,
                                'Engine',8,preProcessingEngineStoreFields,
                                preProcessingEngineStoreData,null,
                                "preProcessingFieldSetSet_"+this.chainTypesNumber); 
                                
         multiType.addSpace("preProcessingSpace3_"+this.chainTypesNumber,4, 
                                "preProcessingFieldSetSet_"+this.chainTypesNumber);  
                                
          // Add Pre Processing Output Type Combo                      
         var outputTypeStoreFields=['value'];
         //var outputTypeStoreData=armsManager.getTypesList();
         var outputTypeStoreData=armsManager.getWatchesList();
         
         multiType.addComboField("preProcessingOutputWatch_"+this.chainTypesNumber,
                         'Output Watch',66,outputTypeStoreFields,outputTypeStoreData,
                         null,"preProcessingFieldSetSet_"+this.chainTypesNumber);    
                         
         
         multiType.addSpace("preProcessingSpace4_"+this.chainTypesNumber,4, 
                                "preProcessingFieldSetSet_"+this.chainTypesNumber,2);
         
         // Add Metadata Processing Script Local File
         multiType.addFileField("preProcessingScript_"+this.chainTypesNumber, 
                                        "Processing Script", 75, "rest/storeddata/preProcessingScript_"+this.chainTypesNumber+"_file", 
                                        null,
                                        "upload-icon",
                                        "resources/images/loaderFile.gif",
                                        "resources/images/fail.png",
                                        "resources/images/success.png", 
                                        "preProcessingFieldSetSet_"+this.chainTypesNumber,5);
    };
    
    
    
    this.addMetadataProcessingOptions=function(multiType){
      multiType.addFieldSet("metadataProcessingFieldSet_"+this.chainTypesNumber, 
             "Metadata extraction processing", "chainTypeFieldSetSet_"+this.chainTypesNumber, true);
             
      // Add Metadata Processing Engine Type Combo
      var metadataProcessingEngineStoreFields=['value'];
      var metadataProcessingEngineStoreData=[['shell']];
         
      multiType.addComboField("metadataProcessingEngine_"+this.chainTypesNumber,
                                'Engine',8,metadataProcessingEngineStoreFields,
                                metadataProcessingEngineStoreData,null,
                                "metadataProcessingFieldSet_"+this.chainTypesNumber); 
                                
      multiType.addSpace("metadataProcessingSpace3_"+this.chainTypesNumber,4, 
                                "metadataProcessingFieldSet_"+this.chainTypesNumber,5);   
                                
      // Add Metadata Processing Script Local File
      multiType.addFileField("metadataProcessingScript_"+this.chainTypesNumber, 
                                   "Processing Script", 75, "rest/storeddata/metadataProcessingScript_"+this.chainTypesNumber+"_file", 
                                   null,
                                   "upload-icon",
                                   "resources/images/loaderFile.gif",
                                   "resources/images/fail.png",
                                   "resources/images/success.png", 
                                   "metadataProcessingFieldSet_"+this.chainTypesNumber,5);                          
        
    };
    
    
    this.addNotificationsOptions=function(multiType){
        multiType.addFieldSet("notificationOptionsFieldSet_"+this.chainTypesNumber, 
             "Notify Options", "chainTypeFieldSetSet_"+this.chainTypesNumber, true);
             
      // Add Notification Service URL
      multiType.addTextField("notificationServiceURL_"+this.chainTypesNumber, 
         "Service URL", "", 80, "notificationOptionsFieldSet_"+this.chainTypesNumber,5);
                                
      
                                
      // Add Notification Topic  
      multiType.addTextField("notifyTopic_"+this.chainTypesNumber, 
         "Topic", "", 38, "notificationOptionsFieldSet_"+this.chainTypesNumber);
                                
      multiType.addSpace("notificationSpace4_"+this.chainTypesNumber,3, 
                                "notificationOptionsFieldSet_"+this.chainTypesNumber,1); 
                                
      // Add Notification Event Type
      multiType.addTextField("notifyEventType_"+this.chainTypesNumber, 
         "Event Type", "", 38, "notificationOptionsFieldSet_"+this.chainTypesNumber);    
    }
     
     
     this.onDeleteAllChainTypes=function(){
         var chainTypeMulti=Ext.getCmp('chainTypesMultiInput');
         chainTypeMulti.removeAll(true); 
         chainTypeMulti.doLayout();  
    };
    
     this.onDeleteChainType=function(chainTypeIndex){

        var chainTypesMulti=Ext.getCmp('chainTypesMultiInput');
         
        var cmp=Ext.getCmp("chainTypeFieldSetSet_"+chainTypeIndex);
          if(cmp)
             cmp.destroy();
         
        chainTypesMulti.doLayout();    
    };
    
    this.loadChainTypeDefaultValues=function(chainTypeIndex){ 
        
        var defaultValues=this.chainTypesListLoaded.chainTypesList[chainTypeIndex];
        
        if(defaultValues.typeName)
          Ext.getCmp("chainTypeName_"+chainTypeIndex).setValue(defaultValues.typeName);
       
       
       
         if(defaultValues.ppIdScriptFileStored !=null){
          Ext.getCmp("preProcessingFieldSetSet_"+chainTypeIndex).expand();
          Ext.getCmp("preProcessingEngine_"+chainTypeIndex).setValue(defaultValues.ppEngineType);
          if(defaultValues.ppOutputWatch=="")
              Ext.getCmp("preProcessingOutputWatch_"+chainTypeIndex).setValue("No Output Watch");
          else
              Ext.getCmp("preProcessingOutputWatch_"+chainTypeIndex).setValue(defaultValues.ppOutputWatch);
          Ext.getCmp("preProcessingScript_"+chainTypeIndex+"UploadID").setValue(defaultValues.ppIdScriptFileStored); 
        }else
           Ext.getCmp("preProcessingFieldSetSet_"+chainTypeIndex).collapse();
       
        
        if(defaultValues.mpIdScriptFileStored !=null){
          Ext.getCmp("metadataProcessingFieldSet_"+chainTypeIndex).expand();
          Ext.getCmp("metadataProcessingEngine_"+chainTypeIndex).setValue(defaultValues.mpEngineType);
          Ext.getCmp("metadataProcessingScript_"+chainTypeIndex+"UploadID").setValue(defaultValues.mpIdScriptFileStored); 
        }else
           Ext.getCmp("metadataProcessingFieldSet_"+chainTypeIndex).collapse();    
         
        if(defaultValues.notifyURL !=null){
           Ext.getCmp("notificationOptionsFieldSet_"+chainTypeIndex).expand();
           Ext.getCmp("notificationServiceURL_"+chainTypeIndex).setValue(defaultValues.notifyURL);
           Ext.getCmp("notifyTopic_"+chainTypeIndex).setValue(defaultValues.notifyTopic);
           Ext.getCmp("notifyEventType_"+chainTypeIndex).setValue(defaultValues.notifyEventType);  
            
        } else
            Ext.getCmp("notificationOptionsFieldSet_"+chainTypeIndex).collapse();
    };
    
    this.getChainTypeData=function(chainTypeIndex){
        
        
        if(Ext.getCmp("chainTypeName_"+chainTypeIndex)){
            if(Ext.getCmp("chainTypeName_"+chainTypeIndex).getValue()=="")
                return this.chainTypesListLoaded.chainTypesList[chainTypeIndex];
        }
        
        var chainTypeData=new ChainType();
        var value;
   
        if(Ext.getCmp("chainTypeName_"+chainTypeIndex) && Ext.getCmp("chainTypeName_"+chainTypeIndex).validate()){
           value=Ext.getCmp("chainTypeName_"+chainTypeIndex).getValue();
           if(value){
               chainTypeData.typeName=value;
               chainTypeData.idString+=value;
           }else
              return null; 
        }else
           return null; 
       
       
       if(Ext.getCmp("preProcessingFieldSetSet_"+chainTypeIndex)){
           value=Ext.getCmp("preProcessingFieldSetSet_"+chainTypeIndex).collapsed;
           if(!value){
             value=Ext.getCmp("preProcessingEngine_"+chainTypeIndex).getValue();  
             chainTypeData.ppEngineType=value;
             chainTypeData.idString+=value;
             
             value=Ext.getCmp("preProcessingOutputWatch_"+chainTypeIndex).getValue();  
             if(value=='No Output Watch')
                 value="";
             chainTypeData.ppOutputWatch=value;
             chainTypeData.idString+=value;
             
             value=Ext.getCmp("preProcessingScript_"+chainTypeIndex+"UploadID").getValue();  
             chainTypeData.ppIdScriptFileStored=value;
             chainTypeData.idString+=value;
           }
        }
       
       
       if(Ext.getCmp("metadataProcessingFieldSet_"+chainTypeIndex)){
           value=Ext.getCmp("metadataProcessingFieldSet_"+chainTypeIndex).collapsed;
           if(!value){
             value=Ext.getCmp("metadataProcessingEngine_"+chainTypeIndex).getValue();  
             chainTypeData.mpEngineType=value;
             chainTypeData.idString+=value;
 
             value=Ext.getCmp("metadataProcessingScript_"+chainTypeIndex+"UploadID").getValue();  
             chainTypeData.mpIdScriptFileStored=value;
             chainTypeData.idString+=value;
           }
        }
       
       if(Ext.getCmp("notificationOptionsFieldSet_"+chainTypeIndex)){
           value=Ext.getCmp("notificationOptionsFieldSet_"+chainTypeIndex).collapsed;
           if(!value){
             value=Ext.getCmp("notificationServiceURL_"+chainTypeIndex).getValue();  
             chainTypeData.notifyURL=value;
             chainTypeData.idString+=value;
 
             value=Ext.getCmp("notifyTopic_"+chainTypeIndex).getValue();  
             chainTypeData.notifyTopic=value;
             chainTypeData.idString+=value;
             
             value=Ext.getCmp("notifyEventType_"+chainTypeIndex).getValue();  
             chainTypeData.notifyEventType=value;
             chainTypeData.idString+=value;
           }
        }

       return chainTypeData;  
    };
    
    
     this.onSave=function(){
        /*var maskChainTypes=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskChainTypes.show(); */
         var chainTypesListJSON=new Object();
         var chainTypesSaved=0;
         chainTypesListJSON.chainTypesList=new Array();
         var chainTypeData;

         for(var i=0; i<this.chainTypesNumber; i++){
             chainTypeData=this.getChainTypeData(i);
             if(chainTypeData)
                chainTypesListJSON.chainTypesList.push(chainTypeData);    
         }
         chainTypesSaved=chainTypesListJSON.chainTypesList.length;
         
         for(i=0; i<this.chainTypesListLoaded.chainTypesNumber; i++){
             if(this.chainTypesListLoaded.chainTypesList[i].hidden)
              chainTypesListJSON.chainTypesList.push(this.chainTypesListLoaded.chainTypesList[i]);    
         }
         chainTypesListJSON.chainTypesNumber=chainTypesListJSON.chainTypesList.length;
        
      
        
        var postChainTypesListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Save Chain Type List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var postChainTypesList= function(response){
                var jsonResponse=JSON.parse(response);
               // maskChainTypes.hide(); 
                if(jsonResponse.success){
                    Ext.Msg.show({
                    title:'Changes Saved',
                    msg: 'Number of chain Types saved: ' + chainTypesSaved,
                    fn: function(){
                        armsManager.workspacePanel.cleanPanel();
                        armsManager.loadXmlInterface('ChainTypesARMS',
                                   "armsManager.chainTypesInterface",
                                  armsManager.chainTypesInterfaceObj
                                  );
                    },
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
                     this.restChainTypesListURL,
                     true, JSON.stringify(chainTypesListJSON, null),
                     "loginValues['user']", "loginValues['password']", 
                     800000, postChainTypesList, postChainTypesListTimeOut,null,
                     null, null); 
    };

};


ChainType=function(){
    this.idString="";
    this.hidden= false;
    this.typeName= null;
    
    this.ppIdScriptFileStored=null;
    this.ppEngineType=null;
    this.ppOutputWatch= null;
    
    
    this.mpIdScriptFileStored=null;
    this.mpEngineType=null;
    
    this.notifyURL=null;
    this.notifyTopic=null;
    this.notifyEventType=null;

}