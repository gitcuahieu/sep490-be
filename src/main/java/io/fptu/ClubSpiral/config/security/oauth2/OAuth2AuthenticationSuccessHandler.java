package io.fptu.ClubSpiral.config.security.oauth2;

import io.fptu.ClubSpiral.common.utils.JsonUtils;
import io.fptu.ClubSpiral.common.utils.JwtUtils;
import io.fptu.ClubSpiral.config.security.UserDetailsImpl;
import io.fptu.ClubSpiral.config.security.UserDetailsServiceImpl;
import io.fptu.ClubSpiral.model.dto.response.UserDetailResponse;
import io.fptu.ClubSpiral.service.TokenStoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final TokenStoreService tokenStoreService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var oidcUser = (OidcUser) authentication.getPrincipal();


        var user = userDetailsService.processOAuth2User(oidcUser);

        var userDetails = new UserDetailsImpl(user);

        var accessToken = jwtUtils.generateAccessToken(userDetails);
        var refreshToken = jwtUtils.generateRefreshToken(userDetails);

        tokenStoreService.storeAccessToken(accessToken, jwtUtils.getAccessTokenExpirationMs());
        tokenStoreService.storeRefreshToken(refreshToken, jwtUtils.getRefreshTokenExpirationMs());

        UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getFullName().trim())
                .enabled(user.isEnabled())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store"); // optional
        response.getWriter().write(JsonUtils.marshal(userDetailResponse));
        response.getWriter().flush();
    }
}

