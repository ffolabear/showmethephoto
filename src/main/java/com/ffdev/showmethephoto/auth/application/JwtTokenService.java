package com.ffdev.showmethephoto.auth.application;

import com.ffdev.showmethephoto.user.domain.User;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService implements TokenService{

    @Override
    public String createAccessToken(User user) {
        return "";
    }
}
