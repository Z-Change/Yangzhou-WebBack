
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import whut.pilipili.mall.api.mall.param.SaveCartItemParam;
import whut.pilipili.mall.api.mall.param.UpdateCartItemParam;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToMallUser;
import whut.pilipili.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.entity.PilipiliMallShoppingCartItem;
import whut.pilipili.mall.service.PilipiliMallShoppingCartService;
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
@Tag(description = "v1", name = "新蜂商城购物车相关接口")
@RequestMapping("/api/v1")
public class PilipiliMallShoppingCartAPI {

    @Resource
    private PilipiliMallShoppingCartService pilipiliMallShoppingCartService;

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
        return ResultGenerator.genSuccessResult(pilipiliMallShoppingCartService.getMyShoppingCartItems(pageUtil));
    }

    @GetMapping("/shop-cart")
    @Operation(summary = "购物车列表(网页移动端不分页)", description = "")
    public Result<List<PilipiliMallShoppingCartItemVO>> cartItemList(@TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        return ResultGenerator.genSuccessResult(pilipiliMallShoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId()));
    }

    @PostMapping("/shop-cart")
    @Operation(summary = "添加商品到购物车接口", description = "传参为商品id、数量")
    public Result savePilipiliMallShoppingCartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                                                 @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        String saveResult = pilipiliMallShoppingCartService.savePilipiliMallCartItem(saveCartItemParam, loginMallUser.getUserId());
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @Operation(summary = "修改购物项数据", description = "传参为购物项id、数量")
    public Result updatePilipiliMallShoppingCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                                   @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        String updateResult = pilipiliMallShoppingCartService.updatePilipiliMallCartItem(updateCartItemParam, loginMallUser.getUserId());
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @Operation(summary = "删除购物项", description = "传参为购物项id")
    public Result updatePilipiliMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long newBeeMallShoppingCartItemId,
                                                   @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        PilipiliMallShoppingCartItem newBeeMallCartItemById = pilipiliMallShoppingCartService.getPilipiliMallCartItemById(newBeeMallShoppingCartItemId);
        if (!loginMallUser.getUserId().equals(newBeeMallCartItemById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = pilipiliMallShoppingCartService.deleteById(newBeeMallShoppingCartItemId,loginMallUser.getUserId());
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
        float priceTotal = 0;
        List<PilipiliMallShoppingCartItemVO> itemsForSettle = pilipiliMallShoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds), loginMallUser.getUserId());
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
