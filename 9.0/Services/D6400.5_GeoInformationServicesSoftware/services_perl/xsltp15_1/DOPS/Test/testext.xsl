<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:greeting="#DOPS/Test/greeting.pm">

<greeting:string>Hi World!</greeting:string>

<xsl:template match="/">
<html>
<body>
<h1>Test of extension element</h1>
<b><greeting:hello/></b><br/>
<h1>Test of extension function</h1>
<b><xsl:value-of select="greeting:hello()"/></b>
</body>
</html>
</xsl:template>

</xsl:stylesheet>