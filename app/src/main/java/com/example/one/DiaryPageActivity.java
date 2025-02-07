package com.example.one;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.Toast;
import java.util.ArrayList;

public class DiaryPageActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_REQUEST = 1;
    private EditText editTitle, editContent;
    private DiaryDatabaseHelper dbHelper;
    private String pageTitle;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_page);

        // Initialize views
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        dbHelper = new DiaryDatabaseHelper(this);  // ✅ Initialize dbHelper first

        // Get page title from intent
        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");

        if (pageTitle != null) {
            DiaryPage page = dbHelper.getPage(pageTitle);
            editTitle.setText(page.getTitle());
            editContent.setText(page.getContent());

            // Load and insert images after text is set
            ArrayList<String> resourceUris = dbHelper.getResourcesForPage(pageTitle);
            for (String uri : resourceUris) {
                insertImageAtCursor(Uri.parse(uri));
            }
        }
    }

    private void insertImageAtCursor(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            int maxHeight = editContent.getLineHeight() * 6;
            if (bitmap.getHeight() > maxHeight) {
                float ratio = (float) maxHeight / bitmap.getHeight();
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * ratio), maxHeight, false);
            }

            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            int cursorPosition = editContent.getSelectionStart();
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

            editContent.getText().insert(cursorPosition, spannableString);
        } catch (Exception e) {
            Toast.makeText(this, "Error inserting image", Toast.LENGTH_SHORT).show();
        }
    }

    public void deletePage(View view) {
        String title = editTitle.getText().toString().trim();

        if (!title.isEmpty()) {
            dbHelper.deletePage(title);
            Toast.makeText(this, "Page deleted", Toast.LENGTH_SHORT).show();

            // Navigate back to MainActivity
            Intent intent = new Intent(DiaryPageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Page title is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        if (!title.isEmpty() && !content.isEmpty()) {
            if (pageTitle != null) {
                dbHelper.updatePage(pageTitle, title, content);
            } else {
                dbHelper.addPage(title, content);
            }
        }
    }

    // Method to pick image from gallery
    public void onResourceImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_REQUEST) {
            Uri selectedImageUri = data.getData();
            String pageTitle = editTitle.getText().toString().trim();

            if (!pageTitle.isEmpty() && selectedImageUri != null) {
                dbHelper.addResource(pageTitle, selectedImageUri.toString());
                insertImageAtCursor(selectedImageUri);
            }
        }
    }
}
