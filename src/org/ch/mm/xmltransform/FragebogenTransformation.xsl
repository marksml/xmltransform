<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
    <xsl:template match="/">
        <xsl:value-of select="/topmostSubform/Frage1[1]"/>,<xsl:value-of select="/topmostSubform/Frage2[1]"/>,<xsl:value-of select="/topmostSubform/Frage3[1]"/>,<xsl:value-of select="/topmostSubform/Frage4[1]"/>,<xsl:value-of select="/topmostSubform/Frage5[1]"/>,<xsl:value-of select="/topmostSubform/Frage6[1]"/>,<xsl:value-of select="/topmostSubform/Frage7[1]"/>,<xsl:value-of select="/topmostSubform/Frage8[1]"/>,<xsl:value-of select="/topmostSubform/Frage9[1]"/>,<xsl:value-of select="/topmostSubform/Frage10[1]"/>,<xsl:value-of select="/topmostSubform/Frage11[1]"/>
    </xsl:template>
</xsl:stylesheet>
