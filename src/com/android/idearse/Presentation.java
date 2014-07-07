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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class Presentation extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.presentation);
		
		getWindow().getAttributes().windowAnimations = R.style.Fade;
		
		new delay().execute();
	}
	
	public class delay extends AsyncTask<String, Void, String>{
		protected String doInBackground(String... params) {
			
			try {
	            Thread.sleep(800);
		    } catch (InterruptedException e) {                                
		            e.printStackTrace();
		    }
	        return null;
		}
		protected void onPostExecute(String response) {
			Intent goToLogin = new Intent(getApplicationContext(), Login.class);
			startActivity(goToLogin); 
		}
	}
}
