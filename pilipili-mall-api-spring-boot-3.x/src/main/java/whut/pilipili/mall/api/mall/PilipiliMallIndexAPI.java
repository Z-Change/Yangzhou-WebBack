/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.IndexConfigTypeEnum;
import whut.pilipili.mall.api.mall.vo.IndexInfoVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallIndexCarouselVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallIndexConfigGoodsVO;
import whut.pilipili.mall.service.PilipiliMallCarouselService;
import whut.pilipili.mall.service.PilipiliMallIndexConfigService;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
@Tag(description = "v1", name = "新蜂商城首页接口")
@RequestMapping("/api/v1")
public class PilipiliMallIndexAPI {

    @Resource
    private PilipiliMallCarouselService pilipiliMallCarouselService;

    @Resource
    private PilipiliMallIndexConfigService pilipiliMallIndexConfigService;

    @GetMapping("/index-infos")
    @Operation(summary = "获取首页数据", description = "轮播图、新品、推荐等")
    public Result<IndexInfoVO> indexInfo() {
        IndexInfoVO indexInfoVO = new IndexInfoVO();
        List<PilipiliMallIndexCarouselVO> carousels = pilipiliMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<PilipiliMallIndexConfigGoodsVO> hotGoodses = pilipiliMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<PilipiliMallIndexConfigGoodsVO> newGoodses = pilipiliMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<PilipiliMallIndexConfigGoodsVO> recommendGoodses = pilipiliMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        indexInfoVO.setCarousels(carousels);
        indexInfoVO.setHotGoodses(hotGoodses);
        indexInfoVO.setNewGoodses(newGoodses);
        indexInfoVO.setRecommendGoodses(recommendGoodses);
        return ResultGenerator.genSuccessResult(indexInfoVO);
    }
}
