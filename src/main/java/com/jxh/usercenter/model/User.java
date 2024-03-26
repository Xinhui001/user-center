package com.jxh.usercenter.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 20891
 */
@Data
@TableName("`user`")
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
