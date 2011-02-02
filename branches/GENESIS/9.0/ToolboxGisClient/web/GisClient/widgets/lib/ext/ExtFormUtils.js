/******************************************************
 *          EXTJS FORM UTIL                           *  
 * ****************************************************/
//-- Global
var numberColsField=3;
var numberColsForm;
var supportToolbars=new Array();
var proxyRedirect="ProxyRedirect";
var barProgress;

var gisClientError="";
//--- New vtype definition

Ext.apply(Ext.form.VTypes, {
  daterange: function(val, field) {
   
    var date = field.parseDate(val);
    
    // We need to force the picker to update values to recaluate the disabled dates display
    var dispUpd = function(picker) {
      var ad = picker.activeDate;
      picker.activeDate = null;
      picker.update(ad);
    };
    
    if (field.startDateField) {
      var sd = Ext.getCmp(field.startDateField);
      sd.maxValue = date;
      if (sd.menu && sd.menu.picker) {
        sd.menu.picker.maxDate = date;
        dispUpd(sd.menu.picker);
      }
    } else if (field.endDateField) {
      var ed = Ext.getCmp(field.endDateField);
      ed.minValue = date;
      if (ed.menu && ed.menu.picker) {
        ed.menu.picker.minDate = date;
        dispUpd(ed.menu.picker);
      }
    }
    /* Always return true since we're only using this vtype
     * to set the min/max allowed values (these are tested
     * for after the vtype test)
     */
    return true;
  },
  
  timerange: function(val, field) {
   
    var startTime,endTime;
    var newHStore, temp=new Array(), i;
    var hStart,mStart,hEnd,mEnd,check;
    if(field.hStart)
       if(Ext.getCmp(field.hStart).getValue()) 
          hStart=Ext.getCmp(field.hStart).getValue();
       else
          return true; 
    else{
       hStart=val;  
       /*/temp=new Array();
       for(i=val;i<field.maxH;i++)
          if(i<10)
            temp[i]="0"+i;
      else  
        temp[i]=i;
      newHStore=new Ext.data.Store({
           data:temp 
      });
      Ext.getCmp(field.hEnd).store=newHStore;  */
      Ext.getCmp(field.mStart).validate();
    }
    if(field.mStart)
       if(Ext.getCmp(field.mStart).getValue())  
          mStart=Ext.getCmp(field.mStart).getValue();
       else
          return true;   
    else{
       mStart=val; 
    }
    if(field.hEnd)
       if(Ext.getCmp(field.hEnd).getValue())   
          hEnd=Ext.getCmp(field.hEnd).getValue();
       else
         return true; 
    else{
       hEnd=val;
       /*/temp=new Array();
       for(i=0;i<val+1;i++)
          if(i<10)
            temp[i]="0"+i;
      else  
        temp[i]=i;
      newHStore=new Ext.data.Store({
           data:temp 
      });
      Ext.getCmp(field.hStart).store=newHStore; */ 
      Ext.getCmp(field.mEnd).validate();
    }
    if(field.mEnd)
       if(Ext.getCmp(field.mEnd).getValue()) 
          mEnd=Ext.getCmp(field.mEnd).getValue();
       else
          return true;   
    else{
       mEnd=val; 
      // Ext.getCmp(field.hEnd).validate();
      
    }
    startTime=""+hStart+mStart;
    endTime=""+hEnd+mEnd;
 
    return (endTime >= startTime);
  },
  timerangeText: 'The Start Time must be smaller of the End Time',
  
  password: function(val, field) {
    if (field.initialPassField) {
      var pwd = Ext.getCmp(field.initialPassField);
      return (val == pwd.getValue());
    }
    return true;
  },
  passwordText: 'Passwords do not match',
  
  latitude: function(val) {
      return ((val >= -90)&&(val <= 90));
  },
  latitudeText: 'The Latitude Coordinate is between -90 and 90.',
  
  longitude: function(val) {
      return ((val >= -180)&&(val <= 180));
  },
  longitudeText: 'The Longitude Coordinate is between -180 and 180.',
  
  percentage: function(val) {
      return ((val >= 0)&&(val <= 100));
  },
  percentageText: 'The Percentage is between 0 and 100.',
  
  seconds: function(val){
     return ((val >= 0)&&(val <= 59)); 
  },
  secondsText: 'The seconds value is between 0 and 59.',
  
  minutes: function(val){
     return ((val >= 0)&&(val <= 59)); 
  },
  minutesText: 'The minutes value is between 0 and 59.',
  
  millseconds: function(val){
     return ((val >= 0)&&(val <= 999)); 
  },
  millsecondsText: 'The millseconds value is between 0 and 999.'
  
});



// ----end new vtype definition

function generateFormFieldSet(title, fieldSets, numCol){
  var newForm;
 
  newForm = new Ext.FormPanel({
        frame: true,
        title: title,
        autoShow : true,
        width: '100%',
        items: generateListOfFieldSet(fieldSets, numCol)
    });
    
  return(newForm);  
    
}

function createPanelExjFormByXml(xmlDocument){
  var contentFormPanel, tabPanel, forms=null, idElementTabs=null;  
  var formObjCreated;
  forms= new Array();
  var fieldSetConfValues=new Array();
  var inputInterfaceXml;
    inputInterfaceXml = Sarissa.getDomDocument();
    inputInterfaceXml.async=false;
    inputInterfaceXml.validateOnParse=false;
    inputInterfaceXml.load(xmlDocument);
    inputInterfaceXml.setProperty("SelectionLanguage","XPath");
    Sarissa.setXpathNamespaces(inputInterfaceXml,
                       "xmlns:gis='http://gisClient.pisa.intecs.it/gisClient'");
  var sectionInterfaceElements;
    sectionInterfaceElements=
               inputInterfaceXml.selectNodes("/gis:inputInterface/gis:section");
           
  if(sectionInterfaceElements.length >1){
    var nameTab,cols;

    
    idElementTabs= new Array();
    var items= new Array();
    
    for(var i=0;i<sectionInterfaceElements.length;i++){
        nameTab =sectionInterfaceElements[i].getAttribute("name"); 
        cols =sectionInterfaceElements[i].getAttribute("cols"); 
        if(!cols)
            cols=numberColsField;
        else
          cols=cols*numberColsField;
        numberColsForm=cols;
        idElementTabs[i]=nameTab+"Div";
        items[i]={
              id: nameTab,
              title: nameTab,
              enableTabScroll:true,
              autoScroll:true,
              html: "<div id='"+idElementTabs[i]+"'></div>",
              closable:false
        };
       formObjCreated=createExjFormByElement("",sectionInterfaceElements[i],cols); 
  
       fieldSetConfValues.push(formObjCreated.valuesControl);
       forms[i]= formObjCreated.fieldSet;   
    }    
    tabPanel=new Ext.TabPanel({
        activeTab: 0,
       /*/ minTabWidth: 115,
        tabWidth:135,*/
        enableTabScroll:true,
        defaults: {autoScroll:true},
        items: items
    });  
    
  }else{
   idElementTabs= new Array();   
   nameTab =sectionInterfaceElements[0].getAttribute("name"); 
   cols =sectionInterfaceElements[0].getAttribute("cols");
   if(!cols)
          cols=numberColsField;
        else
          cols=cols*numberColsField;
   numberColsForm=cols;
   idElementTabs[0]=nameTab+"Div";
   var  item={
              id: nameTab,
              //title: nameTab,
              enableTabScroll:true,
              autoScroll:true,
              html: "<div id='"+idElementTabs[0]+"'></div>",
              closable:false
        };
    formObjCreated=createExjFormByElement("",sectionInterfaceElements[0],cols);
    fieldSetConfValues.push(formObjCreated.valuesControl);
    forms[0]=formObjCreated.fieldSet;
    tabPanel=new Ext.Panel({
        items: [item],
        closable:false});
  }   
  var panels;
  var buttonElements;
  buttonElements=inputInterfaceXml.selectNodes("/gis:inputInterface/gis:requestInformations/gis:buttons/gis:button");     
  var type,onclickFunction,disabled;  
  var contentButtonPanel;
  if(buttonElements.length>0){
    var buttons= new Array();  
    for(i=0;i<buttonElements.length;i++){
       disabled=buttonElements[i].getAttribute("disabled");  
       onclickFunction=buttonElements[i].getAttribute("onclick"); 
        buttons[i]={
                    colspan: 1,
                      
                    items: [new Ext.Button({
                          text: buttonElements[i].getAttribute("label"),
                          handler: eval(onclickFunction),
                          disabled: disabled,
                          icon:buttonElements[i].getAttribute("icon")
                          })]
        };   
    }   
    contentButtonPanel=new Ext.Panel({
        border:false,
        layout: 'table',
        layoutConfig: {
                     columns: buttonElements.length
                  },
        enableTabScroll:false,
        bodyStyle : {background: '#99bbe8'},
        items: buttons,
        closable:false});
    panels=[tabPanel,contentButtonPanel];
  }
  else{
      panels=[tabPanel]; 
      contentButtonPanel=null;
  }
     

    contentFormPanel=new Ext.Panel({
        autoScroll: true,
        border:false,
        bodyStyle : {background: '#99bbe8'},
        items: panels
    }); 
  var outputManager;
  var outputInformationElements=inputInterfaceXml.selectNodes("/gis:inputInterface/gis:outputInformations");
  if(outputInformationElements.length>0){  
     outputManager= getOutputMangerByElement(outputInformationElements[0]);
  }   
  else
     outputManager=null; 
 
  var formObj={
                formsPanel: contentFormPanel,
                formsTab: tabPanel,
                buttonPanel: contentButtonPanel,
                formsArray: forms,
                tabElementsId: idElementTabs,
                confValues: fieldSetConfValues,
                responseType: "",
                outputManager: outputManager,
                getDimFormPanel: function(){
                    return contentFormPanel.getSize();
                },
                destroy: function (){
                   this.formsTab.destroy();
                   if(this.buttonPanel)
                      this.buttonPanel.destroy();
                   this.formsPanel.destroy(); 
                },
                render: function(){
                    var a,b;
                      if(this.formsArray.length > 1) 
                        for(var i=0; i<this.formsArray.length;i++){
                          this.formsTab.setActiveTab(i);                
                          this.formsArray[i].render(document.getElementById(this.tabElementsId[i])); 
                          a=this.formsArray[i].getSize();
                        // alert("formSize: "+a.heigth);
                        }
                      else{
                         this.formsArray[0].render(document.getElementById(this.tabElementsId[0])); 
                         a=this.formsArray[0].getSize();
                         
                      //   alert("formSize: "+a.width);
                      }   
                    for(var u=0;u<supportToolbars.length;u++){
                      supportToolbars[u].toolbar.render(supportToolbars[u].id);
                      var toc2 = new WebGIS.Control.Toc({map: map, parseWMS: false, autoScroll: true});
                      for(var j=0; j<supportToolbars[u].buttons.length; j++){
                          var button=eval(supportToolbars[u].buttons[j]);
                          supportToolbars[u].toolbar.add(button);
                      }
                      toc2.update();
                    } 
                },
                resetFormValues: function(){
                   var xtypeArray=["textfield","combo","datefield","numberfield","checkbox"];
                   var input,i,u,j;
                   for(i=0; i<this.formsArray.length; i++ ){
                      for(u=0; u<xtypeArray.length; u++){
                         input=this.formsArray[i].findByType(xtypeArray[u]); 
                         for(j=0; j<input.length; j++){
                             if( !input[j].disabled )
                             input[j].setValue("");          
                         }
                      }
                    }  
                },
                setDefaultValues: function (values){
                  var field,type,i,u;
                   for(i=0; i<this.formsArray.length; i++ ){
                      for(u=0; u<this.confValues[i].length; u++){
                         field=this.formsArray[i].getForm().findField(this.confValues[i][u].id); 
                         type=this.confValues[i][u].type;
                        // alert(this.confValues[i][u].type);
                       /*/  alert(values[this.confValues[i][u].id]);
                         alert(field);*/
                           
                         if(this.confValues[i][u].id!="idRequest")   
                           switch(type) {
                            case "text":
                                      
                                     field.setValue(values[this.confValues[i][u].id]); 
                                    break; 
                            case "label":
                                      if(values[this.confValues[i][u].id])
                                        field.setValue(values[this.confValues[i][u].id]); 
                                    break; 
                            case "password":
                                     field.setValue(values[this.confValues[i][u].id]);
                                    break;
                            case "numeric":
                                    //alert(this.confValues[i][u].id);
                                    field.setValue(values[this.confValues[i][u].id]); 
                                    break;
                            case "checkbox":
                                    field.setValue(values[this.confValues[i][u].id]); 
                                    break;        

                            case "combo":
                                   // alert(this.confValues[i][u].id);
                                    field.enable();
                                    field.store=values[this.confValues[i][u].id].store;
                                    field.setValue(values[this.confValues[i][u].id].value); 
                                    break;

                            case "bbox":
                                   /*/ alert("BBOX");
                                    alert(this.confValues[i][u].id);
                                    alert(values[this.confValues[i][u].id]);*/
                                    var split=values[this.confValues[i][u].id].split(',');
                                    this.formsArray[i].getForm().findField(this.confValues[i][u].id+'WestBBOX').setValue(split[0]); 
                                    this.formsArray[i].getForm().findField(this.confValues[i][u].id+'SouthBBOX').setValue(split[1]); 
                                    this.formsArray[i].getForm().findField(this.confValues[i][u].id+'EastBBOX').setValue(split[2]); 
                                    this.formsArray[i].getForm().findField(this.confValues[i][u].id+'NorthBBOX').setValue(split[3]); 
                                    //Disegnare Area .....
                                    break;          

                            case "rangedate":
                                    var range=values[this.confValues[i][u].id];
                                    this.formsArray[i].getForm().findField(this.confValues[i][u].id+'StartDate').setValue(range.startDate); 
                                    this.formsArray[i].getForm().findField(this.confValues[i][u].id+'EndDate').setValue(range.endDate); 
                                    break;
                            case "date":
                                    field.setValue(values[this.confValues[i][u].id]); 
                                    break;     
                            case "time":
                                   // alert(values[this.confValues[i][u].id]);
                                    var timeSplit=values[this.confValues[i][u].id].split('-'); 
                                    this.formsArray[i].getForm().findField('h'+this.confValues[i][u].id).setValue(timeSplit[0]);
                                    this.formsArray[i].getForm().findField('m'+this.confValues[i][u].id).setValue(timeSplit[1]);
                                    this.formsArray[i].getForm().findField('s'+this.confValues[i][u].id).setValue(timeSplit[2]);
                                    this.formsArray[i].getForm().findField('ms'+this.confValues[i][u].id).setValue(timeSplit[3]);
                                    break;
                            case "rangetime":
                                  //  alert(values[this.confValues[i][u].id]);
                                    var timeSplitStart=values[this.confValues[i][u].id].startTime.split('-'); 
                                    var timeSplitEnd=values[this.confValues[i][u].id].endTime.split('-'); 
                                    this.formsArray[i].getForm().findField('hStart'+this.confValues[i][u].id).setValue(timeSplitStart[0]);
                                    this.formsArray[i].getForm().findField('mStart'+this.confValues[i][u].id).setValue(timeSplitStart[1]);
                                    this.formsArray[i].getForm().findField('hEnd'+this.confValues[i][u].id).setValue(timeSplitEnd[0]);
                                    this.formsArray[i].getForm().findField('mEnd'+this.confValues[i][u].id).setValue(timeSplitEnd[1]);
                                    break;
                          }
                      }
                    }                      
                },
                getResponseType: function(){
                  if(this.responseType == "")
                      this.getFormValues();
                  return this.responseType;
                },
                getFormValues: function(label){
                  var xtypeArray;  
                  
                  if(label)  
                    xtypeArray=["textfield","combo","datefield","numberfield","checkbox","field"];
                  else    
                    xtypeArray=["textfield","combo","datefield","numberfield","checkbox"];  
                  var input,i,u,j;
                  var idRequest="";
                  var formValues=new Array();
                  var complexValues=new Array();
                  for(i=0; i<this.formsArray.length; i++ ){
                      for(u=0; u<xtypeArray.length; u++){
                         input=this.formsArray[i].findByType(xtypeArray[u]); 
                         for(j=0; j<input.length; j++){
                             if(!input[j].validate())
                                return(null); 
                             
                             if((xtypeArray[u] == "combo") && label) 
                                 formValues[input[j].getItemId()]={
                                          id: input[j].getItemId(),
                                          value:input[j].value,
                                          store: input[j].store
                                      };
                             else     
                                 if(xtypeArray[u] == "field") {
                                     formValues[input[j].getItemId()]={
                                              id: input[j].getItemId(),
                                              value:input[j].value
                                          };
                                 }else
                                   formValues[input[j].getItemId()]={
                                              id: input[j].getItemId(),
                                              value:input[j].getValue()
                                          };  
                                
                             if(input[j].getItemId() == "outputFormat")    
                                this.responseType=input[j].getValue();
                                      
                         }
                      }
                    }  
                  formValues["idRequest"]="";
                  var tempform,tempFormat,tempTime1,tempTime2;  
                  
                  for(i=0;i<this.confValues.length;i++){
                     tempform=this.confValues[i];
                     
                     for(u=0;u<tempform.length;u++){
                        
                        switch(tempform[u].type) {
                          case "text":
                                    if(formValues[tempform[u].id].value){
                                        complexValues[tempform[u].id]=formValues[tempform[u].id].value;
                                        idRequest+=formValues[tempform[u].id].value;
                                    }else
                                        complexValues[tempform[u].id]=null;
                                  break; 
                          case "label":
                                  if(label)
                                    if(formValues[tempform[u].id].value ){
                                        complexValues[tempform[u].id]=formValues[tempform[u].id].value;
                                       // idRequest+=formValues[tempform[u].id].value;
                                    }else
                                        complexValues[tempform[u].id]=null;
                                  break;
                          case "numeric":
                                  //alert(tempform[u].id);
                                  if(formValues[tempform[u].id].value){
                                      complexValues[tempform[u].id]=formValues[tempform[u].id].value;
                                      idRequest+=formValues[tempform[u].id].value;
                                    }  
                                  else
                                      complexValues[tempform[u].id]='0';
                                  //alert(complexValues[tempform[i].id]);
                                  break;
                          case "password":
                                   if(formValues[tempform[u].id].value){
                                        complexValues[tempform[u].id]=formValues[tempform[u].id].value;
                                        idRequest+=formValues[tempform[u].id].value;
                                   }else
                                       complexValues[tempform[u].id]=null;
                                  break;
                          case "checkbox":
                                  //alert(tempform[u].id);
                                  //alert(formValues[tempform[u].id].value);
                                  //if(formValues[tempform[u].id].value){
                                      complexValues[tempform[u].id]=""+formValues[tempform[u].id].value;
                                      idRequest+=formValues[tempform[u].id].value;
                                    //}
                                  //else
                                    //  complexValues[tempform[u].id]=null;
                                  //alert(complexValues[tempform[i].id]);
                                  break;        
                                  
                          case "combo":
                                  //alert(tempform[u].id);
                                  //alert(formValues[tempform[u].id].value);
                                  if(formValues[tempform[u].id].value){
                                     if(!label){
                                       complexValues[tempform[u].id]=formValues[tempform[u].id].value;
                                      idRequest+=formValues[tempform[u].id].value;  
                                     }else{
                                        
                                        complexValues[tempform[u].id]={
                                                              value:formValues[tempform[u].id].value,
                                                              store: formValues[tempform[u].id].store
                                                            }; 
                                     } 
                                      
                                    } 
                                  else
                                      complexValues[tempform[u].id]=null;
                                  //alert(complexValues[tempform[i].id]);
                                  break;
                                  
                          case "bbox":
                                 /*/ alert("WEST: " +formValues[tempform[u].id+'WestBBOX'].value);
                                  alert("EAST: " +formValues[tempform[u].id+'EastBBOX'].value);
                                  alert("NORTH: " +formValues[tempform[u].id+'NorthBBOX'].value);
                                  alert("SOUTH: " +formValues[tempform[u].id+'SouthBBOX'].value);*/
                                  if((formValues[tempform[u].id+'WestBBOX'].value != '')&&
                                     (formValues[tempform[u].id+'EastBBOX'].value != '')&&
                                     (formValues[tempform[u].id+'NorthBBOX'].value != '')&&
                                     (formValues[tempform[u].id+'SouthBBOX'].value != '')&&
                                     (formValues[tempform[u].id+'WestBBOX'].value > -180)&&
                                     (formValues[tempform[u].id+'EastBBOX'].value > -180)&&
                                     (formValues[tempform[u].id+'NorthBBOX'].value > -90)&&
                                     (formValues[tempform[u].id+'SouthBBOX'].value > -90))
                                    {   
                                      tempFormat=tempform[u].format;
                                      tempFormat = tempFormat.replace("W",
                                        formValues[tempform[u].id+'WestBBOX'].value);
                                      tempFormat = tempFormat.replace("E",
                                        formValues[tempform[u].id+'EastBBOX'].value);
                                      tempFormat = tempFormat.replace("N",
                                        formValues[tempform[u].id+'NorthBBOX'].value);
                                      tempFormat = tempFormat.replace("S",
                                        formValues[tempform[u].id+'SouthBBOX'].value);    
                                      complexValues[tempform[u].id]=tempFormat;
                                      idRequest+=tempFormat;
                                  }else
                                    complexValues[tempform[u].id]=null;      
                                  //alert("BBOX: " + complexValues[tempform[i].id]);
                                  break;          
                           
                          case "rangedate":
                                  //alert(tempform[u].id);
                                  if(formValues[tempform[u].id+'StartDate'].value &&
                                      formValues[tempform[u].id+'EndDate'].value){
                                      complexValues[tempform[u].id]={
                                          startDate: formValues[tempform[u].id+'StartDate'].value.format(tempform[u].format),
                                          endDate: formValues[tempform[u].id+'EndDate'].value.format(tempform[u].format)
                                      };
                                    idRequest+=complexValues[tempform[u].id].startDate;  
                                    idRequest+=complexValues[tempform[u].id].endDate; 
                                  }else
                                    complexValues[tempform[u].id]=null;  
                                  //alert("StartDate:" +complexValues[tempform[u].id].startDate);
                                  //alert("EndDate:" +complexValues[tempform[u].id].endDate);
                                  break;
                          case "date":
                                
                                  if(formValues[tempform[u].id].value){
                                      if(!label){
                                          complexValues[tempform[u].id]=formValues[tempform[u].id].value.format(tempform[u].format);
                                          idRequest+=formValues[tempform[u].id].value.format(tempform[u].format);    
                                      }else
                                          complexValues[tempform[u].id]=formValues[tempform[u].id].value; 
                                  }else
                                    complexValues[tempform[u].id]=null;  
                                   
                                  break;     
                          case "time":
                                /*  alert(tempform[u].id);
                                  alert(formValues['h'+tempform[u].id].value);
                                  alert(formValues['m'+tempform[u].id].value);
                                  alert(formValues['s'+tempform[u].id].value);
                                  alert(formValues['ms'+tempform[u].id].value);*/
                                                         
                                      if(!formValues['ms'+tempform[u].id].value)  
                                         formValues['ms'+tempform[u].id].value="000"; 
                                      if(formValues['h'+tempform[u].id].value &&
                                         formValues['m'+tempform[u].id].value  && 
                                         formValues['s'+tempform[u].id].value /*&&
                                         formValues['ms'+tempform[u].id].value*/){
                                         if(!label){                                
                                              tempFormat=tempform[u].format;
                                              tempTime1=tempFormat.replace("H", formValues['h'+tempform[u].id].value);
                                              tempTime1=tempTime1.replace("h", formValues['h'+tempform[u].id].value);
                                              tempTime1=tempTime1.replace("m", formValues['m'+tempform[u].id].value);
                                              tempTime1=tempTime1.replace("s", formValues['s'+tempform[u].id].value);
                                              tempTime1=tempTime1.replace("ms", formValues['ms'+tempform[u].id].value);
                                              complexValues[tempform[u].id]=tempTime1;
                                              idRequest+=complexValues[tempform[u].id]; 
                                         }else{
                                              complexValues[tempform[u].id]=formValues['h'+tempform[u].id].value+"-"+
                                                                  formValues['m'+tempform[u].id].value+"-"+
                                                                  formValues['s'+tempform[u].id].value+"-"+
                                                                  formValues['ms'+tempform[u].id].value;
                                         }       
                                                                               
                                      }else
                                        complexValues[tempform[u].id]=null;  
                                  
                                  break;
                          case "rangetime":
                                  //alert(tempform[u].id);
                                  if(formValues['hStart'+tempform[u].id].value &&
                                     formValues['mStart'+tempform[u].id].value  && 
                                     formValues['hEnd'+tempform[u].id].value &&
                                     formValues['mEnd'+tempform[u].id].value){
                                      if(!label){  
                                          tempFormat=tempform[u].format;
                                          tempTime1=tempFormat.replace("H", formValues['hStart'+tempform[u].id].value);
                                          tempTime1=tempTime1.replace("h", formValues['hStart'+tempform[u].id].value);
                                          tempTime1=tempTime1.replace("i", formValues['mStart'+tempform[u].id].value);
                                          tempTime2=tempFormat.replace("H", formValues['hEnd'+tempform[u].id].value);
                                          tempTime2=tempTime2.replace("h", formValues['hEnd'+tempform[u].id].value);
                                          tempTime2=tempTime2.replace("i", formValues['mEnd'+tempform[u].id].value);
                                          complexValues[tempform[u].id]={
                                              startTime: tempTime1,
                                              endTime: tempTime2
                                          };
                                          idRequest+=complexValues[tempform[u].id].startTime;  
                                          idRequest+=complexValues[tempform[u].id].endTime;
                                      }else{
                                        complexValues[tempform[u].id]={
                                              startTime: formValues['hStart'+tempform[u].id].value+"-"+
                                                                  formValues['mStart'+tempform[u].id].value+"-",
                                              endTime: formValues['hEnd'+tempform[u].id].value+"-"+
                                                                  formValues['mEnd'+tempform[u].id].value+"-"
                                          };  
                                      }     
                                  }else    
                                    complexValues[tempform[u].id]=null;   
                                  break;
                        } 
              
                     }
                  } 
                 complexValues["idRequest"]=idRequest; 
                 return(complexValues); 
                },
                getXmlKeyValueDocument: function(returnType, namespace){
                    var formKeyValue=new OpenLayers.Format.XMLKeyValue();
                    var keyValueObj={
                        confValues: this.confValues,
                        formValues: this.getFormValues()
                    };
                    return(formKeyValue.write(keyValueObj, {returnType: returnType, namespace: namespace}));
                },
                 sendXmlKeyValueRequest: function(renderObjectId,timeout){
                   var timeoutRequest;
                   if(timeout)
                     timeoutRequest=timeout;
                   else
                     timeoutRequest=60;
                   var outputManager=this.outputManager;
                   var serviceRequest=this.getXmlKeyValueDocument("String");
                   var serviceResponse="";
                   var ajax = assignXMLHttpRequest(); 
                   var proxyResponseUrlTag="responseUrl";
                   var objElementOutput=document.getElementById(renderObjectId);
                   barProgress=Ext.Msg.progress("GisClient", 'Sending request', "Please Wait.." );
                   objElementOutput.innerHTML="<br><br><table width='100%'><tr><td align='center'><img src='style/img/loader/loader1.gif'></td></tr><tr><td align='center'>Please Wait...</td></tr></table>";
                   var onload=this.executeonLoadOperations;
                   var keyValueResponse= function(response){
                          barProgress=barProgress.updateProgress(0.7,  "Response Recived", "Request Sent" );
                          var outputStore;
                          objElementOutput.innerHTML="";
                          serviceResponse=response;
                          var startUrl=serviceResponse.indexOf("<"+proxyResponseUrlTag+">")+2+proxyResponseUrlTag.length;
                          var url=serviceResponse.substr(startUrl);
                          url=url.substr(0,url.indexOf("</"+proxyResponseUrlTag+">"));
                          switch(outputManager.container){
                              case "grid":
                                          outputStore=new Ext.data.Store({
                                                        nocache : true,
                                                        url: url,
                                                        reader : outputManager.readerTempalte,
                                                        remoteSort : false//,
                                                   
                                          });
                                          
                                          outputStore.load({
                                              callback: function(){
                                                  barProgress=barProgress.updateProgress(1,  "Processing...", "Response Recived" );
                                                  setTimeout('barProgress.hide()',800);
                                                  onload(outputPanel.store,outputManager);
                                              }
                                          });  
                                          
                                          var outputPanel = new Ext.grid.GridPanel({
                                                  store: outputStore,
                                                  colModel: outputManager.colMod,
                                                  autoHeight : true,
                                                  trackMouseOver:true,
                                                  sm: new Ext.grid.RowSelectionModel({selectRow:Ext.emptyFn}),
                                                  plugins: outputManager.plugins,
                                                  loadMask: true,
                                                  viewConfig: {
                                                            forceFit:true
                                                  },
                                      
                                                  split: true,
                                                  renderTo: renderObjectId
                                          });
                                          break;
                          }
                  };
                  var keyValueResponseTimeOut= function(){
                      Ext.Msg.alert('Error', 'Time OUT Xml Key Value Request');
                  };
                  var keyValueError= function(response){
                      if(response)
                         gisClientError="<br><br><p align='center'><textarea rows='"+screen.height/36+"' cols='"+ screen.width/18+"'>"+response+"</textarea></p>";
                      else
                         gisClientError="Not Details";
                     if(!document.getElementById('ErrorGisClient')){
                        var errorNode=document.createElement('div');
                        errorNode.setAttribute("id", "ErrorGisClient");
                        document.getElementsByTagName("body")[0].appendChild(errorNode);
                     }   
                      var windDetailsError="javascript:document.getElementById(\'ErrorGisClient\').innerHTML=gisClientError;new Ext.Window({"+
                            "title: 'Error Details',"+
                            "border: false,"+
                            "animCollapse : true,"+
                            "autoScroll : true,"+
                            "resizable : true,"+
                            "collapsible: true,"+
                            "maximizable: true,"+
                            "layout: 'fit',"+
                            "width: "+ screen.width/2+","+
			    "height: "+ screen.height/2+","+
                            "closeAction:'close',"+
                            "contentEl: 'ErrorGisClient'"+
                       "}).show();";
                      objElementOutput.innerHTML="<br><br><table width='100%'><tr><td align='center'><img src='style/img/error.png'></td></tr>"+
                                                 "<tr><td align='center'>Response ERROR</td></tr>"+
                                                 "<tr><td align='center'><a href=\""+windDetailsError+"\">Details</a></td></tr></table>";
                                             //"html: '<textarea id='ErrorGisClientText'row='"+screen.height/10+"' cols='"+ screen.width/10+"'><textarea>',"+
                  }
                  barProgress=barProgress.updateProgress(0.2,  "Waiting Response", "Request Sent");
                  sendXmlHttpRequestTimeOut("POST", 
                                proxyRedirect, 
                                true, serviceRequest, timeoutRequest, keyValueResponse, keyValueResponseTimeOut, null, null, keyValueError);
                },
                executeonLoadOperations:function(store,outMan){
                    var i;
                          for(var z=0; z<outMan.onLoadOperations.length; z++){
                             switch(outMan.onLoadOperations[z].type){
                                  case "render":
                                      if(outMan.onLoadOperations[z].vectorLayer){
                                          var mapObj=eval(outMan.onLoadOperations[z].mapObjcetName);
                                          mapObj.removeLayer(outMan.onLoadOperations[z].vectorLayer);
                                          outMan.onLoadOperations[z].vectorLayer=null;
                                      }    
                                      for(i=0;i<store.getCount();i++){
                                         //outMan.onLoadOperations[z].actionOnLoad(store.getAt(i).data[outMan.onLoadOperations[z].attributeGeometry],'lat,lon',' ');
                                         outMan.onLoadOperations[z].actionOnLoad(store.getAt(i).data[outMan.onLoadOperations[z].attributeGeometry]);
                                      }
                                      break;
                             } 
                          }
                }
                
              };
  return(formObj);
}

function createExjFormByElement(title, formDataElement, numCols){

   var valuesControl=new Array(); 
   var groupFormElements;
  
    groupFormElements= formDataElement.selectNodes("gis:group");
 
 
   var inputFormElements,optionInputElements;
   var fieldSets=new Array();
   var inputArray = new Array();
   var optionArray = new Array();
   var i,j,value="",label;
   for(var u=0; u<groupFormElements.length;u++){
      inputArray = new Array();
      
          inputFormElements= groupFormElements[u].selectNodes("gis:input");
         
      for(i=0; i<inputFormElements.length; i++){
        
          optionInputElements =inputFormElements[i].selectNodes("gis:option");
        
      
       for(j=0; j<optionInputElements.length;j++){
             value=optionInputElements[j].getAttribute("value");
             label=optionInputElements[j].firstChild.nodeValue;
            
             if(value==null)
                optionArray[j]=[label, label];
             else
                optionArray[j]=[value, label];       
       }
       
       inputArray[i]={
                        name: inputFormElements[i].getAttribute("name"),
                        type: inputFormElements[i].getAttribute("type"),
                          id: inputFormElements[i].getAttribute("id"),
                        size: inputFormElements[i].getAttribute("size"),
                        icon: inputFormElements[i].getAttribute("iconImage"),
                     colSpan: inputFormElements[i].getAttribute("colSpan"),
                        cols: inputFormElements[i].getAttribute("cols"),
                        rows: inputFormElements[i].getAttribute("rows"),
                       label: inputFormElements[i].getAttribute("label"),
                  labelStart: inputFormElements[i].getAttribute("labelStart"),
                    labelEnd: inputFormElements[i].getAttribute("labelEnd"),
                        grow: inputFormElements[i].getAttribute("grow"),
                  labelStyle: inputFormElements[i].getAttribute("labelStyle"),
              labelSeparator: inputFormElements[i].getAttribute("labelSeparator"),
                      hidden: inputFormElements[i].getAttribute("hidden"),
                    disabled: inputFormElements[i].getAttribute("disabled"),
               maxLengthText: inputFormElements[i].getAttribute("maxLengthText"),
               minLengthText: inputFormElements[i].getAttribute("minLengthText"),
                       vtype: inputFormElements[i].getAttribute("contentControl"),
                   vtypeText: inputFormElements[i].getAttribute("NotValidValueMessage"),
                 invalidText: inputFormElements[i].getAttribute("invalidText"),
                   blankText: inputFormElements[i].getAttribute("blankText"),
                  allowBlank: inputFormElements[i].getAttribute("optional"),
                      format: inputFormElements[i].getAttribute("format"),
                       value: inputFormElements[i].getAttribute("value"),
                   labelList: inputFormElements[i].getAttribute("labelList"),
                   valueList: inputFormElements[i].getAttribute("valueList"),
                 valueCheked: inputFormElements[i].getAttribute("valueCheked"),
            decimalSeparator: inputFormElements[i].getAttribute("decimalSeparator"),
            decimalPrecision: inputFormElements[i].getAttribute("decimalPrecision"),
                   hideLabel: inputFormElements[i].getAttribute("hideLabel"),
                    onChange: inputFormElements[i].getAttribute("onChange"),
             onclickFunction: inputFormElements[i].getAttribute("onclick"),
             enableInputList: inputFormElements[i].getAttribute("enableInputList"),
      formObjectInstanceName: inputFormElements[i].getAttribute("formObjectInstanceName"),
           handlerParameters: inputFormElements[i].getAttribute("handlerParameters"),
                       store: inputFormElements[i].getAttribute("store"),
                      action: inputFormElements[i].getAttribute("action"),
                      target: inputFormElements[i].getAttribute("target"),
                 submitLabel: inputFormElements[i].getAttribute("submitLabel"),
                   storeData: eval(inputFormElements[i].getAttribute("storeData")),
                 storeFields: eval(inputFormElements[i].getAttribute("storeFields")),
                  secondsDiv: eval(inputFormElements[i].getAttribute("secondsDiv")),
                     seconds: eval(inputFormElements[i].getAttribute("seconds"))
                  };
                  
       valuesControl.push(inputArray[i]);            
      }
     
     fieldSets[u] = { name: groupFormElements[u].getAttribute("label"),
                      fields: inputArray
                 }; 
                 
     inputArray = null;            
   }
   valuesControl.push({
                          name:"idRequest",
                          id:"idRequest",
                          type:"text",
                          value: ""
                      }); 
   return({
           fieldSet: generateFormFieldSet(title, fieldSets, numCols),
           valuesControl: valuesControl  
          }); 
}

function getOutputMangerByElement(outputInformationElement){
  var outputManager={
        container: "",
        onLoadOperations:null,
        readerTempalte: null,
        plugins: null,
        colMod: null
  };  
  var templateElement= outputInformationElement.selectNodes("gis:template");
  var templateContainer=templateElement[0].getAttribute("container");
  var templateFormat=templateElement[0].getAttribute("format");
  var rootStore=templateElement[0].getAttribute("rootStore");
  var nameAttributes=templateElement[0].getAttribute("attributeNamesStore");
  var titleAttributes=templateElement[0].getAttribute("attributeTitlesStore");
  var splitlNameAttributes=nameAttributes.split(",");
  var splitlTitleAttributes=titleAttributes.split(",");
  var record= new Array();
  for(var i=0; i<splitlNameAttributes.length;i++){
      record.push({name : splitlTitleAttributes[i], mapping: splitlNameAttributes[i]});  
  }
  if(templateFormat == "json"){ 
     var readerOutput = new Ext.data.JsonReader({
                        root : rootStore
                        }, 
                        Ext.data.Record.create(record)); 
     outputManager.readerTempalte=readerOutput;                   
  }
  switch(templateContainer){
         case "grid":
                     outputManager.container="grid";
                     var gridAttrbutesNode= templateElement[0].selectNodes("gis:gridAttrbutes");
                     var gridAttrbutes=gridAttrbutesNode[0].getAttribute("value").split(",");
                     var widthCols=gridAttrbutesNode[0].getAttribute("widthCols");
                     var sortable=gridAttrbutesNode[0].getAttribute("sortable");
                     var tempalteHtmlElement=templateElement[0].selectNodes("gis:tempalteHtml");
                     var generalHtmlTemplate=tempalteHtmlElement[0].getAttribute("value");
                     var tempalteOperations= templateElement[0].selectNodes("gis:templateOperations/gis:templateOperation");
                     var onloadOperations= templateElement[0].selectNodes("gis:onloadOperations/gis:onloadOperation");
                     var onLoadOperationsObjects=new Array();
                     var columsGrid= new Array();
                     for(i=0; i<gridAttrbutes.length; i++){
                         columsGrid.push("{id:'"+ gridAttrbutes[i]+"', header: '"+
                                         gridAttrbutes[i]+
                                         "', width: "+widthCols+", dataIndex: '"+
                                         gridAttrbutes[i]+
                                         "', sortable: "+sortable+"}"); 
                     }  
                     var opertaionHtml="";
                     var temp;
                     for(i=0; i<onloadOperations.length; i++){
                         temp=createCodeOnLoadOperation(onloadOperations[i]);
                         onLoadOperationsObjects.push(temp);
                     } 
                     for(i=0; i<tempalteOperations.length; i++){
                         opertaionHtml+=createHtmlTemplateOperation(tempalteOperations[i]);
                     }    
                     var expander = 
                         new Ext.grid.RowExpander({
                            tpl : new Ext.Template(generalHtmlTemplate+opertaionHtml)
                     });
                     var temp="[expander,";
                                    for(i=0;i<columsGrid.length;i++)
                                        if(i== (columsGrid.length-1))
                                            temp+=columsGrid[i]+"]";  
                                        else    
                                            temp+=columsGrid[i]+",";
                     var colMod=new Ext.grid.ColumnModel(eval(temp));
                     outputManager.onLoadOperations=onLoadOperationsObjects;
                     outputManager.colMod=colMod;
                     outputManager.plugins=expander;
                     break;
  }   
  return(outputManager);
}

function createHtmlTemplateOperation(templateOperationElement){
  var labelButton,imageButton,imageDimMin,imageDimMax;
  var type=templateOperationElement.getAttribute("type");


  var style,mapObjcetName,layerFillOpacity,layerGraphicOpacity,geometry,attributeGeometry,formatPoint,pointSeparator,mapLayerName;
  var posList,posListDimension,layerTitle,urlAttribute;
  switch(type){
         case "details":
                     var tempString="";
                     var attributes=templateOperationElement.getAttribute("attributes").split(",");
                     var container=templateOperationElement.getAttribute("container");
                     var idAttribute=templateOperationElement.getAttribute("idAttribute");
                     var htmlDetailsLine=templateOperationElement.getAttribute("htmlDetailsLine");
                     var layoutStart=templateOperationElement.getAttribute("htmlLayoutStart");
                     var layoutEnd=templateOperationElement.getAttribute("htmlLayoutEnd");
                     var groups=templateOperationElement.selectNodes("gis:group");
                     var tempHtml, tempHtmlLine, tempAttributes, z;
                     var groupsHtml="";
                       
                     if(groups.length>0){   
                        for(var k=0; k<groups.length;k++){
                            tempAttributes=null; 
                            tempAttributes=groups[k].getAttribute("attributes").split(",");
                            if(tempAttributes.length ==0)
                               tempAttributes[0]=groups[k].getAttribute("attributes");  
                            tempHtmlLine=groups[k].getAttribute("htmlLine");
                            tempHtml=groups[k].getAttribute("htmlStart");
                            for(z=0;z<tempAttributes.length;z++){
                                tempString=tempHtmlLine.replace("$attributeValue",tempAttributes[z]); 
                                tempHtml+=tempString.replace("{$attributeName}", tempAttributes[z]);
                            }
                            tempHtml+=groups[k].getAttribute("htmlEnd");
                            groupsHtml+=tempHtml;  
                        }
                        
                     }
                     var htmlDetails="";
                     var htmlOperation="";

                     htmlDetails+=layoutStart;  
                     
                     for(var i=0; i<attributes.length; i++){
                      tempString=htmlDetailsLine.replace("$attributeValue",attributes[i]); 
                      htmlDetails+=tempString.replace("{$attributeName}", attributes[i]);
                     } 
                     htmlDetails+=groupsHtml+layoutEnd;
                     if(container == "window"){
                        var winWidth=eval(templateOperationElement.getAttribute("winWidth"));
                        var winHeight=eval(templateOperationElement.getAttribute("winHeight"));
                        labelButton=templateOperationElement.getAttribute("labelButton");
                        imageButton=templateOperationElement.getAttribute("imageButton");
                        imageDimMin=templateOperationElement.getAttribute("imageDimMin");
                        imageDimMax=templateOperationElement.getAttribute("imageDimMax");
                        var renderIteratorsFunction="";
                        var showdetailsWindow=/*/"function Details_"+idAttribute+"(){ "+*/
                                  "var htmlDetails_"+idAttribute+"='"+htmlDetails+"';"+
                                  "var win = new Ext.Window({ "+
                                            "title: '({"+idAttribute +"}) Result Details', "+
                                            "border: false, "+
                                            "animCollapse : true, "+
                                            "autoScroll : true, "+
                                            "maximizable: true, "+
                                            "resizable : true, "+
                                            "collapsible: true, "+
                                            "layout: 'fit', "+
                                            "width: "+winWidth+", "+
                                            "height: "+winHeight+", "+
                                            "closeAction:'close', "+
                                            "html: htmlDetails_"+idAttribute+
                                  "}); "+
                                  renderIteratorsFunction+
                                  "win.show();";
                       htmlOperation="<img  title='"+labelButton+"' src='"+imageButton+"' onmouseout=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMin+"';this.height='"+imageDimMin+"';\""+ 
                       " onmouseover=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMax+"';this.height='"+imageDimMax+"';\" width='"+imageDimMin+"'  height='"+imageDimMin+"'"+
                       " onclick=\"javascript:"+showdetailsWindow+"\"/>"+
                       "<img src='style/img/empty.png' width='1'  height='"+imageDimMax+"'/>";
                       
                    }
                    else{
                       if(container == "template"){
                           
                       }  
                     }
                     break;
         case "renderAndZoom":
                      style=templateOperationElement.getAttribute("style");
                      mapObjcetName= templateOperationElement.getAttribute("mapObjcetName");
                      layerFillOpacity= templateOperationElement.getAttribute("layerOpacity");
                      layerGraphicOpacity= templateOperationElement.getAttribute("layerGraphicOpacity");
                      geometry= templateOperationElement.getAttribute("geometry");
                      attributeGeometry= templateOperationElement.getAttribute("attributeGeometry");
                      formatPoint= templateOperationElement.getAttribute("formatPoint");
                      pointSeparator= templateOperationElement.getAttribute("pointSeparator");
                      mapLayerName= templateOperationElement.getAttribute("mapLayerName");
                      posList= templateOperationElement.getAttribute("posList"),
                      posListDimension=templateOperationElement.getAttribute("posListDimension"),

                      labelButton=templateOperationElement.getAttribute("labelButton");
                      imageButton=templateOperationElement.getAttribute("imageButton");
                      imageDimMin=templateOperationElement.getAttribute("imageDimMin");
                      imageDimMax=templateOperationElement.getAttribute("imageDimMax");
                     
                      var pointZoom=templateOperationElement.getAttribute("zoomPoint");
                      if(!(pointZoom || pointZoom!=''))
                        pointZoom=attributeGeometry;
                    
                     var zoomFactor=templateOperationElement.getAttribute("zoomFactor");
                     var renderAndZoomFunction=""+ 
                               "geometryrendering ('{"+attributeGeometry+"}', '"+formatPoint+"', '"+pointSeparator+"', '"+geometry+"', '"+mapLayerName+"', eval("+style+"), '"+mapObjcetName+"', "+
                              "{layerFillOpacity: "+layerFillOpacity+","+
                               "layerGraphicOpacity: "+layerGraphicOpacity+" " + 
                              "},true);"+
                              "zoomTo('{"+pointZoom+"}', '"+formatPoint+"', '"+mapObjcetName+"', '"+zoomFactor+"');";
           
                       htmlOperation="<img  title='"+labelButton+"' src='"+imageButton+"' onmouseout=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMin+"';this.height='"+imageDimMin+"';\""+ 
                       " onmouseover=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMax+"';this.height='"+imageDimMax+"';\" width='"+imageDimMin+"'  height='"+imageDimMin+"'"+
                       " onclick=\"javascript:"+renderAndZoomFunction+"\"/>"+
                       "<img src='style/img/empty.png' width='1'  height='"+imageDimMax+"'/>";
             
                    break;
         case "select":
                      style=templateOperationElement.getAttribute("style");
                      mapObjcetName= templateOperationElement.getAttribute("mapObjcetName");
                      layerFillOpacity= templateOperationElement.getAttribute("layerOpacity");
                      layerGraphicOpacity= templateOperationElement.getAttribute("layerGraphicOpacity");
                      geometry= templateOperationElement.getAttribute("geometry");
                      attributeGeometry= templateOperationElement.getAttribute("attributeGeometry");
                      formatPoint= templateOperationElement.getAttribute("formatPoint");
                      pointSeparator= templateOperationElement.getAttribute("pointSeparator");
                      posList= templateOperationElement.getAttribute("posList"),
                      posListDimension=templateOperationElement.getAttribute("posListDimension"),
                      mapLayerName= templateOperationElement.getAttribute("mapLayerName");
                      layerTitle= templateOperationElement.getAttribute("layerTitle"),

                     labelButton=templateOperationElement.getAttribute("labelButton");
                     imageButton=templateOperationElement.getAttribute("imageButton");
                     imageDimMin=templateOperationElement.getAttribute("imageDimMin");
                     imageDimMax=templateOperationElement.getAttribute("imageDimMax");

                     /*var pointZoom=templateOperationElement.getAttribute("zoomPoint");
                     if(!(pointZoom || pointZoom!=''))
                        pointZoom=attributeGeometry;

                     var zoomFactor=templateOperationElement.getAttribute("zoomFactor");*/
                     var renderSelectFunction=""+
                               "geometryrendering ('{"+attributeGeometry+"}', '"+formatPoint+"', '"+pointSeparator+"', '"+geometry+"', '"+mapLayerName+"', eval("+style+"), '"+mapObjcetName+"', "+
                              "{layerFillOpacity: "+layerFillOpacity+","+
                               "layerGraphicOpacity: "+layerGraphicOpacity+"," +
                               "layerTitle: '"+layerTitle+"' " +
                              "},true,'"+posList+"','"+posListDimension+"');";/*+
                              "zoomTo('{"+pointZoom+"}', '"+formatPoint+"', '"+mapObjcetName+"', '"+zoomFactor+"');";*/

                       htmlOperation="<img  title='"+labelButton+"' src='"+imageButton+"' onmouseout=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMin+"';this.height='"+imageDimMin+"';\""+
                       " onmouseover=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMax+"';this.height='"+imageDimMax+"';\" width='"+imageDimMin+"'  height='"+imageDimMin+"'"+
                       " onclick=\"javascript:"+renderSelectFunction+"\"/>"+
                       "<img src='style/img/empty.png' width='1'  height='"+imageDimMax+"'/>";
                    break;
         case "viewImage":
                      var labelImage=templateOperationElement.getAttribute("labelImage");
                      var attribute=templateOperationElement.getAttribute("attribute");
                      var position=templateOperationElement.getAttribute("position");
                      var bgColor=templateOperationElement.getAttribute("bgColor");
                      imageDimMax=templateOperationElement.getAttribute("maxheight");
                      htmlOperation="<table align='"+position+"'><tr BGCOLOR='"+bgColor+"'><td valign='center'><b>"+labelImage+":</b></td><td><img  height='"+imageDimMax+"'align='"+position+"' title='"+labelImage+"' src='{"+attribute+"}' onclick=\"javascript:"+""+"\"/></p></td></tr></table>";
                    break;
         case "zoomAt":
             
                    break;
         case "getRequestPopup":
                     labelButton=templateOperationElement.getAttribute("labelButton");
                     imageButton=templateOperationElement.getAttribute("imageButton");
                     imageDimMin=templateOperationElement.getAttribute("imageDimMin");
                     imageDimMax=templateOperationElement.getAttribute("imageDimMax");
                     idAttribute=templateOperationElement.getAttribute("idAttribute");
                     urlAttribute=templateOperationElement.getAttribute("urlAttribute");
                     winWidth=eval(templateOperationElement.getAttribute("winWidth"));
                     var xslResponse=templateOperationElement.getAttribute("xslResponse");
                     winHeight=eval(templateOperationElement.getAttribute("winHeight"));
                     var serviceURL=templateOperationElement.getAttribute("serviceURL");
                     var serviceURLVariable=templateOperationElement.getAttribute("serviceURLVariable");


                     if(serviceURLVariable)
                       serviceURL=serviceURLVariable;
                     var getRequest;
                     if(urlAttribute){
                        getRequest="{"+urlAttribute+"}";
                        serviceURL="'ProxyRedirect?url='";
                     }
                     else
                      getRequest="httpservice?request=GetRepositoryItem&service=CSW-ebRIM&version=2.0.2&id={"+idAttribute +"}";


                     var showpopupWindow="var targetURL="+serviceURL+"+'"+getRequest+"&XSLResponse="+xslResponse+"'; "+
                                  "var win = new Ext.Window({ "+
                                            "title: '({"+idAttribute +"}) Result Details', "+
                                            "border: false, "+
                                            "animCollapse : true, "+
                                            "autoScroll : true, "+
                                            "maximizable: true, "+
                                            "resizable : true, "+
                                            "collapsible: true, "+
                                            "layout: 'fit', "+
                                            "width: "+winWidth+", "+
                                            "height: "+winHeight+", "+
                                            "closeAction:'close', "+
                                            "autoLoad: {url: targetURL , scripts: true}"+
                                  "}).show();";
                              
                        htmlOperation="<img  title='"+labelButton+"' src='"+imageButton+"' onmouseout=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMin+"';this.height='"+imageDimMin+"';\""+
                       " onmouseover=\"javascript:this.src='"+imageButton+"';this.width='"+imageDimMax+"';this.height='"+imageDimMax+"';\" width='"+imageDimMin+"'  height='"+imageDimMin+"'"+
                       " onclick=\"javascript:"+showpopupWindow+"\"/>"+
                       "<img src='style/img/empty.png' width='1'  height='"+imageDimMax+"'/>";

                    break;
  }       
    return(htmlOperation);              
    
}

function createCodeOnLoadOperation(onLoadOperationElement){
    var onLoadOperation=null;
    var type=onLoadOperationElement.getAttribute("type");
    
  switch(type){
         case "render":
                       onLoadOperation={
                         type: type,
                         vectorLayer: null,
                         style: onLoadOperationElement.getAttribute("style"),
                         mapObjcetName: onLoadOperationElement.getAttribute("mapObjcetName"),
                         layerFillOpacity: onLoadOperationElement.getAttribute("layerOpacity"),
                         layerGraphicOpacity: onLoadOperationElement.getAttribute("layerGraphicOpacity"),
                         geometry: onLoadOperationElement.getAttribute("geometry"),
                         posList: onLoadOperationElement.getAttribute("posList"),
                         posListDimension: onLoadOperationElement.getAttribute("posListDimension"),
                         attributeGeometry: onLoadOperationElement.getAttribute("attributeGeometry"),
                         pointSeparator: onLoadOperationElement.getAttribute("pointSeparator"),
                         formatPoint: onLoadOperationElement.getAttribute("formatPoint"),
                         layerName: onLoadOperationElement.getAttribute("layerName"),
                         //layerName:onLoadOperationElement.getAttribute("layerName"),
                         actionOnLoad: function(pointsString/*, format, separator*/){
                           
                             var pointsArray;
                             var olPointsArray=new Array();
                             
                              if(this.posList && this.posList!=""){
                                
                                 pointsArray=pointsString.split(" ");
                                 if(this.posListDimension == "2"){
                                   var tempLat,tempLong;
                                   for(i=0; i<pointsArray.length;i=i+2){
                                       tempLat=pointsArray[i];
                                       if(pointsArray[i+1])
                                         tempLong=pointsArray[i+1];
                                       else
                                         tempLong=pointsArray[0];
                                  //    alert(tempLat);
                                    //  alert(tempLong);
                                       olPointsArray.push(new OpenLayers.Geometry.Point(tempLong,tempLat));
                                   }
                                 }
                              }else{
                                  
                                 pointsArray=pointsString.split(this.pointSeparator);
                                 if(pointsArray.lenght==0)
                                    pointsArray[0]=pointsString;
                                 var i,latIndex,lonIndex,formatSeparator,tempPointSplit;
                                 var latFormatPosition=this.formatPoint.indexOf('lat');
                                 formatSeparator=this.formatPoint[3];
                                 if(latFormatPosition == 0){
                                    latIndex=0;lonIndex=1;
                                 }else{
                                    latIndex=1;lonIndex=0;
                                 }
                                 for(i=0; i<pointsArray.length;i++){
                                        tempPointSplit=pointsArray[i].split(formatSeparator);
                                        olPointsArray.push(new OpenLayers.Geometry.Point(tempPointSplit[lonIndex], tempPointSplit[latIndex]));
                                 }
                              }

                              var olStyle;
                              if (this.style && this.style!="")
                                  olStyle=this.style;
                              else
                                  olStyle = {
                                    fillColor: "#ee9900",
                                    fillOpacity: 0.4, 
                                    hoverFillColor: "white",
                                    hoverFillOpacity: 0.8,
                                    strokeColor: "#ee9900",
                                    strokeOpacity: 1,
                                    strokeWidth: 1,
                                    strokeLinecap: "round",
                                    hoverStrokeColor: "red",
                                    hoverStrokeOpacity: 1,
                                    hoverStrokeWidth: 0.2,
                                    pointRadius: 6,
                                    hoverPointRadius: 1,
                                    hoverPointUnit: "%",
                                    pointerEvents: "visiblePainted",
                                    cursor: ""
                              }
                              
                              var mapObject=eval(this.mapObjcetName);
                              var layer_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
                              if(this.layerFillOpacity)
                                 layer_style.fillOpacity = this.layerFillOpacity;
                              if(this.layerGraphicOpacity)
                                 layer_style.graphicOpacity = this.layerGraphicOpacity;
                              var feature;
                              switch(this.geometry){
                                    case "polygon":
                                            var linearRing = new OpenLayers.Geometry.LinearRing(olPointsArray);
                                            feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]),null,olStyle);
                                            break;
                                    case   "point":
                                            feature = new OpenLayers.Feature.Vector(olPointsArray[0],null,olStyle);
                                            break; 
                                    case    "line":
                                            feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(olPointsArray),null,olStyle);
                                            break;          
                              }
                             if(!this.vectorLayer){ 
                                this.vectorLayer=new OpenLayers.Layer.Vector(this.layerName, {style: layer_style});
                                mapObject.addLayer(this.vectorLayer);
                                this.vectorLayer.addFeatures([feature]);
                                
                             }else{
                               this.vectorLayer.addFeatures([feature]);  
                             }
                                //alert("render");
                         }
                     }; 
                    
                     
          break;
  }         
  
 return(onLoadOperation);
}


function zoomTo (pointString, formatPoint, mapObjcetName, zoomfactor){
   var mapObj=eval(mapObjcetName);   
   var latFormatPosition=formatPoint.indexOf('lat');
   var latIndex,lonIndex;
   if(latFormatPosition == 0){
          latIndex=0;lonIndex=1; 
       }else{
          latIndex=1;lonIndex=0; 
       }
   var pointSeparator=formatPoint[3];
   var tempPointSplit=pointString.split(pointSeparator);
   var lonLat = new OpenLayers.LonLat(tempPointSplit[lonIndex], tempPointSplit[latIndex]);
   mapObj.setCenter (lonLat, zoomfactor);
}


/*this.style --> style     mapObjcetName--> this.mapObjcetName     this.layerFillOpacity --> layerOptions  this.layerGraphicOpacity-->layerOptions
 *this.geometry --> geometry      this.vectorLayer --> vectorLayer   */
function geometryrendering (pointsString, format, separator, geometry, vectorLayer, style, mapObjcetName, layerOptions, replace, posList, posListDimension){
 
       var i,pointsArray;
       var olPointsArray=new Array();
   
       if(!separator){
          pointsArray=pointsString.split(" ");
          if(posListDimension == "2"){
             var tempLat,tempLong;
             for(i=0; i<pointsArray.length;i=i+2){
                 tempLat=pointsArray[i];
                 if(pointsArray[i+1])
                    tempLong=pointsArray[i+1];
                 else
                    tempLong=pointsArray[0];
                 olPointsArray.push(new OpenLayers.Geometry.Point(tempLong,tempLat));
             }
          }
       }else{

        pointsArray=pointsString.split(separator);
        if(pointsArray.lenght==0)
          pointsArray[0]=pointsString; 
        var latIndex,lonIndex,formatSeparator,tempPointSplit;
        var latFormatPosition=format.indexOf('lat');
        formatSeparator=format[3];
        if(latFormatPosition == 0){
            latIndex=0;lonIndex=1;
        }else{
            latIndex=1;lonIndex=0;
        }
        for(i=0; i<pointsArray.length;i++){
            tempPointSplit=pointsArray[i].split(formatSeparator);
            olPointsArray.push(new OpenLayers.Geometry.Point(tempPointSplit[lonIndex], tempPointSplit[latIndex]));
         }
       }

        var olStyle;
        if (style && style!="")
           olStyle=style;
        else
           olStyle = {
                      fillColor: "#ee9900",
                      fillOpacity: 0.4, 
                      hoverFillColor: "white",
                      hoverFillOpacity: 0.8,
                      strokeColor: "#ee9900",
                      strokeOpacity: 1,
                      strokeWidth: 1,
                      strokeLinecap: "round",
                      hoverStrokeColor: "red",
                      hoverStrokeOpacity: 1,
                      hoverStrokeWidth: 0.2,
                      pointRadius: 6,
                      hoverPointRadius: 1,
                      hoverPointUnit: "%",
                      pointerEvents: "visiblePainted",
                      cursor: ""
                     }
         
         var mapObject=eval(mapObjcetName);
         var layer_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
         if(layerOptions.layerFillOpacity)
            layer_style.fillOpacity = layerOptions.layerFillOpacity;
         if(layerOptions.layerGraphicOpacity)
            layer_style.graphicOpacity = layerOptions.layerGraphicOpacity;
         var feature;
         switch(geometry){
                case "polygon":
                      var linearRing = new OpenLayers.Geometry.LinearRing(olPointsArray);
                      feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]),null,olStyle);
                      break;
                case  "point":
                      feature = new OpenLayers.Feature.Vector(olPointsArray[0],null,olStyle);
                      break; 
                case    "line":
                      //alert("line");
                      feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(olPointsArray),null,olStyle);
                      break;          
               }
            
       var vectorLayerObj=eval(vectorLayer);
       var setLayerFunction="set_"+vectorLayer+"(new OpenLayers.Layer.Vector('"+layerOptions.layerTitle+"', {style: layer_style}));";
       if(!vectorLayerObj){ 
          eval(setLayerFunction);
          vectorLayerObj=eval(vectorLayer);
          mapObject.addLayer(vectorLayerObj);
          vectorLayerObj.addFeatures([feature]);
       }else{
           if(replace){ 
             mapObject.removeLayer(vectorLayerObj);  
             eval(setLayerFunction);
             vectorLayerObj=eval(vectorLayer);
             mapObject.addLayer(vectorLayerObj);
             vectorLayerObj.addFeatures([feature]);
           }    
           else    
            vectorLayerObj.addFeatures([feature]);  
       }         
/*/var selectFeatureControl = new OpenLayers.Control.SelectFeature(mapObject.layers[0],
{onSelect: function(){alert("ciao");}, 
 onUnselect: function(){}});
 mapObject.addControl(selectFeatureControl);
selectFeatureControl.activate();*/ 
}

function generateListOfFieldSet(fieldSets, numberColums){ 
  var fieldSetArray= new Array(), listField;
  for(var i=0;i<fieldSets.length;i++){
     listField=generateListOfField(fieldSets[i].fields); 
     
     fieldSetArray[i]= new Ext.form.FieldSet({
                  title: fieldSets[i].name,
                  layout: 'table',
                  layoutConfig: {
                                  columns: numberColums
                  },
                  autoHeight: true,
               
                  items: listField
                
            });
  }
  
  return(fieldSetArray);  
    
}

function generateListOfField(Fields){
  var fieldsArray= new Array();
  var j=0;
  var k,temp;
  for(var i=0;i<Fields.length;i++){
      switch(Fields[i].type) {
          case "text": temp= new Array();
                  temp=generateTextField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "numeric": temp= new Array();
                  temp=generateNumericField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "combo": temp= new Array();
                  temp=generateComboField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "password": temp= new Array();
                  temp=generatePasswordField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k];
                      j++;
                  }
                  break; 
          case "checkbox": temp= new Array();
                  temp=generateCheckBoxField(Fields[i]);  
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "radiogroup": temp= new Array();
                  temp=generateRadioGroupField(Fields[i]);  
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;        
          case "date": temp= new Array();
                  temp=generateDateField(Fields[i]); 
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "rangedate": temp= new Array();
                  temp=generateRangeDateField(Fields[i]); 
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "label": temp= new Array();
                  temp=generateLabelField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "file": temp= new Array();
                  temp=generateFileField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;        
          case "bbox": temp= new Array();
                  temp=generateBBOXField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "button": temp= new Array();
                  temp=generateButtonField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "timeStep": temp= new Array();
                  temp=generateTimeStepField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "time": temp= new Array();
                  temp=generateTimeField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                  }   
                  break;
          case "rangetime": temp= new Array();
                  temp=generateRangeTimeField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                    }  
                  break;   
          case "percentage": temp= new Array();
                  temp=generatePercentageField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                    }  
                  break;   
          case "textarea": temp= new Array();
                  temp=generateTextAreaField(Fields[i]);
                  for(k=0; k<temp.length; k++){
                      fieldsArray[j]=temp[k]; 
                      j++;
                    }  
                  break; 
        }
   }

  return(fieldsArray);    
}

function generateTextField(field){ 
  var formField=new Array(), size="20";
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  if (field.size)
      size=field.size;
  
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }   
  formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: [new Ext.form.TextField({
				name: field.name,
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id,
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                value: field.value,
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: field.label,
                                grow: field.grow,
                                msgTarget : 'qtip',
                                vtype: field.vtype,
                                vtypeText: field.vtypeText,
                                allowBlank:field.allowBlank
			})]
  };                  

  return(formField);  
}

function generatePasswordField(field){
  var formField=new Array(), size="20";
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  if (field.size)
      size=field.size;

  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  var allowBlank=true;
  if(field.allowBlank == "false")
     allowBlank=false;

  var onchange="";
  if(!allowBlank)
     onchange+="_formObj_[_formObj_.length-1].onChangeFieldControlMandatory();";
  if(field.onChange)
    onchange+=field.onChange;

  var label;
  if(field.localization && field.label!="" && field.label){
    label=field.localization.getLocalMessage(field.label);
  }else
   label=field.label;

  formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: [new Ext.form.TextField({
				name: field.name,
                                autoCreate : {
                                    tag: "input",
                                    id: field.id,
                                    type: "text",
                                    onchange: onchange,
                                    size: size,
                                    autocomplete: "off"
                                },
                                value: field.value,
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                //change: eval(field.onChange),
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: label,
                                grow: field.grow,
                                msgTarget : 'qtip',
                                vtype: field.vtype,
                                vtypeText: field.vtypeText,
                                inputType: 'password',
                                allowBlank:allowBlank
			})]
  };

  return(formField);
}

function generateCheckBoxField(field){
  var formField=new Array();
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  var enableInputFunction;

  var checkboxField=new Ext.form.Checkbox({
				name: field.name,
                                autoCreate : {
                                    tag: "input",
                                    id: field.id,
                                    type: "checkbox",
                                    autocomplete: "off"
                                },
                                boxLabel: field.label,
                                label: field.label,
                                checked: field.value,
                                hideLabel: true,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
                                msgTarget : 'qtip',
                                allowBlank:field.allowBlank
			});
                        
  formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: [checkboxField]
  };
   if(field.enableInputList){
       enableInputFunction=new Function(/*"function(){"+*/
              "var enableInputList=\""+field.enableInputList+"\";"+
              "var arrayInput=enableInputList.split(',');"+
              "var tmp;"+
              "var indexForm;"+
              "for(var i=0; i<arrayInput.length;i++){"+
                  "tmp=arrayInput[i].split('-');"+
                  "indexForm=parseInt(tmp[0]);"+
                  "if(this.getValue())"+
                    field.formObjectInstanceName+".formsArray[indexForm].getForm().findField(tmp[1]).enable();"+
                  "else {"+
                    field.formObjectInstanceName+".formsArray[indexForm].getForm().findField(tmp[1]).disable();"+
                    field.formObjectInstanceName+".formsArray[indexForm].getForm().findField(tmp[1]).setValue('');"+
                  " }"+
              "}");
       checkboxField.on('check', eval(enableInputFunction));
   }
   if(field.onChange)
     checkboxField.on('check', eval(field.onChange));
  return(formField);    
}

function generateRadioGroupField(field){
  var formField=new Array();
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }

  var radioLabels=field.labelList.split(",");
  var radioValues=field.valueList.split(",");
  var checked=false;
  var items= new Array();
  for(var i=0; i<radioLabels.length;i++){
      if(field.valueCheked == radioValues[i])
          checked=true;
      items.push({
          xtype:'radio',
          hideLabel: true,
          checked: checked,
          name: field.id,
          boxLabel: radioLabels[i],
          value: radioValues[i]
      });
      checked=false;
  }
  
  formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: items
  };                  
  return(formField);    
}

function generateNumericField(field){
  var formField=new Array(), size="20";
  if (field.size)
      size=field.size;
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  var numericField=new Ext.form.NumberField({
				name: field.name,
                                autoCreate : {
                                    tag: "input",
                                    id: field.id,
                                    type: "text",
                                    size: size,
                                    autocomplete: "off"
                                },
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                value: field.value,
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: field.label,
                                grow: field.grow,
                                msgTarget : 'qtip',
                                vtype: field.vtype,
                                vtypeText: field.vtypeText,
                                allowBlank:field.allowBlank
			});
  formField[u]={
             colspan: numberColsField+colSpan,
             layout: "form",
             items: [numericField]
  };

   if(field.onChange)
     numericField.on('check', eval(field.onChange));
  return(formField);  
}

function generateTextAreaField(field){
  var formField=new Array(), rows="4", cols="20";
  if (field.cols)
      cols=field.cols;
  if (field.rows)
      rows=field.rows;
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;    

  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  formField[u]={
             colspan: numberColsField+colSpan,
             layout: "form",
             items: [new Ext.form.TextArea({
				name: field.name,
                                autoCreate : {
                                    tag: "textarea", 
                                    id: field.id,
                                    name: field.name,
                                    cols: cols, 
                                    rows: rows, 
                                    autocomplete: "off"
                                },
                                value: field.value,
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: field.label,
                                msgTarget : 'qtip',
                                vtype: field.vtype,
                                vtypeText: field.vtypeText,
                                allowBlank:field.allowBlank
			})]
  };                  
  return(formField);  
}

function generateBBOXField(field){
  var bboxformArray=new Array();
  var colSpan=0,size="15";
  if (field.size)
      size=field.size;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  var offsetCols=numberColsForm-(colSpan+numberColsField);
  if(colSpan>0){
    bboxformArray[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  var selectionToolbar = new Ext.Toolbar({width: size*8});  
  var setAOI="function(){var aoiArray=this.currentAOI.split(',');"+
              "document.getElementById('"+field.id+"WestBBOX').value=aoiArray[0];"+
              "document.getElementById('"+field.id+"SouthBBOX').value=aoiArray[1];"+
              "document.getElementById('"+field.id+"EastBBOX').value=aoiArray[2];"+
              "document.getElementById('"+field.id+"NorthBBOX').value=aoiArray[3];"+
              "}";
  var button="new WebGIS.MapAction.SelectAOI({map: map, onChangeAOI:"+setAOI+"})";    
  var buttons=new Array();
  buttons.push(button);
  bboxformArray[u]={
                    colspan: numberColsField+offsetCols,
                    layout: "form",
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelAOIId',
                            hideLabel: true,
                            value: field.label+":",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>'+this.value+'</p>');   
                                  }
                            }
                   })]
                 };      
  
  bboxformArray[u+1]={
      colspan: colSpan+1,
        html: "&nbsp;"
  };
  bboxformArray[u+2]={
                    colspan: 2+offsetCols,
                    layout: "form",
                    labelAlign: "top",
                    items: [new Ext.form.NumberField({
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id+'NorthBBOX',
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                fieldLabel: 'NORTH',
                                vtype: 'latitude',
                                msgTarget : 'qtip',
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                name: field.id+"North",
                                id: field.id+'NorthBBOX',
                                labelStyle: 'font-size:8px;',
                                hideLabel : false,
                                allowBlank:field.allowBlank
                          
                          })]
                  };
 if(colSpan>0){
    bboxformArray[u+3]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }    
 bboxformArray[u+3]={
                   colspan: 1,
                   layout: "form",
                   labelAlign: "top",
                   items: [new Ext.form.NumberField({
                                name: field.id+"West",
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id+'WestBBOX',
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                vtype: 'longitude',
                                msgTarget : 'qtip',
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                fieldLabel: 'WEST',
                                id: field.id+'WestBBOX',
                                labelStyle: 'font-size:8px;',
                                hideLabel : false,
                                allowBlank:field.allowBlank
                                
			})]
                 };    
  var html="";
 /*/ var panelBBoxBar = new Ext.Panel({
				border: false,
                                width: size,   
				tbar: selectionToolbar
                               // contentEl: field.id+"BBOXbar"
			});*/
                
  supportToolbars.push({toolbar: selectionToolbar, id:field.id+'BBOXbar', buttons: buttons});                      
 /*for(var j=0;j<size*2.5; j++)
      html+="&nbsp;";*/
                html="<div id='"+field.id+"BBOXbar'></div>";
  bboxformArray[u+4]={
        colspan: 1,
        html: html
    };
  bboxformArray[u+5]={
                   colspan: 1+offsetCols, 
                   layout: "form",
                   labelAlign: "top",
                   items: [new Ext.form.NumberField({
                                name: field.id+"East",
                                id: field.id+'EastBBOX',
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id+'EastBBOX',
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                vtype: 'longitude',
                                msgTarget : 'qtip',
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                fieldLabel: 'EAST',
                                labelStyle: 'font-size:8px;',
                                hideLabel : false,
                                allowBlank:field.allowBlank
			})]
                  };   
  
  bboxformArray[u+6]={
      colspan: colSpan+1,
      html: "&nbsp;"
  };
  
  bboxformArray[u+7]={
                   colspan: 2+offsetCols,
                   layout:'form',
                   labelAlign: "top",
                   items: [new Ext.form.NumberField({
                                name: field.id+"South",
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id+'SouthBBOX',
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                id: field.id+'SouthBBOX',
                                vtype: 'latitude',
                                msgTarget : 'qtip',
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                fieldLabel: 'SOUTH',
                                hideLabel : false,
                                labelStyle: 'font-size:8px;',
                                allowBlank:field.allowBlank    
                         })]
                  };    
 
  return(bboxformArray);
}

function generatePercentageField(field){
 var percentageFormArray=new Array();
 var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
 var size=parseFloat(field.decimalPrecision)+5;
 var u=0;
  if(colSpan>0){
    percentageFormArray[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }   
  percentageFormArray[u]={
                    colspan: 2,
                    layout: "form",
                    items: [new Ext.form.NumberField({
                                fieldLabel: field.label,
                                vtype: 'percentage',
                                msgTarget : 'qtip',
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                name: field.name,
                                id: field.id,
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id,
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                allowBlank:field.allowBlank,
                                value: field.value,
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: field.label,
                                vtypeText: field.vtypeText,
                                allowBlank:field.allowBlank
                          })]
                  };   
                  
   percentageFormArray[u+1]={
                    colspan: 1,
                    layout: "form",
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelPercentage'+field.id,
                            hideLabel: true,
                            value: "&nbsp;%",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>'+this.value+'</p>');   
                                  }
                            }
                   })]
                 };                  
  return (percentageFormArray);
}

function generateComboField(field){
 var colSpan=0,formField=new Array();
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
 if(field.store == 'VALUES') 
    var mode='local';      
    var store = new Ext.data.SimpleStore({
        id: "store"+field.id,	
        fields: field.storeFields,
        data : field.storeData
      }); 
  var comboField=new Ext.form.ComboBox({
                            store: store,
                            autoShow: true,
                            storeFields: field.storeFields,
                            displayField: field.storeFields[0],
                            id: field.id,
                            name: field.name,
                            msgTarget : 'qtip',
                            typeAhead: true,
                            disabled: field.disabled,
                            mode: mode,
                            colspan: numberColsField,
                            autoCreate: {tag: "input", type: "text", id:field.id, size: field.size, autocomplete: "on"},
                            fieldLabel: field.label,
                            hideLabel: field.hideLabel,
                            triggerAction: 'all',
                            stateful: false,
                            emptyText: field.label,
                            selectOnFocus:true,
                            arrayStore: field.storeData,
                            allowBlank:field.allowBlank,
                            getValueInformation: function(infoValue){
                                var i;
                      
                                for(i=0;i<this.store.getTotalCount();i++){
                                   // alert("i: "+this.store.getAt(i).get(this.displayField));
                                    if(this.store.getAt(i).get(this.displayField) == this.value)
                                       return(this.store.getAt(i).get(infoValue)); 
                                }
                                return(null);
                            },
                            setStore: function(newStoreData,newstoreFields,newDisplayField){
                                var storeF;
                                if(newstoreFields){
                                  storeF=newstoreFields;
                                  this.storeFields=newDisplayField;
                                }
                                else
                                  storeF=this.storeFields;
                                this.arrayStore=newStoreData;
                                this.storeFields=storeF;
                                var newStore = new Ext.data.SimpleStore({
                                        id: "store"+this.id,	
                                        fields: storeF,
                                        data : newStoreData
                                }); 
                                this.store=newStore;
                            }
                            
                            
                    });
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }               
  formField[u] = {
             colspan: numberColsField,
             layout: "form",
             items: [comboField]
 };     
 if(field.onChange)
    comboField.on('select', eval(field.onChange));
 if(field.value)
    comboField.setValue(field.value);  
 return(formField);  
}


function generateTimeStepField(field){
  var formField=new Array();
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var hideLabel=false;
  if(!field.label)
     hideLabel=true; 
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }  
  formField[u]={
             colspan: numberColsField,
             layout: "form",
             items:[new Ext.form.TimeField({
                        fieldLabel: field.label,
                        hideLabel: hideLabel,
                        name: field.name,
                        format: field.format,
                        allowBlank:field.allowBlank
                    })]
           };
  return(formField);  
}

function generateTimeField(field){
  var hStore=new Array();
  var mStore=new Array();
  var i;
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;;
  var splitFormat=field.format.split(':');
  if(splitFormat[0].indexOf('H')>0)
     maxH=24;
  else
     maxH=12; 
  for(i=0;i<maxH;i++)
      if(i<10)
        hStore[i]="0"+i;
      else  
        hStore[i]=i;
  for(i=0;i<60;i++)
      if(i<10)
        mStore[i]="0"+i;  
      else    
        mStore[i]=i;
  var size="3";
  var timeCombo= new Array();
  var u=0;
  if(field.value){
     var temp=field.value.split(":");
     if(temp.length==0){
        temp=new Array();
        temp[0]=field.value
     }
  }  
  var msvalue="";
  if(temp[temp.length-1].indexOf('.')>=0){
      var temp2=temp[temp.length-1].split('.');
      temp[temp.length-1]=temp2[0];
      msvalue=temp2[1];
  }
  timeCombo[u]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: hStore,
                            typeAhead: true,
                            mode: 'local',
                            colspan: 1,
                            emptyText: "hh",
                            labelStyle: 'font-size:8px;',
                            id: 'h'+field.id,
                            name: 'h'+field.id,
                            autoCreate: {tag: "input", type: "text", id:'h'+field.id, size: size, autocomplete: "off"},
                            fieldLabel: 'H',
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank
                            //onChange: field.onChange
                    })]
                };
  timeCombo[u+1]={
                    colspan: 1,
                    layout: "form",
                   
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelTime'+field.id,
                            hideLabel: true,
                            value: "&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                   })]
                 };                        
  timeCombo[u+2]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: mStore,
                            typeAhead: true,
                            colspan: 1,
                            emptyText: "mm",
                            labelStyle: 'font-size:7px;',
                            id: 'm'+field.id,
                            name: 'm'+field.id,
                            vtype: 'minutes',
                            mode: 'local',
                            autoCreate: {tag: "input", type: "text", id:'m'+field.id, size: size, autocomplete: "off"},
                            fieldLabel: 'M',
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank
                           
                       })]
                };
  if(field.seconds){
    timeCombo[u+3]={
                    colspan: 1,
                    layout: "form",
                   
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelTimeSec'+field.id,
                            hideLabel: true,
                            value: "&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                   })]
                 };  
    timeCombo[u+4]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: mStore,
                            typeAhead: true,
                            colspan: 1,
                            emptyText: "ss",
                            labelStyle: 'font-size:7px;',
                            id: 's'+field.id,
                            name: 's'+field.id,
                            mode: 'local',
                            vtype: 'seconds',
                            autoCreate: {tag: "input", type: "text", id:'s'+field.id, size: size, autocomplete: "off"},
                            fieldLabel: 'S',
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank
                     
                       })]
                };             
  }  
  
  if(field.secondsDiv){
    timeCombo[u+5]={
                    colspan: 1,
                    layout: "form",
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelTimeSecDiv'+field.id,
                            hideLabel: true,
                            value: "&nbsp;&nbsp;&nbsp;.&nbsp;&nbsp;&nbsp;",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                   })]
                 };  
    timeCombo[u+6]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.NumberField({
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id+'secondsDiv',
                                    type: "text", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                fieldLabel: "mS",
                                value: msvalue,
                                vtype: 'millseconds',
                                msgTarget : 'qtip',
                                decimalSeparator: field.decimalSeparator,
                                decimalPrecision : field.decimalPrecision,
                                name: 'ms'+field.id,
                                id: 'ms'+field.id,
                                labelStyle: 'font-size:8px;',
                                hideLabel : false,
                                allowBlank:field.allowBlank
                          })]
                };             
  }  
  var z=u;
  for(var index=0; index<temp.length;index++){
      if(timeCombo[z]){
          timeCombo[z].items[0].setValue(temp[index]);
          z=z+2;
      }      
  }
  return(timeCombo);  
}


function generateRangeTimeField(field){
  var hStore=new Array();
  var mStore=new Array();
  var i,maxH;
  var colSpan=0;
  if (field.colSpan)
      colSpan=parseFloat(field.colSpan)*numberColsField;
  var offsetCols=numberColsForm-(colSpan+numberColsField);
  var splitFormat=field.format.split(':');
  if(splitFormat[0]=='H')
     maxH=24;
  else
     maxH=12;  
  
  for(i=0;i<maxH;i++)
      if(i<10)
        hStore[i]="0"+i;
      else  
        hStore[i]=i;
  
  for(i=0;i<60;i++)
      if(i<10)
        mStore[i]="0"+i;  
      else    
        mStore[i]=i;
  
  var size="3";
  
  var timeCombo= new Array();
  var u=0;
  if(colSpan>0){
    timeCombo[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }  
  timeCombo[u]={
                colspan: numberColsField+offsetCols,
                layout: "form",
                items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            labelStyle: 'font-size:8px;',
                            id: 'labelStartTime'+field.id,
                            hideLabel: true,
                            value: field.labelStart,
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                    })]
                 };      
  timeCombo[u+1]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: hStore,
                            typeAhead: true,
                            vtype: 'timerange',
                            msgTarget : 'qtip',
                            mode: 'local',
                            colspan: 1,
                            maxH: maxH,
                            emptyText: "hh",
                            labelStyle: 'font-size:8px;',
                            id: 'hStart'+field.id,
                            name: 'hStart'+field.id,
                            autoCreate: {tag: "input", type: "text", id:'hStart'+field.id, size: size, autocomplete: "off"},
                            hideLabel: true,
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank,
                            mStart:'mStart'+field.id,
                            hEnd:'hEnd'+field.id,
                            mEnd:'mEnd'+field.id
                            //onChange: field.onChange
                    })]
                };
  timeCombo[u+2]={
                    colspan: 1,
                    layout: "form",
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelTimeSeparatorSart'+field.id,
                            hideLabel: true,
                            value: "&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                   })]
                 };                        
  timeCombo[u+3]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: mStore,
                            typeAhead: true,
                            colspan: 1,
                            vtype: 'timerange',
                            msgTarget : 'qtip',
                            labelStyle: 'font-size:7px;',
                            id: 'mStart'+field.id,
                            emptyText: "mm",
                            name: 'mStart'+field.id,
                            mode: 'local',
                            autoCreate: {tag: "input", type: "text", id:'mStart'+field.id, size: size, autocomplete: "off"},
                            hideLabel: true,
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank,
                            hStart:'hStart'+field.id,
                            hEnd:'hEnd'+field.id,
                            mEnd:'mEnd'+field.id
                           // onChange: field.onChange
                       })]
                };

   u=0;
  if(colSpan>0){
    timeCombo[u+4]={
        colspan: colSpan+offsetCols,
        html: "&nbsp;"
    };
    u++;
  }  
  timeCombo[u+4]={
                    colspan: numberColsField+offsetCols,
                    layout: "form",
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelEndTime'+field.id,
                            labelStyle: 'font-size:8px;',
                            hideLabel: true,
                            value: field.labelEnd,
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                   })]
                 };     
                 
  timeCombo[u+5]={
                colspan: 1,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: hStore,
                            typeAhead: true,
                            mode: 'local',
                            colspan: 1,
                            maxH: maxH,
                            vtype: 'timerange',
                            msgTarget : 'qtip',
                            emptyText: "hh",
                            id: 'hEnd'+field.id,
                            name: 'hEnd'+field.id,
                            autoCreate: {tag: "input", type: "text", id:'hEnd'+field.id, size: size, autocomplete: "off"},
                            hideLabel: true,
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank,
                            mStart:'mStart'+field.id,
                            hStart:'hStart'+field.id,
                            mEnd:'mEnd'+field.id
                            //onChange: field.onChange
                    })]
                };
  timeCombo[u+6]={
                    colspan: 1,
                    layout: "form",
                   
                    items: [new Ext.form.Field({    
                            autoCreate: {tag: 'div', cn:{tag:'div'}},
                            id: 'labelTimeSeparatorEnd'+field.id,
                            hideLabel: true,
                            value: "&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;",
                            setValue:function(val) { 
                                  this.value = val;
                                  if(this.rendered){
                                      this.el.child('div').update(
                                      '<p>   '+this.value+'   </p>');   
                                  }
                            }
                   })]
                 };                        
  timeCombo[u+7]={
                colspan: 1+offsetCols,
                layout: "form",
                labelAlign: "top",
                items: [new Ext.form.ComboBox({
                            store: mStore,
                            typeAhead: true,
                            colspan: 1,
                            labelStyle: 'font-size:7px;',
                            id: 'mEnd'+field.id,
                            name: 'mEnd'+field.id,
                            emptyText: "mm",
                            vtype: 'timerange',
                            msgTarget : 'qtip',
                            mode: 'local',
                            autoCreate: {tag: "input", type: "text", id:'mEnd'+field.id, size: size, autocomplete: "off"},
                            hideLabel: true,
                            triggerAction: 'all',
                            selectOnFocus:true,
                            allowBlank:field.allowBlank,
                            mStart:'mStart'+field.id,
                            hStart:'hStart'+field.id,
                            hEnd:'hEnd'+field.id
                           // onChange: field.onChange
                       })]
                };
  return(timeCombo);  
}

function generateDateField(field){
 
  var formField=new Array();
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }

  formField[u]={
             colspan: numberColsField+colSpan,
             layout: "form",
             items:[{
                     xtype:'datefield',
                     fieldLabel: field.label,
                     name: field.name,
                     id: field.id,
                     msgTarget : 'qtip'
                        //allowBlank:field.allowBlank
                    }]
           };
  return(formField);  
}

function generateRangeDateField(field){
  var formField= new Array();
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  formField[u]={
             colspan: numberColsField+colSpan,
             layout: "form",
             items: [{
                      xtype:'datefield',
                      fieldLabel: field.labelStart,
                      name: field.id+'StartDate',
                      id: field.id+'StartDate',
                      msgTarget : 'qtip',
                      vtype: 'daterange',
                      
                      endDateField: field.id+'EndDate' 
                    },{
                      xtype:'datefield',  
                      fieldLabel: field.labelEnd,
                      name: field.id+'EndDate',
                      id: field.id+'EndDate',
                      msgTarget : 'qtip',
                      vtype: 'daterange',
                      startDateField: field.id+'StartDate'
                    }]
           };
  return(formField);  
}  

function generateFileField(field){
  var formField=new Array(), size="20";
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  if (field.size)
      size=field.size;
  
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }   
 ;
  var fileHtml="<form name='formFile_"+field.id+"' action='"+field.action+"' method='POST' enctype='multipart/form-data' target='"+field.target+"'>"+
            "<input type='file' id='"+field.id+"_file' name='"+field.id+"_file' value='' width='"+size+"' />"+
             "<input type='submit' name='buttonSubmit_"+field.id+"' value='"+field.submitLabel+"'/>"+
            "</form>";
     formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: [new Ext.form.Field({
                        autoCreate: {tag: 'div', cn:{tag:'div'}},
                        id: field.id,
                        name: field.id,
                        hideLabel: true,
                        value: fileHtml,
                        setValue:function(val) { 
                            this.value = val;
                            if(this.rendered){
                                this.el.child('div').update(
                                '<p>'+this.value+'</p>');   
                        }
                      }
                   })]
            };
  
  /*formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: [new Ext.form.TextField({
				name: field.name,
                                autoCreate : {
                                    tag: "input", 
                                    id: field.id,
                                    type: "file", 
                                    size: size, 
                                    autocomplete: "off"
                                },
                                value: field.value,
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: field.label,
                                grow: field.grow,
                                msgTarget : 'qtip',
                                vtype: field.vtype,
                                vtypeText: field.vtypeText,
                                allowBlank:field.allowBlank
			})]
  };      */
  return(formField);  
}

function generateLabelField(field){
  var formField=new Array();
  var colSpan=0;
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
  formField[u]={
             colspan: numberColsField,
             layout: "form",
             items: [new Ext.form.Field({
                        autoCreate: {tag: 'div', cn:{tag:'div'}},
                        id: field.id,
                        name: field.id,
                        hideLabel: true,
                        value: field.value,
                        setValue:function(val) { 
                            this.value = val;
                            if(this.rendered){
                                this.el.child('div').update(
                                '<p>'+this.value+'</p>');   
                        }
                      }
                   })]
            };
  return(formField);    
}

function generateButtonField(field){
  var colSpan=0;
  var formField= new Array();
  if (field.colSpan)
      colSpan=(parseFloat(field.colSpan)-1)*numberColsField;  
  var parameters= null, paramsplit= new Array();
  
  if(field.handlerParameters){
      var listParameters=field.handlerParameters.substr(1,field.handlerParameters.length-2);
      if(listParameters){   
          parameters= new Array();
          paramsplit=listParameters.split(',');
          if(!paramsplit)
              paramsplit[0]=listParameters;
          var i=0, temp;
          for(i=0;i<paramsplit.length; i++){
              temp=paramsplit[i].split(':');
              parameters[ trim(temp[0], ' ')]=trim(trim(temp[1], " "), "'");
          }
    }
  }
  
  var u=0;
  if(colSpan>0){
    formField[0]={
        colspan: colSpan,
        html: "&nbsp;"
    };
    u++;
  }
 
  var button=new Ext.Button({
                  id: field.id,
                  name: field.name,
                  text: field.label,
                  handler: eval(field.onclickFunction),
                  handlerParm: parameters,
                  disabled: field.disabled,
                  icon: field.icon
              });               
  formField[u]={
                 colspan: numberColsField+colSpan,
                  layout: "form",
                  items: [button]
                };
        
  return(formField);  
}




