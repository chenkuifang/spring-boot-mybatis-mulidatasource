package com.example.demo.service.impl;

import com.example.demo.service.UserService;
import com.example.demo.entity.User;
import com.example.demo.mapper.db1.UserMapper;
import com.example.demo.mapper.db2.User2Mapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * 多数据源放入同一个事务线程处理
 *
 * @author QuiFar
 * @version V1.0
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private User2Mapper user2Mapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private DataSourceTransactionManager transactionManager;


    @Override
    public List<User> get1All() {
        return userMapper.getAll();
    }

    @Override
    public List<User> get2All() {
        return user2Mapper.getAll();
    }

    @Override
    public boolean add(User user1, User user2) {
        // 方法1
        boolean value = (Boolean) transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                boolean result = true;
                try {
                    userMapper.insert(user1);
                    user2Mapper.insert(user2);
                } catch (Exception e) {
                    result = false;
                    status.setRollbackOnly();
                    log.debug(e.getMessage());
                }
                return result;
            }
        });

//        //方法2
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        // 开启新事务
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//        TransactionStatus status = transactionManager.getTransaction(def);
//        try {
//            userMapper.insert(user1);
//            user2Mapper.insert(user2);
//            transactionManager.commit(status);
//        } catch (Exception e) {
//            value = false;
//            transactionManager.rollback(status);
//            logger.debug(e.getMessage());
//        }

        return value;
    }


}
