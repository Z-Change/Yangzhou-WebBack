
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import whut.pilipili.mall.api.mall.param.MallUserLoginParam;
import whut.pilipili.mall.api.mall.param.MallUserRegisterParam;
import whut.pilipili.mall.api.mall.param.MallUserUpdateParam;
import whut.pilipili.mall.api.mall.vo.PilipiliMallUserVO;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToMallUser;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.service.PilipiliMallUserService;
import whut.pilipili.mall.util.BeanUtil;
import whut.pilipili.mall.util.NumberUtil;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(description = "v1", name = "新蜂商城用户操作相关接口")
@RequestMapping("/api/v1")
public class PilipiliMallPersonalAPI {

    @Resource
    private PilipiliMallUserService pilipiliMallUserService;

    private static final Logger logger = LoggerFactory.getLogger(PilipiliMallPersonalAPI.class);

    @PostMapping("/user/login")
    @Operation(summary = "登录接口", description = "返回token")
    public Result<String> login(@RequestBody @Valid MallUserLoginParam mallUserLoginParam) {
        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String loginResult = pilipiliMallUserService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());

        logger.info("login api,loginName={},loginResult={}", mallUserLoginParam.getLoginName(), loginResult);

        //登录成功
        if (StringUtils.hasText(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }


    @PostMapping("/user/logout")
    @Operation(summary = "登出接口", description = "清除token")
    public Result<String> logout(@TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        Boolean logoutResult = pilipiliMallUserService.logout(loginMallUser.getUserId());

        logger.info("logout api,loginMallUser={}", loginMallUser.getUserId());

        //登出成功
        if (logoutResult) {
            return ResultGenerator.genSuccessResult();
        }
        //登出失败
        return ResultGenerator.genFailResult("logout error");
    }


    @PostMapping("/user/register")
    @Operation(summary = "用户注册", description = "")
    public Result register(@RequestBody @Valid MallUserRegisterParam mallUserRegisterParam) {
        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String registerResult = pilipiliMallUserService.register(mallUserRegisterParam.getLoginName(), mallUserRegisterParam.getPassword());

        logger.info("register api,loginName={},loginResult={}", mallUserRegisterParam.getLoginName(), registerResult);

        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PutMapping("/user/info")
    @Operation(summary = "修改用户信息", description = "")
    public Result updateInfo(@RequestBody @Parameter(description = "用户信息") MallUserUpdateParam mallUserUpdateParam, @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        Boolean flag = pilipiliMallUserService.updateUserInfo(mallUserUpdateParam, loginMallUser.getUserId());
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @GetMapping("/user/info")
    @Operation(summary = "获取用户信息", description = "")
    public Result<PilipiliMallUserVO> getUserDetail(@TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        //已登录则直接返回
        PilipiliMallUserVO mallUserVO = new PilipiliMallUserVO();
        BeanUtil.copyProperties(loginMallUser, mallUserVO);
        return ResultGenerator.genSuccessResult(mallUserVO);
    }
}
