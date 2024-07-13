
package whut.pilipili.mall.api.mall.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 保存订单param
 */
@Data
public class SaveOrderParam implements Serializable {

    @Schema(title = "订单项id数组")
    private Long[] cartItemIds;

    @Schema(title = "地址id")
    private Long addressId;
}
