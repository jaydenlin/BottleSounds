package com.wearapp.util;

import android.app.Activity;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookUtil {
	private static FacebookOpenSessionDoneDelegate facebookOpenSessionDoneDelegateForInternal;

	public static void ensureThenOpenActiveSession(Activity activity,
			FacebookOpenSessionDoneDelegate facebookOpenSessionDoneDelegate) {

		facebookOpenSessionDoneDelegateForInternal = facebookOpenSessionDoneDelegate;
		Log.w("FacebookOpenSessionDoneDelegate",
				"fFacebookOpenSessionDoneDelegate do");

		if (!isActiveSessionOpen()) {
			Session.openActiveSession(activity, true,
					new Session.StatusCallback() {
						@Override
						public void call(Session session, SessionState state,
								Exception exception) {
							Log.w("FacebookOpenSessionDoneDelegate",
									"fFacebookOpenSessionDoneDelegate postExec");
							facebookOpenSessionDoneDelegateForInternal
									.postExec(session, state, exception);
						}
					});
		}
	}

	public static boolean isActiveSessionOpen() {
		return Session.getActiveSession() != null
				&& Session.getActiveSession().isOpened();
	}

}
