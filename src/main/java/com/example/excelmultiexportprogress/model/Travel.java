package com.example.excelmultiexportprogress.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName(value ="td_travel")
@Data
public class Travel implements Serializable {
    /**
     *
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private String userId;

    @TableField(value = "remarks")
    private String remarks;

    @TableField(exist = false)
    private String userName;

    /**
     * 编号
     */
    @TableField(value = "serial_number")
    private String serialNumber;

    /**
     * 金额合计(大写)
     */
    @TableField(value = "amount_in_words")
    private String amountInWords;
    @TableField(value = "ticket_status")
    private Integer ticketStatus;

    /**
     * 价税合计(小写)
     */
    @TableField(value = "amount_in_figures")
    private BigDecimal amountInFigures;

    /**
     * 发票金额
     */
    @TableField(value = "invoice_amount")
    private BigDecimal invoiceAmount;

    /**
     * 差旅费金额
     */
    @TableField(value = "travel_amount")
    private BigDecimal travelAmount;

    /**
     * 申请状态
     */
    @TableField(value = "travel_status")
    private Integer travelStatus;


    /**
     * 是否撤销申请 1 是 0 否
     */
    @TableField(value = "type_status")
    private Integer typeStatus;
    /**
     * 撤销原因
     */
    @TableField(value = "revoke_remark")
    private String revokeRemark;

    /**
     * 提交时间
     */
    @TableField(value = "create_time")
    private String createTime;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
