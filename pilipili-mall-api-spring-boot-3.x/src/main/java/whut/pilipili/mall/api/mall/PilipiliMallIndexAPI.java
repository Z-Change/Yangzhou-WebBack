
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
@Tag(description = "v1", name = "pilipili商城首页接口")
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
