/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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