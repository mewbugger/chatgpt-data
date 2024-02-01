package com.wly.chatgpt.data.infrastructure.dao;

import com.wly.chatgpt.data.infrastructure.po.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账户DAO
 */
@Mapper
public interface IUserAccountDao {

    int subAccountQuota(String openid);

    UserAccountPO queryUserAccount(String openid);

    void insert(UserAccountPO userAccountPOReq);

    int addAccountQuota(UserAccountPO userAccountPOReq);

}
