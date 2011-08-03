/*
 * WPS Describe Process Edit Area Interface
 * author: Andrea Marongiu
 */


DescribeProcessEditAreaInterface=function(){

    this.xmlInterface="jsScripts/wpsWizard/resources/xml/editAreaWPSDescribeProcess.xml";
    
    this.formInterface=new Object();
     
    this.init=function(){
        this.formInterface=createPanelExjFormByXml(this.xmlInterface, null);
    },

    this.render=function (elementID){
        this.formInterface.formsPanel.render(document.getElementById(elementID));
        this.formInterface.render();
    };


    
    this.onSave=function(){
   
        
    }
    

    this.init();
};




