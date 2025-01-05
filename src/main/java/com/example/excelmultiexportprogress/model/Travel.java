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

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "depart_id")
    private String departId;

    @TableField(value = "depart_name")
    private String departName;

    /**
     * 编号
     */
    @TableField(value = "serial_number")
    private String serialNumber;

    /**
     * 项目编号
     */
    @TableField(value = "project_code")
    private String projectCode;

    /**
     * 项目编号
     */
    @TableField(value = "project_name")
    private String projectName;

    /**
     * 金额
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 申请状态
     */
    @TableField(value = "travel_status")
    private Integer travelStatus;

    /**
     * 发生日期
     */
    @TableField(value = "occur_time")
    private String occurTime;

    /**
     * 提交时间
     */
    @TableField(value = "create_time")
    private String createTime;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
