<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version='1.0'>    
  <xsl:import href="/src/docbook-xsl-1.62.4/fo/docbook.xsl"/>
	<xsl:import href="fop-common.xsl"/>

	<!--
	for readable, though larger, HTML output
	<xsl:param name="chunker.output.indent" select="yes"/>

	shutup!
	<xsl:param name="chunk.quietly" select="1"/>
	-->

	<xsl:param name="page.margin.top" select="'0.5in'"></xsl:param>
	<xsl:param name="page.margin.outer">
		<xsl:choose>
			<xsl:when test="$double.sided != 0">0.5in</xsl:when>
			<xsl:otherwise>0.5in</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	<xsl:param name="page.margin.inner">
		<xsl:choose>
			<xsl:when test="$double.sided != 0">0.5in</xsl:when>
			<xsl:otherwise>0.5in</xsl:otherwise>
		</xsl:choose>
	</xsl:param>
	<xsl:param name="page.margin.bottom" select="'0.5in'"></xsl:param>

	<xsl:param name="chapter.autolabel" select="1"/>
	<xsl:param name="section.autolabel" select="1"/>
	<xsl:param name="html.ext" select="'.html'"/>
	<xsl:param name="html.stylesheet">http://threebit.net/style.css</xsl:param>
	<xsl:param name="toc.section.depth">5</xsl:param>
	<xsl:param name="toc.max.depth">10</xsl:param>
	<xsl:param name="generate.toc">
book toc,title
chapter title
section title
appendix title
	</xsl:param>
	<xsl:param name="generate.section.toc.level" select="5"></xsl:param>

	<!--
	<xsl:param name="article.autolabel" select="1"/>
 	<xsl:param name="callout.graphics.path">/src/docbook-xsl-1.62.4/images/callouts/</xsl:param> 
	<xsl:param name="funcsynopsis.decoration" select="1" />
	<xsl:param name="generate.article.toc" select="1" />
	<xsl:param name="generate.part.toc" select="1" />
	<xsl:param name="generate.section.toc" select="1" />
	<xsl:param name="section.label.includes.component.label" select="1" />
	<xsl:param name="toc.section.depth" select="5"/>
	<xsl:param name="use.id.as.filename" select="1"/>  	
	-->

	<xsl:template match="emphasis">
	<xsl:choose>
		<xsl:when test="(@role='strong') or (@role='bold')">
			<xsl:call-template name="inline.boldseq"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="inline.italicseq"/>
		</xsl:otherwise>
	</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
