package com.sideproject.cafe_cok.auth.exception;

public class NoSuchOAuthTokenException extends RuntimeException{

    public NoSuchOAuthTokenException(final String message) {
        super(message);
    }

    public NoSuchOAuthTokenException() {
        this("존재하지 않는 OAuthToken 입니다.");
    }
}
