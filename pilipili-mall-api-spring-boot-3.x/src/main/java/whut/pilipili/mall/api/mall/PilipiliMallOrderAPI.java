
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import whut.pilipili.mall.api.mall.param.SaveOrderParam;
import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderDetailVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderListVO;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToMallUser;
import whut.pilipili.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.entity.MallUserAddress;
import whut.pilipili.mall.service.PilipiliMallOrderService;
import whut.pilipili.mall.service.PilipiliMallShoppingCartService;
import whut.pilipili.mall.service.PilipiliMallUserAddressService;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(description = "v1", name = "pilipili商城订单操作相关接口")
@RequestMapping("/api/v1")
public class PilipiliMallOrderAPI {

    @Resource
    private PilipiliMallShoppingCartService pilipiliMallShoppingCartService;
    @Resource
    private PilipiliMallOrderService pilipiliMallOrderService;
    @Resource
    private PilipiliMallUserAddressService pilipiliMallUserAddressService;

    @PostMapping("/saveOrder")
    @Operation(summary = "生成订单接口", description = "传参为地址id和待结算的购物项id数组")
    public Result<String> saveOrder(@Parameter(description = "订单参数") @RequestBody SaveOrderParam saveOrderParam, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        float priceTotal = 0;
        if (saveOrderParam == null || saveOrderParam.getCartItemIds() == null || saveOrderParam.getAddressId() == null) {
            PilipiliMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (saveOrderParam.getCartItemIds().length < 1) {
            PilipiliMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<PilipiliMallShoppingCartItemVO> itemsForSave = pilipiliMallShoppingCartService.getCartItemsForSettle(Arrays.asList(saveOrderParam.getCartItemIds()), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSave)) {
            //无数据
            PilipiliMallException.fail("参数异常");
        } else {
            //总价
            for (PilipiliMallShoppingCartItemVO pilipiliMallShoppingCartItemVO : itemsForSave) {
                priceTotal += pilipiliMallShoppingCartItemVO.getGoodsCount() * pilipiliMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                PilipiliMallException.fail("价格异常");
            }
            MallUserAddress address = pilipiliMallUserAddressService.getMallUserAddressById(saveOrderParam.getAddressId());
            if (!loginMallUser.getUserId().equals(address.getUserId())) {
                return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
            }
            //保存订单并返回订单号
            String saveOrderResult = pilipiliMallOrderService.saveOrder(loginMallUser, address, itemsForSave);
            Result result = ResultGenerator.genSuccessResult();
            result.setData(saveOrderResult);
            return result;
        }
        return ResultGenerator.genFailResult("生成订单失败");
    }

    @GetMapping("/order/{orderNo}")
    @Operation(summary = "订单详情接口", description = "传参为订单号")
    public Result<PilipiliMallOrderDetailVO> orderDetailPage(@Parameter(description = "订单号") @PathVariable("orderNo") String orderNo, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        return ResultGenerator.genSuccessResult(pilipiliMallOrderService.getOrderDetailByOrderNo(orderNo, loginMallUser.getUserId()));
    }

    @GetMapping("/order")
    @Operation(summary = "订单列表接口", description = "传参为页码")
    public Result<PageResult<List<PilipiliMallOrderListVO>>> orderList(@Parameter(description = "页码") @RequestParam(required = false) Integer pageNumber,
                                                                       @Parameter(description = "订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功") @RequestParam(required = false) Integer status,
                                                                       @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("orderStatus", status);
        params.put("page", pageNumber);
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(pilipiliMallOrderService.getMyOrders(pageUtil));
    }

    @PutMapping("/order/{orderNo}/cancel")
    @Operation(summary = "订单取消接口", description = "传参为订单号")
    public Result cancelOrder(@Parameter(description = "订单号") @PathVariable("orderNo") String orderNo, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        String cancelOrderResult = pilipiliMallOrderService.cancelOrder(orderNo, loginMallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/order/{orderNo}/finish")
    @Operation(summary = "确认收货接口", description = "传参为订单号")
    public Result finishOrder(@Parameter(description = "订单号") @PathVariable("orderNo") String orderNo, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        String finishOrderResult = pilipiliMallOrderService.finishOrder(orderNo, loginMallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/paySuccess")
    @Operation(summary = "模拟支付成功回调的接口", description = "传参为订单号和支付方式")
    public Result paySuccess(@Parameter(description = "订单号") @RequestParam("orderNo") String orderNo, @Parameter(description = "支付方式") @RequestParam("payType") int payType) {
        String payResult = pilipiliMallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
