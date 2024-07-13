
package whut.pilipili.mall.service.impl;

import whut.pilipili.mall.api.mall.param.SaveCartItemParam;
import whut.pilipili.mall.api.mall.param.UpdateCartItemParam;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import whut.pilipili.mall.dao.PilipiliMallGoodsMapper;
import whut.pilipili.mall.dao.PilipiliMallShoppingCartItemMapper;
import whut.pilipili.mall.entity.PilipiliMallGoods;
import whut.pilipili.mall.entity.PilipiliMallShoppingCartItem;
import whut.pilipili.mall.service.PilipiliMallShoppingCartService;
import whut.pilipili.mall.util.BeanUtil;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PilipiliMallShoppingCartServiceImpl implements PilipiliMallShoppingCartService {

    @Autowired
    private PilipiliMallShoppingCartItemMapper pilipiliMallShoppingCartItemMapper;

    @Autowired
    private PilipiliMallGoodsMapper pilipiliMallGoodsMapper;

    @Override
    public String savePilipiliMallCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        PilipiliMallShoppingCartItem temp = pilipiliMallShoppingCartItemMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            PilipiliMallException.fail(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        PilipiliMallGoods pilipiliMallGoods = pilipiliMallGoodsMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        //商品为空
        if (pilipiliMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = pilipiliMallShoppingCartItemMapper.selectCountByUserId(userId);
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
        if (pilipiliMallShoppingCartItemMapper.insertSelective(pilipiliMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updatePilipiliMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItemUpdate = pilipiliMallShoppingCartItemMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
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
        if (pilipiliMallShoppingCartItemMapper.updateByPrimaryKeySelective(pilipiliMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PilipiliMallShoppingCartItem getPilipiliMallCartItemById(Long newBeeMallShoppingCartItemId) {
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItem = pilipiliMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
        if (pilipiliMallShoppingCartItem == null) {
            PilipiliMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return pilipiliMallShoppingCartItem;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        PilipiliMallShoppingCartItem pilipiliMallShoppingCartItem = pilipiliMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (pilipiliMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(pilipiliMallShoppingCartItem.getUserId())) {
            return false;
        }
        return pilipiliMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<PilipiliMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId) {
        List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS = new ArrayList<>();
        List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems = pilipiliMallShoppingCartItemMapper.selectByUserId(newBeeMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getPilipiliMallShoppingCartItemVOS(pilipiliMallShoppingCartItemVOS, pilipiliMallShoppingCartItems);
    }

    @Override
    public List<PilipiliMallShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long newBeeMallUserId) {
        List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartItemIds)) {
            PilipiliMallException.fail("购物项不能为空");
        }
        List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems = pilipiliMallShoppingCartItemMapper.selectByUserIdAndCartItemIds(newBeeMallUserId, cartItemIds);
        if (CollectionUtils.isEmpty(pilipiliMallShoppingCartItems)) {
            PilipiliMallException.fail("购物项不能为空");
        }
        if (pilipiliMallShoppingCartItems.size() != cartItemIds.size()) {
            PilipiliMallException.fail("参数异常");
        }
        return getPilipiliMallShoppingCartItemVOS(pilipiliMallShoppingCartItemVOS, pilipiliMallShoppingCartItems);
    }

    /**
     * 数据转换
     *
     * @param pilipiliMallShoppingCartItemVOS
     * @param pilipiliMallShoppingCartItems
     * @return
     */
    private List<PilipiliMallShoppingCartItemVO> getPilipiliMallShoppingCartItemVOS(List<PilipiliMallShoppingCartItemVO> pilipiliMallShoppingCartItemVOS, List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems) {
        if (!CollectionUtils.isEmpty(pilipiliMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = pilipiliMallShoppingCartItems.stream().map(PilipiliMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<PilipiliMallGoods> pilipiliMallGoods = pilipiliMallGoodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
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
        List<PilipiliMallShoppingCartItem> pilipiliMallShoppingCartItems = pilipiliMallShoppingCartItemMapper.findMyPilipiliMallCartItems(pageUtil);
        int total = pilipiliMallShoppingCartItemMapper.getTotalMyPilipiliMallCartItems(pageUtil);
        PageResult pageResult = new PageResult(getPilipiliMallShoppingCartItemVOS(pilipiliMallShoppingCartItemVOS, pilipiliMallShoppingCartItems), total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
