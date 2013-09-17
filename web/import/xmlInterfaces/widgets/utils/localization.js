
/* Localization Class*/



localization= function (xmlLocalizationURL){

        this.docLocUrl=xmlLocalizationURL;
        this.rootLocalization=null;

        this.init=function(){
            var localizationXML = new XmlDoc(this.docLocUrl);
            this.rootLocalization=new XmlElement(localizationXML.selectNodes(
                                                        "/loc:Localization")[0]);
                                                        
                                                         
        };
       this.getLocalMessage= function (messageName){
            var newMessage=this.rootLocalization.selectNodes("loc:"+messageName)[0].firstChild.nodeValue;
            if(!newMessage){
              Ext.Msg.show({
                title:'Localization Error',
                buttons: Ext.Msg.OK,
                msg: 'Message: '+messageName + "not defined.",
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
             });
              return("");
            }else
              return(newMessage);
        };

        this.init();
};

