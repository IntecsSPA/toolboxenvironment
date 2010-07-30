



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

  var action;
 if(field.action)
    action=field.action;
 else
    action=rootFolder+utilServlet+"?cmd=putFile&type=proxy&fileId="+field.id+"_file";
 var htmlFormValue="<div id='editAreaDIV_"+field.id+"'><form name='formFile_"+field.id+"_editAreaForm' action='"+action+"' method='POST' onsubmit='javascript:onSubmit_editArea_"+field.id+"()' enctype='multipart/form-data' target='iframeMessage_editArea"+field.id+"'>"+
            "<input type='file' id='"+field.id+"_file' name='"+field.id+"_file' value='' width='"+cols+"' />"+ //onchange='javascript:"+onchange+"' onsubmit='javascript:alert('onsubmit')''
             "<input type='submit' name='buttonSubmit_"+field.id+"' value='"+"Load File"+"'/>"+
            "</form><iframe scrolling='no' FRAMEBORDER='0'name='iframeMessage_editArea"+field.id+"' width='0' height='0' marginwidth='0' marginheight='0/></div>";




  var clientManager= new GisClientManager();
  var loadCallback;
  var saveCallback;

  if(field.loadCallback)
     loadCallback=field.loadCallback;
  else{
      loadCallback="editArea_load_"+field.id;
      var callbackLoadFunction= " var editAreaWindow_"+field.id+"=null; \n"+
             " function editArea_load_"+field.id+"(id) { \n"+
             " if(!editAreaWindow_"+field.id+")" +
                    "editAreaWindow_"+field.id+" = new Ext.Window({ "+
                                            " title: 'Edit Area ("+field.id+") : Load Local File',"+
                                            " id: 'editAreaWindow_"+field.id+"',"+
                                            " border: false,"+
                                            " animCollapse : false,"+
                                            " autoScroll : true,"+
                                            " resizable : true,"+
                                            " collapsible: false,"+
                                            " layout: 'fit',"+
                                            " width:  screen.width/3,"+
                                            " height: 70,"+
                                            " closeAction:'hide',"+
                                            " html: \""+htmlFormValue+"\""+
                                        " }); "+
            " editAreaWindow_"+field.id+".show();"+
          //  "editAreaLoader.setValue(id, \"The content is loaded from the load_callback function into EditArea\");\n"+
            "} \n";
      callbackLoadFunction+="function onSubmit_editArea_"+field.id+"() { \n"+
          "setTimeout('loadFileContent_editArea_"+field.id+"()', 500);"+
          "} \n";
      callbackLoadFunction+="function loadFileContent_editArea_"+field.id+"() { \n"+
          " if(parent.iframeMessage_editArea"+field.id+".document.getElementById('textarea')) { \n"+
              " var filename=document.getElementById('"+field.id+"_file').value;";
       if(field.isMultiFile)
         callbackLoadFunction+=" var new_file= {id: filename, text: parent.iframeMessage_editArea"+field.id+".document.getElementById('textarea').value, syntax: '"+field.syntax+"'}; "+
            " editAreaLoader.openFile('"+field.id+"', new_file); ";
       else
         callbackLoadFunction+="  editAreaLoader.setValue('"+field.id+"', parent.iframeMessage_editArea"+field.id+".document.getElementById('textarea').value); ";

       callbackLoadFunction+=" editAreaWindow_"+field.id+".hide(); " +
          " }else {"+
             " if(parent.iframeMessage_editArea"+field.id+".document.getElementsByTagName('head')[0]) "+
                    " Ext.Msg.show({ "+
                                "title:'Load File: Error',"+
                                "buttons: Ext.Msg.OK,"+
                                "msg: 'Utilis or Custom Service  Exception!',"+
                                "animEl: 'elId',"+
                                "icon: Ext.MessageBox.ERROR"+
                          "}); "+
            " else "+
                " setTimeout('loadFileContent_editArea_"+field.id+"()', 500);"+
          " } \n" +

          " } \n";
      clientManager.insertScript(callbackLoadFunction, "EditAreaCallbackLoad_"+field.id);
    }

  if(field.saveCallback)
     saveCallback=field.saveCallback;
  else{
      saveCallback="editArea_save_"+field.id;
      var callbackSaveFunction=" function editArea_save_"+field.id+"(id, content) { \n"+

            "alert(\"Here is the content of the EditArea '\"+ id +\"' as received by the save callback function:\\n\"+content);\n"+
            "} \n ";
      clientManager.insertScript(callbackSaveFunction, "EditAreaCallbackSave_"+field.id);
    }


  var editAreaLodearFunction="editAreaLoader.init({"+
            		"id: field.id"+	// id of the textarea to transform
			",start_highlight: true"+
			",font_size: '8'";
                        if(field.fontFamily)
                            editAreaLodearFunction+=",font_family: '"+field.fontFamily+"'"
    editAreaLodearFunction+=//",allow_resize: 'y'"+
			",allow_toggle: false"+
                        ",word_wrap: true"+
			",language: 'en'"+ //change for localization
			",syntax: '"+field.syntax+"'"+
			",toolbar: '"+field.toolbar+"'"+
			",load_callback: '"+loadCallback+"'"+
			",save_callback: '"+saveCallback+"'";
                         if(field.defaultFiles || field.value){
                           var eA_load_callback=" function editAreaLoaded"+field.id+" () { \n";
                                                  if(field.defaultFiles){
                                                     eA_load_callback+=" var defaultFiles="+field.defaultFiles+"; \n"+
                                                     "for(var i=0; i<defaultFiles.length; i++){ \n"+
                                                        " if(defaultFiles[i].loadURL){ \n"+
                                                            "var setEditArea=function(response){ \n"+
                                                                 " if(!response){ \n"+
                                                                          "Ext.Msg.show({ \n"+
                                                                                "title:'Load Content: Error', \n"+
                                                                                "buttons: Ext.Msg.OK, \n"+
                                                                                "msg: 'Service Exception!', \n"+
                                                                                "animEl: 'elId', \n"+
                                                                                "icon: Ext.MessageBox.ERROR \n"+
                                                                          "}); \n"+
                                                                      "}else{ \n"+
                                                                              " defaultFiles[i].text=response; \n";
                                                                  if(field.isMultiFile)
                                                                     eA_load_callback+="editAreaLoader.openFile('"+field.id+"', defaultFiles[i]); \n";
                                                                  else
                                                                     eA_load_callback+="editAreaLoader.setValue('"+field.id+"', defaultFiles[i].text); \n";
                                                   eA_load_callback+="} \n"+
                                                            " }; \n"+
                                                             "var setEditAreaTimeOut=function(){ \n"+
                                                                 "Ext.Msg.show({ \n"+
                                                                    "title:'Load Content: Error', \n"+
                                                                    "buttons: Ext.Msg.OK, \n"+
                                                                    "msg: 'Request TIME-OUT!', \n"+
                                                                    "animEl: 'elId', \n"+
                                                                    "icon: Ext.MessageBox.ERROR \n"+
                                                                "}); \n"+
                                                             "};"+
                                                            "var onSubmit=sendXmlHttpRequestTimeOut('GET', " +
                                                                "defaultFiles[i].loadURL, " +
                                                                "false, null, 80000, setEditArea, setEditAreaTimeOut,null); "+
                                                       "} else \n";
                                                       if(field.isMultiFile)
                                                          eA_load_callback+="editAreaLoader.openFile('"+field.id+"', defaultFiles[i]); \n";
                                                       else
                                                          eA_load_callback+="editAreaLoader.setValue('"+field.id+"', defaultFiles[i].text); \n";
                             eA_load_callback+=" } \n";
                                      }else
                                        eA_load_callback+=" var defaultFiles='"+field.value+"'; \n"+
                                                            "editAreaLoader.setValue('"+field.id+"', defaultFiles); \n";
                        eA_load_callback+="\n }";

                        clientManager.insertScript(eA_load_callback, "EditAreaCallbackLoad_"+field.id);
                        editAreaLodearFunction+=",EA_load_callback: 'editAreaLoaded"+field.id+"'";
                      }
                        if(field.syntaxSelectionAllow)
                          editAreaLodearFunction+=",syntax_selection_allow: '"+field.syntaxSelectionAllow+"'"
                        if(field.isMultiFile)
                          editAreaLodearFunction+=",is_multi_files: '"+field.isMultiFile+"'"
                        if(field.plugins)
                            editAreaLodearFunction+=",plugins: '"+field.plugins+"'"
                        if(field.charmapDefault)
                            editAreaLodearFunction+=",charmap_default: '"+field.charmapDefault+"'"

   editAreaLodearFunction+="});";



  eval(editAreaLodearFunction);

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
                                    tag: "textarea",
                                    id: field.id,
                                    name: field.name,
                                    //onchange: onchange,
                                    cols: cols,
                                    rows: rows,
                                    autocomplete: "off"
                                },
                                //value: eval(field.defaultFiles),
                                hideLabel: field.hideLabel,
                                disabled: field.disabled,
                                hidden: field.hidden,
                                id: field.id,
                                fieldType: "editarea",
                                labelStyle: field.labelStyle,
                                labelSeparetor: field.labelSeparetor,
				fieldLabel: label,
                                msgTarget : 'qtip',
                                multipleFile: field.isMultiFile,
                                vtype: field.vtype,
                                vtypeText: field.vtypeText,
                                getEditorValue: function(){
                                  if(this.multipleFile){
                                    return(editAreaLoader.getAllFiles(field.id));
                                  }else
                                    return(editAreaLoader.getValue(field.id));
                                },
                                //objValue String for single File
                                // or Object
                                 /*   *  id : (required) A string that will identify the file. it's the only required field.
                                      Type: String
                                    * title : (optionnal) The title that will be displayed in the tab area.
                                      Type: String
                                      Default: set with the id field value
                                    * text : (optionnal) The text content of the file.
                                      Type: String
                                      Default: ""
                                    * syntax : (optionnal) The syntax to use for this file.
                                      Type: String
                                      Default: ""
                                    * do_highlight : (optionnal) Set if the file should start highlighted or not
                                      Type: String
                                      Default: ""*/
                                // For multiple File
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


