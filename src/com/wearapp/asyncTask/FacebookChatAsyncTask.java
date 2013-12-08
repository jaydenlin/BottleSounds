package com.wearapp.asyncTask;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.Sasl;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLMechanism;

import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class FacebookChatAsyncTask extends AsyncTask<String, Void, Void>{

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
	    String targetFacebookId = params[0];
	    String title=params[1];
	    String message = params[2];
	    ConnectionConfiguration config = new ConnectionConfiguration("chat.facebook.com", 5222);
	    config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        
        //prevent keystore problem
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            config.setTruststoreType("AndroidCAStore");
            config.setTruststorePassword(null);
            config.setTruststorePath(null);
        } else {
            config.setTruststoreType("BKS");
            String path = System.getProperty("javax.net.ssl.trustStore");
            if (path == null)
                path = System.getProperty("java.home") + File.separator + "etc"
                    + File.separator + "security" + File.separator
                    + "cacerts.bks";
            config.setTruststorePath(path);
        }
        
        //set up connection
        XMPPConnection xmpp = new XMPPConnection(config);
        SASLAuthentication.registerSASLMechanism("X-FACEBOOK-PLATFORM",SASLXFacebookPlatformMechanism.class);
        SASLAuthentication.supportSASLMechanism("X-FACEBOOK-PLATFORM", 0);
        
        try {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        	String apiKey = Session.getActiveSession().getApplicationId();
            String sessionKey = Session.getActiveSession().getAccessToken();
            xmpp.connect();
            xmpp.login(apiKey, sessionKey);
            Chat chat = xmpp.getChatManager().createChat( "-"+targetFacebookId+"@chat.facebook.com", null );
            Message msg = new Message("-"+targetFacebookId+"@chat.facebook.com", Message.Type.chat);
            msg.setSubject(title);
            msg.setBody(message);
            chat.sendMessage(msg);
            chat.sendMessage( msg );

        } catch (XMPPException e1) {
            Log.i("XMPPClient",
                    "Unable to " + xmpp.getHost());
            e1.printStackTrace();
        }
	    
		return null;
	}

	
	public static class SASLXFacebookPlatformMechanism extends SASLMechanism
    {
        public static final String NAME = "X-FACEBOOK-PLATFORM";

        public SASLXFacebookPlatformMechanism(
                SASLAuthentication saslAuthentication )
        {
            super( saslAuthentication );
        }

        private String apiKey = "";

        private String accessToken = "";

        @Override
        protected void authenticate() throws IOException, XMPPException
        {
            AuthMechanism stanza = new AuthMechanism( getName(), null );
            getSASLAuthentication().send( stanza );
        }

        @SuppressWarnings( "hiding" )
        @Override
        public void authenticate( String apiKey, String host, String accessToken )
                throws IOException, XMPPException
        {
            if( apiKey == null || accessToken == null )
            {
                throw new IllegalStateException( "Invalid parameters!" );
            }

            this.apiKey = apiKey;
            this.accessToken = accessToken;
            this.hostname = host;

            String[] mechanisms = { "DIGEST-MD5" };
            Map<String, String> props = new HashMap<String, String>();
            this.sc = Sasl.createSaslClient( mechanisms, null, "xmpp", host,
                    props, this );
            authenticate();
        }

        @Override
        public void authenticate( String username, String host,
                CallbackHandler cbh ) throws IOException, XMPPException
        {
            String[] mechanisms = { "DIGEST-MD5" };
            Map<String, String> props = new HashMap<String, String>();
            this.sc = Sasl.createSaslClient( mechanisms, null, "xmpp", host,
                    props, cbh );
            authenticate();
        }

        @Override
        protected String getName()
        {
            return NAME;
        }

        @Override
        public void challengeReceived( String challenge ) throws IOException
        {
            byte response[] = null;
            if( challenge != null )
            {
                String decodedResponse = new String(
                        org.jivesoftware.smack.util.Base64.decode( challenge ) );
                Map<String, String> parameters = getQueryMap( decodedResponse );

                String version = "1.0";
                String nonce = parameters.get( "nonce" );
                String method = parameters.get( "method" );

                Long callId = Long.valueOf( System.currentTimeMillis() );

                String composedResponse = String
                        .format(
                                "method=%s&nonce=%s&access_token=%s&api_key=%s&call_id=%s&v=%s",
                                URLEncoder.encode( method, "UTF-8" ),
                                URLEncoder.encode( nonce, "UTF-8" ),
                                URLEncoder.encode( this.accessToken, "UTF-8" ),
                                URLEncoder.encode( this.apiKey, "UTF-8" ),
                                callId, URLEncoder.encode( version, "UTF-8" ) );
                response = composedResponse.getBytes();
            }

            String authenticationText = "";

            if( response != null )
            {
                authenticationText = org.jivesoftware.smack.util.Base64
                        .encodeBytes(
                                response,
                                org.jivesoftware.smack.util.Base64.DONT_BREAK_LINES );
            }

            Response stanza = new Response( authenticationText );

            getSASLAuthentication().send( stanza );
        }

        private Map<String, String> getQueryMap( String query )
        {
            String[] params = query.split( "&" );
            Map<String, String> map = new HashMap<String, String>();
            for( String param : params )
            {
                String name = param.split( "=" )[0];
                String value = param.split( "=" )[1];
                map.put( name, value );
            }
            return map;
        }
    }

}
