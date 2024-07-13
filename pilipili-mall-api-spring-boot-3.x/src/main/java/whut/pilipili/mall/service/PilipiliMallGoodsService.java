
package whut.pilipili.mall.service;

import whut.pilipili.mall.entity.PilipiliMallGoods;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;

import java.util.List;

public interface PilipiliMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getPilipiliMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String savePilipiliMallGoods(PilipiliMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param pilipiliMallGoodsList
     * @return
     */
    void batchSavePilipiliMallGoods(List<PilipiliMallGoods> pilipiliMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updatePilipiliMallGoods(PilipiliMallGoods goods);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    PilipiliMallGoods getPilipiliMallGoodsById(Long id);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchPilipiliMallGoods(PageQueryUtil pageUtil);
}
