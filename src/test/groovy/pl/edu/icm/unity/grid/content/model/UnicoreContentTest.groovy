package pl.edu.icm.unity.grid.content.model

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

/**
 * @author: R.Kluszczynski
 */
class UnicoreContentTest extends Specification {

    def 'should parse jsonek'() {
        given:
        def expectedInspectorsGroup = new InspectorsGroup('/inspectors', ['CN=inspector1,C=PL', 'CN=inspector2,C=DE'])
        def expectedCentralSites = [
                new UnicoreSiteGroup('site-1', ['CN=site-1,O=unicore'], null),
                new UnicoreSiteGroup('site-2', ['CN=site-2,O=unicore'], null)
        ]
        def expectedCentralGroup = new UnicoreCentralGroup('/vo.unicore', expectedCentralSites, ['CN=central-server'])
        def expectedSiteGroup = new UnicoreSiteGroup('/vo.site', ['CN=long-site,O=unicore'], 'long')

        def expectedContent = new UnicoreContent(
                expectedInspectorsGroup, [expectedCentralGroup], [expectedSiteGroup], ["/portal"])

        def inputStream = getClass().getClassLoader().getResourceAsStream("content-test.json")

        expect:
        new ObjectMapper().readValue(inputStream, UnicoreContent) == expectedContent
    }
}
