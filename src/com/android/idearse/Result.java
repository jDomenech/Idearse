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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sourceforge.zbar.Symbol;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Result extends SherlockActivity {

	public String sessionid, session_name, token, QR, name, code, pic, status, nid, type;
	public Animation rotation;
	public ImageView loadima;
	private static final int ZBAR_QR_SCANNER_REQUEST = 0;
	public SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		
		getWindow().getAttributes().windowAnimations = R.style.Fade;
		
		Bundle extras = getIntent().getExtras();
		QR = extras.getString("qr"); 
		
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		type = preferences.getString("type","");
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sessionid = preferences.getString("sessionid","");
		session_name = preferences.getString("session_name","");
		token = preferences.getString("token","");
		nid = preferences.getString("place_id","");
		
		loadima = (ImageView) findViewById(R.id.loading);
		rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.drawable.rotate_loading);
		rotation.setRepeatCount(Animation.INFINITE);
		loadima.startAnimation(rotation);
		
		ActionBar actionBar = getSupportActionBar();
		
		setTitle("");
		getSupportActionBar().setIcon(R.drawable.title_logo);
		
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
	    
	    Button qr_again2 = (Button) findViewById(R.id.detect_again2);
	    Button qr_again = (Button) findViewById(R.id.detect_again);
	    Button call = (Button) findViewById(R.id.call);
	    
	    qr_again2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (isCameraAvailable()) {
                    Intent intent = new Intent(getApplicationContext(), ZBarScannerActivity.class);
                    intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
                    startActivityForResult(intent, ZBAR_QR_SCANNER_REQUEST);
                } else {
                	Intent goToLogin = new Intent(getApplicationContext(), Login.class);
        			startActivity(goToLogin);  
                }
			}
		});	
	    qr_again.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (isCameraAvailable()) {
                    Intent intent = new Intent(getApplicationContext(), ZBarScannerActivity.class);
                    intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
                    startActivityForResult(intent, ZBAR_QR_SCANNER_REQUEST);
                } else {
                	Intent goToLogin = new Intent(getApplicationContext(), Login.class);
        			startActivity(goToLogin);  
                }
			}
		});
	    call.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent goToCall = new Intent(getApplicationContext(), Call.class);
                startActivity(goToCall);
			}
		});
	    
	    if(type.contains("treballador")){
	    	new QRquery().execute(getString(R.string.URL) + "conectar_mobil/trabajadores.json?nid=" + nid + "&nif=" + QR);
	    } else if(type.contains("maquines")){
	    	new QRquery().execute(getString(R.string.URL) + "conectar_mobil/maquinas.json?nid=" + nid + "&matricula=" + QR);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.result_menu, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Intent goToPanel = new Intent(getApplicationContext(), Panel.class);
	    switch (item.getItemId()) {
	        case R.id.gohome:
				startActivity(goToPanel);        
	        default:
	            return super.onOptionsItemSelected(item);
	        }  
    }
	
	public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
	
	public class QRquery extends AsyncTask<String, Void, String>{
		protected String doInBackground(String... params) {			
			String url = params[0];
			try {
	            HttpClient client = new DefaultHttpClient();
	            HttpGet httpget = new HttpGet(url);
	            httpget.addHeader("X-CSRF-Token", token);
	            httpget.addHeader("Content-type", "application/json");
	            httpget.addHeader("cookie", session_name + "=" + sessionid);
	            
	            HttpResponse response = client.execute(httpget);
	            
	            HttpEntity entity = response.getEntity();
	            
	            if(entity == null) {
	                return null;        
	            }
	            InputStream is = entity.getContent();
	            return streamToString(is);
	        }
	        catch(IOException e){
	        }
	        return null;
	    }
		protected void onPostExecute(String sJson) {
			try {
				JSONArray aJson = new JSONArray(sJson);
				Log.i("json", "Json: "+aJson);
				if(aJson.toString().contains("Access denied")){
					Intent goToLogin = new Intent(getApplicationContext(), Login.class);
					startActivity(goToLogin);
				}
				else {
					JSONObject json = aJson.getJSONObject(0);
					if(type.contains("treballador")){
						if(json.has("field_nif_1")){
							name = json.getString("field_nom_treballador");
							status = json.getString("field_estado_trabajador");
							code = json.getString("field_nif_1");
							pic = json.getString("field_foto_trabajador");
							new downloadingPainting().execute(pic);
						}
					}
					if(type.contains("maquines")){
						if(json.has("field_estado_trabajador")){
							String tipo = json.getString("Tipo");
							JSONObject modelo_obj = json.getJSONObject("Modelo");
							String modelo = modelo_obj.getString("value");
							status = json.getString("field_estado_trabajador");
							code = QR;
							
							TextView identifier = (TextView) findViewById(R.id.identifier);
							TextView type = (TextView) findViewById(R.id.type);
							TextView model = (TextView) findViewById(R.id.model);
							TextView auth = (TextView) findViewById(R.id.auth_bar);
							TextView semiauth = (TextView) findViewById(R.id.semi_auth_bar);
							TextView noauth = (TextView) findViewById(R.id.no_auth_bar);
							
							identifier.setText(code);
							type.setText(getString(R.string.tipo) + " " + tipo);
							model.setText(getString(R.string.modelo) + " " + modelo);
							
							RelativeLayout authorized = (RelativeLayout) findViewById(R.id.authorized);
							LinearLayout machines = (LinearLayout) findViewById(R.id.machines);
							machines.setVisibility(View.VISIBLE);
							loadima.clearAnimation();
							loadima.setVisibility(View.GONE);
							
							if(status.contains("Verde")){
								auth.setVisibility(View.VISIBLE);
							} else if(status.contains("Naranja")){
								semiauth.setVisibility(View.VISIBLE);
							} else if(status.contains("Rojo")){
								noauth.setVisibility(View.VISIBLE);
							}
							
							authorized.setVisibility(View.VISIBLE);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				RelativeLayout unauthorized = (RelativeLayout) findViewById(R.id.unauthorized);
				loadima.clearAnimation();
				loadima.setVisibility(View.GONE);
				unauthorized.setVisibility(View.VISIBLE);
			}
		} 		
		
		public String streamToString(final InputStream is) throws IOException{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder(); 
	        String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } 
	        catch (IOException e) {
	            throw e;
	        } 
	        finally {           
	            try {
	                is.close();
	            } 
	            catch (IOException e) {
	                throw e;
	            }
	        }
	        return sb.toString();
	    }
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
	
	private class downloadingPainting extends AsyncTask<String,Integer,Bitmap> {    		    	
		protected Bitmap doInBackground(String... path) {
			return getBitmapFromURL(path[0]);
		}
		protected void onPostExecute(Bitmap profile_image) { 
			ImageView pic = (ImageView) findViewById(R.id.pic);
			if(pic != null){
				float bmheight = ((float)profile_image.getHeight()/(float)profile_image.getWidth())*180;
				Log.i("bmheight", "bmheight"+bmheight);
				profile_image = Bitmap.createScaledBitmap(profile_image, (int)180, (int)bmheight, false);
				
		        final TransitionDrawable td = new TransitionDrawable(new Drawable[] {
		                        new ColorDrawable(Color.TRANSPARENT),
		                        new BitmapDrawable(getResources(), profile_image)
		                });
		        pic.setImageDrawable(td);
		        td.startTransition(200);
			}
			TextView name_v = (TextView) findViewById(R.id.name);
			TextView code_v = (TextView) findViewById(R.id.id);
			TextView auth = (TextView) findViewById(R.id.auth_bar);
			TextView semiauth = (TextView) findViewById(R.id.semi_auth_bar);
			TextView noauth = (TextView) findViewById(R.id.no_auth_bar);
			name_v.setText(name);
			code_v.setText(code);
			
			RelativeLayout authorized = (RelativeLayout) findViewById(R.id.authorized);
			LinearLayout workers = (LinearLayout) findViewById(R.id.workers);
			workers.setVisibility(View.VISIBLE);
			loadima.clearAnimation();
			loadima.setVisibility(View.GONE);
			
			if(status.contains("Verde")){
				auth.setVisibility(View.VISIBLE);
			} else if(status.contains("Naranja")){
				semiauth.setVisibility(View.VISIBLE);
			} else if(status.contains("Rojo")){
				noauth.setVisibility(View.VISIBLE);
			}
			
			authorized.setVisibility(View.VISIBLE);
		}
	 }
		
     public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
     }
}