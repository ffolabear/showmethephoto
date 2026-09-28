package com.ffdev.showmethephoto.auth.application;


import com.ffdev.showmethephoto.user.domain.User;

public interface TokenService {
    String createAccessToken(User user);
}
