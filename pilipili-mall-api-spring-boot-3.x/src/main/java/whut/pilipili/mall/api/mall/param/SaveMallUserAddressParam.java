
package whut.pilipili.mall.api.mall.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 添加收货地址param
 */
@Data
public class SaveMallUserAddressParam {

    @Schema(title = "收件人名称")
    private String userName;

    @Schema(title = "收件人联系方式")
    private String userPhone;

    @Schema(title = "是否默认地址 0-不是 1-是")
    private Byte defaultFlag;

    @Schema(title = "省")
    private String provinceName;

    @Schema(title = "市")
    private String cityName;

    @Schema(title = "区/县")
    private String regionName;

    @Schema(title = "详细地址")
    private String detailAddress;
}
