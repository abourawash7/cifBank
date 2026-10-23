package com.cif.cifbank.exception;

public class CustomerNotFoundException extends Exception{
    public CustomerNotFoundException(String message){
        super("Customer Id Not found:" + message);
    }
}
