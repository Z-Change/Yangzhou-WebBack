
package whut.pilipili.mall.api.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import whut.pilipili.mall.api.admin.param.BatchIdParam;
import whut.pilipili.mall.api.admin.param.IndexConfigAddParam;
import whut.pilipili.mall.api.admin.param.IndexConfigEditParam;
import whut.pilipili.mall.common.IndexConfigTypeEnum;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToAdminUser;
import whut.pilipili.mall.entity.AdminUserToken;
import whut.pilipili.mall.entity.IndexConfig;
import whut.pilipili.mall.service.PilipiliMallIndexConfigService;
import whut.pilipili.mall.util.BeanUtil;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

/**
 
 */
@RestController
@Tag(description = "v1", name = "后台管理系统首页配置模块接口")
@RequestMapping("/manage-api/v1")
public class PilipiliAdminIndexConfigAPI {

    private static final Logger logger = LoggerFactory.getLogger(PilipiliAdminIndexConfigAPI.class);

    @Resource
    private PilipiliMallIndexConfigService pilipiliMallIndexConfigService;

    /**
     * 列表
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.GET)
    @Operation(summary = "首页配置列表", description = "首页配置列表")
    public Result list(@RequestParam(required = false) @Parameter(description = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(description = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @Parameter(description = "1-搜索框热搜 2-搜索下拉框热搜 3-(首页)热销商品 4-(首页)新品上线 5-(首页)为你推荐") Integer configType, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)) {
            return ResultGenerator.genFailResult("非法参数！");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("configType", configType);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(pilipiliMallIndexConfigService.getConfigsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.POST)
    @Operation(summary = "新增首页配置项", description = "新增首页配置项")
    public Result save(@RequestBody @Valid IndexConfigAddParam indexConfigAddParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        IndexConfig indexConfig = new IndexConfig();
        BeanUtil.copyProperties(indexConfigAddParam, indexConfig);
        String result = pilipiliMallIndexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.PUT)
    @Operation(summary = "修改首页配置项", description = "修改首页配置项")
    public Result update(@RequestBody @Valid IndexConfigEditParam indexConfigEditParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        IndexConfig indexConfig = new IndexConfig();
        BeanUtil.copyProperties(indexConfigEditParam, indexConfig);
        String result = pilipiliMallIndexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/indexConfigs/{id}", method = RequestMethod.GET)
    @Operation(summary = "获取单条首页配置项信息", description = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        IndexConfig config = pilipiliMallIndexConfigService.getIndexConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(config);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.DELETE)
    @Operation(summary = "批量删除首页配置项信息", description = "批量删除首页配置项信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (pilipiliMallIndexConfigService.deleteBatch(batchIdParam.getIds())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}