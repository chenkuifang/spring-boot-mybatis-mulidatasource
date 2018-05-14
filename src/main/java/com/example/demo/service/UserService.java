package com.example.demo.service;

import com.example.demo.entity.User;

import java.util.List;

/**
 * @author QuiFar
 * @version V1.0
 **/
public interface UserService {
    /**
     * 数据源1的用户列表
     *
     * @return
     */
    List<User> get1All();

    /**
     * 数据源2的用户列表
     *
     * @return
     */
    List<User> get2All();

    /**
     * 新增
     *
     * @param user1
     * @param user2
     * @return
     */
    boolean add(User user1, User user2);
}
