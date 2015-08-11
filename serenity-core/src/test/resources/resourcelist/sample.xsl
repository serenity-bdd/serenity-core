<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <body>
                <h2>Acceptance test result: <xsl:value-of select="/acceptance-test-run/@title"/></h2>
                <table>
                    <tr>
                        <th>Step</th>
                        <th>Outcome</th>
                    </tr>
                    <xsl:for-each select="/acceptance-test-run/test-step">
                        <tr>
                            <td>
                                <xsl:value-of select="description" />
                            </td>
                            <td>
                                <xsl:value-of select="@result" />
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>