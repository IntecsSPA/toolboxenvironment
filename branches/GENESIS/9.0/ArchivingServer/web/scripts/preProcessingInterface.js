
/*
 * Toolbox Set Pre Processing Interface
 * @author: Andrea Marongiu
 */


PreProcessingInterface=function(){

     this.xmlInterface="resources/interfaces/preProcessingPanel.xml";

     this.preProcessingNumber=0;
     this.formInterface=new ExtXmlInterface(this.xmlInterface);
     
     this.restPreProcessingListURL="rest/config/preprocessing";
     
     this.preProcessingListLoaded=null;
     
     this.render=function (elementID){
        var maskPreProcessing=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskPreProcessing.show(); 
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
        
        var getPreProcessingListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Get Pre Processing List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var preProcessingInterface=this;
         var getPreProcessingList= function(response){
                var preProcessingCollection=JSON.parse(response);
                preProcessingInterface.preProcessingListLoaded=preProcessingCollection;
                for(var i=0; i<preProcessingCollection.dataProcessingNumber;i++){
                    if(! preProcessingCollection.dataProcessingList[i].hidden)
                        preProcessingInterface.onAddPreProcessing(preProcessingCollection.dataProcessingList[i]);
                }
                maskPreProcessing.hide();
            };

         //var loginValues=this.formInterface.getFormValues();
        /* this.user= loginValues['user'];
           this.password=loginValues['password'];*/
        /* interfacesManager.user= loginValues['user'];
           interfacesManager.password=loginValues['password'];*/
           
           sendAuthenticationXmlHttpRequestTimeOut("GET",
                     this.restPreProcessingListURL,
                     true, null, "loginValues['user']", "loginValues['password']", 
                     800000, getPreProcessingList, getPreProcessingListTimeOut,null,
                     null, null);
        
        
     };

     this.onAddPreProcessing=function(defaultValues){
         var preProcessingMulti=Ext.getCmp('preProcessingMultiInput');
         
         // Add Pre Processing Field Set
         preProcessingMulti.addFieldSet("preProcessingFieldSetSet_"+this.preProcessingNumber, 
                                    "Pre processing "+this.preProcessingNumber);
         
         // Add Pre Processing Name Type
         preProcessingMulti.addTextField("preProcessingType_"+this.preProcessingNumber, 
         "Type", "", 20, "preProcessingFieldSetSet_"+this.preProcessingNumber);
         
         preProcessingMulti.addSpace("preProcessingSpace2_"+this.preProcessingNumber,4, 
                          "preProcessingFieldSetSet_"+this.preProcessingNumber);
         
         // Add Pre Processing Engine Type Combo
         var preProcessingEngineStoreFields=['value'];
         var preProcessingEngineStoreData=[['shell']];
         
         preProcessingMulti.addComboField("preProcessingEngine_"+this.preProcessingNumber,
                                'Engine',8,preProcessingEngineStoreFields,
                                preProcessingEngineStoreData,null,
                                "preProcessingFieldSetSet_"+this.preProcessingNumber); 
                                
         preProcessingMulti.addSpace("preProcessingSpace3_"+this.preProcessingNumber,4, 
                                "preProcessingFieldSetSet_"+this.preProcessingNumber);  
                                
          // Add Pre Processing Output Type Combo                      
         var outputTypeStoreFields=['value'];
         var outputTypeStoreData=armsManager.getTypesList();
         preProcessingMulti.addComboField("preProcessingOutputType_"+this.preProcessingNumber,
                         'Output Type',10,outputTypeStoreFields,outputTypeStoreData,
                         null,"preProcessingFieldSetSet_"+this.preProcessingNumber);                       
         
         // Add Metadata Processing Script Local File
         preProcessingMulti.addFileField("preProcessingScript_"+this.preProcessingNumber, 
                                        "Processing Script", 50, "rest/storeddata", 
                                        null,
                                        "upload-icon",
                                        "resources/images/loaderFile.gif",
                                        "resources/images/fail.png",
                                        "resources/images/success.png", 
                                        "preProcessingFieldSetSet_"+this.preProcessingNumber,3);
         
         preProcessingMulti.addSpace("preProcessingSpace4_"+this.preProcessingNumber,4, 
                                "preProcessingFieldSetSet_"+this.preProcessingNumber);
         
         // Add Metadata Processing Remove Button
         preProcessingMulti.addButtonField("preProcessingDeleteButton_"+this.preProcessingNumber,'Remove',
                        "armsManager.preProcessingInterface.onDeletePreProcessing("+this.preProcessingNumber+")",
                        null, "preProcessingFieldSetSet_"+this.preProcessingNumber);
             
         preProcessingMulti.doLayout();    
             
         if(defaultValues)
             this.loadPreProcessingDefaultValues(this.preProcessingNumber, defaultValues);    
         this.preProcessingNumber++;    
    };
     
     
     this.onDeleteAllPreProcessing=function(){
         var preProcessingMulti=Ext.getCmp('preProcessingMultiInput');
         preProcessingMulti.removeAll(true); 
         preProcessingMulti.doLayout();  
    };
    
     this.onDeletePreProcessing=function(preProcessingIndex){

        var preProcessingMulti=Ext.getCmp('preProcessingMultiInput');
         
        var cmp=Ext.getCmp("preProcessingFieldSetSet_"+preProcessingIndex);
          if(cmp)
             cmp.destroy();
         
        preProcessingMulti.doLayout();    
    };
    
    this.loadPreProcessingDefaultValues=function(preProcessingIndex, defaultValues){ 
        if(defaultValues.type)
          Ext.getCmp("preProcessingType_"+preProcessingIndex).setValue(defaultValues.type);
       
        if(defaultValues.engineType)
          Ext.getCmp("preProcessingEngine_"+preProcessingIndex).setValue(defaultValues.engineType);
       
        if(defaultValues.ouputType)
          Ext.getCmp("preProcessingOutputType_"+preProcessingIndex).setValue(defaultValues.ouputType);
      
        if(defaultValues.idScriptFileStored){
           Ext.getCmp("preProcessingScript_"+preProcessingIndex+"UploadID").setValue(defaultValues.idScriptFileStored); 
            
        }  

    };
    
    this.getPreProcessingData=function(preProcessingIndex){
        var preProcessingData=new PreProcessing();
        var value;
   
        if(Ext.getCmp("preProcessingType_"+preProcessingIndex)){
           value=Ext.getCmp("preProcessingType_"+preProcessingIndex).getValue();
           if(value){
               preProcessingData.type=value;
               preProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null; 
       
       
       if(Ext.getCmp("preProcessingEngine_"+preProcessingIndex)){
           value=Ext.getCmp("preProcessingEngine_"+preProcessingIndex).getValue();
           if(value){
               preProcessingData.engineType=value;
               preProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null;
       
       
       if(Ext.getCmp("preProcessingOutputType_"+preProcessingIndex)){
           value=Ext.getCmp("preProcessingOutputType_"+preProcessingIndex).getValue();
           if(value){
               preProcessingData.ouputType=value;
               preProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null;
       
       
       if(Ext.getCmp("preProcessingScript_"+preProcessingIndex+"UploadID")){
           value=Ext.getCmp("preProcessingScript_"+preProcessingIndex+"UploadID").getValue();
           if(value){
               preProcessingData.idScriptFileStored=value;
               preProcessingData.idString+=value;
           }else
              return null; 
        }else
           return null;
       

       return preProcessingData;  
    };
    
    
     this.onSave=function(){
        var maskPreProcessing=new Ext.LoadMask(armsManager.workspacePanel.body,
            {msg:"Please wait..."}
        ); 
        maskPreProcessing.show(); 
         var preProcessingListJSON=new Object();
         var preProcessingSaved=0;
         preProcessingListJSON.dataProcessingList=new Array();
         var preProcessingData;

         for(var i=0; i<this.preProcessingNumber; i++){
             preProcessingData=this.getPreProcessingData(i);
             alert(JSON.stringify(preProcessingData, null));
             if(preProcessingData)
                preProcessingListJSON.dataProcessingList.push(preProcessingData);    
         }
         preProcessingSaved=preProcessingListJSON.dataProcessingList.length;
         
         for(i=0; i<this.preProcessingListLoaded.dataProcessingNumber; i++){
             if(this.preProcessingListLoaded.dataProcessingList[i].hidden)
              preProcessingListJSON.dataProcessingList.push(this.preProcessingListLoaded.dataProcessingList[i]);    
         }
         preProcessingListJSON.dataProcessingNumber=preProcessingListJSON.dataProcessingList.length;
        
      
        
        var postPreProcessingListTimeOut=function(){
                   Ext.Msg.show({
                       title:'Save Pre Processing List: Error',
                       buttons: Ext.Msg.OK,
                       msg: 'Request TIME-OUT!',
                       animEl: 'elId',
                       icon: Ext.MessageBox.ERROR
                   });
            }
            
         var postPreProcessingList= function(response){
                var jsonResponse=JSON.parse(response);
                maskPreProcessing.hide(); 
                if(jsonResponse.success){
                    Ext.Msg.show({
                    title:'Changes Saved',
                    msg: 'Number of pre processing saved: ' + preProcessingSaved,
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
                     this.restPreProcessingListURL,
                     true, JSON.stringify(preProcessingListJSON, null),
                     "loginValues['user']", "loginValues['password']", 
                     800000, postPreProcessingList, postPreProcessingListTimeOut,null,
                     null, null); 
    };

};


PreProcessing=function(){
    this.idString="";
    this.hidden= false;
    this.type= null;
    this.engineType=null;
    this.ouputType= null;
    this.idScriptFileStored=null;
    

}