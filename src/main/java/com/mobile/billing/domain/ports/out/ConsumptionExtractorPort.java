package com.mobile.billing.domain.ports.out;

import java.util.List;

import com.mobile.billing.domain.model.ConsumptionDetail;
import com.mobile.billing.domain.model.DataSource;

public interface ConsumptionExtractorPort {
public List<ConsumptionDetail> extraerConsumptions( DataSource dataSource,
        Integer anioPeriodo,
        Integer mesPeriodo,
        byte[] excelBytes ) ;
}
