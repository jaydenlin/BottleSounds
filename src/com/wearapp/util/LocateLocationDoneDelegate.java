package com.wearapp.util;

import android.location.Location;

public interface LocateLocationDoneDelegate {
	public void postExec(Location lastKnownocation);
}
