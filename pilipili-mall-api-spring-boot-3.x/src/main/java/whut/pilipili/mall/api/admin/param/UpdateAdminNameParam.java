
package whut.pilipili.mall.api.admin.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class UpdateAdminNameParam {

    @NotEmpty(message = "loginUserName不能为空")
    private String loginUserName;

    @NotEmpty(message = "nickName不能为空")
    private String nickName;
}
