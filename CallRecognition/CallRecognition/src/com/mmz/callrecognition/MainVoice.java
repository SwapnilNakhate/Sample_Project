package com.mmz.callrecognition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainVoice extends Activity implements TextToSpeech.OnInitListener{
	private static final int REQUEST_CODE = 1234;
	Button Start;
	Dialog match_text_dialog;
	ListView textlist;
	ArrayList<String> matches_text;
	Cursor phones;
	String n1,phoneno;
	TextToSpeech tts;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.voice);
	    phones=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null,null);
	    tts = new TextToSpeech(this, this);
	}

	//checks internet connection
	
	public void clickSpeech(View v)
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

	//checks internet connection isConnected Method
	
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
	     		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, matches_text);
	    
	     		String v=matches_text.get(0).toLowerCase();
	     		Toast.makeText(getApplicationContext(),v,Toast.LENGTH_LONG).show();
	    	
	    	 //CALL Function
	    	 if( v.startsWith("call",0))
	    	{
	    	   String name=v.substring(5).trim();
	    	   //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
	    	   phones.moveToFirst();
	    	   	while(phones.moveToNext())
	   			{
	    		   n1=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		   			
	   			 	if(name.equalsIgnoreCase(n1))
	   				{
	   					phoneno=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	   			 		Intent i=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneno));
	   			 		startActivity(i);
	   			 		break;
	   				}
	   			}
	    	   //in case if Contact is not in Contact List
	    	   if(!phones.moveToNext())
	    		   speakOut("Sorry sir,you have no such Contact.");
	   		
	    	}
	    	 //Start Camera Function
	    	else if( v.startsWith("camera",0))
	    	{
	    		Intent ci=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    		startActivity(ci);
	    	}
	    	 
	    	 //Start Search on Google
	    	else if( v.startsWith("search",0))
	    	{
	    		String search=v.substring(6);
	    		Intent i=new Intent(Intent.ACTION_WEB_SEARCH);
	    		i.putExtra(SearchManager.QUERY, search);
	    		startActivity(i);
	    		speakOut("searching for your information on google.");
		    }
	    	 
	    	 //Find Location On Maps
	    	else if( v.startsWith("locate",0))
	    	{
	    		String search=v.substring(6);
	    		Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q="+search));
	    		startActivity(i);
	    		speakOut("Finding your location on Google Map.");
		    }
	    	 
	    	 //Search for Information on Wikipedia
	    	else if( v.startsWith("wiki",0))
	    	{
	    		String search=v.substring(5);
	    		String u=search.replace(" ","_");
	    	
	    		Intent i=new Intent();
	    		i.setAction(Intent.ACTION_VIEW);
	    		i.setData(Uri.parse("http://en.wikipedia.org/wiki/"+u));
	    		startActivity(i);
	    		speakOut("searching for your information on Wikipedia.");
	    	}
	    	 
	    	//Greeting messages
	    	else if( v.startsWith("good",0))
	    	{
	    		String greet=v.substring(5);
	    		int hour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	    		
	    		//For checking morning
	    		if(hour>=0 && hour<12)
	    		{
	    			if(greet.startsWith("morning",0))
	    				speakOut("Good Morning Sir, Have a nice day.");
	    			
	    			else
	    				speakOut("Did you mean Good Morning");
	    		}
	    		
	    		//for checking afternoon
	    		else if(hour>=12 && hour<17)
	    		{
	    			if(greet.startsWith("afternoon",0))
	    				speakOut("Good afternoon Sir, Have a great day ahead.");
	    			
	    			else
	    				speakOut("Did you mean Good Afternoon");
	    		}
	    		
	    		//for checking evening
	    		else if(hour>=17 && hour<19)
	    		{
	    			if(greet.startsWith("evening",0))
	    				speakOut("Good evening. Enjoy the evening sir.");
	    			
	    			else
	    				speakOut("Did you mean Good evening");
	    		}
	    		
	    		//for checking night
	    		else if(hour>=19 && hour<24)
	    		{
	    			if(greet.startsWith("night",0))
	    				speakOut("Good night sweet dream");
	    			
	    			else
	    				speakOut("Did you mean Good night");
	    		}
	    	}
	    	 
	    	else if( v.startsWith("sms",0))
	    	{
		    	   String name=v.substring(4).trim();
		    	   phones.moveToFirst();
		    	   while(phones.moveToNext())
		   		{
		   			 n1=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			   			
		   			if(name.equalsIgnoreCase(n1))
		   			{
		   			 phoneno=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		   			 Intent i=new Intent(this,SMS.class);
		   			 i.putExtra("name",name);
		   			i.putExtra("phone",phoneno);
		   			 startActivity(i);
		   			 break;
		   			}
		   		}
		    	   if(!phones.moveToNext())
		    		   speakOut("Sorry sir, You have no such Contact.");
		   		
		    	}
	    	else
	    	{
	    		speakOut("Invalid Command. Please refer manual.");
	    	}
	    	
	     }
	     super.onActivityResult(requestCode, resultCode, data);
	    }
	 
	 @Override
	 public void onDestroy() {
	 // shutdown tts!
	 if (tts != null) {
	     tts.stop();
	     tts.shutdown();
	 }
	 super.onDestroy();
	 }

	 @Override
	 public void onInit(int status) 
	 {
		 if (status == TextToSpeech.SUCCESS) 
		 {
			 int result = tts.setLanguage(Locale.US);

			 	if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) 
			 	{
			 		Log.e("TTS", "This Language is not supported");
			 	}
			 	else
			 	{
			 		welcome();
		    	}
			 	
		 }
		 else
		 {
			 Log.e("TTS", "Initilization Failed!");
		 }
	 }

	 private void speakOut(String text) 
	 {
		 tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	 }

	 private void welcome() 
	 {
		int hour= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			//For checking morning
		if(hour>=0 && hour<12)
		{
			speakOut("Good Morning Sir.How can I be assistance to you ?");
		}
		
		//for checking afternoon
		else if(hour>=12 && hour<17)
		{
				speakOut("Good afternoon Sir.How can I be assistance to you ?");
		}
		//for checking evening
		else if(hour>=17 && hour<19)
		{
				speakOut("Good evening Sir.How can I be assistance to you ?");
		}
		
		//for checking night
		else if(hour>=19 && hour<24)
		{
				speakOut("Hello Sir, I hope you had a nice day.How can I be assistance to you ?");
		}
		else
		{
			speakOut("There is a Error in Time.");
		}
	 }

}
