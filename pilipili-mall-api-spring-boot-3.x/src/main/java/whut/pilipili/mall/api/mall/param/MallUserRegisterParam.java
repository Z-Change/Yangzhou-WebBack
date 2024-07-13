
package whut.pilipili.mall.api.mall.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 用户注册param
 */
@Data
public class MallUserRegisterParam implements Serializable {

    @Schema(title = "登录名")
    @NotEmpty(message = "登录名不能为空")
    private String loginName;

    @Schema(title = "用户密码")
    @NotEmpty(message = "密码不能为空")
    private String password;
}
