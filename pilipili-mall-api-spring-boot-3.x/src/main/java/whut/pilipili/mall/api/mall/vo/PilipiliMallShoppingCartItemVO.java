
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 购物车页面购物项VO
 */
@Data
public class PilipiliMallShoppingCartItemVO implements Serializable {

    @Schema(title = "购物项id")
    private Long cartItemId;

    @Schema(title = "商品id")
    private Long goodsId;

    @Schema(title = "商品数量")
    private Integer goodsCount;

    @Schema(title = "商品名称")
    private String goodsName;

    @Schema(title = "商品图片")
    private String goodsCoverImg;

    @Schema(title = "商品价格")
    private Float sellingPrice;
}
