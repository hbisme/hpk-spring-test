package com.hb.service;

import com.hb.Application;
import com.hb.dto.UserDto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import static org.junit.Assert.*;

/**
 * spring boot 参数校验测试,好像有点问题.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired // <1.1>
    private Validator validator;

    @Test
    public void testGet() {
        userService.get(-1);
    }

    @Test
    public void testAdd() {
        UserDto addDTO = new UserDto();
        userService.add(addDTO);
    }

    @Test
    public void testAdd01() {
        UserDto addDTO = new UserDto();
        userService.add1(addDTO);
    }


    @Test
    public void testValidator() {
        System.out.println(validator);
        UserDto addDTO = new UserDto();
        System.out.println(addDTO);
        final Set<ConstraintViolation<UserDto>> result = validator.validate(addDTO);

        for (ConstraintViolation<UserDto> userDtoConstraintViolation : result) {
            System.out.println(userDtoConstraintViolation);
        }

    }

}