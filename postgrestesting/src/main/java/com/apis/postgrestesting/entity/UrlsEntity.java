package com.apis.postgrestesting.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="urls")
public class UrlsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long urlId;


    @Column(name="urlname",nullable = false)
    private String urlname;

    @Column(name="urldescription",nullable = false)
    private  String urldescription;

    @Column(name="urllink", nullable = false)
    private  String urllink;

    @Column(name="urlcategory" ,nullable = false)
    private  String urlcategory;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

}
