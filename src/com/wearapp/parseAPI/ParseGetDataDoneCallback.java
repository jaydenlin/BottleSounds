package com.wearapp.parseAPI;


import java.util.ArrayList;

import com.parse.ParseObject;

public interface ParseGetDataDoneCallback {
	void afterGetListDone(ArrayList<ParseObject> parseObjectList);
	void afterGetObjectDone(ParseObject parseObject);	
	
}
