
package whut.pilipili.mall.api.mall.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改购物项param
 */
@Data
public class UpdateCartItemParam implements Serializable {

    @Schema(title = "购物项id")
    private Long cartItemId;

    @Schema(title = "商品数量")
    private Integer goodsCount;
}
