package com.apis.postgrestesting.controller;

import com.apis.postgrestesting.entity.UsersEntity;
import com.apis.postgrestesting.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/apis/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Map<String, Object> registerUser(@RequestBody Map<String, Object> requestJson) {
        UsersEntity usersEntity = userService.registerUser(requestJson);

        Map<String, Object> responseJson = new HashMap<>();
        responseJson.put("userId", usersEntity.getUserId().toString());
        responseJson.put("email", usersEntity.getEmail().toString());
        responseJson.put("dob", usersEntity.getDob().toString());
        responseJson.put("mobileNumber", usersEntity.getMobileNumber().toString());
        responseJson.put("profession", usersEntity.getProfession().toString());
        return responseJson;
    }

    @PutMapping("/update")
    public Map<String, Object> updateUser(HttpServletRequest request,@RequestBody Map<String, Object> requestJson)throws  Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        String userIdStr = claims.get("userId").toString();
        Long userId = Long.valueOf(userIdStr);

        UsersEntity updatedUser = userService.updateUser(userId, requestJson);


        Map<String, Object> response = new HashMap<>();
        response.put("userId", updatedUser.getUserId());
        response.put("email", updatedUser.getEmail());
        response.put("message", "Upadted successful");
        response.put("mobileNumber", updatedUser.getMobileNumber());
        response.put("dob", updatedUser.getDob());
        response.put("profession", updatedUser.getProfession());

        return response;
    }


    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public Map<String, Object> retrieveUser(HttpServletRequest request) throws Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        Long userId = Long.valueOf(claims.get("userId").toString());
        UsersEntity userDetails = userService.retrieveUser(userId);

        Map<String, Object> userDetailsResponse = new HashMap<>();
        userDetailsResponse.put("id", userDetails.getUserId()); // ✅ matches frontend user.id
        userDetailsResponse.put("email", userDetails.getEmail());
        userDetailsResponse.put("dob", userDetails.getDob());
        userDetailsResponse.put("profession", userDetails.getProfession());
        userDetailsResponse.put("mobileNumber", userDetails.getMobileNumber()); // ✅ matches user.mobileNumber

        return userDetailsResponse;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String, Object> loginUser(@RequestBody Map<String, Object> userInputDetails)throws Exception {

        String email = userInputDetails.get("email").toString();
        String password = userInputDetails.get("password").toString();
        Map<String, Object> responseObject = userService.loginUser(email, password);
        return responseObject;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Map<String, Object> deleteUser(HttpServletRequest request) throws Exception{

        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        String userIdStr = claims.get("userId").toString();
        Long userId = Long.valueOf(userIdStr);

        Map<String, Object> deleteUserResponse = userService.deleteUserId(userId);
        return deleteUserResponse;
    }
}


