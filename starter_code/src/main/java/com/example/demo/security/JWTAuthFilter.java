package com.example.demo.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.example.demo.constants.GlobalConstants;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
            HttpServletResponse res) throws AuthenticationException {
        try {
            User user = new ObjectMapper()
                    .readValue(req.getInputStream(), User.class);

            String inputUserName = user.getUsername();
            String inputPassword = user.getPassword();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    inputUserName,
                    inputPassword,
                    new ArrayList<>());

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        Long currentTime = new Date().getTime();
        Long expiredTime = currentTime + GlobalConstants.AUTH_EXPIRED_TIME;
        String loggedUserName = ((org.springframework.security.core.userdetails.User) auth.getPrincipal())
                .getUsername();
        String token = JWT.create()
                .withSubject(loggedUserName)
                .withExpiresAt(new Date(expiredTime))
                .sign(HMAC512(GlobalConstants.AUTH_SECRET.getBytes()));
        res.addHeader(GlobalConstants.AUTH_HEADER, GlobalConstants.AUTH_TOKEN_TYPE + token);
    }
}