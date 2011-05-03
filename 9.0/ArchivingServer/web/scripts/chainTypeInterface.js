
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
        var maskChainTypes=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskChainTypes.show(); 
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
                    if(! chainTypesCollection.chainTypesList[i].hidden)
                      chainTypesInterface.onAddChainType(chainTypesCollection.chainTypesList[i]);
                }
                maskChainTypes.hide();
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

     this.onAddChainType=function(defaultValues){
         var chainTypesMulti=Ext.getCmp('chainTypesMultiInput');
         
         // Add Chain Type Field Set
         chainTypesMulti.addFieldSet("chainTypeFieldSetSet_"+this.chainTypesNumber, 
                                    "Chain Type "+this.chainTypesNumber);
         
         // Add Chain Type Name
         chainTypesMulti.addTextField("chainTypeName_"+this.chainTypesNumber, 
         "Name", "", 20, "chainTypeFieldSetSet_"+this.chainTypesNumber);
         
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
         
   
         chainTypesMulti.doLayout();    
         
         Ext.getCmp("preProcessingFieldSetSet_"+this.chainTypesNumber).collapse();
         Ext.getCmp("metadataProcessingFieldSet_"+this.chainTypesNumber).collapse();
             
         if(defaultValues)
             this.loadChainTypeDefaultValues(this.chainTypesNumber, defaultValues);    
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
         var outputTypeStoreData=armsManager.getTypesList();
         multiType.addComboField("preProcessingOutputType_"+this.chainTypesNumber,
                         'Chain Output Type',10,outputTypeStoreFields,outputTypeStoreData,
                         null,"preProcessingFieldSetSet_"+this.chainTypesNumber);    
                         
         
         multiType.addSpace("preProcessingSpace4_"+this.chainTypesNumber,4, 
                                "preProcessingFieldSetSet_"+this.chainTypesNumber,2);
         
         // Add Metadata Processing Script Local File
         multiType.addFileField("preProcessingScript_"+this.chainTypesNumber, 
                                        "Processing Script", 50, "rest/storeddata/preProcessingScript_"+this.chainTypesNumber+"_file", 
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
                                   "Processing Script", 50, "rest/storeddata/metadataProcessingScript_"+this.chainTypesNumber+"_file", 
                                   null,
                                   "upload-icon",
                                   "resources/images/loaderFile.gif",
                                   "resources/images/fail.png",
                                   "resources/images/success.png", 
                                   "metadataProcessingFieldSet_"+this.chainTypesNumber,5);                          
        
    };
     
     
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
    
    this.loadChainTypeDefaultValues=function(chainTypeIndex, defaultValues){ 
        if(defaultValues.typeName)
          Ext.getCmp("chainTypeName_"+chainTypeIndex).setValue(defaultValues.typeName);
       
       
       
         if(defaultValues.ppIdScriptFileStored !=null){
          Ext.getCmp("preProcessingFieldSetSet_"+chainTypeIndex).expand();
          Ext.getCmp("preProcessingEngine_"+chainTypeIndex).setValue(defaultValues.ppEngineType);
          Ext.getCmp("preProcessingOutputType_"+chainTypeIndex).setValue(defaultValues.ppOuputType);
          Ext.getCmp("preProcessingScript_"+chainTypeIndex+"UploadID").setValue(defaultValues.ppIdScriptFileStored); 
        }else
           Ext.getCmp("preProcessingFieldSetSet_"+chainTypeIndex).collapse();
       
        
        if(defaultValues.mpIdScriptFileStored !=null){
          Ext.getCmp("metadataProcessingFieldSet_"+chainTypeIndex).expand();
          Ext.getCmp("metadataProcessingEngine_"+chainTypeIndex).setValue(defaultValues.mpEngineType);
          Ext.getCmp("metadataProcessingScript_"+chainTypeIndex+"UploadID").setValue(defaultValues.mpIdScriptFileStored); 
        }else
           Ext.getCmp("metadataProcessingFieldSet_"+chainTypeIndex).collapse();    
         

    };
    
    this.getChainTypeData=function(chainTypeIndex){
        var chainTypeData=new ChainType();
        var value;
   
        if(Ext.getCmp("chainTypeName_"+chainTypeIndex)){
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
             
             value=Ext.getCmp("preProcessingOutputType_"+chainTypeIndex).getValue();  
             chainTypeData.ppOuputType=value;
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
       

       return chainTypeData;  
    };
    
    
     this.onSave=function(){
        var maskChainTypes=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskChainTypes.show(); 
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
                maskChainTypes.hide(); 
                if(jsonResponse.success){
                    Ext.Msg.show({
                    title:'Changes Saved',
                    msg: 'Number of chain Types saved: ' + chainTypesSaved,
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
    this.ppOuputType= null;
    
    
    this.mpIdScriptFileStored=null;
    this.mpEngineType=null;

}