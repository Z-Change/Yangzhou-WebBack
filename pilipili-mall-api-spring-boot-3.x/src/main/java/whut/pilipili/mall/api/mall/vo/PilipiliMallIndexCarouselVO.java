
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 首页轮播图VO
 */
@Data
public class PilipiliMallIndexCarouselVO implements Serializable {

    @Schema(title = "轮播图图片地址")
    private String carouselUrl;

    @Schema(title = "轮播图点击后的跳转路径")
    private String redirectUrl;
}
