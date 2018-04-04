<!--
  ~ Copyright (C) 2018 Inera AB (http://www.inera.se)
  ~
  ~ This file is part of sklintyg (https://github.com/sklintyg).
  ~
  ~ sklintyg is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ sklintyg is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output omit-xml-declaration="yes" indent="no"/>
  <xsl:strip-space elements="*"/>
  <xsl:param name="removeElementsNamed" select="'|skickatTidpunkt|relation|status|valideringsunderlag|'"/>

  <xsl:template match="*">
    <xsl:choose>
      <xsl:when test="contains($removeElementsNamed,concat('|',name(),'|' ))">
        <!--
          Remove the unwanted elements
        -->
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="{local-name(.)}">
          <xsl:apply-templates select="node()|@*"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
    Remove document root
  -->
  <xsl:template match="/*">
    <xsl:apply-templates select="*[1]"/>
  </xsl:template>

  <!--
    Remove namespace

    local-name(someNode) returns only the local name of the node, and that doesn't
    include the prefix and colon in case the node is an element or an attribute.
  -->
  <xsl:template match="@*">
    <xsl:copy/>
  </xsl:template>

</xsl:stylesheet>