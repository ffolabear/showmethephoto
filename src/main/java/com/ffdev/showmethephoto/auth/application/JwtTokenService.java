package com.ffdev.showmethephoto.auth.application;

import com.ffdev.showmethephoto.auth.security.JwtProperties;
import com.ffdev.showmethephoto.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class JwtTokenService implements TokenService{

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    @Override
    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(
                jwtProperties.accessTokenTtlSeconds()
        );
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("showmethephoto")
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

}
