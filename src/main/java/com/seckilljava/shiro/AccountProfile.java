package com.seckilljava.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * @author husky
 * @version 1.0
 * @date 2020/12/12 19:59
 */
@Data
public class AccountProfile implements Serializable {
    private Long id;
    private String username;
    private Long roleid;
    private String rolename;
}
