package com.apis.postgrestesting.respository;

import com.apis.postgrestesting.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity,Long> {
    Optional<UsersEntity> findByEmail(String email);
    Optional<UsersEntity> findByuserId(Long userId);

}
