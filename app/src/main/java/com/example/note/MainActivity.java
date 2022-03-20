package com.example.note;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final String testAreaKey = "com.example.note.testAreaKey";
    private SharedPreferences pref;
    private EditText textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        textArea = this.findViewById(R.id.textArea);

        String text = pref.getString(testAreaKey, "");
        textArea.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveNote) {
            SharedPreferences.Editor editor = pref.edit();
            String text = textArea.getText().toString();

            editor.putString(testAreaKey, text);
            editor.apply();

            toastMsg("notes have been saved");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toastMsg(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}