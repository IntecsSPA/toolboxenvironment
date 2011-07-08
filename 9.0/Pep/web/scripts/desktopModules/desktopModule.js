

/*
 * PEP Generic Desktop Module
 * author: Andrea Marongiu
 */

var DesktopModule = Ext.extend(Ext.app.Module, {
    init : function(){
                  
    },

    createWindow : function(src){
    
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('bogus'+src.windowId);
        var renderOp;
        if(!win){
            switch (src.type){
                case 'html':
                    win = desktop.createWindow({
                        id: 'bogus'+src.windowId,
                        title:src.text,
                        width:src.width,
                        height:src.height,
                        html : src.html,
                        iconCls: 'bogus',
                        shim:false,
                        animCollapse:false,
                        constrainHeader:true
                    });
                    break;
                case 'iframe':
                    win = desktop.createWindow({
                        id: 'bogus'+src.windowId,
                        title:src.text,
                        width:src.width,
                        height:src.height,
                        html : "<div id='"+src.frameDocId+"'></div><iframe src='"+src.docUrl+"' name='"+src.frameDocId+"_frame'  scrolling='yes' width='99%' height='99%' marginwidth='0' marginheight='0'></iframe>",
                        iconCls: 'bogus',
                        shim:false,
                        animCollapse:false,
                        constrainHeader:true
                    });
                    break;
                case 'interface':
                    if(src.genInterfaceString)
                         eval(src.extInterface+"="+src.genInterfaceString+";");
                    renderOp=src.extInterface+".render(\"extInterface"+src.windowId+"\")"; 
                    win = desktop.createWindow({
                        id: 'bogus'+src.windowId,
                        title:src.text,
                        width: src.width,
                        renderOp: renderOp,
                        height: src.height,
                        iconCls: 'bogus',
                        layout:'fit',
                        listeners: {
                          "resize":function(){
                              setTimeout(this.renderOp,500);
                              
                          },
                          "close": function(){
                              this.destroy(true);
                                           
                          } 
                        },
                        shim:false,
                         closeAction:'close',
                        animCollapse:false,
                        constrainHeader:true,
                        html: "<div id='extInterface"+src.windowId+"'/>"    
                    });
                    win.show(); 
                    renderOp=src.extInterface+".render(\"extInterface"+src.windowId+"\")";
                    setTimeout(renderOp,500);
                    break;    
               /* case 'tabInterface':
                    var formTabInterface=createPanelExjFormByXml(src.xmlInterfaceUrl);
                    win = desktop.createWindow({
                        id: 'bogus'+src.windowId,
                        title:src.text,
                        width:(screen.height/100)*71,
                        height:(screen.height/100)*71,
                        iconCls: 'bogus',
                        layout:'fit',
                        shim:false,
                        animCollapse:false,
                        constrainHeader:true,
                        html: "<div id='xmlTabInterface"+src.windowId+"'/>"    
                    });
                    win.show();
                    formTabInterface.formsPanel.render(document.getElementById("xmlTabInterface"+src.windowId));    
                    formTabInterface.render();
                    break;*/
                            /*win = new Ext.Window({
                            title:src.text,
                            width: src.width,
                            height: src.height,
                            id: 'bogus'+src.windowId,
                            //iconCls: 'icon-grid',
                            shim:false,
                            animCollapse:false,
                            constrainHeader:true,
                            layout: 'fit',
                            border: false,
                            maximizable: true,
                            resizable : true,
                            closeAction:'hide',
                            html : "<div id='configurationInterfaceDiv'></div>"
                    }); */
            }
                                
        }else{
            win.show();
            if(src.type == 'interface'){
                renderOp=src.extInterface+".render(\"extInterface"+src.windowId+"\")";
                setTimeout(renderOp,500);
            }
        }
            
    }
}); 