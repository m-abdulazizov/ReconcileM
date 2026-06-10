package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

public interface MatchingRule
{
    String name();

    MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target);
}