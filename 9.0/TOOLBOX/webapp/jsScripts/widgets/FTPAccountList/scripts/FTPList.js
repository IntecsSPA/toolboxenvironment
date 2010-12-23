Ext.namespace('xmlInterfaces');


xmlInterfaces.Application = function()
{
      var formsObject;  
      var formsCatInterfObject;
      var catalogueInterfaceWindow=null;
	return {
		init: function()
		{
                    
                       var xmlDocumentUrl="jsScripts/widgets/FTPAccountList/FTPAccountList.xml";
                       formsObject=createPanelExjFormByXml(xmlDocumentUrl);
                     
                       var catalogueWindow = new Ext.Window({
				title: 'FTP Account List',
				border: false,
                                animCollapse : true,
                                autoScroll : true,
                                resizable : true,
                                collapsible: true,
				layout: 'fit',
				width: 800,
				height: 600,
                                closeAction:'hide',
                                items:[formsObject.formsPanel]
			});
                        
			catalogueWindow.show();
                        catalogueWindow.setPosition((screen.width/2)-400,(screen.height/2)-300);
                      
                        formsObject.render();     
      
		},
                sendWritePermissionMessageToManager: function()
                {
                    var checkboxValue;
                    var url;
                        
                   checkboxValue=formsObject.formsArray[0].getForm().findField(this.handlerParm.fieldId).getValue();
                   url=this.handlerParm.managerUrl;
                   url+="&writePermission="+checkboxValue;
                  
                    var ajax=assignXMLHttpRequest();
                    ajax.open('POST',url,true);
                    ajax.setRequestHeader('Content-Length',0);
                    ajax.setRequestHeader('connection','close');
                   
                ajax.onreadystatechange= function() {
		if(ajax.readyState == 4) {
                      if(ajax.status!=200)
                          Ext.Msg.show({
   title:'Error',
   msg: 'Cannot set write permissions',
   buttons: Ext.Msg.OK,
   animEl: 'elId',
   icon: Ext.MessageBox.INFO
});
                      else Ext.Msg.show({
   title:'Status',
   msg: 'Write permissions set',
   buttons: Ext.Msg.OK,
   animEl: 'elId',
   icon: Ext.MessageBox.INFO
});
                      
                }
                 
	    }                    
              ajax.send('');  
           
                  //formsObject.formsPanel.findById(this.handlerParm.disableId).disable();
                },
                sendDeleteUserMessageToManager: function()
                {
                    var url=this.handlerParm.managerUrl;
                  
                    var ajax=assignXMLHttpRequest();
                    ajax.open('POST',url,true);
                    ajax.setRequestHeader('Content-Length',0);
                    ajax.setRequestHeader('connection','close');
                    ajax.onreadystatechange= function() {
		if(ajax.readyState == 4)
                     window.location="redirectToFTPList.jsp";
                       }                    
                    ajax.send('');        
                },
                checkboxChanged: function()
                {
                    var id;
                    
                    id=this.handlerParm.id;
                    
                    formsObject.formsArray[0].getForm().findField(id).enable();
                }
                
                
	};
}();

var xmlInt = xmlInterfaces.Application;
// Run the application when browser is ready
Ext.onReady(xmlInt.init, xmlInterfaces.Application);








