<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="yes" indent="no"/>
  <xsl:strip-space elements="*"/>

  <!-- Second block removes the elements named in the select attribute below -->
  <xsl:param name="removeElementsNamed" select="'|skickatTidpunkt|relation|status|valideringsunderlag|'"/>

  <xsl:template match="node()|@*" name="identity">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*">
    <xsl:if test=
        "not(contains($removeElementsNamed,
                 concat('|',name(),'|' )
                 )
        )
   ">
      <xsl:call-template name="identity"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
