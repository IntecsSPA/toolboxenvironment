<html>
    <head>
        <%
            String syntax = (request.getParameter("syntax") == null ? "html": request.getParameter("syntax"));
            String multifiles = (request.getParameter("multifiles") == null ? "false": request.getParameter("multifiles"));
            String gisClientPath = (request.getParameter("gcpath") == null ? "import/editarea/edit_area_full.js": request.getParameter("gcpath")+"/import/editarea/edit_area_full.js");
        %>
        <script language="Javascript" type="text/javascript" src="<%=gisClientPath%>"></script>
	<script language="Javascript" type="text/javascript">
		// initialisation
		editAreaLoader.init({
			id: "textAreaId"	// id of the textarea to transform
			,start_highlight: true	// if start with highlight
			,allow_resize: "both"
			,allow_toggle: false
			,word_wrap: true
                        ,show_line_colors: true
                        ,syntax: "<%=syntax%>"
                        ,is_multi_files: <%=multifiles%>
                        ,syntax_selection_allow: "css,html,js,php,python,vb,xml,c,cpp,sql,basic,pas,brainfuck"
                        ,toolbar: "search, go_to_line, |, undo, redo, |, select_font, |, syntax_selection, |, change_smooth_selection, highlight, reset_highlight, |, help"
			,language: "en"
		});


                // callback functions

                function setValue(value){
                   editAreaLoader.setValue("textAreaId", value);
                }
  
		function getValue(){
                    editAreaLoader.getValue("textAreaId");
                }

		
       </script>
    </head>
    <body>
        <textarea  id="textAreaId" style="height: 100%; width: 100%;" name="test_2">
            <html></html>
        </textarea>
    </body>

</html>