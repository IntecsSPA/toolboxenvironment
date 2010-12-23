package Tree

libs dir or . (cgi-bin)
          +
          ++ CGIXSLT.pm
          +
          ++ DOPS
              +
              ++XMLDocument.pm
              +
              ++XMLPrint.pm
              +
              ++XMLTree.pm
              +
              ++XPathvars.pm
              +
              ++XPath.pm
              +
              ++XSLT.pm
              +
              ++autoxsl
                   +
                   ++ *.pm
              +
              ++Extensions
                   +
                   ++http.pm

cgi-bin
        +
        ++xsltp.pl #rename to .cgi if it is needed
        +
        ++bytes.enc #or place it to site/lib/XML/Parser/Encodings
        +
        ++doc.xml
        +
        ++doc.xsl
        +
        ++Test
           +
           ++greeting.pm
           +
           ++msg.pm

Test the program
/cgi-bin/xsltp.pl if it returns "stylesheet is not passed" then all is OK
/cgi-bin/xsltp.pl?source=doc #read documentation 

Changes:
1.51
Since version 1.51 the global variables are evaluated differently to previous versions, namely
1. The global variable is processed only if the reference to it is encountered in a template(or other variable).
2. The order of global variables can be any. In earlier versions the global variables were treated the same way as templates. It produces the error "x is unknown variable" in the following
<xsl:variable name="x" select="1"/>
<xsl:variable name="y" select="x+1"/>
3.Now the globale variable that is used for an independent transformation must be initialized 
in some template of the main transformation.
<xsl:template match="/">
<xsl:variable name="_init_glob" select="$glob_name"/>
..........
Notice that it is needed only if the $glob_name variable is used nowhere in the main transformation.

Incorrect parsing of element names of the form <x-1> bug fixed 

     