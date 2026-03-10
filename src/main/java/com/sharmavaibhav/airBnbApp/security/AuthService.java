package com.sharmavaibhav.airBnbApp.security;

import com.sharmavaibhav.airBnbApp.dto.LoginDto;
import com.sharmavaibhav.airBnbApp.dto.SignUpRequestDto;
import com.sharmavaibhav.airBnbApp.dto.UserDto;
import com.sharmavaibhav.airBnbApp.entity.User;
import com.sharmavaibhav.airBnbApp.entity.enums.Role;
import com.sharmavaibhav.airBnbApp.exceptions.ResourceNotFoundException;
import com.sharmavaibhav.airBnbApp.repository.UserRepository;
import io.jsonwebtoken.security.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto){
        log.info("Starting the signUp Service for: " + signUpRequestDto);

        String email = signUpRequestDto.getEmail();
        //1- Check if user already exists(by email)
        User user = userRepository.findByEmail(email).orElse(null);
        log.info("Fetched the user from userRepo (user should exist" + user);
        if(user != null){
            throw new RuntimeException("User already exists with email: "+ user.getEmail());
        }

        log.info("Creating user Object from singUpRequestDto: " + signUpRequestDto);

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        log.info("Created user Object from singUpRequestDto user: " + newUser);

        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, UserDto.class);


    }

    public String[] login(LoginDto loginDto){
        log.info("Starting the login Service for: " + loginDto);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));

        log.info("got the authentication from manafer " + authentication);


        User user = (User)authentication.getPrincipal();

        String []arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        log.info("got the authentication from manafer " + authentication);


        return arr;
    }

    public String refreshToken(String refreshToken){
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found with RefreshToken"));

        return jwtService.generateAccessToken(user);

    }
}
