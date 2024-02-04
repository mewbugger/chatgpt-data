package com.wly.chatgpt.data.trigger.http;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.eventbus.EventBus;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wly.chatgpt.data.domain.auth.service.IAuthService;
import com.wly.chatgpt.data.domain.order.model.entity.PayOrderEntity;
import com.wly.chatgpt.data.domain.order.model.entity.ProductEntity;
import com.wly.chatgpt.data.domain.order.model.entity.ShopCartEntity;
import com.wly.chatgpt.data.domain.order.service.IOrderService;
import com.wly.chatgpt.data.trigger.http.dto.SaleProductDTO;
import com.wly.chatgpt.data.types.common.Constants;
import com.wly.chatgpt.data.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import com.alibaba.fastjson.JSON;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 售卖服务
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/sale/")
public class SaleController {
    @Autowired(required = false)
    private NotificationParser notificationParser;

    @Value("${alipay.config.alipay_public_key}")
    private String alipayPublicKey;

    @Resource
    private IOrderService orderService;
    @Resource
    private IAuthService authService;
    @Resource
    private EventBus eventBus;
    @Resource
    private AlipayClient alipayClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    /**
     * 商品列表查询
     * 开始地址：http://localhost:8091/api/v1/sale/query_product_list
     * 测试地址：http://apix.natapp1.cc/api/v1/sale/query_product_list
     * <p>
     * curl -X GET \
     * -H "Authorization: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJveGZBOXc4LTI..." \
     * -H "Content-Type: application/x-www-form-urlencoded" \
     * http://localhost:8091/api/v1/sale/query_product_list
     */
    @RequestMapping(value = "query_product_list", method = RequestMethod.GET)
    public Response<List<SaleProductDTO>> queryProductList(@RequestHeader("Authorization") String token) {
        try {
            // 1. Token 校验
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<List<SaleProductDTO>>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }
            // 2. 查询商品
            List<ProductEntity> productEntityList = orderService.queryProductList();
            log.info("商品查询 {}", JSON.toJSONString(productEntityList));

            List<SaleProductDTO> mallProductDTOS = new ArrayList<>();
            for (ProductEntity productEntity : productEntityList) {
                SaleProductDTO mallProductDTO = SaleProductDTO.builder()
                        .productId(productEntity.getProductId())
                        .productName(productEntity.getProductName())
                        .productDesc(productEntity.getProductDesc())
                        .price(productEntity.getPrice())
                        .quota(productEntity.getQuota())
                        .build();
                mallProductDTOS.add(mallProductDTO);
            }

            // 3. 返回结果
            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(mallProductDTOS)
                    .build();
        } catch (Exception e) {
            log.error("商品查询失败", e);
            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 用户商品下单
     * 开始地址：http://localhost:8091/api/v1/sale/create_pay_order?productId=
     * 测试地址：http://apix.natapp1.cc/api/v1/sale/create_pay_order
     * <p>
     * curl -X POST \
     * -H "Authorization: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJveGZBOXc4LTI..." \
     * -H "Content-Type: application/x-www-form-urlencoded" \
     * -d "productId=1001" \
     * http://localhost:8091/api/v1/sale/create_pay_order
     */
    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    public Response<String> createParOrder(@RequestHeader("Authorization") String token, @RequestParam Integer productId) {
        try {
            // 1. Token 校验
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<String>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 2. Token 解析
            String openid = authService.openid(token);
            assert null != openid;
            log.info("用户商品下单，根据商品ID创建支付单开始 openid:{} productId:{}", openid, productId);

            ShopCartEntity shopCartEntity = ShopCartEntity.builder()
                    .openid(openid)
                    .productId(productId).build();

            PayOrderEntity payOrder = orderService.createOrder(shopCartEntity);
            log.info("用户商品下单，根据商品ID创建支付单完成 openid: {} productId: {} orderPay: {}", openid, productId, payOrder.toString());

            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrder.getPayUrl())
                    .build();
        } catch (Exception e) {
            log.error("用户商品下单，根据商品ID创建支付单失败", e);
            System.out.println("外层");
            e.printStackTrace();
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 支付回调
     * 开发地址：http:/localhost:8091/api/v1/sale/pay_notify
     * 测试地址：http://apix.natapp1.cc/api/v1/sale/pay_notify
     * 线上地址：https://你的域名/api/v1/sale/pay_notify
     */
    @PostMapping("pay_notify")
    public String payNotify(HttpServletRequest request) {
        try {
            log.info("支付回调，消息接收 {}", request.getParameter("trade_status"));
            if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
                Map<String, String> params = new HashMap<>();
                Map<String, String[]> requestParams = request.getParameterMap();
                for (String name : requestParams.keySet()) {
                    params.put(name, request.getParameter(name));
                }

                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");
                String total_amount = params.get("total_amount");
                BigDecimal totalAmount = new BigDecimal(total_amount);
                String gmt_payment = params.get("gmt_payment");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date payTime = new Date();
                try {
                    payTime = format.parse(gmt_payment);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                String sign = params.get("sign");
                String content = AlipaySignature.getSignCheckContentV1(params);
                boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 验证签名
                // 支付宝验签
                if (checkSignature) {
                    // 验签通过
                    log.info("支付回调，交易名称: {}", params.get("subject"));
                    log.info("支付回调，交易状态: {}", params.get("trade_status"));
                    log.info("支付回调，支付宝交易凭证号: {}", params.get("trade_no"));
                    log.info("支付回调，商户订单号: {}", params.get("out_trade_no"));
                    log.info("支付回调，交易金额: {}", params.get("total_amount"));
                    log.info("支付回调，买家在支付宝唯一id: {}", params.get("buyer_id"));
                    log.info("支付回调，买家付款时间: {}", params.get("gmt_payment"));
                    log.info("支付回调，买家付款金额: {}", params.get("buyer_pay_amount"));
                    log.info("支付回调，支付回调，更新订单 {}", tradeNo);
                    // 更新订单未已支付
                    orderService.changeOrderPaySuccess(tradeNo, alipayTradeNo, totalAmount, payTime);
                    // 推送消息【自己的业务场景中可以使用MQ消息】
                    eventBus.post(tradeNo);
                }
            }
            return "success";
        } catch (Exception e) {
            log.error("支付回调，处理失败", e);
            return "false";
        }
    }
//    public void payNotify(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            // 获取订单号
//            String orderId = requestBody; // 这里假设requestBody就是订单号，实际情况可能需要你自己解析
//
//            // 创建API对应的request类
//            AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
//            alipayRequest.setBizContent("{" +
//                    "\"out_trade_no\":\"" + orderId + "\"" +
//                    "}");
//
//            // 通过alipayClient调用API，获得对应的response类
//            AlipayTradeFastpayRefundQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
//
//            if (alipayResponse.isSuccess()) {
//                // 支付成功
//                String transactionId = alipayResponse.getTradeNo();
//                BigDecimal totalAmount = new BigDecimal(alipayResponse.getTotalAmount());
//                String successTime = alipayResponse.getSendBackFee();
//                log.info("支付成功 orderId:{} total:{} successTime: {}", orderId, totalAmount, successTime);
//
//                // 更新订单
//                boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, totalAmount, dateFormat.parse(successTime));
//                if (isSuccess) {
//                    // 发布消息
//                    eventBus.post(orderId);
//                }
//                response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
//            } else {
//                // 支付失败
//                response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
//            }
//        } catch (AlipayApiException e) {
//            log.error("支付失败", e);
//            response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public void payNotify(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            // 随机串
//            String nonceStr = request.getHeader("Wechatpay-Nonce");
//            // 微信传递过来的签名
//            String signature = request.getHeader("Wechatpay-Signature");
//            // 证书序列号（微信平台）
//            String serialNo = request.getHeader("Wechatpay-Serial");
//            // 时间戳
//            String timestamp = request.getHeader("Wechatpay-Timestamp");
//
//            // 构造 RequestParam
//            com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
//                    .serialNumber(serialNo)
//                    .nonce(nonceStr)
//                    .signature(signature)
//                    .timestamp(timestamp)
//                    .body(requestBody)
//                    .build();
//
//            // 以支付通知回调为例，验签、解密并转换成 Transaction
//            Transaction transaction = notificationParser.parse(requestParam, Transaction.class);
//
//            Transaction.TradeStateEnum tradeState = transaction.getTradeState();
//            if (Transaction.TradeStateEnum.SUCCESS.equals(tradeState)) {
//                // 支付单号
//                String orderId = transaction.getOutTradeNo();
//                String transactionId = transaction.getTransactionId();
//                Integer total = transaction.getAmount().getTotal();
//                String successTime = transaction.getSuccessTime();
//                log.info("支付成功 orderId:{} total:{} successTime: {}", orderId, total, successTime);
//                // 更新订单
//                boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, new BigDecimal(total).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), dateFormat.parse(successTime));
//                if (isSuccess) {
//                    // 发布消息
//                    eventBus.post(orderId);
//                }
//                response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>");
//            } else {
//                response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
//            }
//        } catch (Exception e) {
//            log.error("支付失败", e);
//            response.getWriter().write("<xml><return_code><![CDATA[FAIL]]></return_code></xml>");
//        }
//    }

}
