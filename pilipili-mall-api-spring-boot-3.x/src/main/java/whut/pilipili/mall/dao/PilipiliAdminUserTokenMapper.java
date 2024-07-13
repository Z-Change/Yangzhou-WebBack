
package whut.pilipili.mall.dao;

import whut.pilipili.mall.entity.AdminUserToken;

public interface PilipiliAdminUserTokenMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(AdminUserToken record);

    int insertSelective(AdminUserToken record);

    AdminUserToken selectByPrimaryKey(Long userId);

    AdminUserToken selectByToken(String token);

    int updateByPrimaryKeySelective(AdminUserToken record);

    int updateByPrimaryKey(AdminUserToken record);
}