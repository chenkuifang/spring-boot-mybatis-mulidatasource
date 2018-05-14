package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取数据源1的用户列表
     *
     * @return
     */
    @RequestMapping("/getUsers1")
    public List<User> getUsers() {
        List<User> users = userService.get1All();
        return users;
    }

    /**
     * 获取数据源2的用户列表
     *
     * @return
     */
    @RequestMapping("/getUsers2")
    public List<User> getUsers2() {
        List<User> users = userService.get2All();
        return users;
    }

    @RequestMapping("/add")
    public String add() {
        String result = "No Ok";
        User user1 = new User();
        user1.setUserName("quifarDB1");
        user1.setPassWord("123456");
        user1.setUserSex("男");

        User user2 = new User();
        user2.setUserName("quifarDB2");
        user2.setPassWord("456789");
        user2.setUserSex("男");

        if (userService.add(user1, user2)) {
            result = "OK";
        }

        return result;
    }

}