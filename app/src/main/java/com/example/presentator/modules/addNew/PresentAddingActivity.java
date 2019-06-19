package com.example.presentator.modules.addNew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.presentator.R;
import com.example.presentator.common.Menu;
import com.example.presentator.model.entities.Gift;

import java.io.IOException;

public class PresentAddingActivity extends AppCompatActivity implements PresentAddingView {
    private PresentAddingController controller = new PresentAddingController(this);
    private ImageView imageView;
    private Button addPresentButton;
    private int GALLERY = 1, CAMERA = 2;
    private EditText presentName;
    private EditText description;
    private ImageButton goToFeedImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present_adding);

        controller.requestMultiplePermissions();

        imageView = findViewById(R.id.picture);
        presentName = findViewById(R.id.presentName);
        description = findViewById(R.id.description);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });


        goToFeedImageButton = findViewById(R.id.profile_feed_btn);
        goToFeedImageButton.setOnClickListener(view -> {
            Menu.goToFeed(this);
        });


        addPresentButton = findViewById(R.id.addPresentButton);
        addPresentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String presentNameString = presentName.getText().toString().trim();
                    controller.ifStringIsEmptyThenThrowIllegalArgumentException(presentNameString,
                            "Present name required");
                    String descriptionString = description.getText().toString().trim();
                    controller.ifStringIsEmptyThenThrowIllegalArgumentException(descriptionString,
                            "Description required");
                    controller.addNewPresent(new Gift(presentNameString, descriptionString), getGiftImage());
                } catch (IllegalArgumentException e){
                    showErrMsgWithToast(e.getMessage());
                }
            }
        });

        ImageButton addPresentProfileButton = (ImageButton) findViewById(R.id.add_present_profile_btn);
        addPresentProfileButton.setOnClickListener(view -> {
            Menu.goToProfile(this);
        });

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5F8109")));
    }

    private void showErrMsgWithToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private Bitmap getGiftImage() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = controller.saveImage(bitmap);
                    showError("Image saved!");
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    showError("An error occurred: " + e.getLocalizedMessage());
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
            controller.saveImage(thumbnail);
            Toast.makeText(PresentAddingActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void finishAddingView() {
        Menu.goToFeed(this);
    }

    @Override
    public void showError(String err) {
        Toast.makeText(PresentAddingActivity.this, err, Toast.LENGTH_SHORT).show();
    }
}