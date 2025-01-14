package com.devteria.profile.controller;

import java.util.List;

import com.devteria.profile.dto.request.LoginRequest;
import com.devteria.profile.dto.response.LoginResponse;
import com.devteria.profile.entity.Profile;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.devteria.profile.dto.ApiResponse;
import com.devteria.profile.dto.request.RegistrationRequest;
import com.devteria.profile.dto.response.ProfileResponse;
import com.devteria.profile.service.ProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProfileController {
    ProfileService profileService;

    @PostMapping("/register")
    ApiResponse<ProfileResponse> register(@RequestBody @Valid RegistrationRequest request) {
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.register(request))
                .build();
    }

    @GetMapping("/profiles")
    ApiResponse<List<ProfileResponse>> getAllProfiles() {
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(profileService.getAllProfiles())
                .build();
    }

//    @GetMapping("/profiles/{profileId}")
//    public ApiResponse<ProfileResponse> getProfileById(@PathVariable String profileId) {
//        // Lấy thông tin người dùng từ ProfileService
//        ProfileResponse profileResponse = profileService.getProfileById(profileId);
//
//        // Trả về thông tin người dùng dưới dạng ApiResponse
//        return ApiResponse.<ProfileResponse>builder()
//                .result(profileResponse)
//                .build();
//    }



    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest loginRequest) {
        // Xác thực người dùng và lấy token
        String token = profileService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());

        // Trả về token
        return ApiResponse.<String>builder().result(token).build();
    }


//    @PostMapping("/remote-login")
//    public ApiResponse<String> loginWithRemoteFederation(@RequestBody @Valid LoginRequest loginRequest) {
//        try {
//            // Authenticate user via Remote User Federation and generate token
//            String token = profileService.loginWithRemoteFederation(loginRequest.getUsername(), loginRequest.getPassword());
//
//            // Return the token as part of the response
//            return ApiResponse.<String>builder()
//                    .result(token)
//                    .message("Login successful")
//                    .build();
//        } catch (Exception e) {
//            // Handle errors (invalid credentials, external system error, etc.)
//            return ApiResponse.<String>builder()
//                    .message(e.getMessage())
//                    .build();
//        }
//    }


    @GetMapping("/username/{username}")
    public ApiResponse<ProfileResponse> getProfileByUsername(@PathVariable String username) {
        try {
            // Get profile information based on the username
            ProfileResponse profileResponse = profileService.getProfileByUsername(username);

            // Return profile data wrapped in ApiResponse
            return ApiResponse.<ProfileResponse>builder()
                    .result(profileResponse)
                    .message("Profile fetched successfully")
                    .build();
        } catch (Exception e) {
            // Handle error (user not found, profile not found, etc.)
            return ApiResponse.<ProfileResponse>builder()
                    .message(e.getMessage())
                    .build();
        }
    }






}
