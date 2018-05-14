package com.example.demo.mapper.db1;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {

    List<User> getAll();

    User getOne(Long id);

    int insert(User user);

    void update(User user);

    void delete(Long id);

}