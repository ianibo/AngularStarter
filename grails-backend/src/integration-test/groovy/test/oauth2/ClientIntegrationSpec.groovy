package test.oauth2

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class ClientIntegrationSpec extends Specification {

    void "client secret should be encoded differently for each public client"() {
        when:
        def client1 = new Client(clientId: 'client-1').save(flush: true)
        def client2 = new Client(clientId: 'client-2').save(flush: true)

        then:
        client1.clientSecret != null
        client2.clientSecret != null

        and:
        client1.clientSecret != client2.clientSecret
    }
}
