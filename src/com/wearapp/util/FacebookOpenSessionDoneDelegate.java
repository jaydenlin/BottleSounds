package com.wearapp.util;

import com.facebook.Session;
import com.facebook.SessionState;

public interface FacebookOpenSessionDoneDelegate {
	public void postExec(Session session, SessionState state,Exception exception);
}
