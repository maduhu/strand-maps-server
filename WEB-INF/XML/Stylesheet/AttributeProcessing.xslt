<?xml version="1.0" encoding="UTF-8"?>
<!-- 
 Copyright 2002-2005 Boulder Learning Technologies Lab & American Association for the Advancement of Science, 
 DLC-1B20, Department of Computer Science, University of Colorado at Boulder,
 CO-80309, Tel: 303-492-0916, email: fahmad@colorado.edu
 
 This file is part of the Concept Map Service Software Project.
 
 The Concept Map Service Project is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of the
 License, or (at your option) any later version.
 
 The Concept Map Service OAI Project is distributed in the hope that it will be
 useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with The Concept Map Service System; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 USA
 
 Author: Faisal Ahmad
 Date  : Sept 14, 2005
 email : fahmad@colorado.edu
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:c="http://sms.dlese.org">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:template name="ProcessScope">
		<xsl:param name="scope"/>
		<xsl:param name="rest"/>
		
		<xsl:if test="$scope != ''">
			( O.Object_Type = '<xsl:value-of select="$scope"/>' )
			<xsl:if test="$rest != ''"> or </xsl:if>
		</xsl:if>

		<xsl:if test="$rest != ''">
			<xsl:call-template name="ProcessScope">
				<xsl:with-param name="scope" select="substring-before($rest,'+')"/>
				<xsl:with-param name="rest" select="substring-after($rest,'+')"/>
			</xsl:call-template>
		</xsl:if>
		
	</xsl:template>
	
</xsl:stylesheet>
