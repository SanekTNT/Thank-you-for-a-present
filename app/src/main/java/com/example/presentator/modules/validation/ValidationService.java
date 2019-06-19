package com.example.presentator.modules.validation;

import com.example.presentator.model.entities.User;

public class ValidationService {

    private final static String MAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public void checkMailNotNullAndMatchRegex(String mail){
        ifStringIsEmptyThenThrowIllegalArgumentException(mail, "Mail required");
        if(!mail.matches(MAIL_REGEX)) {
            throw new IllegalArgumentException("Mail is incorrect");
        }
    }

    public void checkPasswordNotEmptyAndContainsMoreThanSixSymbols(String password, int number){
        ifStringIsEmptyThenThrowIllegalArgumentException(password,
                "Field password" + number + " is empty");
        if(password.length() < 6) {
            throw new IllegalArgumentException("Password" + number + " must contain 6 symbols or more");
        }
    }

    public void checkPasswordForIdentity(String password1, String password2){
        if(!password1.equals(password2)){
            throw new IllegalArgumentException("Passwords are not same");
        }
    }

    public void checkGenderForEmpty(User.Gender gender){
        if(gender == null) {
            throw new IllegalArgumentException("Gender required!");
        }
    }

    public void ifStringIsEmptyThenThrowIllegalArgumentException(String string, String message){
        if(string.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
