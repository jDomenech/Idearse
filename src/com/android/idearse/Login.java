/***************************************************************************************************************************
Copyright © 2014 Joan Domènech and Ana Olivé

This file is part of Idearse.

Idearse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as 
published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Idearse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Foobar. If not, see http://www.gnu.org/licenses/.
****************************************************************************************************************************/

package com.android.idearse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class Login extends SherlockActivity {

	public Animation rotation;
	public ImageView loadima;
	public String cookie;
	public TextView error_msg;
	public SharedPreferences preferences;
	public EditText username, password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		getWindow().getAttributes().windowAnimations = R.style.Fade;
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		loadima = (ImageView) findViewById(R.id.loading);
		rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.drawable.rotate_loading);
		rotation.setRepeatCount(Animation.INFINITE);
		
		ActionBar actionBar = getSupportActionBar();
		
		setTitle("");
		getSupportActionBar().setIcon(R.drawable.title_logo);
		
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
	    
	    username = (EditText) findViewById(R.id.username);
	    password = (EditText) findViewById(R.id.password);
	    
	    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String user = preferences.getString("user","");
		String pass = preferences.getString("password","");
		
		username.setText(user);
		password.setText(pass);
	    
	    error_msg = (TextView) findViewById(R.id.error_msg);
	    Button login = (Button) findViewById(R.id.login_btn);
	    
	    final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbtn);
	    
	    checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                	SharedPreferences.Editor editor = preferences.edit();
					editor.putString("user", "");
             		editor.putString("password", "");
             		editor.commit();
             		username.setText("");
            		password.setText("");
                }
            }
        });
	    
	    if(user.length() != 0 && pass.length() != 0){
	    	checkBox.setChecked(true);
		}
	    
	    login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				
				if(username.length() == 0 || password.length() == 0){
					error_msg.setText("Completa los campos");	
					error_msg.setVisibility(View.VISIBLE);
				}
				else {	
					if(checkBox.isChecked()) {
						SharedPreferences.Editor editor = preferences.edit();
	             		editor.putString("user",username.getText().toString());
	             		editor.putString("password",password.getText().toString());
	             		editor.commit();
					}
					error_msg.setVisibility(View.GONE);
					loadima.startAnimation(rotation);
					loadima.setVisibility(View.VISIBLE);
					new LoginQuery().execute(getString(R.string.URL) + "conectar_mobil/user/login");
				}
			}
		});	
	}
	
	public class LoginQuery extends AsyncTask<String, Void, String>{
		protected String doInBackground(String... params) {			
			String url = params[0];
			try {
				HttpClient client = new DefaultHttpClient();
		 		HttpPost httppost = new HttpPost(url);
	            
		 		List<NameValuePair> vars = new ArrayList<NameValuePair>();
	 			vars.add(new BasicNameValuePair("username", username.getText().toString()));
		 		vars.add(new BasicNameValuePair("password", password.getText().toString()));
		 		httppost.setEntity(new UrlEncodedFormEntity(vars));
	            
		 		HttpResponse response = client.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            
	            if(entity == null) {
	                return null;        
	            }
	            
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            response.getEntity().writeTo(out);
	            out.close();
	            String responseString = out.toString();
	            
	            return responseString;
	        }
	        catch(IOException e){
	        }
	        return null;
		}
		protected void onPostExecute(String sJson) {
			try {
				JSONObject json = new JSONObject(sJson);
				if(json.has("sessid")){
					String sessionid = json.getString("sessid");
					String session_name = json.getString("session_name");
					String token = json.getString("token");
					
					SharedPreferences.Editor editor = preferences.edit();
             		editor.putString("sessionid",sessionid);
             		editor.putString("session_name",session_name);
             		editor.putString("token",token);
             		editor.commit();
					
					Intent goToPanel = new Intent(getApplicationContext(), Panel.class);
					startActivity(goToPanel);  
				}
				else {
					loadima.clearAnimation();
					loadima.setVisibility(View.GONE);
					error_msg.setText("Usuario o contraseña incorrectos");	
					error_msg.setVisibility(View.VISIBLE);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				loadima.clearAnimation();
				loadima.setVisibility(View.GONE);
				error_msg.setText("Usuario o contraseña incorrectos");	
				error_msg.setVisibility(View.VISIBLE);
			}		
		}
	}
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
    }
}
