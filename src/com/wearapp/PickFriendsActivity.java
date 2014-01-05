/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wearapp;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.wearapp.asyncTask.FacebookChatAsyncTask;
import com.wearapp.resultcode.ResultCode;
import com.wearapp.util.LocationUtil;
import com.wearapp.util.MySQLUtil;
import com.wearapp.util.UploadUtil;

// This class provides an example of an Activity that uses FriendPickerFragment to display a list of
// the user's friends. It takes a programmatic approach to creating the FriendPickerFragment with the
// desired parameters -- see PickPlaceActivity in the PlacePickerSample project for an example of an
// Activity creating a fragment (in this case a PlacePickerFragment) via XML layout rather than
// programmatically.
public class PickFriendsActivity extends FragmentActivity {
	FriendPickerFragment friendPickerFragment;
	NewPermissionsRequest newPermissionsRequest;
	EditText editDialog;
    StringBuffer friendslist_selected;
    
    
   
	// A helper to simplify life for callers who want to populate a Bundle with
	// the necessary
	// parameters. A more sophisticated Activity might define its own set of
	// parameters; our needs
	// are simple, so we just populate what we want to pass to the
	// FriendPickerFragment.
    
	public static void populateParameters(Intent intent, String userId, boolean multiSelect, boolean showTitleBar) {
		intent.putExtra(FriendPickerFragment.USER_ID_BUNDLE_KEY, userId);
		intent.putExtra(FriendPickerFragment.MULTI_SELECT_BUNDLE_KEY, multiSelect);
		intent.putExtra(FriendPickerFragment.SHOW_TITLE_BAR_BUNDLE_KEY, showTitleBar);
	}

	private void sendMessage() {
		// TODO Auto-generated method stub
		List<GraphUser> selectedUsers = friendPickerFragment.getSelection();
		String targetFacebookId;
		String title = "Heare";	    
		String message = "\n---------------------------\n"+editDialog.getText().toString() + "\n---------------------------\n"+"\n Sent by Heare in "+ LocationUtil.selectedlocation.getName() + "\n"+"https://maps.google.com/maps?q="+LocationUtil.selectedlocation.getLocation().getLatitude()+","+LocationUtil.selectedlocation.getLocation().getLongitude() +"\n---------------------------\n"; 
		
    	for (GraphUser selectedUser : selectedUsers){            		            	    	
			targetFacebookId = selectedUser.getId();;	 	   
	        new FacebookChatAsyncTask().execute(targetFacebookId,title,message);	             
	    }
    	
    	insertVoiceToMySQL(message, "test", LocationUtil.selectedlocation);
    	
    	Toast.makeText(getApplicationContext(), "Message just sent to "+friendslist_selected.toString(), Toast.LENGTH_LONG).show();
    	friendslist_selected.delete(0, friendslist_selected.length());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_friends_activity);
		newPermissionsRequest = new NewPermissionsRequest(this, Arrays.asList("xmpp_login"));
		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null) {
			// First time through, we create our fragment programmatically.
			final Bundle args = getIntent().getExtras();
			friendPickerFragment = new FriendPickerFragment(args);
			fm.beginTransaction().add(R.id.friend_picker_fragment, friendPickerFragment).commit();
		} else {
			// Subsequent times, our fragment is recreated by the framework and
			// already has saved and
			// restored its state, so we don't need to specify args again. (In
			// fact, this might be
			// incorrect if the fragment was modified programmatically since it
			// was created.)
			friendPickerFragment = (FriendPickerFragment) fm.findFragmentById(R.id.friend_picker_fragment);
		}

		friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
			@Override
			public void onError(PickerFragment<?> fragment, FacebookException error) {
				PickFriendsActivity.this.onError(error);
			}
		});

		friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> fragment) {
				// We just store our selection in the Application for other
				// activities to look at.
				// FriendPickerApplication application =
				// (FriendPickerApplication) getApplication();
				List<GraphUser> selectedUsers = friendPickerFragment.getSelection();
				Session session = Session.getActiveSession();
				friendslist_selected = new StringBuffer();
				friendslist_selected.delete(0, friendslist_selected.length());
				
				editDialog = new EditText(PickFriendsActivity.this);
				editDialog.setRawInputType(Configuration.KEYBOARD_QWERTY);
    	    	editDialog.setInputType(InputType.TYPE_CLASS_TEXT);    	    	     	    		    	    	    	 								

				if (session.isOpened()) {
					session.requestNewReadPermissions(newPermissionsRequest);

					for (GraphUser selectedUser : selectedUsers) {
						if (selectedUsers.size() - 1 != selectedUsers.indexOf(selectedUser))
							friendslist_selected.append(selectedUser.getName() + ",");
						else
							friendslist_selected.append(selectedUser.getName());
					}

				
    	    	 new AlertDialog.Builder(PickFriendsActivity.this
    	    			).setMessage("Please edit the messages to send to "+friendslist_selected+" in "+LocationUtil.selectedlocation.getName()+" ?" 
    	             	).setView(editDialog
    	    			).setPositiveButton("YES", 
    	             			new DialogInterface.OnClickListener() {
    	 							@Override
    	 							public void onClick(DialogInterface dialog, int which) {
    	 								// TODO Auto-generated method stub													
    	 								sendMessage();
    	 								finishActivity();
    	 							}
    	             	}
    	             	).setNegativeButton("NO", 
    	     			new DialogInterface.OnClickListener() {
    	 					@Override
    	 					public void onClick(DialogInterface dialog, int which) {
    	 						// TODO Auto-generated method stub
    	 						Toast.makeText(getApplicationContext(), "Message not sent", Toast.LENGTH_LONG).show();
    	 						finishActivity();
    	 					}
    	 				}
    	             	).show();
	    	    	 

				}

			}
		});
	}

	private void onError(Exception error) {
		// String text = getString("Error", error.getMessage());
		// Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		// toast.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			// Load data, unless a query has already taken place.
			friendPickerFragment.loadData(false);
		} catch (Exception ex) {
			onError(ex);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

	}
	
	private void finishActivity(){
		Intent intent = new Intent();
		intent.putExtra("Friend", friendslist_selected.toString());
		setResult(ResultCode.PickFriendsActivity);
		
		finish();
	}
	
	private void insertVoiceToMySQL(String message,String tag,GraphPlace location){
		MySQLUtil.insertVoice(message, tag, location);
	}
	
	
}
