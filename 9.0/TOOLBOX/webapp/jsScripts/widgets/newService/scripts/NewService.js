Ext.namespace('xmlInterfaces');


xmlInterfaces.Application = function()
{
      var formsObject;  
      var formsCatInterfObject;
      var catalogueInterfaceWindow=null;
	return {
		init: function()
		{
                    
                       var xmlDocumentUrl="jsScripts/widgets/newService/resources/xml/FirstPanel.xml";
                       formsObject=createPanelExjFormByXml(xmlDocumentUrl);
                     
                       var catalogueWindow = new Ext.Window({
				title: 'Service creation',
				border: false,
                                animCollapse : true,
                                autoScroll : true,
                                resizable : true,
                                collapsible: true,
				layout: 'fit',
				width: 350,
				height: 118,
                                closeAction:'hide',
                                items:[formsObject.formsPanel]
			});
                        
			catalogueWindow.show();
                        catalogueWindow.setPosition((Window.width/2)-125,(Window.height/2)-75);
                      
                        formsObject.render();     
      
		}
	};
}();

var xmlInt = xmlInterfaces.Application;
// Run the application when browser is ready
Ext.onReady(xmlInt.init, xmlInterfaces.Application);








