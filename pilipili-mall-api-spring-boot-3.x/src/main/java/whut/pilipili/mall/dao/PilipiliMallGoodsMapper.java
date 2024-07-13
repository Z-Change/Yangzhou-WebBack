
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.PilipiliMallGoods;
import whut.pilipili.mall.entity.StockNumDTO;
import whut.pilipili.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PilipiliMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(PilipiliMallGoods record);

    int insertSelective(PilipiliMallGoods record);

    PilipiliMallGoods selectByPrimaryKey(Long goodsId);

    PilipiliMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(PilipiliMallGoods record);

    int updateByPrimaryKeyWithBLOBs(PilipiliMallGoods record);

    int updateByPrimaryKey(PilipiliMallGoods record);

    List<PilipiliMallGoods> findPilipiliMallGoodsList(PageQueryUtil pageUtil);

    int getTotalPilipiliMallGoods(PageQueryUtil pageUtil);

    List<PilipiliMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<PilipiliMallGoods> findPilipiliMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalPilipiliMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("newBeeMallGoodsList") List<PilipiliMallGoods> pilipiliMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}