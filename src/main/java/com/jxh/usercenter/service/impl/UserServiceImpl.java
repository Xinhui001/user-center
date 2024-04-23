package com.jxh.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxh.usercenter.model.domain.User;
import com.jxh.usercenter.service.UserService;
import com.jxh.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jxh.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 20891
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-04-01 22:20:47
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 盐值
     */
    private static final String SALT = "jxh";


    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册服务实现
     *
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        //账户、密码、校验码不为空
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)) {
            //TODO
            return -1;
        }
        //账户长度不小于4
        if (userAccount.length() < 4) {
            return -2;
        }
        //账户中不得包含特殊字符
        String validPattern="[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -3;
        }
        //密码和校验码不小于8
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -4;
        }
        //密码和校验码要相同
        if (!userPassword.equals(checkPassword)) {
            return -5;
        }
        //账户不可重复
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        Long selectCount = userMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            return -6;
        }

        //对密码加密
//        final String SALT = "jxh";
        String safetyPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(safetyPassword);
        int saveResult = userMapper.insert(user);
        if (saveResult < 0) {
            return -7;
        }

        return user.getId();
    }

    /**
     * 用户登录服务实现
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 用户脱敏信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //账户、密码、校验码不为空
        if (StringUtils.isAnyBlank(userAccount,userPassword)) {
            //TODO
            return null;
        }
        //账户长度不小于4
        if (userAccount.length() < 4) {
            return null;
        }
        //账户中不得包含特殊字符
        String validPattern="[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        //密码和校验码不小于8
        if (userPassword.length() < 8) {
            return null;
        }
        String encodePassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount",userAccount);
        userQueryWrapper.eq("userPassword",encodePassword);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info("user login failed,userAccount cannot match userPassword");
            return null;
        }
        //脱敏用户信息
        User safetyUser = getSafetyUser(user);
        //记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        //返回用户脱敏信息
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    @Override
    public User getSafetyUser(User user){

        if (user == null) {
            return null;
        }

        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }

    /**
     * 退出登录
     *
     * @param request 获取session信息
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}




