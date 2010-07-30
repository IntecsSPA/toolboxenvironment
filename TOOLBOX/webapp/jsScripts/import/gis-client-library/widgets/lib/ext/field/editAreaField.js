

function editAreaLoadDip(){
    gcManager.loadScript("import/edit_area/edit_area_full.js");
    
}

/*Ext.Client.Interface.*/EditAreaField=function (field, numberColsField){
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
             colspan: numberColsField+colSpan,
             layout: "form",
             items: [new Ext.form.Field({
				name: field.name,
                                autoCreate : {
                                    src: "getEditArea.jsp?syntax=php&gcpath=jsScripts/import/gis-client-library&multifiles=false",
                                    tag: "iframe",
                                    id: field.id+"TextAreaIframe",
                                    name: field.id+"TextAreaIframe",
                                    width: '100%',
                                    height: '100%',
                                  //  style: "border-width: 0px; overflow: hidden; display: inline; width: 200; height: 200;",
                                    frameborder: "0"
                                },
                              // html: "<iframe scrolling='no' FRAMEBORDER='0' src='getEditArea.jsp?syntax=php&gcpath=jsScripts/import/gis-client-library&multifiles=false' name='"+field.id+"TextAreaIframe' width='100%' height='300' marginwidth='0' marginheight='0'></iframe>",

                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: false,
                                id: field.id,
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
                                multipleFile: field.isMultiFile,
                                listeners:{
                                  'beforerender': function(){
                                     var fieldset=this.findParentByType("fieldset");
                                     var size=fieldset.getSize();
                                     this.autoCreate.width= size.width-50;
                                     this.autoCreate.height= 400;
                                     
                                     
                                  }
                               
                                

                                },
                               setValue:function(val) {
                                    this.value = val;
                                    if(this.rendered){
                                        this.el.child('div').update(
                                        "<pre>"+this.value+"</pre>");
                                    }
                                },
                                getEditorValue: function(){
                                  if(this.multipleFile){
                                    return(editAreaLoader.getAllFiles(field.id));
                                  }else
                                    return(editAreaLoader.getValue(field.id));
                                },
                                
                                setEditorValue: function(objValue){
                                  if(this.multipleFile){
                                     for(var i=0; i<objValue.length; i++){
                                        editAreaLoader.openFile(field.id, objValue[i]);
                                     }
                                  }else
                                     editAreaLoader.setValue(field.id,objValue);
                                },
                                allowBlank:allowBlank
			})]
  };


  return(formField);
}


