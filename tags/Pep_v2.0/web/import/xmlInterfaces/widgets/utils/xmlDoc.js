
/*XML Document Object
 * author: Andrea Marongiu
 **/


XmlDoc = function (XmlURL){
    var xmlDoc;
    if(window.XMLHttpRequest){ // code for IE7+, Firefox, Chrome, Opera, Safari
       if(!(XmlURL instanceof XMLDocument)){
         var xmlhttp = new window.XMLHttpRequest();
         xmlhttp.open("GET", XmlURL, false);
         xmlhttp.send(null);
         xmlDoc = xmlhttp.responseXML;
       }else
         xmlDoc=XmlURL;

   }else{
        if(!(XmlURL instanceof ActiveXObject)){ // code for IE6, IE5
            xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            xmlhttp.open("GET",XmlURL,false);
            xmlhttp.send();
            xmlDoc=xmlhttp.responseXML; 
        }else
            xmlDoc=XmlURL;  
    }
    
  return{
    xmlDocument: xmlDoc,

    selectNodes: function (xpath){
      /*  if(BrowserDetect.browser == "Firefox" || BrowserDetect.browser == "Explorer")
           return this.xmlDocument.selectNodes(xpath);
        else{*/
            var elementsName=xpath.split('/');
            var eleCurrList;
            var eleCurr=this.xmlDocument;
            for(var i=0; i<elementsName.length;i++){
                if(elementsName[i]!=''){
                    if(elementsName[i].indexOf(':')!= -1)
                       elementsName[i]=elementsName[i].split(':')[1];
                    eleCurrList=eleCurr.getElementsByTagName(elementsName[i]);
                    eleCurr=eleCurrList[0];
                }
            }
           return(eleCurrList);
        }
    //}
  }

}


XmlElement = function (objectElement){
  return{
    xmlElement: objectElement,

    selectNodes: function (xpath){
        /*if(BrowserDetect.browser == "Firefox" || BrowserDetect.browser == "Explorer")
           if (this.xmlElement)
            return this.xmlElement.selectNodes(xpath);
           else
               return null;

        else{*/

            var elementsName=xpath.split('/');
            var eleCurrList;
            var eleCurr=this.xmlElement;
            for(var i=0; i<elementsName.length;i++){
                if(elementsName[i]!=''){
                    if(elementsName[i].indexOf(':')!= -1)
                       elementsName[i]=elementsName[i].split(':')[1];
                    eleCurrList=eleCurr.getElementsByTagName(elementsName[i]);
                    eleCurr=eleCurrList[0];
                }
            }
           return(eleCurrList);
        }
    }

 // }

}


