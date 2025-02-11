
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.Carousel;
import whut.pilipili.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselMapper {

    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel record);

    int insertSelective(Carousel record);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimaryKey(Carousel record);

    List<Carousel> findCarouselList(PageQueryUtil pageUtil);

    int getTotalCarousels(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids);

    List<Carousel> findCarouselsByNum(@Param("number") int number);
}