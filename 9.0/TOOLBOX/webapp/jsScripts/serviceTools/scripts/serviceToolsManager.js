


/*Import Form Interfaces -- START*/
    gcManager.loadGlobalScript("jsScripts/serviceTools/scripts/harvestFromURL.js");
    gcManager.loadGlobalScript("jsScripts/serviceTools/scripts/harvestFromFile.js");
 /*Import Form Interfaces -- END*/


/* Service Tool Interface Objects */
var harvestFromURL;
var harvestFromFile;


/* Service Tool Windows*/
var harvestFromFileWin=null;
var harvestFromURLWin=null;


var spot = new Ext.ux.Spotlight({
        easing: 'easeOut',
        duration: .3
    });

var spot2 = new Ext.ux.Spotlight({
        easing: 'easeOut',
        duration: .3
    });



function harvestFromFileInterface(serviceName){
    if(harvestFromFileWin == null){
        harvestFromFile=new HarvestFromFileInterface(serviceName);
        harvestFromFileWin=new WebGIS.Panel.WindowInterfacePanel({
                        title: 'Harvest from filesystem',
                        id: 'HarvestFromFileSystemWin',
                        border: false,
                        animCollapse : true,
                        maximizable : true,
                        autoScroll : true,
                        resizable : false,
                        draggable: false,
                        collapsible: false,
                        layout: 'fit',
                        serviceName: serviceName,
                        loadingBarImg: "images/loader1.gif",
                        loadingBarImgPadding: 60,
                        loadingMessage: "Loading... Please Wait...",
                        loadingMessagePadding: 30,
                        loadingMessageColor: "black",
                        loadingPanelColor: "#d9dce0",
                        loadingPanelDuration: 1000,
                        listeners:{
                          hide: function(){
                            spot.hide();
                          },
                          collapse: function(){
                            spot.hide();
                          },
                          expand: function(){
                            spot.show('HarvestFromFileSystemWin');
                          }
                        },
                        width: screen.width/2,
                        height: screen.height/3,
                        closeAction:'hide',
                        html: "<div id='HarvestFromFileSystemDiv'/>"
	});
        harvestFromFileWin.show();
        harvestFromFileWin.insertLoadingPanel();
        harvestFromFile.render("HarvestFromFileSystemDiv");
    }else
       harvestFromFileWin.show();
       spot.show('HarvestFromFileSystemWin');  
}

function harvestFromUrlInterface(serviceName){
    if(harvestFromURLWin == null){
        harvestFromURL=new HarvestFromURLInterface(serviceName);
        harvestFromURLWin=new WebGIS.Panel.WindowInterfacePanel({
                        title: 'Harvest from URL',
                        id: 'HarvestFromURLWin',
                        border: false,
                        animCollapse : true,
                        maximizable : true,
                        autoScroll : true,
                        resizable : false,
                        draggable: false,
                        collapsible: false,
                        layout: 'fit',
                        serviceName: serviceName,
                        loadingBarImg: "images/loader1.gif",
                        loadingBarImgPadding: 60,
                        loadingMessage: "Loading... Please Wait...",
                        loadingMessagePadding: 30,
                        loadingMessageColor: "black",
                        loadingPanelColor: "#d9dce0",
                        loadingPanelDuration: 1000,
                        listeners:{
                          hide: function(){
                            spot2.hide();
                          },
                          collapse: function(){
                            spot2.hide();
                          },
                          expand: function(){
                            spot2.show('HarvestFromURLWin');
                          }
                        },
                        width: screen.width/2,
                        height: screen.height/3,
                        closeAction:'hide',
                        html: "<div id='HarvestFromURLDiv'/>"
	});
        harvestFromURLWin.show();
        harvestFromURLWin.insertLoadingPanel();
        harvestFromURL.render("HarvestFromURLDiv");
    }else
       harvestFromURLWin.show();
       spot2.show('HarvestFromURLWin');
    


}