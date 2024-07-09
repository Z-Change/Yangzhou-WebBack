/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.mall;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import ltd.newbee.mall.api.mall.param.SaveCartItemParam;
import ltd.newbee.mall.api.mall.param.UpdateCartItemParam;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.PilipiliMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToMallUser;
import ltd.newbee.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(description = "v1", name = "新蜂商城购物车相关接口")
@RequestMapping("/api/v1")
public class PilipiliMallShoppingCartAPI {

    @Resource
    private NewBeeMallShoppingCartService newBeeMallShoppingCartService;

    @GetMapping("/shop-cart/page")
    @Operation(summary = "购物车列表(每页默认5条)", description = "传参为页码")
    public Result<PageResult<List<PilipiliMallShoppingCartItemVO>>> cartItemPageList(Integer pageNumber, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("page", pageNumber);
        params.put("limit", Constants.SHOPPING_CART_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(newBeeMallShoppingCartService.getMyShoppingCartItems(pageUtil));
    }

    @GetMapping("/shop-cart")
    @Operation(summary = "购物车列表(网页移动端不分页)", description = "")
    public Result<List<PilipiliMallShoppingCartItemVO>> cartItemList(@TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        return ResultGenerator.genSuccessResult(newBeeMallShoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId()));
    }

    @PostMapping("/shop-cart")
    @Operation(summary = "添加商品到购物车接口", description = "传参为商品id、数量")
    public Result saveNewBeeMallShoppingCartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                                                 @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        String saveResult = newBeeMallShoppingCartService.saveNewBeeMallCartItem(saveCartItemParam, loginMallUser.getUserId());
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @Operation(summary = "修改购物项数据", description = "传参为购物项id、数量")
    public Result updateNewBeeMallShoppingCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                                   @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        String updateResult = newBeeMallShoppingCartService.updateNewBeeMallCartItem(updateCartItemParam, loginMallUser.getUserId());
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @Operation(summary = "删除购物项", description = "传参为购物项id")
    public Result updateNewBeeMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long newBeeMallShoppingCartItemId,
                                                   @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        NewBeeMallShoppingCartItem newBeeMallCartItemById = newBeeMallShoppingCartService.getNewBeeMallCartItemById(newBeeMallShoppingCartItemId);
        if (!loginMallUser.getUserId().equals(newBeeMallCartItemById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = newBeeMallShoppingCartService.deleteById(newBeeMallShoppingCartItemId,loginMallUser.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    @Operation(summary = "根据购物项id数组查询购物项明细", description = "确认订单页面使用")
    public Result<List<PilipiliMallShoppingCartItemVO>> toSettle(Long[] cartItemIds, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        if (cartItemIds.length < 1) {
            PilipiliMallException.fail("参数异常");
        }
        int priceTotal = 0;
        List<PilipiliMallShoppingCartItemVO> itemsForSettle = newBeeMallShoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSettle)) {
            //无数据则抛出异常
            PilipiliMallException.fail("参数异常");
        } else {
            //总价
            for (PilipiliMallShoppingCartItemVO pilipiliMallShoppingCartItemVO : itemsForSettle) {
                priceTotal += pilipiliMallShoppingCartItemVO.getGoodsCount() * pilipiliMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                PilipiliMallException.fail("价格异常");
            }
        }
        return ResultGenerator.genSuccessResult(itemsForSettle);
    }
}
