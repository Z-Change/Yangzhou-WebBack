
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import whut.pilipili.mall.api.mall.param.SaveMallUserAddressParam;
import whut.pilipili.mall.api.mall.param.UpdateMallUserAddressParam;
import whut.pilipili.mall.api.mall.vo.PilipiliMallUserAddressVO;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToMallUser;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.entity.MallUserAddress;
import whut.pilipili.mall.service.PilipiliMallUserAddressService;
import whut.pilipili.mall.util.BeanUtil;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
@Tag(description = "v1", name = "pilipili商城个人地址相关接口")
@RequestMapping("/api/v1")
public class PilipiliMallUserAddressAPI {

    @Resource
    private PilipiliMallUserAddressService mallUserAddressService;

    @GetMapping("/address")
    @Operation(summary = "我的收货地址列表", description = "")
    public Result<List<PilipiliMallUserAddressVO>> addressList(@TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        return ResultGenerator.genSuccessResult(mallUserAddressService.getMyAddresses(loginMallUser.getUserId()));
    }

    @PostMapping("/address")
    @Operation(summary = "添加地址", description = "")
    public Result<Boolean> saveUserAddress(@RequestBody SaveMallUserAddressParam saveMallUserAddressParam,
                                           @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        MallUserAddress userAddress = new MallUserAddress();
        BeanUtil.copyProperties(saveMallUserAddressParam, userAddress);
        userAddress.setUserId(loginMallUser.getUserId());
        Boolean saveResult = mallUserAddressService.saveUserAddress(userAddress);
        //添加成功
        if (saveResult) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult("添加失败");
    }

    @PutMapping("/address")
    @Operation(summary = "修改地址", description = "")
    public Result<Boolean> updateMallUserAddress(@RequestBody UpdateMallUserAddressParam updateMallUserAddressParam,
                                                 @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        MallUserAddress mallUserAddressById = mallUserAddressService.getMallUserAddressById(updateMallUserAddressParam.getAddressId());
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        MallUserAddress userAddress = new MallUserAddress();
        BeanUtil.copyProperties(updateMallUserAddressParam, userAddress);
        userAddress.setUserId(loginMallUser.getUserId());
        Boolean updateResult = mallUserAddressService.updateMallUserAddress(userAddress);
        //修改成功
        if (updateResult) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult("修改失败");
    }

    @GetMapping("/address/{addressId}")
    @Operation(summary = "获取收货地址详情", description = "传参为地址id")
    public Result<PilipiliMallUserAddressVO> getMallUserAddress(@PathVariable("addressId") Long addressId,
                                                                @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        MallUserAddress mallUserAddressById = mallUserAddressService.getMallUserAddressById(addressId);
        PilipiliMallUserAddressVO pilipiliMallUserAddressVO = new PilipiliMallUserAddressVO();
        BeanUtil.copyProperties(mallUserAddressById, pilipiliMallUserAddressVO);
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        return ResultGenerator.genSuccessResult(pilipiliMallUserAddressVO);
    }

    @GetMapping("/address/default")
    @Operation(summary = "获取默认收货地址", description = "无传参")
    public Result getDefaultMallUserAddress(@TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        MallUserAddress mallUserAddressById = mallUserAddressService.getMyDefaultAddressByUserId(loginMallUser.getUserId());
        return ResultGenerator.genSuccessResult(mallUserAddressById);
    }

    @DeleteMapping("/address/{addressId}")
    @Operation(summary = "删除收货地址", description = "传参为地址id")
    public Result deleteAddress(@PathVariable("addressId") Long addressId,
                                @TokenToMallUser @Parameter(hidden = true) MallUser loginMallUser) {
        MallUserAddress mallUserAddressById = mallUserAddressService.getMallUserAddressById(addressId);
        if (!loginMallUser.getUserId().equals(mallUserAddressById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = mallUserAddressService.deleteById(addressId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }
}
