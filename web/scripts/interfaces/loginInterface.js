
/*
 * PEP Login Interface
 * author: Andrea Marongiu
 */


LoginInterface=function(desktopInterface){

    this.xmlInterface="resources/interfaces/login.xml";

    this.formInterface=new Object();
     
    this.desktopInterface=desktopInterface;
    
    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, interfacesManager.lang);
    },

    this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
    };


    
    this.onLogin=function(){
        var messageElement=document.getElementById("logging-msg");
        messageElement.innerHTML="<p class='info'>Please wait ...</p>";
        var deskInt=this.desktopInterface;
        var getUserPanelsFunc=function(response){
            var jsonResponse=JSON.parse(response);
            
            if(jsonResponse.success){
                pepManager.hideMask.defer(250);
                //pepManager.loginWindow.hide(); 
                deskInt.render();                
            }else{
              /*  Ext.Msg.show({
                title:'User access: Error',
                buttons: Ext.Msg.OK,
                msg: 'Wrong user or password.',
                animEl: 'logging-msg',
                icon: Ext.MessageBox.ERROR
                });   */
                var messageElement=document.getElementById("logging-msg");
                messageElement.innerHTML="<p class='error'>User access: Error - Wrong user or password.</p>";
            }            
        }

        var getUserPanelsError=function(){
            Ext.Msg.show({
                title:'User access: Error',
                buttons: Ext.Msg.OK,
                msg: 'Wrong user or password.',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

        var getUserPanelsTimeOut=function(){
            Ext.Msg.show({
                title:'User access: Error',
                buttons: Ext.Msg.OK,
                msg: 'Request TIME-OUT!',
                animEl: 'elId',
                icon: Ext.MessageBox.ERROR
            });
        }

    /*    var loginValues=this.formInterface.getFormValues();
          
        interfacesManager.user= loginValues['user'+this.formInterface.multiInterfaceId];
        interfacesManager.password=loginValues['password'+this.formInterface.multiInterfaceId];*/
           
        interfacesManager.user=document.getElementById("user").value;
        interfacesManager.password=document.getElementById("password").value;
        if(interfacesManager.user && interfacesManager.password)
   
        var onSubmit=sendAuthenticationXmlHttpRequestTimeOut("GET",
                "rest/userprofiles/"+interfacesManager.user,
                true, null, interfacesManager.user, interfacesManager.password, 800000, getUserPanelsFunc, getUserPanelsTimeOut,null,
                null, getUserPanelsError);
        else{
            var messageElement=document.getElementById("logging-msg");
            messageElement.innerHTML="<p class='warn'>Please insert user and password</p>";
            
        }
            


              
           
    };
    
    this.removeMsg= function(){
        var messageElement=document.getElementById("logging-msg");
        messageElement.innerHTML="";
    
    };
    
    

    this.init();
};

