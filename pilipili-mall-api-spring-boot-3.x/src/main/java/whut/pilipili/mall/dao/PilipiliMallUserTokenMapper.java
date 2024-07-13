
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.MallUserToken;

public interface PilipiliMallUserTokenMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(MallUserToken record);

    int insertSelective(MallUserToken record);

    MallUserToken selectByPrimaryKey(Long userId);

    MallUserToken selectByToken(String token);

    int updateByPrimaryKeySelective(MallUserToken record);

    int updateByPrimaryKey(MallUserToken record);
}