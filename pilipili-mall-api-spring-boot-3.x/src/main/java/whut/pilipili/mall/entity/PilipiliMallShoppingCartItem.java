
package whut.pilipili.mall.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PilipiliMallShoppingCartItem {
    private Long cartItemId;

    private Long userId;

    private Long goodsId;

    private Integer goodsCount;

    private Byte isDeleted;

    private Date createTime;

    private Date updateTime;
}