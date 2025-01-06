package com.example.excelmultiexportprogress.excelExportFramework;

import java.util.List;

public interface DataGetter {

    /**
     * 分页读取数据（需实现）
     * @param pageSize 分页大小
     * @param pageNum  页码（从1开始： 1、2、3、4、5 ......）
     * @param sqlFilterClass  用户传入的用于筛选的类，没传则为null（每次回调该函数，该类都会初始化为一开始传入的样子）
     * @return
     */
    public List<Object> readData(Integer pageSize,Integer pageNum, Object sqlFilterClass);

    /**
     * 分页数据总数（需实现）
     * @param sqlFilterClass  用户传入的用于筛选的类，没传则为null
     * @return
     */
    public Long countDataTotal(Object sqlFilterClass);

}
