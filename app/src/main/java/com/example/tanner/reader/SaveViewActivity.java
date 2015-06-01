package com.example.tanner.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SaveViewActivity extends Activity implements OnClickListener {

    private Button buttonSave;
    private Button buttonLoad;
    private Button buttonDelete;
    private EditText nameInput;
    private TextView recipeList;
    public static String recipe;
    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_view_activity);

        //instantiate objects
        buttonSave = (Button)findViewById(R.id.save_recipe_button);
        buttonSave.setOnClickListener(this);
        buttonLoad = (Button)findViewById(R.id.load_recipe_button);
        buttonLoad.setOnClickListener(this);
        buttonDelete = (Button)findViewById(R.id.delete_button);
        buttonDelete.setOnClickListener(this);
        nameInput = (EditText)findViewById(R.id.filename_textarea);
        nameInput.setOnClickListener(this);
        recipeList = (TextView)findViewById(R.id.list_recipes);
        recipeList.setOnClickListener(this);
        recipe = getIntent().getStringExtra("theRecipe");

        //set the onFocus listener to change the border of the EditText area
        recipeList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.backwithborder);
                } else {
                    v.setBackgroundResource(R.drawable.backwithborder);
                }
            }
        });

        File f = new File(getFilesDir().toString());
        files = f.listFiles();
        for(File file: files){
            if(file.isFile()){
                String fileName = file.getName();
                recipeList.append("\n" + fileName + "\n");
            }
        }

        //onClick for the save button
        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                if(name == "" || name == null) {
                    Toast.makeText(getApplicationContext(), "Filename cannot contain nothing..", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveRecipe(name);
                }
            }
        });

        //onClick for the load button
        buttonLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameInput.getText().toString() == ""){
                    Toast.makeText(getApplication(), "Recipe name cannot be nothing..", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        //refactor to open last recipe using a method call to maybe lastRecipe()(would need to return a string)
                        String name = nameInput.getText().toString() + ".txt";
                        InputStream in = openFileInput(name);
                        if (in != null) {
                            InputStreamReader tmp = new InputStreamReader(in);
                            BufferedReader reader = new BufferedReader(tmp);
                            String str;
                            StringBuilder buf = new StringBuilder();
                            while ((str = reader.readLine()) != null) {
                                buf.append(str + "\n");
                            }
                            in.close();
                            //set file to a variable
                            Intent theIntent = new Intent();
                            theIntent.putExtra("RECIPE", buf.toString());
                            setResult(1, theIntent);
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //method to delete a file
        buttonDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //get name of recipe they're trying to delete
                String name = nameInput.getText().toString();
                //if the recipe name cannot be found display a message
                File recipeFile = new File(getFilesDir(), name + ".txt");
                boolean fileExists =  recipeFile.isFile();
                if(!fileExists){
                    Toast.makeText(getApplicationContext(), "There is no recipe with that name.", Toast.LENGTH_SHORT).show();
                }
                else{
                    File dir = getFilesDir();
                    File file = new File(dir, name);
                    boolean deleted = file.delete();
                    String isDeleted = String.valueOf(deleted);
                    Toast.makeText(getApplicationContext(), "Did file get deleted: " + isDeleted + "\n" + "Does file exist: " + fileExists, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save_view_activity, menu);
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

    //method to save a recipe to a set name
    public void saveRecipe(String filename) {
        // add-write text into file
        try {
            File file = new File(getFilesDir(), filename + ".txt");
            boolean fileExists = file.isFile();
            if (fileExists) {
                Toast.makeText(getBaseContext(), "Filename already exists.." + "\n" + "please choose another.", Toast.LENGTH_SHORT).show();
            } else {
                FileOutputStream fileout = openFileOutput(filename + ".txt", MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                outputWriter.write(recipe);
                outputWriter.close();
                //display file saved message
                Toast.makeText(getBaseContext(), "File: " + filename + "\n" + "File saved successfully!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
    }
}
