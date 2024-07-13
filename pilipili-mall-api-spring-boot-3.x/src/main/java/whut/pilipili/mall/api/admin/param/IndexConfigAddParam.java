
package whut.pilipili.mall.api.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class IndexConfigAddParam {

    @Schema(title = "配置项名称")
    @NotEmpty(message = "configName不能为空")
    private String configName;

    @Schema(title = "配置类别")
    @NotNull(message = "configType不能为空")
    @Min(value = 1, message = "configType最小为1")
    @Max(value = 5, message = "configType最大为5")
    private Byte configType;

    @Schema(title = "商品id")
    @NotNull(message = "商品id不能为空")
    @Min(value = 1, message = "商品id不能为空")
    private Long goodsId;

    @Schema(title = "排序值")
    @Min(value = 1, message = "configRank最低为1")
    @Max(value = 200, message = "configRank最高为200")
    @NotNull(message = "configRank不能为空")
    private Integer configRank;
}