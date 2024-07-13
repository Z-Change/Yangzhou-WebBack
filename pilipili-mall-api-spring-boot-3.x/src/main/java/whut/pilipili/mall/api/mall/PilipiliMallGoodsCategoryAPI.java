
package whut.pilipili.mall.api.mall;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.api.mall.vo.PilipiliMallIndexCategoryVO;
import whut.pilipili.mall.service.PilipiliMallCategoryService;
import whut.pilipili.mall.util.Result;
import whut.pilipili.mall.util.ResultGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.util.List;

@RestController
@Tag(description = "v1", name = "新蜂商城分类页面接口")
@RequestMapping("/api/v1")
public class PilipiliMallGoodsCategoryAPI {

    @Resource
    private PilipiliMallCategoryService pilipiliMallCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "获取分类数据", description = "分类页面使用")
    public Result<List<PilipiliMallIndexCategoryVO>> getCategories() {
        List<PilipiliMallIndexCategoryVO> categories = pilipiliMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            PilipiliMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(categories);
    }
}
