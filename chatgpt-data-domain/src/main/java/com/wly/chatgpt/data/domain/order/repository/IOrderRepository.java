package com.wly.chatgpt.data.domain.order.repository;

import com.wly.chatgpt.data.domain.order.model.aggregates.CreateOrderAggregate;
import com.wly.chatgpt.data.domain.order.model.entity.PayOrderEntity;
import com.wly.chatgpt.data.domain.order.model.entity.ProductEntity;
import com.wly.chatgpt.data.domain.order.model.entity.ShopCartEntity;
import com.wly.chatgpt.data.domain.order.model.entity.UnpaidOrderEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单仓储接口
 */
public interface IOrderRepository {

    UnpaidOrderEntity queryUnpaidOrder(ShopCartEntity shopCartEntity);

    ProductEntity queryProduct(Integer productId);

    void saveOrder(CreateOrderAggregate aggregate);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime);

    CreateOrderAggregate queryOrder(String orderId);

    void deliverGoods(String orderId);

    List<String> queryReplenishmentOrder();

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    List<ProductEntity> queryProductList();

}
