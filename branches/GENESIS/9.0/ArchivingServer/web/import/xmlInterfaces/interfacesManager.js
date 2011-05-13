/* 
 * XMLInterfaces Manager 
 * author: Andrea Marongiu
 */


//create onDomReady Event
window.onDomReady = DomReady;

//Setup the event
function DomReady(fn)
{
  if(document.addEventListener){
      document.addEventListener("DOMContentLoaded", fn, false);
  }else{
     document.onreadystatechange = function(){readyState(fn)}
  }
}

//IE execute function
function readyState(fn)
{       
 if(document.readyState == "interactive"){
   fn();
 }
}



InterfacesManager= function(lang, xmlClientLibPath, proxyUrl, utilsUrl) {

 this.lang="eng";
 this.xmlClientLibPath="";
 this.utilsUrl="Utils";
 this.proxyUrl="ProxyRedirect";
 this.loadScripts= new Array();
 this.loadCSS= new Array();
 this.initFun=null;


 this.init= function(){
     if(lang)
       this.lang=lang;
     if(xmlClientLibPath)
       this.xmlClientLibPath=xmlClientLibPath;
     if(proxyUrl)
       this.proxyUrl=proxyUrl;
     if(utilsUrl)
       this.utilsUrl=utilsUrl;
   
   
   
 };

 this.setXmlClientLibPath= function(xmlClientLibPath){
     if(xmlClientLibPath)
       this.xmlClientLibPath=xmlClientLibPath;

   
     /*Browser util*/
     this.loadScript("widgets/utils/browserDetect.js");
 }


 this.setLanguage= function(lang){
     if(lang)
       this.lang=lang;
 }



 this.loadGlobalScript= function(url){
            if(!document.getElementById(url)){
                var script=document.createElement('script');
                script.defer=false;script.type="text/javascript";
                script.src=url;
                script.id=url;
                document.getElementsByTagName('head')[0].appendChild(script);
                this.loadScripts.push(script);
            }
};

 

 this.loadScript= function(url, onLoadFn){

            if(!document.getElementById(url)){
                var script;
               /* var agent = navigator.userAgent;
                var docWrite = (agent.match("MSIE") || agent.match("Safari"));
                if(docWrite){
                    alert(url);
                    script = "<script src='" + url + "' id='" + url + "'></script>";
                    document.write(script);
                    if(onLoadFn)
                      onLoadFn();
                }else{*/
                    script=document.createElement('script');
                    script.defer=false;script.type="text/javascript";
                    script.src=this.xmlClientLibPath+"/"+url;
                    script.id=url;

                    if(onLoadFn){


                     script.onreadystatechange= function () {
                          
                        if (this.readyState == 'loaded') onLoadFn();
                       }
                     script.onload= onLoadFn;
                     }
                    document.getElementsByTagName('head')[0].appendChild(script);
                    this.loadScripts.push(script);
                    if(onLoadFn){
                        var agent = navigator.userAgent;
                            var docWrite = agent.match("MSIE");
                            if(docWrite){
                                onLoadFn();
                            }
                                
                    }
              // }
            }
 };
 this.insertScript= function(content, id){
   var script=document.createElement('script');
   script.defer=false;script.type="text/javascript";
   script.innerHTML=content;
   script.id=id;
   document.getElementsByTagName('head')[0].appendChild(script);
   //this.loadScripts.push(script);
};
 this.loadCSS= function(url){
            if(!document.getElementById(url)){
                var link=document.createElement('link');
                link.defer=false;
                link.rel="stylesheet";
                link.type="text/css";
                link.href=this.xmlClientLibPath+"/"+url;;
                link.id=url;
                document.getElementsByTagName('head')[0].appendChild(link);
               // this.loadCSS.push(link);
            }
};


 this.mainImport= function(){

    
    
    /*Extjs Import*/
    this.extjsImport();

    /*GIS Import*/
   // this.gisImport();


    /*Sarissa Import*/
    this.sarissaImport();
    

    this.widgetsImport();
    

 };
 
 

 this.extjsImport= function(){

     /*EXTjs Import -- START*/
     this.loadCSS("import/ext/resources/css/ext-all.css");

     /*var agent = navigator.userAgent;
     if((agent.match("MSIE") || agent.match("Safari"))){
        this.loadScript("import/ext/adapter/ext/ext-base.js");
        this.loadScript("import/ext/ext-all.js");
     }else{ */
         //this.loadCSS("import/ext/resources/css/xtheme-gray.css");

         this.loadScript("import/ext/adapter/ext/ext-base.js",
                function(){
                   // alert("ext-all import");
                    interfacesManager.loadScript("import/ext/ext-all.js",
                       /*Util Import*/
                        function(){
                             interfacesManager.extExtensionImport();
                             interfacesManager.utilImport();
                             /*interfacesManager.gisImport();*/
                             interfacesManager.extInterfaceImport();
                          }
                    );
                 }
          );
    // }
     /*EXTjs Import -- END*/
 };

 this.extExtensionImport= function(){
        /* Extension CSS*/
            /* this.loadCSS("import/ext/ux/css/Portal.css");
             this.loadCSS("import/ext/ux/css/GroupTab.css");*/

         /*Extension Javascript*/
             /*this.loadScript("import/ext/ux/GroupTabPanel.js");
             this.loadScript("import/ext/ux/GroupTab.js");
             this.loadScript("import/ext/ux/Portal.js");
             this.loadScript("import/ext/ux/PortalColumn.js");
             this.loadScript("import/ext/ux/PortalColumn.js");*/
             this.loadScript("import/ext/ux/BufferView.js");
             this.loadScript("import/ext/ux/RowExpander.js");
 };

 this.gisImport= function(){
    // alert("gis imported");
    /*OpenLayers Import -- START*/
     this.loadScript("import/OpenLayers/lib/OpenLayers.js", function(){
         interfacesManager.loadScript("widgets/lib/openlayers/Control/ScaleBar.js");
        interfacesManager.loadScript("widgets/lib/openlayers/Control/SetBox.js");
        interfacesManager.loadScript("widgets/lib/openlayers/Format/XMLKeyValue.js");
        interfacesManager.loadScript("widgets/style/locale/en"/*+this.lang*/+".js");

     });
     
     this.loadCSS("widgets/style/css/scalebar-thin.css");
    /*OpenLayers Import -- END*/
    
    /*WebGis Import -- START*/
   //  this.loadScript("widgets/style/locale/en"/*+this.lang*/+".js");
     this.loadCSS("widgets/style/css/webgis.css");
     this.loadScript("widgets/lib/webgis/Control/Toc.js");
     this.loadScript("widgets/lib/webgis/Control/ScaleList.js");
     this.loadScript("widgets/lib/webgis/MapAction/MapAction.js");
     this.loadScript("widgets/lib/webgis/MapAction/Basic.js");
     this.loadScript("widgets/lib/webgis/MapAction/Editor.js");
     this.loadScript("widgets/lib/webgis/MapAction/Measure.js");
     this.loadScript("widgets/lib/webgis/MapAction/Identify.js");
    /*WebGis Import -- END*/

      
 };

 this.sarissaImport= function(){
   this.loadScript("import/sarissa/sarissa-compressed.js", function(){interfacesManager.loadScript("import/sarissa/sarissa_ieemu_xpath-compressed.js");});
 };


 this.widgetsImport= function(){

    //alert("widget imported");

    /*EXTjs widgets Import -- START*/
    
     this.loadScript("widgets/interface/extjs/ExtInterface.js");
     

     /*Import Ext Interface Field */
    // interfacesManager.extInterfaceImport();

    /*EXTjs widgets Import -- END*/

 };

 this.utilImport= function(){

  /*Spotlight util*/
 // interfacesManager.loadScript("import/ext/ux/Spotlight.js");

  /*JSON util*/
  interfacesManager.loadScript("widgets/utils/json.js");

  /*Encoders*/
  interfacesManager.loadScript("widgets/utils/encoders/base64.js");

  /*general util*/
  interfacesManager.loadScript("widgets/utils/general.js",function(){
            /*XML util*/
           interfacesManager.loadScript("widgets/utils/xmlDoc.js",function(){
            /*Localization util*/
            interfacesManager.loadScript("widgets/utils/localization.js", function(){ 
                var agent = navigator.userAgent;
                if(agent.match("MSIE")){
                  setTimeout("interfacesManager.initFun();", 5000);
                }else
                  interfacesManager.initFun();
            }
            );})
        });

 };
 
 this.extInterfaceImport= function(){

    /*EXT INTERFACE FIELD -- Start Import*/


    /*Ext ux extension*/
    interfacesManager.loadScript("import/ext/ux/fileuploadfield/FileUploadField.js");

    /* BBOX field*/
  //  interfacesManager.loadScript("widgets/lib/ext/field/bboxField.js");

    /* FILE field*/
    interfacesManager.loadCSS("import/ext/ux/fileuploadfield/css/fileuploadfield.css");
    interfacesManager.loadScript("widgets/interface/extjs/fields/fileField.js");

    /* MULTITEXT field*/
    interfacesManager.loadScript("widgets/interface/extjs/fields/multiTextField.js");

    /* CHECKBOXGROUP field*/
   // interfacesManager.loadScript("widgets/lib/ext/field/checkBoxGroupField.js");

    /* SPINNER field*/
   // interfacesManager.loadScript("widgets/lib/ext/field/spinnerField.js");

    /* EDITAREA field*/
  //  interfacesManager.loadScript("widgets/lib/ext/field/editAreaField.js");

  //  interfacesManager.loadScript("widgets/lib/ext/RowExpander.js");

    /*EXT INTERFACE FIELD -- End Import*/
 };

 this.onReady= function(startFun){
    this.initFun=startFun;
    this.mainImport();
 };

this.onLoadControl= function(cssNumber, scriptNumber){
    

};

this.init();

}


var interfacesManager=null;
interfacesManager= new InterfacesManager();
