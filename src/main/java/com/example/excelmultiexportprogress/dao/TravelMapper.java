package com.example.excelmultiexportprogress.dao;

import com.example.excelmultiexportprogress.excelExportFrameworkImpl.TravelExpenseExtraInfo;
import com.example.excelmultiexportprogress.model.TravelSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TravelMapper {

    List<TravelExpenseExtraInfo> getTravelList(TravelSearchDto travelSearchDto);
    Long getTravelListCount(TravelSearchDto travelSearchDto);

}
