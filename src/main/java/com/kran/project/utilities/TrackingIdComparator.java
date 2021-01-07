package com.kran.project.utilities;
import java.util.Comparator;

import com.kran.project.farmer.entities.FinanceReportDetails;

public class TrackingIdComparator implements Comparator<FinanceReportDetails> 
{
    @Override
    public int compare(FinanceReportDetails o1, FinanceReportDetails o2) {
        return o2.getTrackingId().compareTo(o1.getTrackingId());
    }

}