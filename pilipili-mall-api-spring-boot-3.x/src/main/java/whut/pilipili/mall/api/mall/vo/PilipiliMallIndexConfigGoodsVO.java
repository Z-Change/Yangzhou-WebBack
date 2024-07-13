
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 首页配置商品VO
 */
@Data
public class PilipiliMallIndexConfigGoodsVO implements Serializable {

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
}
