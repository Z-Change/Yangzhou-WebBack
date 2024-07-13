
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单详情页页面订单项VO
 */
@Data
public class PilipiliMallOrderItemVO implements Serializable {

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
