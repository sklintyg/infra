<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><xsl:output omit-xml-declaration="yes" indent="no"/><xsl:strip-space elements="*"/><xsl:param name="removeElementsNamed" select="'|skickatTidpunkt|relation|status|valideringsunderlag|'"/><xsl:template match="*"><xsl:choose><xsl:when test="contains($removeElementsNamed,concat('|',name(),'|' ))"></xsl:when><xsl:otherwise><xsl:element name="{local-name(.)}"><xsl:apply-templates select="node()|@*"/></xsl:element></xsl:otherwise></xsl:choose></xsl:template><xsl:template match="@*"><xsl:copy/></xsl:template></xsl:stylesheet>