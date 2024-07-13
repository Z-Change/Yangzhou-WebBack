
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.PilipiliMallOrder;
import whut.pilipili.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PilipiliMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(PilipiliMallOrder record);

    int insertSelective(PilipiliMallOrder record);

    PilipiliMallOrder selectByPrimaryKey(Long orderId);

    PilipiliMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(PilipiliMallOrder record);

    int updateByPrimaryKey(PilipiliMallOrder record);

    List<PilipiliMallOrder> findPilipiliMallOrderList(PageQueryUtil pageUtil);

    int getTotalPilipiliMallOrders(PageQueryUtil pageUtil);

    List<PilipiliMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}