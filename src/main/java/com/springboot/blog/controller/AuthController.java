package com.springboot.blog.controller;

import java.util.Collections;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.blog.entity.Role;
import com.springboot.blog.entity.User;
import com.springboot.blog.payload.JwtAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.SignupDto;
import com.springboot.blog.repository.RoleRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/v1/auth")
@Api(value = "Auth controller exposes sign in and sign out REST APIs")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	
	@ApiOperation(value = "REST API to login user to blog application")
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthResponse> authenticateUser(@RequestBody LoginDto loginDto){
		Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(),
				loginDto.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		//get token from tokenProvider
		String token = tokenProvider.generateToken(authentication);
	
		return ResponseEntity.ok(new JwtAuthResponse(token));
	}

	@ApiOperation(value = "REST API to sign up user to blog application")
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupDto signupDto){
		
		//add check for user name exists in db
		if(userRepository.existsByUsername(signupDto.getUsername())) {
			return new ResponseEntity<>("User name is already taken", HttpStatus.BAD_REQUEST);
		}
		
		//add check for email exists in DB
		if(userRepository.existsByEmail(signupDto.getEmail())) {
			return new ResponseEntity<>("Email is already taken", HttpStatus.BAD_REQUEST);
		}
		
		//create user object
		User user = new User();
		user.setName(signupDto.getName());
		user.setUsername(signupDto.getName());
		user.setEmail(signupDto.getEmail());
		user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
		
		Role roles = roleRepository.findByName("ROLE_ADMIN").get();
		
		user.setRoles(Collections.singleton(roles));
		userRepository.save(user);
		return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
		
		
		
		
		
		
	}
	
	
	
	
	
}
