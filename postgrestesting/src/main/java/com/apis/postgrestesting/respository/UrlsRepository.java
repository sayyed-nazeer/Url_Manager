package com.apis.postgrestesting.respository;

import com.apis.postgrestesting.entity.UrlsEntity;
import com.apis.postgrestesting.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UrlsRepository extends JpaRepository<UrlsEntity,Long> {
    List<UrlsEntity> findByUser(UsersEntity user);
    Optional<UrlsEntity> findByUrlIdAndUser(Long urlId, UsersEntity user);
    Optional<UrlsEntity> findByUrlIdAndUser_UserId(Long urlId, Long userId);





}

