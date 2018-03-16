<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="yes" indent="no"/>
  <xsl:strip-space elements="*"/>

  <!-- First block removes the root node. -->
  <xsl:template match="/*">
    <xsl:apply-templates select="*[1]"/>
  </xsl:template>

  <!-- identity for all other nodes -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
