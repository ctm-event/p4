package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.constants.GlobalConstants;

@Component
public class JWTAuthVerificationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthVerificationFilter.class);

    public JWTAuthVerificationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(GlobalConstants.AUTH_HEADER);
        if (header == null || !header.startsWith(GlobalConstants.AUTH_TOKEN_TYPE)) {
            chain.doFilter(req, res);
        } else {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(GlobalConstants.AUTH_HEADER);

        if (token == null) {
            return null;
        }

        String user = JWT.require(Algorithm.HMAC512(GlobalConstants.AUTH_SECRET.getBytes()))
                .build()
                .verify(token.replace(GlobalConstants.AUTH_TOKEN_TYPE, ""))
                .getSubject();

        if (user == null) {
            logger.info("Login faild");
            return null;
        }

        logger.info("Login succress: {}", user);
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    };
}