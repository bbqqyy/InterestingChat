package com.bqy.common;

import com.bqy.common.user.dao.UserDao;
import com.bqy.common.user.domain.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserDaoTest {
    @Resource
    private UserDao userDao;

    @Test
    public void test(){
        User newUser = new User();
        newUser.setName("111");
        newUser.setOpenId("123");
        boolean result = userDao.save(newUser);
        System.out.println(result);

    }
}
