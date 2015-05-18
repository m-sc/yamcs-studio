package org.yamcs.studio.core;

import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.yamcs.api.ws.YamcsConnectionProperties;
import org.yamcs.protobuf.Rest.RestExceptionMessage;
import org.yamcs.protobuf.Rest.RestListAvailableParametersRequest;
import org.yamcs.protobuf.Rest.RestListAvailableParametersResponse;
import org.yamcs.studio.core.web.ResponseHandler;
import org.yamcs.studio.core.web.RestClient;

import com.google.protobuf.MessageLite;

public class YamcsLoginModule implements LoginModule {

    private static final Logger log = Logger.getLogger(YamcsLoginModule.class.getName());

    private Subject subject;
    private CallbackHandler callbackHandler;

    /** Name of authenticated user or <code>null</code> */
    private String user = null;
    private char[] password = null;

    public YamcsLoginModule() {
        System.out.println("yamcs login, constructor");
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {

        this.subject = subject;
        this.callbackHandler = callbackHandler;

    }

    @Override
    public boolean login() throws LoginException {
        log.info("");

        if (callbackHandler == null)
            throw new LoginException("No CallbackHandler");

        final String user_pw[] = getUserPassword();
        if (authenticate(user_pw[0], user_pw[1]))
        {
            user = user_pw[0];
            password = user_pw[1].toCharArray();
            return true;
        }
        else
        {
            try {
                YamcsPlugin.getDefault().setAuthenticatedPrincipal(null);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            throw new LoginException("wrong credentials.");
        }
    }

    /**
     * Obtain user name and password via callbacks
     *
     * @return Array with user name and password
     * @throws LoginException
     *             on error
     */
    private String[] getUserPassword() throws LoginException
    {
        final NameCallback name = new NameCallback("User Name:");
        final PasswordCallback password = new PasswordCallback("Password :", false);
        try
        {
            callbackHandler.handle(new Callback[] { name, password });
        } catch (Throwable ex)
        {
            ex.printStackTrace();
            throw new LoginException("Cannot get user/password");
        }
        final String result[] = new String[]
        {
                name.getName(),
                new String(password.getPassword())
        };
        password.clearPassword();
        return result;
    }

    /**
     * authenticate() This is a method to test if authorization is allowed via the REST Api. It
     * could be attempted on any service of the API, the actual data returned is not used.
     */
    private boolean authenticate(final String user, final String password)
    {
        System.out.println("yamcs login, authenticating " + user + "/" + password);

        RestListAvailableParametersRequest.Builder req = RestListAvailableParametersRequest.newBuilder();
        req.addNamespaces(YamcsPlugin.getDefault().getMdbNamespace());

        YamcsConnectionProperties webProps = YamcsPlugin.getDefault().getWebProperties();
        RestClient restClient = new RestClient(webProps, new YamcsCredentials(user, password));

        AuthReponseHandler arh = new AuthReponseHandler();
        restClient.listAvailableParameters(req.build(), arh);

        while (!arh.resultReceived)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
            }
        }
        return arh.authenticated;
    }

    @Override
    public boolean commit() throws LoginException {
        System.out.println("yamcs login, commit");
        if (user == null)
            return false;
        Principal principal = new SimplePrincipal(user);
        subject.getPrincipals().add(principal);

        try {
            YamcsPlugin.getDefault().setAuthenticatedPrincipal(new YamcsCredentials(user, password));
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
            throw new LoginException("Unable to establish connections to Yamcs. " + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        System.out.println("yamcs login, abort");
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        System.out.println("yamcs login, logout");
        try {
            YamcsPlugin.getDefault().setAuthenticatedPrincipal(null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new LoginException(e.getMessage());
        }
        return true;
    }

    private class AuthReponseHandler implements ResponseHandler
    {

        public boolean resultReceived = false;
        public boolean authenticated = false;

        @Override
        public void onMessage(MessageLite responseMsg) {
            if (responseMsg instanceof RestExceptionMessage) {
                log.log(Level.WARNING, "Exception returned by server: " + responseMsg);
            } else {
                RestListAvailableParametersResponse response = (RestListAvailableParametersResponse) responseMsg;
            }
            resultReceived = true;
            authenticated = true;
        }

        @Override
        public void onException(Exception e) {
            log.log(Level.SEVERE, "Could not fetch available yamcs parameters", e);

            resultReceived = true;
            authenticated = false;
        }

    }

}