/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package whut.pilipili.mall.service.impl;

import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderDetailVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderItemVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderListVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import whut.pilipili.mall.common.*;
import whut.pilipili.mall.dao.*;
import whut.pilipili.mall.entity.*;
import whut.pilipili.mall.common.*;
import whut.pilipili.mall.dao.*;
import whut.pilipili.mall.entity.*;
import whut.pilipili.mall.service.PilipiliMallOrderService;
import whut.pilipili.mall.util.BeanUtil;
import whut.pilipili.mall.util.NumberUtil;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class PilipiliMallOrderServiceImpl implements PilipiliMallOrderService {

    @Autowired
    private PilipiliMallOrderMapper pilipiliMallOrderMapper;
    @Autowired
    private PilipiliMallOrderItemMapper pilipiliMallOrderItemMapper;
    @Autowired
    private PilipiliMallShoppingCartItemMapper pilipiliMallShoppingCartItemMapper;
    @Autowired
    private PilipiliMallGoodsMapper pilipiliMallGoodsMapper;
    @Autowired
    private PilipiliMallOrderAddressMapper pilipiliMallOrderAddressMapper;

    @Override
    public PilipiliMallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        PilipiliMallOrder pilipiliMallOrder = pilipiliMallOrderMapper.selectByPrimaryKey(orderId);
        if (pilipiliMallOrder == null) {
            PilipiliMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<PilipiliMallOrderItem> orderItems = pilipiliMallOrderItemMapper.selectByOrderId(pilipiliMallOrder.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            List<PilipiliMallOrderItemVO> pilipiliMallOrderItemVOS = BeanUtil.copyList(orderItems, PilipiliMallOrderItemVO.class);
            PilipiliMallOrderDetailVO pilipiliMallOrderDetailVO = new PilipiliMallOrderDetailVO();
            BeanUtil.copyProperties(pilipiliMallOrder, pilipiliMallOrderDetailVO);
            pilipiliMallOrderDetailVO.setOrderStatusString(PilipiliMallOrderStatusEnum.getPilipiliMallOrderStatusEnumByStatus(pilipiliMallOrderDetailVO.getOrderStatus()).getName());
            pilipiliMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(pilipiliMallOrderDetailVO.getPayType()).getName());
            pilipiliMallOrderDetailVO.setPilipiliMallOrderItemVOS(pilipiliMallOrderItemVOS);
            return pilipiliMallOrderDetailVO;
        } else {
            PilipiliMallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }

    @Override
    public PilipiliMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        PilipiliMallOrder pilipiliMallOrder = pilipiliMallOrderMapper.selectByOrderNo(orderNo);
        if (pilipiliMallOrder == null) {
            PilipiliMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!userId.equals(pilipiliMallOrder.getUserId())) {
            PilipiliMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<PilipiliMallOrderItem> orderItems = pilipiliMallOrderItemMapper.selectByOrderId(pilipiliMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            PilipiliMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<PilipiliMallOrderItemVO> pilipiliMallOrderItemVOS = BeanUtil.copyList(orderItems, PilipiliMallOrderItemVO.class);
        PilipiliMallOrderDetailVO pilipiliMallOrderDetailVO = new PilipiliMallOrderDetailVO();
        BeanUtil.copyProperties(pilipiliMallOrder, pilipiliMallOrderDetailVO);
        pilipiliMallOrderDetailVO.setOrderStatusString(PilipiliMallOrderStatusEnum.getPilipiliMallOrderStatusEnumByStatus(pilipiliMallOrderDetailVO.getOrderStatus()).getName());
        pilipiliMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(pilipiliMallOrderDetailVO.getPayType()).getName());
        pilipiliMallOrderDetailVO.setPilipiliMallOrderItemVOS(pilipiliMallOrderItemVOS);
        return pilipiliMallOrderDetailVO;
    }


    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = pilipiliMallOrderMapper.getTotalPilipiliMallOrders(pageUtil);
        List<PilipiliMallOrder> pilipiliMallOrders = pilipiliMallOrderMapper.findPilipiliMallOrderList(pageUtil);
        List<PilipiliMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(pilipiliMallOrders, PilipiliMallOrderListVO.class);
            //设置订单状态中文显示值
            for (PilipiliMallOrderListVO pilipiliMallOrderListVO : orderListVOS) {
                pilipiliMallOrderListVO.setOrderStatusString(PilipiliMallOrderStatusEnum.getPilipiliMallOrderStatusEnumByStatus(pilipiliMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = pilipiliMallOrders.stream().map(PilipiliMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<PilipiliMallOrderItem> orderItems = pilipiliMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<PilipiliMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(PilipiliMallOrderItem::getOrderId));
                for (PilipiliMallOrderListVO pilipiliMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(pilipiliMallOrderListVO.getOrderId())) {
                        List<PilipiliMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(pilipiliMallOrderListVO.getOrderId());
                        //将PilipiliMallOrderItem对象列表转换成PilipiliMallOrderItemVO对象列表
                        List<PilipiliMallOrderItemVO> pilipiliMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, PilipiliMallOrderItemVO.class);
                        pilipiliMallOrderListVO.setPilipiliMallOrderItemVOS(pilipiliMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String cancelOrder(String orderNo, Long userId) {
        PilipiliMallOrder pilipiliMallOrder = pilipiliMallOrderMapper.selectByOrderNo(orderNo);
        if (pilipiliMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(pilipiliMallOrder.getUserId())) {
                PilipiliMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (pilipiliMallOrder.getOrderStatus().intValue() == PilipiliMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || pilipiliMallOrder.getOrderStatus().intValue() == PilipiliMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || pilipiliMallOrder.getOrderStatus().intValue() == PilipiliMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || pilipiliMallOrder.getOrderStatus().intValue() == PilipiliMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单状态&&恢复库存
            if (pilipiliMallOrderMapper.closeOrder(Collections.singletonList(pilipiliMallOrder.getOrderId()), PilipiliMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0 && recoverStockNum(Collections.singletonList(pilipiliMallOrder.getOrderId()))) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        PilipiliMallOrder pilipiliMallOrder = pilipiliMallOrderMapper.selectByOrderNo(orderNo);
        if (pilipiliMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(pilipiliMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (pilipiliMallOrder.getOrderStatus().intValue() != PilipiliMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            pilipiliMallOrder.setOrderStatus((byte) PilipiliMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            pilipiliMallOrder.setUpdateTime(new Date());
            if (pilipiliMallOrderMapper.updateByPrimaryKeySelective(pilipiliMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        PilipiliMallOrder pilipiliMallOrder = pilipiliMallOrderMapper.selectByOrderNo(orderNo);
        if (pilipiliMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (pilipiliMallOrder.getOrderStatus().intValue() != PilipiliMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            pilipiliMallOrder.setOrderStatus((byte) PilipiliMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            pilipiliMallOrder.setPayType((byte) payType);
            pilipiliMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            pilipiliMallOrder.setPayTime(new Date());
            pilipiliMallOrder.setUpdateTime(new Date());
            if (pilipiliMallOrderMapper.updateByPrimaryKeySelective(pilipiliMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUser loginMallUser, MallUserAddress address, List<PilipiliMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(PilipiliMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(PilipiliMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<PilipiliMallGoods> pilipiliMallGoods = pilipiliMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<PilipiliMallGoods> goodsListNotSelling = pilipiliMallGoods.stream()
                .filter(newBeeMallGoodsTemp -> newBeeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            PilipiliMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, PilipiliMallGoods> newBeeMallGoodsMap = pilipiliMallGoods.stream().collect(Collectors.toMap(PilipiliMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (PilipiliMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                PilipiliMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                PilipiliMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(pilipiliMallGoods)) {
            if (pilipiliMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = pilipiliMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    PilipiliMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                float priceTotal = 0;
                //保存订单
                PilipiliMallOrder pilipiliMallOrder = new PilipiliMallOrder();
                pilipiliMallOrder.setOrderNo(orderNo);
                pilipiliMallOrder.setUserId(loginMallUser.getUserId());
                //总价
                for (PilipiliMallShoppingCartItemVO pilipiliMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += pilipiliMallShoppingCartItemVO.getGoodsCount() * pilipiliMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    PilipiliMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                pilipiliMallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                pilipiliMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (pilipiliMallOrderMapper.insertSelective(pilipiliMallOrder) > 0) {
                    //生成订单收货地址快照，并保存至数据库
                    PilipiliMallOrderAddress pilipiliMallOrderAddress = new PilipiliMallOrderAddress();
                    BeanUtil.copyProperties(address, pilipiliMallOrderAddress);
                    pilipiliMallOrderAddress.setOrderId(pilipiliMallOrder.getOrderId());
                    //生成所有的订单项快照，并保存至数据库
                    List<PilipiliMallOrderItem> pilipiliMallOrderItems = new ArrayList<>();
                    for (PilipiliMallShoppingCartItemVO pilipiliMallShoppingCartItemVO : myShoppingCartItems) {
                        PilipiliMallOrderItem pilipiliMallOrderItem = new PilipiliMallOrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(pilipiliMallShoppingCartItemVO, pilipiliMallOrderItem);
                        //PilipiliMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        pilipiliMallOrderItem.setOrderId(pilipiliMallOrder.getOrderId());
                        pilipiliMallOrderItems.add(pilipiliMallOrderItem);
                    }
                    //保存至数据库
                    if (pilipiliMallOrderItemMapper.insertBatch(pilipiliMallOrderItems) > 0 && pilipiliMallOrderAddressMapper.insertSelective(pilipiliMallOrderAddress) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    PilipiliMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                PilipiliMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            PilipiliMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        PilipiliMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public PageResult getPilipiliMallOrdersPage(PageQueryUtil pageUtil) {
        List<PilipiliMallOrder> pilipiliMallOrders = pilipiliMallOrderMapper.findPilipiliMallOrderList(pageUtil);
        int total = pilipiliMallOrderMapper.getTotalPilipiliMallOrders(pageUtil);
        PageResult pageResult = new PageResult(pilipiliMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(PilipiliMallOrder pilipiliMallOrder) {
        PilipiliMallOrder temp = pilipiliMallOrderMapper.selectByPrimaryKey(pilipiliMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(pilipiliMallOrder.getTotalPrice());
            temp.setUpdateTime(new Date());
            if (pilipiliMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<PilipiliMallOrder> orders = pilipiliMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (PilipiliMallOrder pilipiliMallOrder : orders) {
                if (pilipiliMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += pilipiliMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (pilipiliMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += pilipiliMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (pilipiliMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<PilipiliMallOrder> orders = pilipiliMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (PilipiliMallOrder pilipiliMallOrder : orders) {
                if (pilipiliMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += pilipiliMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (pilipiliMallOrder.getOrderStatus() != 1 && pilipiliMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += pilipiliMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (pilipiliMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<PilipiliMallOrder> orders = pilipiliMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (PilipiliMallOrder pilipiliMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (pilipiliMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += pilipiliMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (pilipiliMallOrder.getOrderStatus() == 4 || pilipiliMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += pilipiliMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间&&恢复库存
                if (pilipiliMallOrderMapper.closeOrder(Arrays.asList(ids), PilipiliMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0 && recoverStockNum(Arrays.asList(ids))) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<PilipiliMallOrderItemVO> getOrderItems(Long orderId) {
        PilipiliMallOrder pilipiliMallOrder = pilipiliMallOrderMapper.selectByPrimaryKey(orderId);
        if (pilipiliMallOrder != null) {
            List<PilipiliMallOrderItem> orderItems = pilipiliMallOrderItemMapper.selectByOrderId(pilipiliMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<PilipiliMallOrderItemVO> pilipiliMallOrderItemVOS = BeanUtil.copyList(orderItems, PilipiliMallOrderItemVO.class);
                return pilipiliMallOrderItemVOS;
            }
        }
        return null;
    }

    /**
     * 恢复库存
     *
     * @param orderIds
     * @return
     */
    public Boolean recoverStockNum(List<Long> orderIds) {
        //查询对应的订单项
        List<PilipiliMallOrderItem> pilipiliMallOrderItems = pilipiliMallOrderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(pilipiliMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        int updateStockNumResult = pilipiliMallGoodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            PilipiliMallException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }
    }
}
