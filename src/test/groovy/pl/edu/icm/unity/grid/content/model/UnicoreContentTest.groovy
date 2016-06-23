package pl.edu.icm.unity.grid.content.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import pl.edu.icm.unity.stdext.identity.X500Identity
import spock.lang.Specification

import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.DEFAULT_QUEUE
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.ROLE
import static pl.edu.icm.unity.grid.content.model.UnicoreAttributes.XLOGIN

/**
 * @author: R.Kluszczynski
 */
class UnicoreContentTest extends Specification {

    def 'should parse json content'() {
        given:
        def expectedInspectorsGroup = new InspectorsGroup('/inspectors', ['CN=inspector1,C=PL', 'CN=inspector2,C=DE'])
        def expectedCentralSites = [
                new UnicoreSiteGroup('site-1', null, null, ['CN=site-1,O=unicore'], null),
                new UnicoreSiteGroup('site-2', null, null, ['CN=site-2,O=unicore'], null)
        ]
        def expectedCentralGroup = new UnicoreCentralGroup('/vo.unicore', expectedCentralSites, ['CN=central-server'])

        def expectedSiteGroupAgent = new ObjectNode(new JsonNodeFactory())
        expectedSiteGroupAgent.put(X500Identity.ID, 'CN=monitor,O=unicore')
        expectedSiteGroupAgent.put(XLOGIN.attributeName, 'monitor')
        expectedSiteGroupAgent.put(ROLE.attributeName, 'user')

        def expectedSiteGroupAttributes = new ObjectNode(new JsonNodeFactory())
        expectedSiteGroupAttributes.put(DEFAULT_QUEUE.attributeName, 'short')

        def expectedSiteGroup = new UnicoreSiteGroup(
                '/vo.site', [expectedSiteGroupAgent], null, ['CN=long-site,O=unicore'], expectedSiteGroupAttributes)

        def expectedContent = new UnicoreContent(
                expectedInspectorsGroup, [expectedCentralGroup], [expectedSiteGroup], ['/portal'])

        def inputStream = getClass().getClassLoader().getResourceAsStream('content-test.json')

        expect:
        new ObjectMapper().readValue(inputStream, UnicoreContent) == expectedContent
    }
}
