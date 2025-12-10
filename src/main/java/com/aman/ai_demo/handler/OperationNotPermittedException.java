package com.aman.ai_demo.handler;

public class OperationNotPermittedException  extends  RuntimeException{
    public  OperationNotPermittedException(String message){
        super(message);
    }
}
