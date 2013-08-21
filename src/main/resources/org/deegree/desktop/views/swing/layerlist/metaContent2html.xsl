<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:iso19115="http://schemas.opengis.net/iso19115full" xmlns:smXML="http://metadata.dgiwg.org/smXML"
xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco">
	<xsl:output method="html"/>
	<xsl:template match="//iso19115:MD_Metadata">
		<html>
			<!--
	no body element for java JTextPane required
			<body>
-->
			<h2>
					Title: <xsl:value-of select="iso19115:identificationInfo/*/smXML:citation/smXML:CI_Citation/smXML:title/smXML:CharacterString"/>
			</h2>
			<table cellspacing="3" border="0" width="400">
				<tbody>
					<tr>
						<th align="left" width="100">Element</th>
						<th align="left" width="300">Value</th>
					</tr>
					<tr>
						<td>fileIdentifier</td>
						<td>
							<xsl:value-of select="iso19115:fileIdentifier/smXML:CharacterString"/>
						</td>
					</tr>
					<tr>
						<td>hierarchylevel</td>
						<td>
							<xsl:value-of select="iso19115:hierarchyLevelName/smXML:CharacterString"/>
						</td>
					</tr>
					<tr>
						<td>abstract</td>
						<td>
							<xsl:value-of select="iso19115:identificationInfo/*/smXML:abstract/smXML:CharacterString"/>
						</td>
					</tr>
					<tr>
						<td>purpose</td>
						<td>
							<xsl:value-of select="iso19115:identificationInfo/*/smXML:purpose/smXML:CharacterString"/>
						</td>
					</tr>
				</tbody>
			</table>
			<!--
	no body element for java JTextPane required
			</body>
				-->
		</html>
	</xsl:template>
	
	<xsl:template match="//gmd:MD_Metadata">
        <html>
            <!--
    no body element for java JTextPane required
            <body>
-->
            <h2>
                    Title: <xsl:value-of select="gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/>
            </h2>
            <table cellspacing="3" border="0" width="400">
                <tbody>
                    <tr>
                        <th align="left" width="100">Element</th>
                        <th align="left" width="300">Value</th>
                    </tr>
                    <tr>
                        <td>fileIdentifier</td>
                        <td>
                            <xsl:value-of select="gmd:fileIdentifier/gco:CharacterString"/>
                        </td>
                    </tr>
                    <tr>
                        <td>hierarchy level</td>
                        <td>
                            <xsl:value-of select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue"/>
                        </td>
                    </tr>
                    <tr>
                        <td>abstract</td>
                        <td>
                            <xsl:value-of select="gmd:identificationInfo/*/gmd:abstract/gco:CharacterString"/>
                        </td>
                    </tr>
                    <tr>
                        <td>purpose</td>
                        <td>
                            <xsl:value-of select="gmd:identificationInfo/*/gmd:purpose/gco:CharacterString"/>
                        </td>
                    </tr>
                </tbody>
            </table>
            <!--
    no body element for java JTextPane required
            </body>
                -->
        </html>
    </xsl:template>
</xsl:stylesheet>
