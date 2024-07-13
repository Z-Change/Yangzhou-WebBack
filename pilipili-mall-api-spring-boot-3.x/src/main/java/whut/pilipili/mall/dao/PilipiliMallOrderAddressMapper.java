
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.PilipiliMallOrderAddress;

public interface PilipiliMallOrderAddressMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(PilipiliMallOrderAddress record);

    int insertSelective(PilipiliMallOrderAddress record);

    PilipiliMallOrderAddress selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(PilipiliMallOrderAddress record);

    int updateByPrimaryKey(PilipiliMallOrderAddress record);
}