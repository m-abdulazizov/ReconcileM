package com.reconcilem.core.normalizer;

import com.reconcilem.core.model.ReconciliationRecord;

public interface RecordNormalizer {
    ReconciliationRecord normalize(ReconciliationRecord record);
}
