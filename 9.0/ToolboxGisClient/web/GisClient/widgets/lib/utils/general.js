

function replaceAll(text, strA, strB)
{
    return text.replace( new RegExp(strA,"g"), strB );
}

function assignXMLHttpRequest() {
   var XHR = null;
   browserUtente = navigator.userAgent.toUpperCase();
   if(typeof(XMLHttpRequest) === "function" || typeof(XMLHttpRequest) === "object")
      XHR = new XMLHttpRequest();
   else
      if(window.ActiveXObject && browserUtente.indexOf("MSIE 4") < 0)
	{
	 if(browserUtente.indexOf("MSIE 5") < 0)
	    XHR = new ActiveXObject("Msxml2.XMLHTTP");
         else
	    XHR = new ActiveXObject("Microsoft.XMLHTTP");
        }
  return XHR;
}

 var request=null;
 var timeOutEvent=null;
// var xhrTimeout=null;
function sendXmlHttpRequestTimeOut(requestMethod, requestUrl, requestAsync, requestBody, timeOutRequest, eventResponse, eventTimeOut, headers, loading, eventError){
    request=assignXMLHttpRequest();
    timeOutEvent=eventTimeOut;
    if(timeOutRequest <1000)
       timeOutRequest=timeOutRequest*1000;
    var message;
    request.open(requestMethod, requestUrl, requestAsync);
    request.setRequestHeader("connection", "close");
    var headSplit;
    if(headers){
       for(var i=0;i<headers.length; i++){
           headSplit=headers[i].split(",");
           request.setRequestHeader(headSplit[0], headSplit[1]);
       }
    }
    var xhrTimeout=setTimeout("request.abort();timeOutEvent.call();",timeOutRequest);
    if(requestAsync){
          request.onreadystatechange= function(){
                 ajaxResponseManager(request,xhrTimeout,message,eventResponse,eventError);
          };
    }
    if(loading){
       message=Ext.MessageBox.wait(loading.message,loading.title);
       //String title, String msg, [String progressText] )
     }
     request.send(requestBody);
     if(!requestAsync){
         var result=ajaxResponseManager(request,xhrTimeout,message,eventResponse,eventError);
         return result;
     }
}

function ajaxResponseManager(request,xhrTimeout,message,eventResponse,eventError){
  try{
      if(request.readyState == 4){
         clearTimeout(xhrTimeout);
         xhrTimeout=null;
         if(request.status == 200){
                    if(message)
                       message.hide();
                     eventResponse(request.responseText);
                     return true;
                }else{
                   if(eventError)
                     eventError(request.responseText);
                   if(request){
                      Ext.Msg.show({
                          title: 'Error: ' + request.statusText,
                          buttons: Ext.Msg.OK,
                          width: Math.floor((screen.width/100)*50),
                          msg: request.responseText,
                          animEl: 'elId',
                          icon: Ext.MessageBox.ERROR
                      });
                   }
                 return false;
               }
     }
  }catch(e){
     Ext.Msg.show({
        title:'Request Exception',
        buttons: Ext.Msg.OK,
        msg: 'EXCEPTION: '+e.message,
        animEl: 'elId',
        icon: Ext.MessageBox.ERROR
     });
     return false;
  }
}



    /*
     *resumeEvents
     *
     *
     *xhr.onreadystatechange=function(){

   if (xhr.readyState == 4 && xhr.status == 200) {
      clearTimeout(xhrTimeout);   // Looks like we didn't time out!
      // Use xhr.responseText to parse the server's response
      alert(xhr.responseText);
   }
}

// Now that we're ready to handle the response, we can make the request
xhr.send("My excellent post info");
// Timeout to abort in 5 seconds
var xhrTimeout=setTimeout("ajaxTimeout();",5000);
function ajaxTimeout(){
   xhr.abort();
   alert("Well dang, the AJAX request timed out.  Did you lose network "+
         "connectivity for some reason?");
   // Note that at this point you could try to send a notification to the
   // server that things failed, using the same xhr object.
}
</SCRIPT>
     **/
function pausecomp(millis){
var date = new Date();
var curDate = null;
do { curDate = new Date(); }
while(curDate-date < millis);
}



function removeXmlDiterctive(xmlString){
 var check=xmlString.indexOf("<?");
 if(check>=0){
    newXmlString=xmlString.substr(xmlString.indexOf("?>")+2,xmlString.length);
    return(newXmlString);
 }
 else
   return(xmlString);
}

/**
*
*  Javascript trim, ltrim, rtrim
*  http://www.webtoolkit.info/
*  Javascript trim implementation removes all leading and trailing occurrences of a set of characters specified. If no characters are specified it will trim whitespace characters from the beginning or end or both of the string.
*  Without the second parameter, they will trim these characters:
*   /*  " " (ASCII 32 (0x20)), an ordinary space.
    * "\t" (ASCII 9 (0x09)), a tab.
    * "\n" (ASCII 10 (0x0A)), a new line (line feed).
    * "\r" (ASCII 13 (0x0D)), a carriage return.
    * "\0" (ASCII 0 (0x00)), the NUL-byte.
    * "\x0B" (ASCII 11 (0x0B)), a vertical tab.
**/

function trim(str, chars) {
    return ltrim(rtrim(str, chars), chars);
}

function ltrim(str, chars) {
    chars = chars || "\\s";
    return str.replace(new RegExp("^[" + chars + "]+", "g"), "");
}

function rtrim(str, chars) {
    chars = chars || "\\s";
    return str.replace(new RegExp("[" + chars + "]+$", "g"), "");
}

// Modificare Utilizzando Reg
function normalize_html(text){
  text=replaceAll(text,"\"","\\\"");
  text=replaceAll(text,"\n","\"+\"");
  text=replaceAll(text,"\b","");
  text=replaceAll(text,"\f","");
  text=replaceAll(text,"\t","");
  text=replaceAll(text,"\r","");
  text=trim(text);
  //text=normalize_space(text);
  return(text);
}

// Modificare Utilizzando Reg
function adapt_xml(text){

  text=replaceAll(text,"<","&lt;");
  text=replaceAll(text,">","&gt;");
  text=replaceAll(text,"&","&amp;");
  text=replaceAll(text,"\"","\\\"");

  /*/text=replaceAll(text,"\t","");
  text=replaceAll(text,"\r","");
  text=trim(text);
  text=normalize_space(text);  */
  return(text);
}