
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
        alert(jsonRequest);

        var restRequest=this.formInterface.getFormValues()["DuplicateRestReuqst"];

        alert(restRequest);

        var duplicateServivcesResponse=function(response){

              alert(response);


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


