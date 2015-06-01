package com.example.tanner.reader;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Locale;


public class MainActivity extends Activity implements OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech ttsobj;
    public static EditText write;
    private Button speakButton;
    private Button cancelButton;
    private Button saveButton;
    private Button nextButton;
    private Button prevButton;
    //array to hold sentences when they're parsed
    private String[] sentences;
    //integer to keep track of the index
    private int indexHolder = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        write = (EditText)findViewById(R.id.input_area);
        speakButton = (Button)findViewById(R.id.speak_button);
        speakButton.setOnClickListener(this);
        cancelButton = (Button)findViewById(R.id.cancel_recipe_button);
        cancelButton.setOnClickListener(this);
        saveButton = (Button)findViewById(R.id.save_recipe_button);
        saveButton.setOnClickListener(this);
        prevButton = (Button)findViewById(R.id.prev_button);
        prevButton.setOnClickListener(this);
        nextButton = (Button)findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        ttsobj = new TextToSpeech(MainActivity.this, MainActivity.this);
        ttsobj.setLanguage(Locale.US);
        ttsobj.setSpeechRate((float) 0.85);

        //onClick event for the speak button
        speakButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        parseText();
                        Toast.makeText(getApplicationContext(), "Text has been successfully parsed",
                                Toast.LENGTH_SHORT).show();
            }
        });

        //onClick event for the cancel button
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        write.setText("");
            }
        });

        //onClick event for the save recipe button
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        final String recipe = write.getText().toString();
                        Intent intent = new Intent(v.getContext(), SaveViewActivity.class);
                        startActivityForResult(intent, 1);
            }
        });

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        forwardRead();
            }
        });

        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                        previousRead();
            }
        });

        //set the onFocus listener to change the border of the EditText area
        write.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus){
                    v.setBackgroundResource(R.drawable.backwithborder);
                }
                else{
                    v.setBackgroundResource(R.drawable.backwithborder);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //onPause method for the app.
    @Override
    public void onPause(){
        if(ttsobj !=null){
            ttsobj.stop();
            ttsobj.shutdown();
        }
        super.onPause();
    }

    public void speakText(String text){
        ttsobj.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    //method to parse text from input area
    public void parseText(){
        Toast.makeText(this, "Parsing text..", Toast.LENGTH_LONG).show();
        String x = write.getText().toString();
        sentences = x.split("\\.");
    }

    //go forward a sentence
    public void forwardRead(){
        if((indexHolder +1) <= sentences.length ) {
            speakText(sentences[indexHolder]);
            indexHolder += 1;
        }
        else{
            indexHolder = 0;
            speakText(sentences[indexHolder]);
            indexHolder += 1;
        }
    }

    //go back one sentence
    public void previousRead(){
        if(indexHolder -1 > 0 ){
            indexHolder -= 1;
            speakText(sentences[indexHolder]);
        }
        else{
            indexHolder = sentences.length-1;
            speakText(sentences[indexHolder]);
        }
    }

    //override method for the interface OnClickListener, handled in the
    //onCreate method by my own onClickListeners.
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            ttsobj.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //check if the request code is the same as was passed with the original intent
        if(requestCode == 1){
            if(data != null){
                //fetch data string
                String recipe = data.getStringExtra("RECIPE");
                write.setText(recipe);
            }
        }
    }
}
