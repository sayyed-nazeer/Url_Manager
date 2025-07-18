package com.apis.postgrestesting.service;


import com.apis.postgrestesting.entity.UrlsEntity;
import com.apis.postgrestesting.entity.UsersEntity;
import com.apis.postgrestesting.respository.UrlsRepository;
import com.apis.postgrestesting.respository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class UrlsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UrlsRepository urlsRepository;
    public UrlsEntity insertSingleUrl(Long userId, Map<String, Object> urlMap) {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UrlsEntity url = new UrlsEntity();
        url.setUser(user);
        url.setUrlname(urlMap.get("url_name").toString());
        url.setUrlcategory(urlMap.get("url_category").toString());
        url.setUrldescription(urlMap.get("url_description").toString());
        url.setUrllink(urlMap.get("url_link").toString());

        return urlsRepository.save(url);
    }

    public UrlsEntity updateUrl(Long userId, Long urlId, Map<String, Object> updateUrlData) throws Exception {
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        UrlsEntity url = urlsRepository.findByUrlIdAndUser(urlId, user)
                .orElseThrow(() -> new RuntimeException("URL not found for this user."));

        if (updateUrlData.get("urlname") != null)
            url.setUrlname(updateUrlData.get("urlname").toString());

        if (updateUrlData.get("urldescription") != null)
            url.setUrldescription(updateUrlData.get("urldescription").toString());

        if (updateUrlData.get("urllink") != null)
            url.setUrllink(updateUrlData.get("urllink").toString());

        if (updateUrlData.get("urlcategory") != null)
            url.setUrlcategory(updateUrlData.get("urlcategory").toString());

        return urlsRepository.save(url);
    }


    public List<UrlsEntity> getUrlsByUserId(Long userId)throws Exception {
        UsersEntity user = usersRepository.findByuserId(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found with Id: " + userId));

        return urlsRepository.findByUser(user);
    }
    public UrlsEntity getUrlByUrlIdAndUserId(Long urlId, Long userId) throws Exception{
        UsersEntity user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found with ID: " + userId));

        return urlsRepository.findByUrlIdAndUser(urlId, user)
                .orElseThrow(() -> new RuntimeException("URL Not Found for this user with urlId: " + urlId));
    }

    public String deleteUrlByIdAndUserId(Long urlId, Long userId)throws Exception {
        Optional<UrlsEntity> urlOptional = urlsRepository.findByUrlIdAndUser_UserId(urlId, userId);

        if (urlOptional.isPresent()) {
            urlsRepository.deleteById(urlId);
            return "URL deleted successfully";
        } else {
            throw new Exception("URL not found or does not belong to user with ID: " + userId);
        }
    }


}





