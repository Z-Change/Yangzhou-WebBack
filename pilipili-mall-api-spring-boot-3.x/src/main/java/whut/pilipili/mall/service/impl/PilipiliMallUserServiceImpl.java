
package whut.pilipili.mall.service.impl;

import whut.pilipili.mall.api.mall.param.MallUserUpdateParam;
import whut.pilipili.mall.common.Constants;
import whut.pilipili.mall.common.PilipiliMallException;
import whut.pilipili.mall.common.ServiceResultEnum;
import whut.pilipili.mall.dao.MallUserMapper;
import whut.pilipili.mall.dao.PilipiliMallUserTokenMapper;
import whut.pilipili.mall.entity.MallUser;
import whut.pilipili.mall.entity.MallUserToken;
import whut.pilipili.mall.service.PilipiliMallUserService;
import whut.pilipili.mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whut.pilipili.mall.util.*;

import java.util.Date;
import java.util.List;

@Service
public class PilipiliMallUserServiceImpl implements PilipiliMallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private PilipiliMallUserTokenMapper pilipiliMallUserTokenMapper;

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setIntroduceSign(Constants.USER_INTRO);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", user.getUserId());
            MallUserToken mallUserToken = pilipiliMallUserTokenMapper.selectByPrimaryKey(user.getUserId());
            //当前时间
            Date now = new Date();
            //过期时间
            Date expireTime = new Date(now.getTime() + 2 * 24 * 3600 * 1000);//过期时间 48 小时
            if (mallUserToken == null) {
                mallUserToken = new MallUserToken();
                mallUserToken.setUserId(user.getUserId());
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //新增一条token数据
                if (pilipiliMallUserTokenMapper.insertSelective(mallUserToken) > 0) {
                    //新增成功后返回
                    return token;
                }
            } else {
                mallUserToken.setToken(token);
                mallUserToken.setUpdateTime(now);
                mallUserToken.setExpireTime(expireTime);
                //更新
                if (pilipiliMallUserTokenMapper.updateByPrimaryKeySelective(mallUserToken) > 0) {
                    //修改成功后返回
                    return token;
                }
            }

        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public Boolean updateUserInfo(MallUserUpdateParam mallUser, Long userId) {
        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            PilipiliMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUser.getNickName());
        //user.setPasswordMd5(mallUser.getPasswordMd5());
        //若密码为空字符，则表明用户不打算修改密码，使用原密码保存
        if (!MD5Util.MD5Encode("", "UTF-8").equals(mallUser.getPasswordMd5())){
            user.setPasswordMd5(mallUser.getPasswordMd5());
        }
        user.setIntroduceSign(mallUser.getIntroduceSign());
        if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean logout(Long userId) {
        return pilipiliMallUserTokenMapper.deleteByPrimaryKey(userId) > 0;
    }

    @Override
    public PageResult getPilipiliMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean lockUsers(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
