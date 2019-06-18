package com.example.presentator.modules.auth.signUp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.presentator.R;
import com.example.presentator.model.entities.User;
import com.example.presentator.modules.newsFeed.NewsFeedActivity;

public class SignUpActivity extends AppCompatActivity implements SignUpView {
    private SignUpController controller = new SignUpController(this);
    private EditText fullNameEt;
    private EditText usernameEt;
    private EditText emailEt;
    private EditText passwordEt;
    private EditText passwordConfirmationEt;
    private RadioGroup genderRadioGroup;
    private Button signUpBtn;
    private static final String BASE_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/presentator-2e392.appspot.com/o/account.png?alt=media&token=fec23e84-3cdc-40e7-b98d-bb4d608c5554";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);
        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5F8109")));
        initWidgetFields();
        bindButtons();
    }

    private void initWidgetFields() {
        fullNameEt = findViewById(R.id.sign_up_et_full_name);
        usernameEt = findViewById(R.id.sign_up_et_username);
        emailEt = findViewById(R.id.sign_up_et_email);
        passwordEt = findViewById(R.id.description);
        passwordConfirmationEt = findViewById(R.id.sign_up_et_password_confirmation);
        genderRadioGroup = findViewById(R.id.sign_up_radio_group_gender);
        signUpBtn = findViewById(R.id.sign_up_btn_sign_up);
    }

    private void bindButtons() {
        signUpBtn.setOnClickListener(view -> {
            signUp();
        });
    }

    private void signUp() {
        String password = passwordEt.getText().toString().trim();
        String passwordConfirmation = passwordConfirmationEt.getText().toString().trim();
        if (!password.equals(passwordConfirmation)) {
            showPasswordsNotSameError();
            return;
        }
        User.Gender gender = getSelectedGender();
        if (gender == null) {
            showGenderRequiredError();
            return;
        }
        String fullName = fullNameEt.getText().toString().trim();
        String username = usernameEt.getText().toString().trim();
        String email = emailEt.getText().toString().trim();
        User user = new User(fullName, username, gender);
        user.setImageURL(BASE_PHOTO_URL);
        controller.signUp(user, email, password);
    }

    private void showGenderRequiredError() {
        showErrMsgWithToast("Gender required");
    }

    private User.Gender getSelectedGender() {
        int selectedRadioBtnId = genderRadioGroup.getCheckedRadioButtonId();
        switch (selectedRadioBtnId) {
            case R.id.sign_up_rb_gender_male:
                return User.Gender.MALE;
            case R.id.sign_up_rb_gender_female:
                return User.Gender.FEMALE;
            default:
                return null;
        }
    }


    private void showPasswordsNotSameError() {
        showErrMsgWithToast("Passwords are not same");
    }

    private void showErrMsgWithToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void endSignUp() {
        Intent intent = new Intent(this, NewsFeedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String errorMsg) {
        showErrMsgWithToast(errorMsg);
    }
}
