
package whut.pilipili.mall.service.impl;

import whut.pilipili.mall.api.mall.vo.PilipiliMallSearchGoodsVO;
import whut.pilipili.mall.common.PilipiliMallCategoryLevelEnum;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.dao.GoodsCategoryMapper;
import whut.pilipili.mall.dao.PilipiliMallGoodsMapper;
import whut.pilipili.mall.entity.GoodsCategory;
import whut.pilipili.mall.entity.PilipiliMallGoods;
import whut.pilipili.mall.service.PilipiliMallGoodsService;
import whut.pilipili.mall.util.BeanUtil;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PilipiliMallGoodsServiceImpl implements PilipiliMallGoodsService {

    @Autowired
    private PilipiliMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getPilipiliMallGoodsPage(PageQueryUtil pageUtil) {
        List<PilipiliMallGoods> goodsList = goodsMapper.findPilipiliMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalPilipiliMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String savePilipiliMallGoods(PilipiliMallGoods goods) {
        goods.setGoodsCarousel(goods.getGoodsCoverImg());
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != PilipiliMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSavePilipiliMallGoods(List<PilipiliMallGoods> pilipiliMallGoodsList) {
        if (!CollectionUtils.isEmpty(pilipiliMallGoodsList)) {
            goodsMapper.batchInsert(pilipiliMallGoodsList);
        }
    }

    @Override
    public String updatePilipiliMallGoods(PilipiliMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != PilipiliMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        PilipiliMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        PilipiliMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PilipiliMallGoods getPilipiliMallGoodsById(Long id) {
        PilipiliMallGoods pilipiliMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (pilipiliMallGoods == null) {
            PilipiliMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return pilipiliMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchPilipiliMallGoods(PageQueryUtil pageUtil) {
        List<PilipiliMallGoods> goodsList = goodsMapper.findPilipiliMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalPilipiliMallGoodsBySearch(pageUtil);
        List<PilipiliMallSearchGoodsVO> pilipiliMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            pilipiliMallSearchGoodsVOS = BeanUtil.copyList(goodsList, PilipiliMallSearchGoodsVO.class);
            for (PilipiliMallSearchGoodsVO pilipiliMallSearchGoodsVO : pilipiliMallSearchGoodsVOS) {
                String goodsName = pilipiliMallSearchGoodsVO.getGoodsName();
                String goodsIntro = pilipiliMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    pilipiliMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 100) {
                    goodsIntro = goodsIntro.substring(0, 100) + "...";
                    pilipiliMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(pilipiliMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
