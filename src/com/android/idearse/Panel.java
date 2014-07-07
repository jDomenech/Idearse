/***************************************************************************************************************************
Copyright © 2014 Joan Domnech and Ana OlivŽ

This file is part of Idearse.

Idearse is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as 
published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Idearse is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Foobar. If not, see http://www.gnu.org/licenses/.
****************************************************************************************************************************/

package com.android.idearse;

import net.sourceforge.zbar.Symbol;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Panel extends SherlockActivity {

	public String cookie;
	public SharedPreferences preferences;
	public EditText qr_hand;
	private static final int ZBAR_QR_SCANNER_REQUEST = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel);
		
		getWindow().getAttributes().windowAnimations = R.style.Fade;
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String pref = preferences.getString("place_id","");
		if(pref.length() == 0){
			Intent goToPlace = new Intent(getApplicationContext(), PlaceID.class);
            startActivity(goToPlace);
		}
		
		ActionBar actionBar = getSupportActionBar();
		
		setTitle("");
		getSupportActionBar().setIcon(R.drawable.title_logo);
		
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
	    	    
	    final Button QR1 = (Button) findViewById(R.id.qr1);
	    final Button QR2 = (Button) findViewById(R.id.qr2);
	    
	    QR1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				String type = "treballadors";
				SharedPreferences.Editor editor = preferences.edit();
         		editor.putString("type",type);
         		editor.commit();
				preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String pref = preferences.getString("place_id","");
				if(pref.length() != 0){
					if(qr_hand.length() > 0){
						Intent goToResult = new Intent(getApplicationContext(), Result.class);
						goToResult.putExtra("qr", qr_hand.getText().toString());
	            		startActivity(goToResult);
					} else {
						Intent intent = new Intent(getApplicationContext(), ZBarScannerActivity.class);
		                intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
		                startActivityForResult(intent, ZBAR_QR_SCANNER_REQUEST);
					}
				}
				else {
					Intent goToPlace = new Intent(getApplicationContext(), PlaceID.class);
		            startActivity(goToPlace);
				}
			}
		});	
	    QR2.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View arg0) {
	    		String type = "maquines";
				SharedPreferences.Editor editor = preferences.edit();
         		editor.putString("type",type);
         		editor.commit();
		    	preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String pref = preferences.getString("place_id","");
				if(pref.length() != 0){
					if(qr_hand.length() > 0){
						Intent goToResult = new Intent(getApplicationContext(), Result.class);
						goToResult.putExtra("qr", qr_hand.getText().toString());
	            		startActivity(goToResult);
					} else {
						Intent intent = new Intent(getApplicationContext(), ZBarScannerActivity.class);
		                intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
		                startActivityForResult(intent, ZBAR_QR_SCANNER_REQUEST);
					}
				}
				else {
					Intent goToPlace = new Intent(getApplicationContext(), PlaceID.class);
		            startActivity(goToPlace);
				}
		    }
		});
	    qr_hand = (EditText) findViewById(R.id.qr_hand);
	    qr_hand.addTextChangedListener(new TextWatcher() { 
	        public void afterTextChanged(Editable s) { 
	            if (s.length() > 0)
	            {
	            	QR1.setText(getResources().getString(R.string.treballadors_ma));
	            	QR2.setText(getResources().getString(R.string.maquines_ma));
	            }
	            else {
	            	QR1.setText(getResources().getString(R.string.qr1));
	            	QR2.setText(getResources().getString(R.string.qr2));
	            }
	        } 
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {} 
	        public void onTextChanged(CharSequence s, int start, int before, int count) {} 
	    }); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {   
	        case R.id.action_code:    
	        	Intent goToPlace = new Intent(getApplicationContext(), PlaceID.class);
                startActivity(goToPlace);
	            return true;  
	        case R.id.action_logout:
	        	SharedPreferences.Editor editor = preferences.edit();
         		editor.putString("place_id","");
         		editor.putString("user","");
         		editor.putString("password","");
         		editor.putString("sessionid","");
         		editor.putString("session_name","");
         		editor.putString("token","");
         		editor.putString("telefono","");
         		editor.commit();
				Intent goToLogin = new Intent(getApplicationContext(), Login.class);
				startActivity(goToLogin);        
	            return true; 
	        case R.id.action_support:    
	        	Intent goToCall = new Intent(getApplicationContext(), Call.class);
                startActivity(goToCall);
	            return true; 
	        default:
	            return super.onOptionsItemSelected(item);
	        }  
    }
	
    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_QR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {                    
                    Intent goToResult = new Intent(getApplicationContext(), Result.class);
					goToResult.putExtra("qr", data.getStringExtra(ZBarConstants.SCAN_RESULT));
            		startActivity(goToResult);
                } else if(resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if(!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
	
	public void onBackPressed() {
		Intent goToLogin = new Intent(getApplicationContext(), Login.class);
		startActivity(goToLogin);
    }
}
