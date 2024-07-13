
package whut.pilipili.mall.service;

import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderDetailVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallOrderItemVO;
import whut.pilipili.mall.api.mall.vo.PilipiliMallShoppingCartItemVO;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.entity.MallUserAddress;
import whut.pilipili.mall.entity.PilipiliMallOrder;
import whut.pilipili.mall.util.PageQueryUtil;
import whut.pilipili.mall.util.PageResult;

import java.util.List;

public interface PilipiliMallOrderService {
    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    PilipiliMallOrderDetailVO getOrderDetailByOrderId(Long orderId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    PilipiliMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    String saveOrder(MallUser loginMallUser, MallUserAddress address, List<PilipiliMallShoppingCartItemVO> itemsForSave);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getPilipiliMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param pilipiliMallOrder
     * @return
     */
    String updateOrderInfo(PilipiliMallOrder pilipiliMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    List<PilipiliMallOrderItemVO> getOrderItems(Long orderId);
}
