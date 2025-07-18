package com.apis.postgrestesting.controller;

import com.apis.postgrestesting.entity.UrlsEntity;
import com.apis.postgrestesting.service.UrlsService;
import com.apis.postgrestesting.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/apis/urls")
public class UrlsController {

    @Autowired
    private UrlsService urlsService;

    @Autowired
    private UserService userService;
    @PostMapping("/insert")
    public ResponseEntity<Map<String, Object>> addUrlToUser(@RequestBody Map<String, Object> urlMap, HttpServletRequest request) throws Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        String userIdStr = claims.get("userId").toString();
        Long userId = Long.valueOf(userIdStr);

        // Insert single URL
        UrlsEntity savedUrl = urlsService.insertSingleUrl(userId, urlMap);

        Map<String, Object> urlInfo = new HashMap<>();
        urlInfo.put("urlId", savedUrl.getUrlId());
        urlInfo.put("urlname", savedUrl.getUrlname());
        urlInfo.put("urldescription", savedUrl.getUrldescription());
        urlInfo.put("urllink", savedUrl.getUrllink());
        urlInfo.put("urlcategory", savedUrl.getUrlcategory());

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Successfully Inserted");
        res.put("userId", userId);
        res.put("url", urlInfo);

        return ResponseEntity.ok(res);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUrl(@RequestBody Map<String, Object> updateUrlData,HttpServletRequest request)throws Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        String userIdStr = claims.get("userId").toString();
        Long userId = Long.valueOf(userIdStr);

        Long urlId = Long.valueOf(updateUrlData.get("urlId").toString());

        UrlsEntity updatedUrl = urlsService.updateUrl(userId, urlId, updateUrlData);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "URL updated successfully");
        response.put("userId", userId);
        response.put("urlId", updatedUrl.getUrlId());
        response.put("urlname", updatedUrl.getUrlname());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/retrieve-url-by-user-id")
    public ResponseEntity<?> getUrlsByUserId(HttpServletRequest request) throws Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        String userIdStr = claims.get("userId").toString();
        Long userId = Long.valueOf(userIdStr);
        List<UrlsEntity> urls = urlsService.getUrlsByUserId(userId);

        List<Map<String, Object>> urlDetails = new ArrayList<>();
        if (urls != null) {
            for (UrlsEntity url : urls) {
                Map<String, Object> details = new HashMap<>();
                details.put("urlId", url.getUrlId());
                details.put("urlname", url.getUrlname());
                details.put("urlcategory", url.getUrlcategory());
                details.put("urldescription", url.getUrldescription());
                details.put("urllink", url.getUrllink());
                urlDetails.add(details);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("urls", urlDetails); // ✅ Even if empty, return empty list

        return ResponseEntity.ok(response); // ✅ Always return 200
    }



    @GetMapping("/retrieve-url-by-id/{urlId}")
    public ResponseEntity<Map<String, Object>> getUrlByUserIdAndUrlId(HttpServletRequest request, @PathVariable Long urlId) throws Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        Long userId = Long.valueOf(claims.get("userId").toString());

        UrlsEntity url = urlsService.getUrlByUrlIdAndUserId(urlId, userId);

        Map<String, Object> urlDetails = new HashMap<>();
        urlDetails.put("urlid", url.getUrlId());
        urlDetails.put("urlname", url.getUrlname());
        urlDetails.put("urlcategory", url.getUrlcategory());
        urlDetails.put("urldescription", url.getUrldescription());
        urlDetails.put("urllink", url.getUrllink());

        Map<String, Object> response = new HashMap<>();
        response.put("url", urlDetails); // ✅ Structure expected by JS

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{urlId}")
    public ResponseEntity<Map<String, Object>> deleteUrlForUser(
            HttpServletRequest request,
            @PathVariable Long urlId)throws Exception {
        String jwtToken = request.getHeader("token");
        Map<String, Object> claims = userService.decodeJwt(jwtToken);

        String userIdStr = claims.get("userId").toString();
        Long userId = Long.valueOf(userIdStr);

        String message = urlsService.deleteUrlByIdAndUserId(urlId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);

        if (message.contains("successfully")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



}



