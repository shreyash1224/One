package com.example.one;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.widget.EditText;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.Toast;

public class DiaryPageActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_REQUEST = 1;
    EditText editTitle, editContent;
    DiaryDatabaseHelper dbHelper;
    String pageTitle;
    private Uri selectedImageUri;

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
            DiaryPage page = dbHelper.getPage(pageTitle);
            editTitle.setText(page.getTitle());
            editContent.setText(page.getContent());
        }
    }
    public void deletePage(View view) {
        String title = editTitle.getText().toString().trim();

        if (!title.isEmpty()) {
            // Call the delete function in the database helper
            dbHelper.deletePage(title);

            // Show a toast message to confirm deletion
            Toast.makeText(this, "Page deleted", Toast.LENGTH_SHORT).show();

            // Navigate back to the MainActivity after deletion
            Intent intent = new Intent(DiaryPageActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optionally finish the current activity
        } else {
            Toast.makeText(this, "Page title is empty", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Check if there are changes and save the content
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();

        // Only save if the title or content has changed
        if (title.length() > 0 && content.length() > 0) {
            if (pageTitle != null) {
                // Update existing page
                dbHelper.updatePage(pageTitle, title, content);
            } else {
                // Save as a new page if the title is empty (optional)
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
            selectedImageUri = data.getData();
            // Add the image into the content at the cursor position
            insertImageAtCursor();
        }
    }

    private void insertImageAtCursor() {
        try {
            if (selectedImageUri != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                // Resize the image to fit within the height of 6 lines of text
                int maxHeight = editContent.getLineHeight() * 6;  // 6 lines of text
                if (bitmap.getHeight() > maxHeight) {
                    float ratio = (float) maxHeight / bitmap.getHeight();
                    int width = (int) (bitmap.getWidth() * ratio);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, maxHeight, false);
                }

                ImageSpan imageSpan = new ImageSpan(this, bitmap);
                int cursorPosition = editContent.getSelectionStart();
                SpannableString spannableString = new SpannableString(" ");  // A space to hold the image
                spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                editContent.getText().insert(cursorPosition, spannableString);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error inserting image", Toast.LENGTH_SHORT).show();
        }
    }
}
