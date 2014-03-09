package com.wearapp.util;

import android.app.Activity;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.wearapp.exception.FacebookUtil.FacebookSessionNotActive;

public class FacebookUtil {
	private static FacebookOpenSessionDoneDelegate facebookOpenSessionDoneDelegateForInternal;
	public static void ensureThenOpenActiveSession(Activity activity, FacebookOpenSessionDoneDelegate facebookOpenSessionDoneDelegate) {

		facebookOpenSessionDoneDelegateForInternal = facebookOpenSessionDoneDelegate;

		if (!isActiveSessionOpen()) {
			Session.openActiveSession(activity, true, new Session.StatusCallback() {

				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if(session.isOpened()){
						facebookOpenSessionDoneDelegateForInternal.postExec(session, state, exception);
						
					}else{
						
						Log.w(this.getClass().getSimpleName(),"FB Session not create");
					}
				}
				
			});
		}
	}

	public static boolean isActiveSessionOpen() {
		return Session.getActiveSession() != null && Session.getActiveSession().isOpened();
	}
	
	public static String getAccessToken() throws FacebookSessionNotActive {
		
		if(isActiveSessionOpen()){
			return Session.getActiveSession().getAccessToken();
		}else{
			throw new FacebookSessionNotActive("FB Session not create");
		}
		
	}
	
	

}
