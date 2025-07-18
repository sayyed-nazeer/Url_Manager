package com.apis.postgrestesting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(name="password",nullable = false)
    private String password;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Column(name="dob",nullable = false)
    private LocalDate dob;

    @Column(name="mobileNumber",nullable = false,unique = true)
    private String MobileNumber;

    @Column(name="profession",nullable = false)
    private String profession;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UrlsEntity> urls;



}
