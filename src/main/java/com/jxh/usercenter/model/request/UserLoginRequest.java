package com.jxh.usercenter.model.request;

import lombok.Data;

/**
 * 登录请求体
 *
 * @author 20891
 */
@Data
public class UserLoginRequest {

    private static final long serialVersionUID = -8878645321233825616L;

    private String userAccount;

    private String userPassword;

}
