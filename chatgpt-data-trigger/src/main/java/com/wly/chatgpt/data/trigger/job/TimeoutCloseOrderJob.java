package com.wly.chatgpt.data.trigger.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.wly.chatgpt.data.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.CloseOrderRequest;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

/**
 * 超时关单任务
 */
@Slf4j
@Component()
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;
    @Autowired(required = false)
    private NativePayService payService;
    @Value("${wxpay.config.mchid}")
    private String mchid;
    @Resource
    private AlipayClient alipayClient;


    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            if (null == alipayClient) {
                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
                return;
            }
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if (orderIds.isEmpty()) {
                log.info("定时任务，超时30分钟订单关闭，暂无超时未支付订单 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                boolean status = orderService.changeOrderClose(orderId);
                //阿里支付关单
                AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
                request.setBizContent("{" +
                        "\"out_trade_no\":\"" + orderId + "\"" +
                        "  }");
                AlipayTradeCloseResponse response = alipayClient.execute(request);
                if (!"10000".equals(response.getCode())) {
                    log.info("定时任务，超时30分钟订单关闭失败 orderId: {}", orderId);
                    continue;
                }

                log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);
            }
        } catch (Exception e) {
            log.error("定时任务，超时15分钟订单关闭失败", e);
        }
    }
//    public void exec() {
//        try {
//            if (null == payService) {
//                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
//                return;
//            }
//            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
//            if (orderIds.isEmpty()) {
//                log.info("定时任务，超时30分钟订单关闭，暂无超时未支付订单 orderIds is null");
//                return;
//            }
//            for (String orderId : orderIds) {
//                boolean status = orderService.changeOrderClose(orderId);
//                //微信关单；暂时不需要主动关闭
//                CloseOrderRequest request = new CloseOrderRequest();
//                request.setMchid(mchid);
//                request.setOutTradeNo(orderId);
//                payService.closeOrder(request);
//
//                log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);
//            }
//        } catch (Exception e) {
//            log.error("定时任务，超时15分钟订单关闭失败", e);
//        }
//    }

}
