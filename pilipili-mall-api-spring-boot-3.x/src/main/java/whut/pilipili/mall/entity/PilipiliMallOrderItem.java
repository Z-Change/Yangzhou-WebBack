
package whut.pilipili.mall.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PilipiliMallOrderItem {
    private Long orderItemId;

    private Long orderId;

    private Long goodsId;

    private String goodsName;

    private String goodsCoverImg;

    private Float sellingPrice;

    private Integer goodsCount;

    private Date createTime;
}