package com.wearapp.util;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.facebook.Session;
public class FacebookUtil {
	
	public static boolean ensureFBOpenFBSession(Activity activity,Session.StatusCallback statusCallback) {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(activity, true, statusCallback);
            return false;
        }
        return true;
    }
	
	public static boolean ensureFBOpenFBSession(FragmentActivity activity,Session.StatusCallback statusCallback) {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(activity, true, statusCallback);
            return false;
        }
        return true;
    }
	
}
