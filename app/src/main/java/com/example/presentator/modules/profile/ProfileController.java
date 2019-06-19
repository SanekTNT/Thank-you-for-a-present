package com.example.presentator.modules.profile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.presentator.model.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ProfileController implements IProfileController {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final ProfileService profileService = new ProfileService(this);
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ProfileView view;
    private User user;

    public ProfileController(ProfileView view) {
        this.view = view;
    }

    public void loadUserInfo() {
        profileService.loadUserInfo(auth.getCurrentUser().getUid());
    }

    public void uploadAvatar(Bitmap bitmap) {
        StorageReference imageRef = storageRef.child(auth.getCurrentUser().getUid() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageRef.delete();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        setUserAvatarUrl(task.getResult().toString());
                    }
                });
            }
        });
    }

    private void setUserAvatarUrl(String url) {
        user.setImageURL(url);
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users_new")
                .child(FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid())
                .setValue(user);
    }

    private void setEmail() {
        view.setEmail(auth.getCurrentUser().getEmail());
    }

    private void setFullName() {
        view.setFullName(user.getName());
    }

    private void setNickName() {
        view.setNickName(user.getNick());
    }

    private void setUserIcon() {
        view.setUserIcon(user.getImageURL());
    }

    private void setBirthday(){
        if(user.getBirthday() != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(user.getBirthday());
            view.setBirthday(strDate);
        }
    }

    @Override
    public void setUserInfo(User user) {
        this.user = user;
        setFieldsInView();
    }

    private void setFieldsInView() {
        setFullName();
        setEmail();
        setUserIcon();
        setNickName();
        setBirthday();
    }

    public String getUserUid() {
        return auth.getCurrentUser().getUid();
    }
}
