package com.example.excelmultiexportprogress.excelExportFrameworkImpl;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class TravelExpenseExtraInfo {

    @ColumnWidth(15)
    @ExcelProperty("编号")
    private String serialNumber;

    @ColumnWidth(8)
    @ExcelProperty("报销人")
    private String userName;

    @ColumnWidth(10)
    @ExcelProperty("部门")
    private String departName;

    @ColumnWidth(30)
    @ExcelProperty("项目名称")
    private String projectName;

    @ColumnWidth(15)
    @ExcelProperty("项目编号")
    private String projectCode;

    @ColumnWidth(30)
    @ExcelProperty("客户名称")
    private String customerName;

    @ColumnWidth(20)
    @ExcelProperty("订单编号")
    private String orderNumber;

    @ColumnWidth(30)
    @ExcelProperty("备注")
    private String remarks;

    @ColumnWidth(25)
    @ExcelProperty("发生日期")
    private String occurTime;

    @ColumnWidth(15)
    @ExcelProperty("费用类型")
    private String expenseTypeName;

    @ExcelProperty("金额")
    @ColumnWidth(8)
    private BigDecimal amount;

}
