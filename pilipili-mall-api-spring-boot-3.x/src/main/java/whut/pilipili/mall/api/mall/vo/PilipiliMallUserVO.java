
package whut.pilipili.mall.api.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class PilipiliMallUserVO implements Serializable {

    @Schema(title = "用户昵称")
    private String nickName;

    @Schema(title = "用户登录名")
    private String loginName;

    @Schema(title = "个性签名")
    private String introduceSign;
}
