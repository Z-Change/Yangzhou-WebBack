
package whut.pilipili.mall.api.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情页页面VO
 */
@Data
public class PilipiliMallOrderDetailVO implements Serializable {

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "订单价格")
    private Float totalPrice;

    @Schema(title = "订单支付状态码")
    private Byte payStatus;

    @Schema(title = "订单支付方式")
    private Byte payType;

    @Schema(title = "订单支付方式")
    private String payTypeString;

    @Schema(title = "订单支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    @Schema(title = "订单状态码")
    private Byte orderStatus;

    @Schema(title = "订单状态")
    private String orderStatusString;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(title = "订单项列表")
    private List<PilipiliMallOrderItemVO> pilipiliMallOrderItemVOS;
}
