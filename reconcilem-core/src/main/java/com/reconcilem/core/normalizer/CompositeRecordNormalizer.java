package com.reconcilem.core.normalizer;
import com.reconcilem.core.model.ReconciliationRecord;

import java.util.Objects;
import java.util.List;

public class CompositeRecordNormalizer implements RecordNormalizer
{
    private final List<RecordNormalizer> normalizers;

    public CompositeRecordNormalizer(List<RecordNormalizer> normalizers) {
        this.normalizers = normalizers == null ? List.of() : List.copyOf(normalizers);
    }

    @Override
    public ReconciliationRecord normalize(ReconciliationRecord record) {
        Objects.requireNonNull(record, "Record must not be null");

        ReconciliationRecord normalized = record;

        for (RecordNormalizer normalizer : normalizers) {
            normalized = normalizer.normalize(normalized);
        }

        return normalized;
    }
}
