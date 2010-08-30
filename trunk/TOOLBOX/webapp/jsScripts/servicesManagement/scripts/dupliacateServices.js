
/*
 * Toolbox Duplicate Service Interface
 * author: Andrea Marongiu
 */

DuplicateServicesInterface=function(){

     this.xmlInterface="jsScripts/servicesManagement/resources/xml/duplicateGroupServicesPanel.xml";

     this.formInterface=createPanelExjFormByXml(this.xmlInterface);

     this.onCheckService=function(){
         var currentSelection=this.formInterface.formsArray[0].getForm().findField("dupServices").getSelected();
         var multiText=Ext.getCmp("DuplicateName");
         multiText.removeAll(false);
         for(var i=0; i<currentSelection.length; i++)
           multiText.addTextField(currentSelection[i],"<b>\""+currentSelection[i]+"\"</b> New name",currentSelection[i]+"_2");
           multiText.doLayout();
    };

    this.onDuplicateServices=function(){
        var multiText=Ext.getCmp("DuplicateName");
        var obj=multiText.getValues();

        var jRequest=new Object();
        jRequest.key=multiText.id;
        jRequest.value=obj;

        var jsonRequest = JSON.stringify(jRequest);
        //alert(jsonRequest);

        var restRequest=this.formInterface.getFormValues()["DuplicateRestReuqst"];

        //alert(restRequest);

        var duplicateServivcesResponse=function(response){
              var checkboxGroup=Ext.getCmp("dupServices_cont");
              var multiText=Ext.getCmp("DuplicateName");
              multiText.removeAll(false);
              checkboxGroup.updateValues();
              var jsonResp=JSON.parse(response);
              if(jsonResp.success){
                  var services=jsonResp.created;
                  var stringServices="";
                  for(var i=0; i<services.length; i++){
                      stringServices+="\n -"+services[i].name;
                      
                  }
                Ext.Msg.show({
                     title: 'Duplicate Group Services',
                     buttons: Ext.Msg.OK,
                     width: Math.floor((screen.width/100)*50),
                     msg: "<pre>The following services have been created: "+stringServices+" </pre>",
                     /*fn: function(){
                        window.location = 'main.jsp';
                     },*/
                     icon: Ext.MessageBox.INFO
                });

              }else
                Ext.Msg.show({
                        title:'Duplicate Services: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Internal Error!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });

        };
       var duplicateServivcesTimeOut=function(){
                     Ext.Msg.show({
                        title:'Duplicate Services: Error',
                        buttons: Ext.Msg.OK,
                        msg: 'Request TIME-OUT!',
                        animEl: 'elId',
                        icon: Ext.MessageBox.ERROR
                    });
       };

       var onSubmit=sendXmlHttpRequestTimeOut("POST",
                 restRequest,
                 true, jsonRequest, 800000,
                 duplicateServivcesResponse,
                 duplicateServivcesTimeOut,null);
    };


     this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();

     }

};


