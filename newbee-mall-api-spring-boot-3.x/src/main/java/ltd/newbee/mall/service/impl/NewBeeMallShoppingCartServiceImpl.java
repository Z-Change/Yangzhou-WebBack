/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.api.mall.param.SaveCartItemParam;
import ltd.newbee.mall.api.mall.param.UpdateCartItemParam;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.PilipiliMallException;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.dao.NewBeeMallShoppingCartItemMapper;
import ltd.newbee.mall.entity.PilipiliMallGoods;
import ltd.newbee.mall.entity.PilipiliMallShoppingCartItem;
import ltd.newbee.mall.service.NewBeeMallShoppingCartService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewBeeMallShoppingCartServiceImpl implements NewBeeMallShoppingCartService {

    @Autowired
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;

    @Autowired
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    @Override
    public String saveNewBeeMallCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        PilipiliMallShoppingCartItem temp = newBeeMallShoppingCartItemMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            PilipiliMallException.fail(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        PilipiliMallGoods pilipiliMallGoods = newBeeMallGoodsMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        //商品为空
        if (pilipiliMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = newBeeMallShoppingCartItemMapper.selectCountByUserId(userId);
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() < 1) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult();
        }
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItem = new PilipiliMallShoppingCartItem();
        BeanUtil.copyProperties(saveCartItemParam, pilipiliMallShoppingCartItem);
        pilipiliMallShoppingCartItem.setUserId(userId);
        //保存记录
        if (newBeeMallShoppingCartItemMapper.insertSelective(pilipiliMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateNewBeeMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItemUpdate = newBeeMallShoppingCartItemMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
        if (pilipiliMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (!pilipiliMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            PilipiliMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        //超出单个商品的最大数量
        if (updateCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!pilipiliMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (updateCartItemParam.getGoodsCount().equals(pilipiliMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        pilipiliMallShoppingCartItemUpdate.setGoodsCount(updateCartItemParam.getGoodsCount());
        pilipiliMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (newBeeMallShoppingCartItemMapper.updateByPrimaryKeySelective(pilipiliMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PilipiliMallShoppingCartItem getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId) {
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItem = newBeeMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
        if (pilipiliMallShoppingCartItem == null) {
            PilipiliMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return pilipiliMallShoppingCartItem;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItem = newBeeMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (pilipiliMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(pilipiliMallShoppingCartItem.getUserId())) {
            return false;
        }
        return newBeeMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<PilipiliMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId) {
        List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS = new ArrayList<>();
        List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems = newBeeMallShoppingCartItemMapper.selectByUserId(newBeeMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getNewBeeMallShoppingCartItemVOS(pilipiliMallShoppingCartItemVOS, pilipiliMallShoppingCartItems);
    }

    @Override
    public List<PilipiliMallShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long newBeeMallUserId) {
        List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartItemIds)) {
            PilipiliMallException.fail("购物项不能为空");
        }
        List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems = newBeeMallShoppingCartItemMapper.selectByUserIdAndCartItemIds(newBeeMallUserId, cartItemIds);
        if (CollectionUtils.isEmpty(pilipiliMallShoppingCartItems)) {
            PilipiliMallException.fail("购物项不能为空");
        }
        if (pilipiliMallShoppingCartItems.size() != cartItemIds.size()) {
            PilipiliMallException.fail("参数异常");
        }
        return getNewBeeMallShoppingCartItemVOS(pilipiliMallShoppingCartItemVOS, pilipiliMallShoppingCartItems);
    }

    /**
     * 数据转换
     *
     * @param pilipiliMallShoppingCartItemVOS
     * @param pilipiliMallShoppingCartItems
     * @return
     */
    private List<PilipiliMallShoppingCartItemVO> getNewBeeMallShoppingCartItemVOS(List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS, List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems) {
        if (!CollectionUtils.isEmpty(pilipiliMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = pilipiliMallShoppingCartItems.stream().map(PilipiliMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<PilipiliMallGoods> pilipiliMallGoods = newBeeMallGoodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, PilipiliMallGoods> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(pilipiliMallGoods)) {
                newBeeMallGoodsMap = pilipiliMallGoods.stream().collect(Collectors.toMap(PilipiliMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (PilipiliMallShoppingCartItem pilipiliMallShoppingCartItem : pilipiliMallShoppingCartItems) {
                PilipiliMallShoppingCartItemVO pilipiliMallShoppingCartItemVO = new PilipiliMallShoppingCartItemVO();
                BeanUtil.copyProperties(pilipiliMallShoppingCartItem, pilipiliMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(pilipiliMallShoppingCartItem.getGoodsId())) {
                    PilipiliMallGoods pilipiliMallGoodsTemp = newBeeMallGoodsMap.get(pilipiliMallShoppingCartItem.getGoodsId());
                    pilipiliMallShoppingCartItemVO.setGoodsCoverImg(pilipiliMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = pilipiliMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    pilipiliMallShoppingCartItemVO.setGoodsName(goodsName);
                    pilipiliMallShoppingCartItemVO.setSellingPrice(pilipiliMallGoodsTemp.getSellingPrice());
                    pilipiliMallShoppingCartItemVOS.add(pilipiliMallShoppingCartItemVO);
                }
            }
        }
        return pilipiliMallShoppingCartItemVOS;
    }

    @Override
    public PageResult getMyShoppingCartItems(PageQueryUtil pageUtil) {
        List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS = new ArrayList<>();
        List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems = newBeeMallShoppingCartItemMapper.findMyNewBeeMallCartItems(pageUtil);
        int total = newBeeMallShoppingCartItemMapper.getTotalMyNewBeeMallCartItems(pageUtil);
        PageResult pageResult = new PageResult(getNewBeeMallShoppingCartItemVOS(pilipiliMallShoppingCartItemVOS, pilipiliMallShoppingCartItems), total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
