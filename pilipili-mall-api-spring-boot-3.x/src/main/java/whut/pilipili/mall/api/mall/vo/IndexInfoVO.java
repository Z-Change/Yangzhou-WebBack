
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IndexInfoVO implements Serializable {

    @Schema(title = "轮播图(列表)")
    private List<PilipiliMallIndexCarouselVO> carousels;

    @Schema(title = "首页热销商品(列表)")
    private List<PilipiliMallIndexConfigGoodsVO> hotGoodses;

    @Schema(title = "首页新品推荐(列表)")
    private List<PilipiliMallIndexConfigGoodsVO> newGoodses;

    @Schema(title = "首页推荐商品(列表)")
    private List<PilipiliMallIndexConfigGoodsVO> recommendGoodses;
}
