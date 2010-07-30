


/*Ext.Client.Interface.*/FileField=function(field, title, numberColsField){

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

  var submitLabel;
  if(field.localization && field.submitLabel!="" && field.submitLabel){
    submitLabel=field.localization.getLocalMessage(field.submitLabel);
  }else
   submitLabel=field.submitLabel;


          var fileUpload=new Ext.ux.form.FileUploadField({
                        id: field.id+"_file",
                        width: eval(field.size),
                        emptyText: field.blankText,
                        fieldLabel: field.label,
                        autoUploadURL: field.autoUploadURL,
                        name: field.id+"_file",
                        labelIcon: "Label"+field.id,
                        iconWait: field.iconWait,
                        buttonText: '',
                        listeners: {
                            "fileselected": function (){
                                if(this.autoUploadURL){
                                   if(this.iconWait){;
                                      var label=this.findParentByType("form").getForm().findField(this.labelIcon);
                                      label.setValue("<img src='"+this.iconWait+"'/>");
                                   }

                                   this.findParentByType("form").getForm().submit({
                                        url: this.autoUploadURL,
                                        method: 'POST',
                                        iconSuccess: this.iconSuccess,
                                        iconFailure: this.iconFailure,
                                        success: function(form, action){
                                            if(form.iconSuccess){
                                              var jsonResponseObj=eval('new Object(' + action.response.responseText + ')');
                                              var idField=form.findField(form.fieldID);
                                            
                                              idField.setValue(jsonResponseObj.id);
                                              var label=form.findField(form.labelIcon);
                                              label.setValue("<img src='"+form.iconSuccess+"'/>");
                                           }

                                        },
                                        failure: function(form, action) {
                                          
                                            if(form.iconFailure){
                                              var label=this.findParentByType("form").getForm().findField(this.labelIcon);
                                              label.setValue("<img src='"+form.iconFailure+"'/>");
                                           }


                                        }
                                     });
                                }
                         }

                        },
                        buttonCfg: {
                            iconCls: field.icon
                        }
                       });

           var contentForm=fileUpload;
           if(field.label)
               contentForm=new Ext.form.FieldSet({
                      title: field.label,
                      layout: 'table',
                      autoHeight: true,
                      items: [fileUpload]
                });

           if(field.autoUploadURL){
               contentForm.items.add(
                    new Ext.form.Field({
                        autoCreate: {tag: 'div', cn:{tag:'div'}},
                        id: "Label"+field.id,
                        name: "Label"+field.id,
                        hideLabel: true,
                        setValue:function(val) {
                            this.value = val;
                            if(this.rendered){
                                this.el.child('div').update(
                                "&nbsp;&nbsp;&nbsp;&nbsp;"+this.value);
                        }
                      }
                   }));
               contentForm.items.add(
                    new Ext.form.TextField({
				name: field.id+"UploadID",
                                value: "",
                                hideLabel: true,
                                hidden: true,
                                id: field.id+"UploadID"
			}));


           }

           var fileUploadFormPanel=new Ext.FormPanel({

                    divName: title+"FileForm"+field.id,
                    fileUpload: true,
                    frame: true,
                    width: '100%',
                    iconSuccess: field.iconSuccess,
                    iconFailure: field.iconFailure,
                    labelIcon: "Label"+field.id,
                    fieldID: field.id+"UploadID",
                    autoShow : true,
                    items: [contentForm]
                });





   /*var formFileLabel=new Ext.form.Field({
                        autoCreate: {tag: 'div', cn:{tag:'div'}},
                        id: "Label"+field.id,
                        name: "Label"+field.id,
                        fileID: field.id,
                        width: eval(field.size),
                        emptyText: field.blankText,
                        hideLabel: true,
                        iconCls: field.icon,
                        autoUploadURL: field.autoUploadURL,
                        fieldLabel: field.label,
                        renderForm: renderFileUploadForm,
                        listeners: {
                            "afterrender": function(){
                                alert("afterrender");
                                this.renderForm();

                            }

                        },
                        setValue:function(val) {
                            this.value = val;
                            if(this.rendered){
                                this.el.child('div').update(
                                this.value);
                        }
                      }
                   });

   formFileLabel.setValue("<div id='testRendering'/>");
   formField[u]={
             colspan: 1,
             layout: "form",
             items: [formFileLabel]
          };

    if(field.autoUploadURL){
        formField[u].items[0].on('fileChange', this.filechanged);
        formField[u+1]={colspan: 1,
                     layout: "form",
                     items: [new Ext.form.TextField({
				name: field.id+"UploadID",
                                value: "",
                                hideLabel: true,
                                hidden: true,
                                id: field.id+"UploadID"
			})]
                   };

    }else
        if(field.action)
          formField[u+1].items[1]=new Ext.Button({
                    text: field.submitLabel,
                  //  renderTo: 'fi-basic-btn',
                    handler: eval(field.action)
                  });*/


   /*formField[u+1]={
      colspan: 1,
      layout: "form",
        html: "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
   };

   formField[u+2]={
             colspan: 1,
             layout: "form",
             items: [new Ext.Button({
                text: field.submitLabel,
              //  renderTo: 'fi-basic-btn',
                handler: eval(field.action)
              })]
       };*/
  return(fileUploadFormPanel);

}

