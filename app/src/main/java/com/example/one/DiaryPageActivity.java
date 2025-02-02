package com.example.one;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DiaryPageActivity extends AppCompatActivity {

    EditText editTitle, editContent;
    DiaryDatabaseHelper dbHelper;
    String pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_page);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        dbHelper = new DiaryDatabaseHelper(this);

        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");

        if (pageTitle != null) {
            // If editing an existing page, load the content
            DiaryPage page = dbHelper.getPage(pageTitle);
            editTitle.setText(page.getTitle());
            editContent.setText(page.getContent());
        }
    }

    public void savePage(View view) {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();

        if (pageTitle != null) {
            // Update existing page
            dbHelper.updatePage(pageTitle, title, content);
            Toast.makeText(this, "Page Updated", Toast.LENGTH_SHORT).show();
        } else {
            // Save new page
            dbHelper.addPage(title, content);
            Toast.makeText(this, "Page Added", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    public void deletePage(View view) {
        if (pageTitle != null) {
            dbHelper.deletePage(pageTitle);
            Toast.makeText(this, "Page Deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
