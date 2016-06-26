package com.mmz.callrecognition;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SMS extends Activity{
	TextView n,mob,mess;
	String name,phone;
	ArrayList<String> matches_text;
	private static final int REQUEST_CODE = 1234;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		b=getIntent().getExtras();
	    name=b.getString("name");
	    phone=b.getString("phone");
		setContentView(R.layout.sms);
	   n=(TextView)findViewById(R.id.name);
	   mob=(TextView)findViewById(R.id.number);
	   mess=(TextView)findViewById(R.id.message);
	   n.setText(name);
	   mob.setText(phone);
	}
	
	public void sendSms(View v)
	{
		if(isConnected())
		{
       	 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        	 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        	 RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        	 startActivityForResult(intent, REQUEST_CODE);
       	}
       	else
       	{
       		Toast.makeText(this, "Plese Connect to Internet", Toast.LENGTH_LONG).show();
       	}
	}
	
	public void go_home(View v)
	{
		if(isConnected())
		{
       	 Intent intent = new Intent(this,MainVoice.class);
       	 startActivityForResult(intent, REQUEST_CODE);
       	}
       	else
       	{
       		Toast.makeText(this, "Plese Connect to Internet", Toast.LENGTH_LONG).show();
       	}
	}
	public  boolean isConnected()
    {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo net = cm.getActiveNetworkInfo();
    
    if (net!=null && net.isAvailable() && net.isConnected())
    {
        return true;
    } 
    else
    {
        return false;
    }
    }
 
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 	{
	     
		 if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) 
	     {
	     matches_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	    	     android.R.layout.simple_list_item_1, matches_text);
	    
	    	String v=matches_text.get(0).toLowerCase();
	    	Toast.makeText(getApplicationContext(),v,Toast.LENGTH_LONG).show();
	    	
	    	try
	    	{
	    		SmsManager sm=SmsManager.getDefault();
	    		sm.sendTextMessage(phone,null,v, null,null);
	    		mess.setText(v);
	    		Toast.makeText(getApplicationContext(),"Message Successfully sent.",Toast.LENGTH_LONG).show();
	    	}
	    	catch(Exception e)
	    	{
	    		Toast.makeText(getApplicationContext(),"Error in sending.",Toast.LENGTH_LONG).show();
		    }
	    	
	     super.onActivityResult(requestCode, resultCode, data);
	    }

	 }
}
