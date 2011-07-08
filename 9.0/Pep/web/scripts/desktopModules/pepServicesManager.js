
/*
 * PEP Services Desktop Module
 * author: Andrea Marongiu
 */


PEPServicesManager = Ext.extend(DesktopModule, {
    init : function(){
        this.launcher = {
            text: "PEP Services Manager",
            iconCls: 'servicesmanager',
            handler: function() {
                return false;
            },
            menu: {
                items:[{ 
                    text: "Create PEP Services",
                    iconCls: 'createservice',
                    handler: function() {
                        return false;
                    },
                    menu: {
                        items:[{
                            text: "From WSDL",
                            iconCls: 'bogus',
                            handler : this.createWindow,
                            scope: this,
                            width: BrowserDetect.getWidth(50),
                            height : BrowserDetect.getHeight(50),
                            extInterface:"pepManager.createPEPServiceInterface",
                            genInterfaceString:"new CreatePEPServiceInterface()",
                            type: 'interface',
                            windowId: 'createPEPServicefromWSDL'
                        }]  
                    }
                }
                ]
            }
        }
    }
});

