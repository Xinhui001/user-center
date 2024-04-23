package com.jxh.usercenter.service;

import com.jxh.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.net.http.HttpRequest;

/**
* @author 20891
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-04-01 22:20:47
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 账户
     * @param userPassword 密码
     * @param request 请求
     * @return 用户脱敏信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user 用户对象
     * @return 脱敏用户信息
     */
    User getSafetyUser(User user);

    /**
     * 退出登录
     *
     * @param request 获取session信息
     * @return
     */
    int userLogout(HttpServletRequest request);
}
