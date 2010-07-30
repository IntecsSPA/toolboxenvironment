/* 
 * GISClient manager functionalities
 * author: Andrea Marongiu
 */

GisClientManager= function(lang, gisClientLibPath, proxyUrl, utilsUrl) {

 this.lang="eng";
 this.gisClientLibPath="";
 this.utilsUrl="Utils";
 this.proxyUrl="ProxyRedirect";
 this.loadScripts= new Array();
 this.loadCSS= new Array();

 this.init= function(){
     if(lang)
       this.lang=lang;
     if(gisClientLibPath)
       this.gisClientLibPath=gisClientLibPath;
     if(proxyUrl)
       this.proxyUrl=proxyUrl;
     if(utilsUrl)
       this.utilsUrl=utilsUrl;

     this.interfaceImport();
 };

 this.loadScript= function(url){
            if(!document.getElementById(url)){
                var script=document.createElement('script');
                script.defer=false;script.type="text/javascript";
                script.src=this.gisClientLibPath+"/"+url;
                script.id=url;
                document.getElementsByTagName('head')[0].appendChild(script);
                this.loadScripts.push(script);
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
                link.href=this.gisClientLibPath+"/"+url;;
                link.id=url;
                document.getElementsByTagName('head')[0].appendChild(link);
               // this.loadCSS.push(link);
            }
};


 this.interfaceImport= function(){

    /*INTERFACE FIELD -- Start Import*/
    this.loadScript("widgets/lib/ext/field/fileField.js");
    this.loadScript("widgets/lib/ext/field/spinnerField.js");
    this.loadScript("widgets/lib/ext/field/editAreaField.js");
        setTimeout("editAreaLoadDip();",3000);
    //setTimeout("spinnerLoadDip();",10000);




    /*INTERFACE FIELD -- End Import*/
 };

this.onLoadControl= function(cssNumber, scriptNumber){
    

};

this.init();

}
