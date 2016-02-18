package pl.edu.icm.unity.grid.content.model

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class ContentEntriesTest extends Specification {

    def 'should parse json'() {
        given:
        def expectedContentGroup = new ContentGroup(
                '/vo.group/site',
                ['CN=site,O=Grid,C=PL'],
                ['simple-statement']
        )
        def expectedContentEntries = new ContentEntries([expectedContentGroup])

        def mapper = new ObjectMapper()
        def input = getClass().getClassLoader().getResourceAsStream("content-test.json")

        when:
        def entries = mapper.readValue(input, ContentEntries)
        println(entries)

        then:
        entries == expectedContentEntries
    }
}
