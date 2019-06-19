package com.example.presentator.modules.auth.signUp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.presentator.R;
import com.example.presentator.model.entities.User;
import com.example.presentator.modules.newsFeed.NewsFeedActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity implements SignUpView {
    private SignUpController controller = new SignUpController(this);
    private EditText fullNameEt;
    private EditText usernameEt;
    private EditText emailEt;
    private EditText passwordEt;
    private EditText passwordConfirmationEt;
    private RadioGroup genderRadioGroup;
    private Button signUpBtn;
    private TextView birthdayTv;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String BASE_PHOTO_URL = "https://firebasestorage.googleapis.com/v0/b/presentator-2e392.appspot.com/o/account.png?alt=media&token=fec23e84-3cdc-40e7-b98d-bb4d608c5554";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);
        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5F8109")));
        initWidgetFields();
        bindButtons();
        setDateListener();
    }

    private void initWidgetFields() {
        fullNameEt = findViewById(R.id.sign_up_et_full_name);
        usernameEt = findViewById(R.id.sign_up_et_username);
        emailEt = findViewById(R.id.sign_up_et_email);
        passwordEt = findViewById(R.id.description);
        passwordConfirmationEt = findViewById(R.id.sign_up_et_password_confirmation);
        genderRadioGroup = findViewById(R.id.sign_up_radio_group_gender);
        signUpBtn = findViewById(R.id.sign_up_btn_sign_up);
        birthdayTv = (TextView) findViewById(R.id.sign_up_tv_birthday);
    }

    private void bindButtons() {
        signUpBtn.setOnClickListener(view -> {
            signUp();
        });
    }

    private void signUp() {
        try {
            String fullName = fullNameEt.getText().toString().trim();
            controller.checkFullName(fullName);
            String username = usernameEt.getText().toString().trim();
            controller.checkNickName(username);
            String email = emailEt.getText().toString().trim();
            controller.checkMail(email);
            String password = passwordEt.getText().toString().trim();
            String passwordConfirmation = passwordConfirmationEt.getText().toString().trim();
            controller.checkPasswords(password, passwordConfirmation);
            User.Gender gender = getSelectedGender();
            controller.checkGender(gender);
            User user = new User(fullName, username, gender);
            user.setImageURL(BASE_PHOTO_URL);
            setDataInUser(user);
            controller.signUp(user, email, password);
        } catch (IllegalArgumentException e) {
            showErrMsgWithToast(e.getMessage());
        }
    }

    private void setDataInUser(User user){
        try {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date date = simpleDateFormat.parse(birthdayTv.getText().toString().substring(
                    birthdayTv.getText().toString().indexOf(":") + 1));
            user.setBirthday(date);
        } catch (ParseException e){
            showErrMsgWithToast(e.getMessage());
        }
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

    private void setDateListener(){
        birthdayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        SignUpActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Because JANUARY = 0, DECEMBER = 11
                month = month + 1;
//                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + dayOfMonth + "/" + month + "/" + year);
                String date = dayOfMonth + "/" + month + "/" + year;
                birthdayTv.setText("Your birthday: " + date);
            }
        };
    }
}
