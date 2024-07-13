
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.PilipiliMallShoppingCartItem;
import whut.pilipili.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PilipiliMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(PilipiliMallShoppingCartItem record);

    int insertSelective(PilipiliMallShoppingCartItem record);

    PilipiliMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    PilipiliMallShoppingCartItem selectByUserIdAndGoodsId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);

    List<PilipiliMallShoppingCartItem> selectByUserId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("number") int number);

    List<PilipiliMallShoppingCartItem> selectByUserIdAndCartItemIds(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("cartItemIds") List<Long> cartItemIds);

    int selectCountByUserId(Long newBeeMallUserId);

    int updateByPrimaryKeySelective(PilipiliMallShoppingCartItem record);

    int updateByPrimaryKey(PilipiliMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);

    List<PilipiliMallShoppingCartItem> findMyPilipiliMallCartItems(PageQueryUtil pageUtil);

    int getTotalMyPilipiliMallCartItems(PageQueryUtil pageUtil);
}