package com.example.one;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    DiaryDatabaseHelper dbHelper;
    ArrayList<String> diaryList;
    ArrayAdapter<String> adapter;
    FloatingActionButton fabAddPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        fabAddPage = findViewById(R.id.fabAddPage);
        dbHelper = new DiaryDatabaseHelper(this);

        // Initialize the list and adapter
        diaryList = dbHelper.getAllPages();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, diaryList);
        listView.setAdapter(adapter);

        // Open a diary page when clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pageTitle = diaryList.get(position);
                Intent intent = new Intent(MainActivity.this, DiaryPageActivity.class);
                intent.putExtra("pageTitle", pageTitle);
                startActivity(intent);
            }
        });

        // FAB click listener
        fabAddPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DiaryPageActivity.class);
                startActivity(intent);
            }
        });
    }

    // This method refreshes the list of diary pages after a change
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list of pages from the database
        diaryList.clear();
        diaryList.addAll(dbHelper.getAllPages());
        adapter.notifyDataSetChanged();
    }
}
