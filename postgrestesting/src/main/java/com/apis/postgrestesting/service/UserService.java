package com.apis.postgrestesting.service;

import com.apis.postgrestesting.entity.UsersEntity;
import com.apis.postgrestesting.respository.UsersRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    public UsersEntity registerUser(Map<String,Object> userDetails) {
        if (!userDetails.containsKey("email") || !userDetails.containsKey("password") ||
                !userDetails.containsKey("dob") || !userDetails.containsKey("mobileNumber") ||
                !userDetails.containsKey("profession")) {
            throw new IllegalArgumentException("Missing one or more required fields.");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDetails.get("password").toString());

        UsersEntity request = new UsersEntity();
        request.setEmail(userDetails.get("email").toString());
        request.setPassword(hashedPassword);
        String dobString = userDetails.get("dob").toString();
        LocalDate dob = LocalDate.parse(dobString);
        request.setDob(dob);
        request.setMobileNumber(userDetails.get("mobileNumber").toString());
        request.setProfession(userDetails.get("profession").toString());

        UsersEntity newUserResponseEntity = usersRepository.save(request);

        return newUserResponseEntity;
    }
    public UsersEntity updateUser(Long userId, Map<String, Object> userDetails) throws Exception {
        UsersEntity existingUser = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // ✅ Only check required non-password fields
        if (!userDetails.containsKey("email") ||
                !userDetails.containsKey("dob") ||
                !userDetails.containsKey("mobileNumber") ||
                !userDetails.containsKey("profession")) {
            throw new IllegalArgumentException("Missing one or more required fields for update.");
        }

        existingUser.setEmail(userDetails.get("email").toString());

        String dobString = userDetails.get("dob").toString();
        LocalDate dob = LocalDate.parse(dobString);
        existingUser.setDob(dob);

        existingUser.setMobileNumber(userDetails.get("mobileNumber").toString());
        existingUser.setProfession(userDetails.get("profession").toString());

        if (userDetails.containsKey("password")) {
            String password = userDetails.get("password").toString().trim();
            if (!password.isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                existingUser.setPassword(encoder.encode(password));
            }
        }
        return usersRepository.save(existingUser);
    }

    public UsersEntity retrieveUser(Long userId) throws Exception{
        return usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }


    public Map<String, Object> loginUser(String email, String password) throws Exception {
        Optional<UsersEntity> userDetailsEntity = usersRepository.findByEmail(email);

        if (userDetailsEntity.isPresent()) {
            UsersEntity user = userDetailsEntity.get();
            String hashedDBPassword = user.getPassword();

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean passwordValidationStatus = passwordEncoder.matches(password, hashedDBPassword);

            if (passwordValidationStatus) {
                String userJwt = createJwt(email, user.getUserId(), 100000000);

                Map<String, Object> responseObject = new HashMap<>();
                responseObject.put("token", userJwt);
                responseObject.put("message", "Successful login");
                return responseObject;
            } else {
                throw new Exception("Invalid password");
            }
        } else {
            throw new Exception("User not found with email: " + email);
        }
    }


    public Map<String, Object> deleteUserId(Long userId) throws Exception{
        Map<String, Object> response = new HashMap<>();
        if (usersRepository.existsById(userId)) {
            usersRepository.deleteById(userId);
            response.put("message", "User deleted successfully");
        } else {
            throw new Exception("User not found with ID: " + userId);
        }
        return response;
    }

    public String createJwt(String email, Long userId, long ttlMillis) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("This is a secret key This is a secret key This is a secret key This is a secret key This is a secret key");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("userId", userId.toString());

        JwtBuilder builder = Jwts.builder()
                .setId(email)
                .setIssuedAt(now)
                .setClaims(claims)
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    public Map<String, Object> decodeJwt(String jwt) {

        Map<String, Object> claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("This is a secret key This is a secret key This is a secret key This is a secret key This is a secret key"))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }




}
