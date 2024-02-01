package com.wly.chatgpt.data.infrastructure.dao;

import com.wly.chatgpt.data.infrastructure.po.OpenAIProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品Dao
 */
@Mapper
public interface IOpenAIProductDao {
    OpenAIProductPO queryProductByProductId(Integer productId);

    List<OpenAIProductPO> queryProductList();

}
