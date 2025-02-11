
package whut.pilipili.mall.api.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import whut.pilipili.mall.api.admin.param.BatchIdParam;
import whut.pilipili.mall.api.admin.param.CarouselAddParam;
import whut.pilipili.mall.api.admin.param.CarouselEditParam;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.config.annotation.TokenToAdminUser;
import whut.pilipili.mall.entity.AdminUserToken;
import whut.pilipili.mall.entity.Carousel;
import whut.pilipili.mall.service.PilipiliMallCarouselService;
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
@Tag(description = "v1", name = "后台管理系统轮播图模块接口")
@RequestMapping("/manage-api/v1")
public class PilipiliAdminCarouselAPI {

    private static final Logger logger = LoggerFactory.getLogger(PilipiliAdminCarouselAPI.class);

    @Resource
    PilipiliMallCarouselService pilipiliMallCarouselService;

    /**
     * 列表
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.GET)
    @Operation(summary = "轮播图列表", description = "轮播图列表")
    public Result list(@RequestParam(required = false) @Parameter(description = "页码") Integer pageNumber,
                       @RequestParam(required = false) @Parameter(description = "每页条数") Integer pageSize, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        Map params = new HashMap(4);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(pilipiliMallCarouselService.getCarouselPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.POST)
    @Operation(summary = "新增轮播图", description = "新增轮播图")
    public Result save(@RequestBody @Valid CarouselAddParam carouselAddParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        Carousel carousel = new Carousel();
        BeanUtil.copyProperties(carouselAddParam, carousel);
        String result = pilipiliMallCarouselService.saveCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.PUT)
    @Operation(summary = "修改轮播图信息", description = "修改轮播图信息")
    public Result update(@RequestBody CarouselEditParam carouselEditParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        Carousel carousel = new Carousel();
        BeanUtil.copyProperties(carouselEditParam, carousel);
        String result = pilipiliMallCarouselService.updateCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/carousels/{id}", method = RequestMethod.GET)
    @Operation(summary = "获取单条轮播图信息", description = "根据id查询")
    public Result info(@PathVariable("id") Integer id, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        Carousel carousel = pilipiliMallCarouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.DELETE)
    @Operation(summary = "批量删除轮播图信息", description = "批量删除轮播图信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser @Parameter(hidden = true) AdminUserToken adminUser) {
        logger.info("adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (pilipiliMallCarouselService.deleteBatch(batchIdParam.getIds())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}