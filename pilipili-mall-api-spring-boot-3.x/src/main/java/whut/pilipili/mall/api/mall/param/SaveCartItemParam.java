
package whut.pilipili.mall.api.mall.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加购物项param
 */
@Data
public class SaveCartItemParam implements Serializable {

    @Schema(title = "商品数量")
    private Integer goodsCount;

    @Schema(title = "商品id")
    private Long goodsId;
}
