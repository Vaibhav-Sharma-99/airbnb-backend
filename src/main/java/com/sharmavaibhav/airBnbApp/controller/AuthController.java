package com.sharmavaibhav.airBnbApp.controller;

import com.sharmavaibhav.airBnbApp.dto.LoginDto;
import com.sharmavaibhav.airBnbApp.dto.LoginResponseDto;
import com.sharmavaibhav.airBnbApp.dto.SignUpRequestDto;
import com.sharmavaibhav.airBnbApp.dto.UserDto;
import com.sharmavaibhav.airBnbApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        log.info("Starting the signUp controller for: " + signUpRequestDto);
        return new ResponseEntity<>(authService.signUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> logIn(@RequestBody LoginDto loginDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){


        log.info("Starting the login controller for: " + loginDto);
        String[] tokens = authService.login(loginDto);

        log.info("got tokens from authService " + Arrays.stream(tokens).toList());

        Cookie cookie = new Cookie("refreshToken", tokens[1]);
//        Cookie cookie2 = new Cookie("meraCookie", "Cookie kha");

        cookie.setHttpOnly(true);
//        cookie2.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);
//        httpServletResponse.addCookie(cookie2);

        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request){
        log.info("Starting the refresh controller for: " );

        String refreshToken = String.valueOf(Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh Token not found in cookies")));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }
}
