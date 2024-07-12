/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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
    void batchSaveNewBeeMallGoods(List<PilipiliMallGoods> pilipiliMallGoodsList);

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
