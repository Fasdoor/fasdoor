package com.os.onestopper.jwtconfig;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("Loading user...");
        // Fetch user details from Google using the default service
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        System.out.println("User info: " + oAuth2User.getAttributes());
        // Process and return the OAuth2User (you can add your own logic here)
        // For example, you can map the user details to your own application user model
        return oAuth2User;
    }
}
