
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import whut.pilipili.mall.api.mall.vo.PilipiliMallSearchGoodsVO;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToMallUser;
import whut.pilipili.mall.api.mall.vo.PilipiliMallGoodsDetailVO;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.entity.PilipiliMallGoods;
import whut.pilipili.mall.service.PilipiliMallGoodsService;
import whut.pilipili.mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(description = "v1", name = "pilipili商城商品相关接口")
@RequestMapping("/api/v1")
public class PilipiliMallGoodsAPI {

    private static final Logger logger = LoggerFactory.getLogger(PilipiliMallGoodsAPI.class);

    @Resource
    private PilipiliMallGoodsService pilipiliMallGoodsService;

    @GetMapping("/search")
    @Operation(summary = "商品搜索接口", description = "根据关键字和分类id进行搜索")
    public Result<PageResult<List<PilipiliMallSearchGoodsVO>>> search(@RequestParam(required = false) @Parameter(description = "搜索关键字") String keyword,
                                                                      @RequestParam(required = false) @Parameter(description = "分类id") Long goodsCategoryId,
                                                                      @RequestParam(required = false) @Parameter(description = "orderBy") String orderBy,
                                                                      @RequestParam(required = false) @Parameter(description = "页码") Integer pageNumber,
                                                                      @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        
        logger.info("goods search api,keyword={},goodsCategoryId={},orderBy={},pageNumber={},userId={}", keyword, goodsCategoryId, orderBy, pageNumber, loginMallUser.getUserId());

        Map params = new HashMap(8);
        //两个搜索参数都为空，直接返回异常
        if (goodsCategoryId == null && !StringUtils.hasText(keyword)) {
            PilipiliMallException.fail("非法的搜索参数");
        }
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("goodsCategoryId", goodsCategoryId);
        params.put("page", pageNumber);
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //对keyword做过滤 去掉空格
        if (StringUtils.hasText(keyword)) {
            params.put("keyword", keyword);
        }
        if (StringUtils.hasText(orderBy)) {
            params.put("orderBy", orderBy);
        }
        //搜索上架状态下的商品
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(pilipiliMallGoodsService.searchPilipiliMallGoods(pageUtil));
    }

    @GetMapping("/goods/detail/{goodsId}")
    @Operation(summary = "商品详情接口", description = "传参为商品id")
    public Result<PilipiliMallGoodsDetailVO> goodsDetail(@Parameter(description = "商品id") @PathVariable("goodsId") Long goodsId, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        logger.info("goods detail api,goodsId={},userId={}", goodsId, loginMallUser.getUserId());
        if (goodsId < 1) {
            return ResultGenerator.genFailResult("参数异常");
        }
        PilipiliMallGoods goods = pilipiliMallGoodsService.getPilipiliMallGoodsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            PilipiliMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        PilipiliMallGoodsDetailVO goodsDetailVO = new PilipiliMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        return ResultGenerator.genSuccessResult(goodsDetailVO);
    }

}
