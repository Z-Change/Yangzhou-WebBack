
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品详情页VO
 */
@Data
public class PilipiliMallGoodsDetailVO implements Serializable {

    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品名称")
    private String goodsName;

    @Schema(title = "商品简介")
    private String goodsIntro;

    @Schema(title = "商品图片地址")
    private String goodsCoverImg;

    @Schema(title = "商品价格")
    private Float sellingPrice;

    @Schema(title = "商品标签")
    private String tag;

    @Schema(title = "商品图片")
    private String[] goodsCarouselList;

    @Schema(title = "商品原价")
    private Float originalPrice;

    @Schema(title = "商品详情字段")
    private String goodsDetailContent;
}
