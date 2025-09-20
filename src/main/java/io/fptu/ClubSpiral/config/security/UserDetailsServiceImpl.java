package io.fptu.ClubSpiral.config.security;

import io.fptu.ClubSpiral.common.utils.LocalizedTextUtils;
import io.fptu.ClubSpiral.model.entity.User;
import io.fptu.ClubSpiral.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        User user = (User) userRepository.findByFullNameOrEmail(input)
                .orElseThrow(() -> new UsernameNotFoundException(LocalizedTextUtils.getLocalizedText("user.not.found")));
        return new UserDetailsImpl(user);

    }


    @Transactional
    public User processOAuth2User(OidcUser oidcUser) {
        var email = oidcUser.getEmail();
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newAccount = User.builder()
                            .fullName(oidcUser.getFullName().trim())
                            .email(email)
                            .enabled(true)
                            .locked(false)
                            .build();
                    return userRepository.save(newAccount);
                });
    }


}
