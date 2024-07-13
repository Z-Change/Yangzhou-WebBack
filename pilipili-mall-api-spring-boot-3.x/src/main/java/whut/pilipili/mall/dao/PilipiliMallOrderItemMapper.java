
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.PilipiliMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PilipiliMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(PilipiliMallOrderItem record);

    int insertSelective(PilipiliMallOrderItem record);

    PilipiliMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<PilipiliMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<PilipiliMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<PilipiliMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(PilipiliMallOrderItem record);

    int updateByPrimaryKey(PilipiliMallOrderItem record);
}