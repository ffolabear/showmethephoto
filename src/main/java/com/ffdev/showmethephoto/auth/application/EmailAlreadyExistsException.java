package com.ffdev.showmethephoto.auth.application;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException() {
        super("이미 가입된 이메일 입니다.");
    }
}
