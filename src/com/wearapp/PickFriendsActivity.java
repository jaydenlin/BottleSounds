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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PickerFragment.GraphObjectFilter;
import com.facebook.widget.PickerFragment.OnSelectionChangedListener;
import com.wearapp.asyncTask.FacebookChatAsyncTask;
import com.wearapp.exception.FacebookUtil.FacebookSessionNotActive;
import com.wearapp.exception.MySQLUtil.UploadFileNotAssign;
import com.wearapp.parseAPI.ParseAPI;
import com.wearapp.resultcode.ResultCode;
import com.wearapp.util.FacebookUtil;
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
    StringBuffer friendslist_selected = new StringBuffer();
	
	String searchterm;
    List<GraphUser> selectedUsers = new ArrayList<GraphUser> ();
    List<GraphUser> selectedUsers_temp1 = new ArrayList<GraphUser> ();
    
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

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Log.w(TAG, "IN createing Search friends OptionsMenu");
	    // Inflate the options menu from XML
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);	    
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName("com.wearapp","com.wearapp.HistoryActivity")));
	    //searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean onQueryTextChange(String keyword) {
				// TODO Auto-generated method stub			
				
				selectedUsers.addAll(selectedUsers_temp1);				
				friendPickerFragment.setSelection(selectedUsers);				
				searchterm = keyword;								
				friendPickerFragment.setFilter(new GraphObjectFilter<GraphUser>() {
			        @Override
			        public boolean includeItem(GraphUser graphObject) {
			            // TODO Auto-generated method stub			  			        	
			        	Pattern p = Pattern.compile(searchterm,Pattern.CASE_INSENSITIVE);
			            Matcher m = p.matcher(graphObject.getName());			            						            	
			            if(m.find()) {
			                return true;
			            }			           
			            return false;
			        }
			    });										
								
				friendPickerFragment.loadData(true);
				
				return false;
			}			
			
		});
	    
	    return true;
	}
	
	
	
	private void sendMessage() {
		// TODO Auto-generated method stub	
		List<String> toFriends = new ArrayList<String>();
		String targetFacebookId;
		String title = "Heare";	    
		String message = "\n---------------------------\n"+editDialog.getText().toString() + "\n---------------------------\n"+"\n Sent by Heare in "+ LocationUtil.selectedlocation.getName() + "\n"+"https://maps.google.com/maps?q="+LocationUtil.selectedlocation.getLocation().getLatitude()+","+LocationUtil.selectedlocation.getLocation().getLongitude() +"\n---------------------------\n"; 
		
		//targetFacebookId="1746264605";
		
    	for (GraphUser selectedUser : selectedUsers){            		            	    	
			targetFacebookId = selectedUser.getId();
			toFriends.add(targetFacebookId);
	        new FacebookChatAsyncTask().execute(targetFacebookId,title,message);	             
	    }
    	
    	/*Location loc1 = new Location("loc1");
		loc1.setLatitude(POS.latitude);
		loc1.setLongitude(POS.longitude);
		
		MarkerOptions markerTest = new MarkerOptions().position(
				new LatLng(position_list.get(i).getLatitude(),
						position_list.get(i).getLongitude())).title(
				"Jayden");
		
		map.addMarker(markerTest);*/
    	try {
			ParseAPI.checkYourVoice(this, FacebookUtil.getAccessToken(), LocationUtil.selectedlocation, message, toFriends);
		} catch (FacebookSessionNotActive e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//insertVoiceToMySQL(message, "test", LocationUtil.selectedlocation);
    	
    	//new FacebookChatAsyncTask().execute(targetFacebookId,title,message);
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

		friendPickerFragment.setOnSelectionChangedListener(new OnSelectionChangedListener() {
			@Override
			public void onSelectionChanged(PickerFragment<?> fragment) {
				// TODO Auto-generated method stub
				
					selectedUsers_temp1 = friendPickerFragment.getSelection();
				
					/*tempselectedUsers = friendPickerFragment.getSelection();
					
					if (selectedUsers.size()==0)
					{
						selectedUsers = tempselectedUsers;
					}
					
					
					for (GraphUser u :tempselectedUsers)
					{										
						if (!selectedUsers.contains(u))
						{
							selectedUsers.add(u);
							System.out.println("XX-add"+u.getName());
						}
						
					}
			
					
					Iterator<GraphUser> it = selectedUsers.iterator();
					   while(it.hasNext()) {
						   
						   GraphUser u = it.next();
						   if (!tempselectedUsers.contains(u))
						   {						   						 
							   it.remove();
							   selectedUsers.remove(u);
							   System.out.println("XX-remove"+u.getName());
						   }
					   }
					
					
					System.out.println("XX===="+selectedUsers.size()+"====");
					for (GraphUser u :selectedUsers)
					{
						System.out.println("XX"+u.getName());
					}
					System.out.println("XX========");*/
				
			}
		});
		
		 
		friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked(PickerFragment<?> fragment) {
				// We just store our selection in the Application for other
				// activities to look at.
				// FriendPickerApplication application =
				// (FriendPickerApplication) getApplication();
				
				
				// if No friends selected , show the dialog
				if((selectedUsers.size()==0) && (selectedUsers_temp1.size()==0))
				{
					Toast.makeText(getApplicationContext(), "No selected users, Message not sent", Toast.LENGTH_LONG).show();
					
				}
				else if((selectedUsers.size()==0) && (selectedUsers_temp1.size()!=0)) // if the friend list not been filtered by keywords, initiate the selectedUsers list
				{
					selectedUsers = selectedUsers_temp1;
				}
				
		
				//remove duplicated friends in the selected friend list
				for (int i = 0; i < selectedUsers.size() - 1; i++) 
				{
					for (int j = selectedUsers.size() - 1; j > i; j--) 
					{
						if (selectedUsers.get(j).getId().equals(selectedUsers.get(i).getId()))
							selectedUsers.remove(j);
					}
				}
				 
			
				Session session = Session.getActiveSession();
				
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
		try {
			MySQLUtil.insertVoice(message, tag, location);
		} catch (UploadFileNotAssign e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
