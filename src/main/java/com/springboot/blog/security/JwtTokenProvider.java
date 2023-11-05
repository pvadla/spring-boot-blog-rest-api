package com.springboot.blog.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.springboot.blog.exception.BlogAPIException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {
		
		@Value("${app.jwt-secret}")
		private String jwtSecret;
		@Value("${app.jwt-expiration-milliseconds}")
		private int jwtExpirationInMs;
		
		
		//generate token
		public String generateToken(Authentication authentication) {
			String username = authentication.getName();
			Date currentDate = new Date();
			Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);
			
			String token = Jwts.builder().
								setSubject(username).
								setIssuedAt(new Date()).
								setExpiration(expireDate).
								signWith(SignatureAlgorithm.HS256, jwtSecret).
								compact();
			
			return token;
		}
		
		//get user name from the token
		public String getUsernameFromJwt(String token) {
			Claims claims = Jwts.parser().
										setSigningKey(jwtSecret)
										.parseClaimsJws(token)
										.getBody();
			
			return claims.getSubject();
		}
		
		//validate Jwt token
		public boolean validateToken(String token) {
			try {
				Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
				return true;
			}catch(SignatureException ex) {
				throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Signature");
			}catch(MalformedJwtException ex) {
				throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
			}catch(ExpiredJwtException ex) {
				throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Expired JWT Token");
			}catch(UnsupportedJwtException ex) {
				throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT Token");
			}catch(IllegalArgumentException ex) {
				throw new BlogAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
			}
		}
		
}
