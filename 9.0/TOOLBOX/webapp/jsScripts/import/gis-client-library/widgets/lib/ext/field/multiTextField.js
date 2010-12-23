

/*Ext.Client.Interface.*/MultiTextField=function (field, numberColsField){


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
 
  if(field.onChange)
    onchange+=field.onChange;

  var label;
  if(field.localization && field.label!="" && field.label){
    label=field.localization.getLocalMessage(field.label);
  }else
   label=field.label;

  if(field.remoteControlURL)
      field.vtype="remotecontrol";





  formField[u]={
             colspan: numberColsField+colSpan,
             layout: "form",
             labelAlign: field.labelAlign/*"top"*/,
             items: [new Ext.Container({
                        autoEl: 'div',
                        size: size,
                        layout: 'form',
                        id: field.id,
                        remoteControlURL: field.remoteControlURL,
                        vtype: field.vtype,
                        colspan: 1,
                        getValues: function(){
                            this.doLayout();
                            var textArray=this.find("xtype", "textfield");
                            var objValue=new Array();
                            for(var i=0; i<textArray.length;i++){
                                var obj={
                                    "id":textArray[i].id,
                                    "value": textArray[i].getValue()
                                };
                                
                                objValue.push(obj);
                            }
                           
                            return objValue;
                        },
                        getStringValues: function(){
                            var textArray=this.find("xtype", "textfield");
                            var stringValue="";
                            for(var i=0; i<textArray.length;i++){
                                stringValue+=textArray[i].getValue();
                                if(i<textArray.length-1)
                                    stringValue+=",";

                            }

                            return stringValue;
                        },
                        
                        addTextField: function(textID, textLabel, textValue){
                                   this.add(
                                             new Ext.form.TextField({
                                                name: this.name+"_"+textID,
                                                autoCreate : {
                                                    tag: "input",
                                                    id: this.id+"_"+textID,
                                                    type: "text",
                                                    size: this.size,
                                                    autocomplete: "off"
                                                },
                                                xtype: "textfield",
                                                remoteControlURL: this.remoteControlURL,
                                                vtype: this.vtype,
                                                value: textValue,
                                                hideLabel: false,
                                                id: textID,
                                                fieldLabel: textLabel
                                               
                                            })
                                 );
                                }

             })]};


  return(formField);



}

