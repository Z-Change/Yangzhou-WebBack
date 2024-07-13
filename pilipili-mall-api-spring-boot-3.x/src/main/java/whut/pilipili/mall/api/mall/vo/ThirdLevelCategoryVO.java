
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 首页分类数据VO(第三级)
 */
@Data
public class ThirdLevelCategoryVO implements Serializable {

    @Schema(title = "当前三级分类id")
    private Long categoryId;

    @Schema(title = "当前分类级别")
    private Byte categoryLevel;

    @Schema(title = "当前三级分类名称")
    private String categoryName;
}
