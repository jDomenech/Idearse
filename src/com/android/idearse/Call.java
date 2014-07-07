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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class Call extends Activity {

	public EditText tlf;
	public SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call);
		
		getWindow().getAttributes().windowAnimations = R.style.Fade;
		
		tlf = (EditText) findViewById(R.id.tlf_num);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String pref = preferences.getString("telefono","");
		if(pref.length() != 0){
			tlf.setText(pref);
		}
		
	    final Button call_btn = (Button) findViewById(R.id.call_btn);
	    
	    call_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				if(tlf.length() != 0) {
					tlf.setBackgroundResource(R.drawable.edittexts);
					SharedPreferences.Editor editor = preferences.edit();
             		editor.putString("telefono",tlf.getText().toString());
             		editor.commit();
             		String number = "tel:" + tlf.getText().toString(); //SET TLF NUMBER
    		        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
    		        startActivity(callIntent);
				} else {
					tlf.setBackgroundResource(R.drawable.edittexts_wrong);
				}
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
