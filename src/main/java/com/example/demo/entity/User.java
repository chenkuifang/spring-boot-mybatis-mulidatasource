package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author QuiFar
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String userName;
    private String passWord;
    private String userSex;
    private String nickName;

}