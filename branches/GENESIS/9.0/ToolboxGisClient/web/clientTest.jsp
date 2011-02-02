<%-- 
    Document   : clientTest
    Created on : 13-nov-2008, 13.13.14
    Author     : root
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>TOOLBOX Web Client</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <script>
            var localToolboxURL="<%=request.getParameter("tbxUrl")%>"; 
            var serviceSelected="<%=request.getParameter("service")%>";
         </script>
        <link rel="stylesheet" type="text/css" href="GisClient/import/ext/resources/css/ext-all.css" />
        <script type="text/javascript" src="GisClient/widgets/lib/jquery/jquery-1.2.6.js"></script>
        <script type="text/javascript" src="GisClient/import/ext/adapter/jquery/ext-jquery-adapter.js"></script>
        <script type="text/javascript" src="GisClient/import/ext/adapter/ext/ext-base.js"></script>
        <script type="text/javascript" src="GisClient/import/ext/ext-all.js"></script>
        
        <script src="GisClient/import/OpenLayers/lib/OpenLayers.js"></script>
        <script type="text/javascript" src="GisClient/import/sarissa/Sarissa.js"></script>
        <script type="text/javascript" src="GisClient/import/sarissa/sarissa_ieemu_xpath.js"></script>

        <link rel="stylesheet" type="text/css" href="GisClient/widgets/style/css/webgis.css" />
        
        <script type="text/javascript" src="GisClient/widgets/lib/utils/general.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/ext/ExtFormUtils.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/ext/ExtFormType.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/ext/RowExpander.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/openlayers/Control/Identify.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/Control/Map.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/Control/Toc.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/Control/ScaleList.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/MapAction/MapAction.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/MapAction/Basic.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/MapAction/Editor.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/MapAction/Measure.js"></script>
        <script type="text/javascript" src="GisClient/widgets/lib/webgis/MapAction/Identify.js"></script>
        
        <script type="text/javascript" src="GisClient/widgets/style/locale/en.js"></script>
        
        <script type="text/javascript" src="script/testCenter.js"></script>
        <link rel="stylesheet" type="text/css" href="style/css/testCenter.css"/>
        
         <!-- Widget SelectionTool import -->
            <script src="GisClient/widgets/lib/openlayers/Control/SetBox.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Control/SelectWMS.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Control/RemoveSelectWMS.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Control/SelectionToolbar.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Filter/GeoSpatial.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Format/XMLKeyValue.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Format/FILTER.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Format/FILTERShort.js"></script>
            <script src="GisClient/widgets/lib/openlayers/Format/SLDShort.js"></script>
            <script src="GisClient/widgets/lib/openlayers/SLDWMS.js"></script>
            
    <!-- Widget SelectionTool End Import -->
    <!-- Edit Area Configuration-->
        <script src="jsScripts/import/editarea/edit_area_full.js"></script>
        <script language="Javascript" type="text/javascript">
        function initEditArea(textArea){
		editAreaLoader.init({
			id: textArea	// id of the textarea to transform
			,start_highlight: true
			,allow_toggle: false
            ,allow_resize: "both"
			,language: "en"
			,syntax: "xml"
			,toolbar: "new_document, |, search, go_to_line, |, undo, redo, |, select_font, |, syntax_selection, |, change_smooth_selection, highlight, reset_highlight, |, help"
			,syntax_selection_allow: "java,html,js,php,python,xml,c,cpp,sql"
			,show_line_colors: true
		});
        }
        </script>
    <!-- End Edit Area Configuration -->
    </head>
  
    <body>
        
    </body>
</html>