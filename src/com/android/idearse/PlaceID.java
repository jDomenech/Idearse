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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PlaceID extends Activity {

	public EditText place;
	public Animation rotation;
	public ImageView loadima;
	public SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_id);
		
		getWindow().getAttributes().windowAnimations = R.style.Fade;
		
		place = (EditText) findViewById(R.id.place_id);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String pref = preferences.getString("place_id","");
		if(pref.length() != 0){
			place.setBackgroundResource(R.drawable.edittexts_correct);
			place.setText(pref);
		}
		
		loadima = (ImageView) findViewById(R.id.loading);
		rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.drawable.rotate_loading);
		rotation.setRepeatCount(Animation.INFINITE);
		
	    final Button validate = (Button) findViewById(R.id.validate_btn);
	    final Button borrar = (Button) findViewById(R.id.borrar);
	    final Button support = (Button) findViewById(R.id.support);
	    
	    validate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				if(place.length() != 0) {
					loadima.startAnimation(rotation);
					loadima.setVisibility(View.VISIBLE);
					place.setBackgroundResource(R.drawable.edittexts_correct);
					SharedPreferences.Editor editor = preferences.edit();
             		editor.putString("place_id",place.getText().toString());
             		editor.commit();
             		finish();
				} else {
					place.setBackgroundResource(R.drawable.edittexts_wrong);
				}
			}
		});	
	    borrar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				SharedPreferences.Editor editor = preferences.edit();
         		editor.putString("place_id","");
         		editor.commit();
         		place.setBackgroundResource(R.drawable.edittexts);
         		place.setText("");
			}
		});	
	    
	    support.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent goToCall = new Intent(getApplicationContext(), Call.class);
                startActivity(goToCall);
			}
		});
	    
	    RelativeLayout content = (RelativeLayout) findViewById(R.id.content);
	    content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
