package com.example.presentator.modules.auth.signUp;

import com.example.presentator.model.entities.User;
import com.example.presentator.modules.validation.ValidationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpController {
    private SignUpView view;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ValidationService validationService = new ValidationService();

    public SignUpController(SignUpView view) {
        this.view = view;
    }

    void signUp(User user, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = auth.getCurrentUser();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference users = db.getReference("users_new");
                users.child(currentUser.getUid()).setValue(user);
                view.endSignUp();
            } else {
                String cause = "";
                if (task.getException() != null) {
                    cause = task.getException().getLocalizedMessage();
                }
                view.showError("Sorry, did not work: " + cause);
            }
        });
    }

    void checkFullName(String fullName){
        validationService.ifStringIsEmptyThenThrowIllegalArgumentException(fullName,
                "Fullname required");
    }

    void checkNickName(String nickname){
        validationService.ifStringIsEmptyThenThrowIllegalArgumentException(nickname,
                "Nickname required");
    }

    void checkMail(String mail){
        validationService.checkMailNotNullAndMatchRegex(mail);
    }

    void checkGender(User.Gender gender){
        validationService.checkGenderForEmpty(gender);
    }

    void checkPasswords(String password1, String password2){
        validationService.checkPasswordNotEmptyAndContainsMoreThanSixSymbols(password1, 1);
        validationService.checkPasswordNotEmptyAndContainsMoreThanSixSymbols(password2, 2);
        validationService.checkPasswordForIdentity(password1, password2);
    }
}