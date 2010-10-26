<?xml version="1.0"?>
<xsl:stylesheet version="1.0">
<xsl:param name="style" as="string"/>
<xsl:template match="/">
<html>
<body>
<h1>Test of xsl:message</h1>
<xsl:message msg-handler="DOPS::Test::msg" style="{$style}">
<html>
<body>The message is printed to file DOPS/Test/test_message.txt</body>
</html>
</xsl:message>
<p>The test sucsessfull</p>
</body>
</html>
</xsl:template>

</xsl:stylesheet>