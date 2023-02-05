package net.serenitybdd.plugins.jira.integration;

public class WhenConnectingToAJIRAServer {

    private static final String JIRA_WEBSERVICE_URL = "https://wakaleo.atlassian.net/rpc/soap/jirasoapservice-v2";

//    @Test
//    public void a_valid_user_should_be_able_to_open_a_session() throws Exception {
//        SOAPSession session = SOAPSession.openConnectionTo(new URL(JIRA_WEBSERVICE_URL))
//                .usingCredentials("bruce", "batm0bile");
//
//        assertThat(session, is(not(nullValue())));
//    }
//
//    @Test(expected = RemoteAuthenticationException.class)
//    public void an_invalid_user_should_not_be_able_to_open_a_session() throws Exception {
//        SOAPSession session = SOAPSession.openConnectionTo(new URL(JIRA_WEBSERVICE_URL))
//                .usingCredentials("bruce", "incorrect-password");
//
//        assertThat(session, is(not(nullValue())));
//    }
//
//    @Test(expected = RemoteException.class)
//    public void an_invalid_url_should_not_be_able_to_open_a_session() throws Exception {
//        SOAPSession session = SOAPSession.openConnectionTo(new URL("http://invalid.jira.url"))
//                .usingCredentials("bruce", "incorrect-password");
//
//        assertThat(session, is(not(nullValue())));
//    }
//
//
//    class TestableSOAPSession extends SOAPSession {
//
//        TestableSOAPSession(URL webServicePort) {
//            super(webServicePort);
//        }
//
//        @Override
//        protected JiraSoapServiceService getServiceLocator() {
//            throw new CouldNotLocateServiceException("oh drat!", null);
//        }
//
//    }
//
//    @Test(expected = CouldNotLocateServiceException.class)
//    public void when_the_session_cant_be_started_an_exception_is_raised() throws Exception {
//        SOAPSession session = new TestableSOAPSession(new URL(JIRA_WEBSERVICE_URL));
//        session.getJiraSoapService();
//    }
}
