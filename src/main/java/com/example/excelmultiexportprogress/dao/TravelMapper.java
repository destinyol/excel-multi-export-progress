package com.example.excelmultiexportprogress.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.excelmultiexportprogress.excelExportFrameworkImpl.TravelExpenseExtraInfo;
import com.example.excelmultiexportprogress.model.Travel;
import com.example.excelmultiexportprogress.model.TravelSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TravelMapper extends BaseMapper<Travel> {

    List<TravelExpenseExtraInfo> getTravelList(TravelSearchDto travelSearchDto);
    Long getTravelListCount(TravelSearchDto travelSearchDto);

}
