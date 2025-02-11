
package whut.pilipili.mall.api.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import whut.pilipili.mall.api.admin.param.BatchIdParam;
import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderDetailVO;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToAdminUser;
import whut.pilipili.mall.entity.AdminUserToken;
import whut.pilipili.mall.service.PilipiliMallOrderService;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.Map;


@RestController
@Tag(description = "v1", name = "后台管理系统订单模块接口")
@RequestMapping("/manage-api/v1")
public class PilipiliAdminOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(PilipiliAdminOrderAPI.class);

    @Resource
    private PilipiliMallOrderService pilipiliMallOrderService;

    /**
     * 列表
     */
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    @Operation(summary = "订单列表", description = "可根据订单号和订单状态筛选")
    public Result list(@RequestParam(required = false) @Parameter(description = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(description = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @Parameter(description = "订单号") String orderNo,
                       @RequestParam(required = false) @Parameter(description = "订单状态") Integer orderStatus, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (StringUtils.hasText(orderNo)) {
            params.put("orderNo", orderNo);
        }
        if (orderStatus != null) {
            params.put("orderStatus", orderStatus);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(pilipiliMallOrderService.getPilipiliMallOrdersPage(pageUtil));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "订单详情接口", description = "传参为订单号")
    public Result<PilipiliMallOrderDetailVO> orderDetailPage(@Parameter(description = "订单号") @PathVariable("orderId") Long orderId, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        return ResultGenerator.genSuccessResult(pilipiliMallOrderService.getOrderDetailByOrderId(orderId));
    }

    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.PUT)
    @Operation(summary = "修改订单状态为配货成功", description = "批量修改")
    public Result checkDone(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = pilipiliMallOrderService.checkDone(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.PUT)
    @Operation(summary = "修改订单状态为已出库", description = "批量修改")
    public Result checkOut(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = pilipiliMallOrderService.checkOut(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.PUT)
    @Operation(summary = "修改订单状态为商家关闭", description = "批量修改")
    public Result closeOrder(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = pilipiliMallOrderService.closeOrder(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}