package com.ase.serwincomm;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class languageOptions extends AppCompatActivity {

    ListView list;
    private ArrayAdapter<String> mAdapter;
    String[] languageList;
    String[] languageCodeList;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_options);

        languageList = new String[]{
                "Greek", "English", "French", "Turkish"
        };

        languageCodeList = new String[]{
                "el", "en", "fr", "tr"
        };

        list = (ListView) findViewById(R.id.listview);

        pref = getApplicationContext().getSharedPreferences("locationPref", MODE_PRIVATE);
        editor = pref.edit();

        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, languageList);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(languageOptions.this, "Choosing "+languageList[position], Toast.LENGTH_SHORT).show();
                editor.putString("language", languageCodeList[position]);
                editor.apply();

                finish();
               startActivity(new Intent(languageOptions.this, weatherConditions.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();

    }
}
