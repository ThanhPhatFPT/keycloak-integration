package com.devteria.profile.service;

import java.util.List;

import com.devteria.profile.dto.identity.TokenExchangeResponse;
import com.devteria.profile.dto.response.LoginResponse;
import com.devteria.profile.entity.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.devteria.profile.dto.identity.Credential;
import com.devteria.profile.dto.identity.TokenExchangeParam;
import com.devteria.profile.dto.identity.UserCreationParam;
import com.devteria.profile.dto.request.RegistrationRequest;
import com.devteria.profile.dto.response.ProfileResponse;
import com.devteria.profile.exception.ErrorNormalizer;
import com.devteria.profile.mapper.ProfileMapper;
import com.devteria.profile.repository.IdentityClient;
import com.devteria.profile.repository.ProfileRepository;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    IdentityClient identityClient;
    ErrorNormalizer errorNormalizer;

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    public List<ProfileResponse> getAllProfiles() {
        var profiles = profileRepository.findAll();
        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    public ProfileResponse register(RegistrationRequest request) {
        try {
            // Create account in KeyCloak
            // Exchange client Token
            var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build());

            log.info("TokenInfo {}", token);
            // Create user with client Token and given info

            // Get userId of keyCloak account
            var creationResponse = identityClient.createUser(
                    "Bearer " + token.getAccessToken(),
                    UserCreationParam.builder()
                            .username(request.getUsername())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            String userId = extractUserId(creationResponse);
            log.info("UserId {}", userId);

            var profile = profileMapper.toProfile(request);
            profile.setUserId(userId);

            profile = profileRepository.save(profile);

            return profileMapper.toProfileResponse(profile);
        } catch (FeignException exception) {
            throw errorNormalizer.handleKeyCloakException(exception);
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").getFirst();
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }


//    public LoginResponse authenticateUser(String username, String password) {
//        // Kiểm tra người dùng trong cơ sở dữ liệu
//        Profile profile = profileRepository.findByUsername(username);
//        if (profile == null || !profile.getEnabled()) {
//            throw new IllegalArgumentException("User not found or disabled");
//        }
//
//        // Xác thực với Keycloak (hoặc hệ thống của bạn)
//        TokenExchangeParam param = TokenExchangeParam.builder()
//                .grant_type("password")
//                .client_id("KeyClockRealm_app_id")
//                .client_secret("6dIZy5fopTn61B0FrFFRsS1TOV50TLWp")
//                .username(username)
//                .password(password)
//                .scope("openid")
//                .build();
//
//        // Thực hiện yêu cầu token từ Keycloak
//        TokenExchangeResponse tokenResponse = identityClient.exchangeToken(param);
//
//        // Kiểm tra nếu không có token, trả về lỗi
//        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
//            throw new IllegalArgumentException("Invalid credentials or token exchange failed");
//        }
//
//        // Lấy token từ response
//        String token = tokenResponse.getAccessToken();
//
//        // Trả về Profile cùng với token trong LoginResponse
//        return new LoginResponse(profile, token);
//    }


    public String authenticateUser(String username, String password) {
        // Kiểm tra người dùng trong cơ sở dữ liệu
        Profile profile = profileRepository.findByUsername(username);
        if (profile == null || !profile.getEnabled()) {
            throw new IllegalArgumentException("User not found or disabled");
        }

        // Xác thực với Keycloak
        TokenExchangeParam param = TokenExchangeParam.builder()
                .grant_type("password")
                .client_id("KeyClockRealm_app_id")
                .client_secret("6dIZy5fopTn61B0FrFFRsS1TOV50TLWp")
                .username(username)
                .password(password)
                .scope("openid")
                .build();

        // Thực hiện yêu cầu token từ Keycloak
        TokenExchangeResponse tokenResponse = identityClient.exchangeToken(param);

        // Kiểm tra nếu không có token, trả về lỗi
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new IllegalArgumentException("Invalid credentials or token exchange failed");
        }

        // Trả về token
        return tokenResponse.getAccessToken();
    }


//    public ProfileResponse getProfileById(String profileId) {
//        // Tìm Profile từ repository dựa trên profileId
//        Profile profile = profileRepository.findById(profileId)
//                .orElseThrow(() -> new IllegalArgumentException("Profile not found for ID: " + profileId));
//
//        // Sử dụng ProfileMapper để chuyển đổi từ entity sang DTO
//        return profileMapper.toProfileResponse(profile);
//    }
//
//    public String getProfileIdByUsername(String username) {
//        // Lấy profileId theo username
//        return profileRepository.findProfileIdByUsername(username);
//    }


    public ProfileResponse getProfileByUsername(String username) {
        // Step 1: Retrieve profileId by username
        String profileId = profileRepository.findProfileIdByUsername(username);

        if (profileId == null) {
            throw new IllegalArgumentException("Profile not found for username: " + username);
        }

        // Step 2: Retrieve the full profile using the profileId
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for ID: " + profileId));

        // Step 3: Convert the profile entity to DTO using ProfileMapper
        return profileMapper.toProfileResponse(profile);
    }

//    ExternalAuthService externalAuthService; // Service to handle external user federation.
//    JwtTokenProvider jwtTokenProvider;
//
//    public String loginWithRemoteFederation(String username, String password) {
//        boolean isAuthenticated = externalAuthService.authenticate(username, password);
//        if (!isAuthenticated) {
//            throw new IllegalArgumentException("Invalid credentials for external user federation");
//        }
//
//        Profile externalProfile = externalAuthService.fetchUserProfile(username);
//        Profile localProfile = profileRepository.findByUsername(username).orElseGet(Profile::new);
//        localProfile.setUsername(externalProfile.getUsername());
//        localProfile.setEmail(externalProfile.getEmail());
//        localProfile.setFirstName(externalProfile.getFirstName());
//        localProfile.setLastName(externalProfile.getLastName());
//        localProfile.setDob(externalProfile.getDob());
//        profileRepository.save(localProfile);
//
//        return jwtTokenProvider.generateToken(localProfile);
//    }

}
