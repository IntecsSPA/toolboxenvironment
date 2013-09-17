<%-- 
    Document   : index
    Created on : 19-feb-2009, 11.47.33
    Author     : Andrea Marongiu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>Policy Enforcement Point Application</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    
    <!-- ************ XMLInterfaces Import *************-->
        <!-- Load jquery -->
        <script type="text/javascript" src="import/xmlInterfaces/import/jquery.js"></script>
        
    <!-- ***********************************************-->    
    
    <!-- ************ Load XMLInterfaces **************-->
        <script type="text/javascript" src="import/xmlInterfaces/interfacesManager.js"></script>
    <!-- **********************************************-->
    
    <script type="text/javascript" src="scripts/pepManager.js"></script>
    
    <style type="text/css">
        .settings {
                background-image:url(resources/images/settings.png);
            }
            .find {
                background-image:url(resources/images/find3.gif);
            }
            .specificfind {
                background-image:url(resources/images/find.png);
            }

            #loading-mask{
                position:absolute;
                left:0;
                top:0;
                width:100%;
                height:100%;
                z-index:20000;
                background-color:white;
            }
            #loading{
                position:absolute;
                left:45%;
                top:40%;
                padding:2px;
                z-index:20001;
                height:auto;
            }
            #loading a {
                color:#225588;
            }
            #loading .loading-indicator{
                background:white;
                color:#444;
                font:bold 13px tahoma,arial,helvetica;
                padding:10px;
                margin:0;
                height:auto;
            }
            #loading-msg {
                font: normal 10px arial,tahoma,sans-serif;
            }
    
            .x-check-group-alt {
                background: #D1DDEF;
                border-top:1px dotted #B5B8C8;
                border-bottom:1px dotted #B5B8C8;
            }
            
            .upload-icon {
                background: url('resources/images/image_add.png') no-repeat 0 0 !important;
            }
            
            .remove {
             background-image:url(resources/images/remove.png) !important;
            }
            
            
            
            .documentation {
                background: url('resources/images/documentation.png') no-repeat 0 0 !important;
            }
            
            .log {
                background: url('resources/images/log.png') no-repeat 0 0 !important;
            }
            
            .chaintypes {
                background: url('resources/images/chainTypes.png') no-repeat 0 0 !important;
            }
            
            .watches {
                background: url('resources/images/watches.png') no-repeat 0 0 !important;
            }
            
            .configuration {
                background: url('resources/images/configuration.png') no-repeat 0 0 !important;
            }
            
            .datalist {
                background: url('resources/images/datalist.gif') no-repeat 0 0 !important;
            }
            
            .manager {
                background: url('resources/images/manager.png') no-repeat 0 0 !important;
            }
            
           table.status {
                border-width: 1px;
                border-style: solid;
                border-color: gray;
                border-collapse: collapse;
                background-color: white;
            }
            table.status th {
                    border-width: 1px;
                    padding: 2px;
                    border-style: solid;
                    border-color: rgb(153, 187, 232);
                    background-color: white;

            }
            table.status td {
                    border-width: 1px;
                    padding: 2px;
                    border-style: solid;
                    border-color: rgb(153, 187, 232);
                    background-color: white;

            }
            
            
            
            table.loadingTable {
                border-width: 3px;
                border-style: double;
                border-color: rgb(153, 187, 232);
                border-collapse: collapse;
                background-color: white;
            }
            table.loadingTable th {
                    border-width: 1px;
                    padding: 1px;
                    border-style: none;
                    border-color: rgb(153, 187, 232);
                    background-color: white;

            }
            table.loadingTable td {
                    border-width: 1px;
                    padding: 1px;
                    border-style: none;
                    border-color: rgb(153, 187, 232);
                    background-color: white;

            }
            
            #loading-mask-workspace{
                position:absolute;
                left:0;
                top:0;
                width:100%;
                height:100%;
                z-index:20000;
                background-color:rgb(228, 231, 231);
            }
            
        
    </style>


  </head>
       
    <body scroll="no">

            <div id="x-desktop">
                <a href="http://www.openlayers.org/" target="_blank" style="margin:5px; float:right;"><table><tr><td><img src="script/desktop/images/toolboxLogo.gif" /></td><td valign="middle"><b style="color: #ffffff;">- Manager</b></td></tr></table></a>
                <!--a href="http://extjs.com" target="_blank" style="margin:5px; float:right;"><img src="script/desktop/images/powered.gif" /></a-->

                <dl id="x-shortcuts">
                    <dt id="downsrs-win-shortcut">
                        <a href="#"><img src="script/desktop/images/s.gif" />
                        <div>Download Gis Client Source</div></a>
                    </dt>
                    <!--dt id="acc-win-shortcut">
                        <a href="#"><img src="home/images/s.gif" />
                        <div>Accordion Window</div></a>
                    </dt-->
                </dl>
            </div>

            <div id="ux-taskbar">
                    <div id="ux-taskbar-start"></div>
                    <div id="ux-taskbuttons-panel"></div>
                    <div class="x-clear"></div>
            </div>

    </body>
</html>
