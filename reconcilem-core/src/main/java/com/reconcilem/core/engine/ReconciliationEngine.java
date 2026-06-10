package com.reconcilem.core.engine;

import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;

import java.util.List;

public interface ReconciliationEngine
{
    ReconciliationResult reconcile(
            List<ReconciliationRecord> sourceRecords,
            List<ReconciliationRecord> targetRecords,
            ReconciliationJob job
    );
}