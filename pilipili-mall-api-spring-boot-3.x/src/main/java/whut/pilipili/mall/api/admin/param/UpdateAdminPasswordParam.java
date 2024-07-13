
package whut.pilipili.mall.api.admin.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class UpdateAdminPasswordParam {

    @NotEmpty(message = "originalPassword不能为空")
    private String originalPassword;

    @NotEmpty(message = "newPassword不能为空")
    private String newPassword;
}
