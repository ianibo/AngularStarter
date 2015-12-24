package test.oauth2

import static helper.ErrorDescriptions.*
import static helper.TokenEndpointAssert.*

class ResourceOwnerCredentialsFunctionalSpec extends AbstractTokenEndpointFunctionalSpec {

    Map params = [grant_type: 'password', username: 'user', password: 'test', scope: 'test']

    void "resource owner credentials with no client"() {
        expect:
        assertAccessTokenErrorRequest(params, 401, 'unauthorized', FULL_AUTHENTICATION_REQUIRED)
    }

    void "resource owner credentials with public client"() {
        given:
        params << [client_id: 'public-client']

        expect:
        assertAccessTokenAndRefreshTokenRequest(params)
    }

    void "resource owner credentials with confidential client and no client secret"() {
        given:
        params << [client_id: 'confidential-client']

        expect:
        assertAccessTokenErrorRequest(params, 401, 'invalid_client', BAD_CLIENT_CREDENTIALS)
    }

    void "resource owner credentials with confidential client and incorrect client secret"() {
        given:
        params << [client_id: 'confidential-client', client_secret: 'incorrect']

        expect:
        assertAccessTokenErrorRequest(params, 401, 'invalid_client', BAD_CLIENT_CREDENTIALS)
    }

    void "resource owner credentials unknown user"() {
        given:
        params << [client_id: 'public-client']
        params.username = 'unknown-user'

        expect:
        assertAccessTokenErrorRequest(params, 400, 'invalid_grant', BAD_CREDENTIALS)
    }

    void "resource owner credentials known user and invalid password"() {
        given:
        params << [client_id: 'public-client']
        params.password = 'invalid-password'

        expect:
        assertAccessTokenErrorRequest(params, 400, 'invalid_grant', BAD_CREDENTIALS)
    }

    void "resource owner credentials with confidential client"() {
        given:
        params << [client_id: 'confidential-client', client_secret: 'secret-pass-phrase']

        expect:
        assertAccessTokenAndRefreshTokenRequest(params)
    }

    void "resource owner credentials requested for unauthorized client"() {
        given:
        params << [client_id: 'no-grant-client']

        expect:
        assertAccessTokenErrorRequest(params, 400, 'invalid_grant', GRANT_TYPE_REQUIRED)
    }

    void "resource owner credentials client is not allowed a refresh token"() {
        given:
        params << [client_id: 'password-only']

        expect:
        assertAccessTokenAndNoRefreshTokenRequest(params)
    }

    void "include user credential for grant type other than resource owner credentials"() {
        given:
        params.grant_type = 'implicit'

        expect:
        assertAccessTokenErrorRequest(params, 401, 'unauthorized', FULL_AUTHENTICATION_REQUIRED)
    }

    void "restrict scope of token"() {
        given:
        params << [client_id: 'password-and-scopes', scope: 'write read']

        expect:
        assertAccessTokenAndScopesRequest(params, ['write', 'read'])
    }
}
