package com.Adarsh.ExpenseTracker.Exception;


public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message){
        super(message);
    }
}
