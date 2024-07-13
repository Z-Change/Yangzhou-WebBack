
package whut.pilipili.mall.service;

import whut.pilipili.mall.api.mall.vo.PilipiliMallIndexCarouselVO;
import whut.pilipili.mall.entity.Carousel;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;

import java.util.List;

public interface PilipiliMallCarouselService {

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<PilipiliMallIndexCarouselVO> getCarouselsForIndex(int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Long[] ids);
}
