package com.example.presentator.modules.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.presentator.R;
import com.example.presentator.common.Menu;
import com.example.presentator.modules.friends.FriendsActivity;
import com.example.presentator.modules.giftsList.GiftsListActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ProfileView {
    private static int CAMERA_REQUEST_CODE = 1;
    private static int PHOTO_FROM_FS_REQUEST_CODE = 2;
    private ProfileController controller = new ProfileController(this);
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5F8109")));
        imageView = findViewById(R.id.profile_image_view);
        bindButtons();
        controller.loadUserInfo();
    }

    private void bindButtons() {
        ImageButton presentAddBtn = (ImageButton) findViewById(R.id.profile_add_present_btn);
        presentAddBtn.setOnClickListener((view) -> {
            Menu.goToFeed(this);
        });

        ImageButton feedBtn = (ImageButton) findViewById(R.id.profile_feed_btn);
        feedBtn.setOnClickListener((view) -> {
            Menu.goToFeed(this);
        });
        Button btnFriends = (Button) findViewById(R.id.button_friends);
        btnFriends.setOnClickListener(view -> {
            goToFriendsActivity();
        });
        Button giftsButton = (Button) findViewById(R.id.button_gifts);
        giftsButton.setOnClickListener(view -> {
            goToGiftsList();
        });

        imageView.setOnClickListener(view -> showPictureDialog());
    }

    private void goToFriendsActivity() {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    private void goToGiftsList() {
        Intent intent = new Intent(this, GiftsListActivity.class);
        intent.putExtra("uid", controller.getUserUid());
        startActivity(intent);
    }

    @Override

    public void setFullName(String fullName) {
        TextView fullNameTw = findViewById(R.id.profile_full_name_et);
        fullNameTw.setText(fullName);
    }

    @Override
    public void setBirthday(String birthdayDate) {
        TextView birthday = findViewById(R.id.profile_birthday_tw);
        birthday.setText(birthdayDate);
    }

    @Override
    public void setEmail(String email) {
        TextView emailTw = findViewById(R.id.profile_email_tw);
        emailTw.setText(email);
    }

    @Override
    public void setUserIcon(String url) {
        CircleImageView userIconImageView = findViewById(R.id.profile_image_view);
        Picasso.with(this).load(url).into(userIconImageView);
    }

    @Override
    public void setNickName(String nickName) {
        TextView profileTw = findViewById(R.id.profile_nickname);
        profileTw.setText(nickName);
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallery();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, PHOTO_FROM_FS_REQUEST_CODE);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        Bitmap bitmap;
        if (requestCode == PHOTO_FROM_FS_REQUEST_CODE) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    controller.uploadAvatar(bitmap);
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    showError("An error occurred: " + e.getLocalizedMessage());
                }
            }

        } else if (requestCode == CAMERA_REQUEST_CODE) {
            bitmap = (Bitmap) data.getExtras().get("data");
            controller.uploadAvatar(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void showError(String errMsg) {
        Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show();
    }
}
