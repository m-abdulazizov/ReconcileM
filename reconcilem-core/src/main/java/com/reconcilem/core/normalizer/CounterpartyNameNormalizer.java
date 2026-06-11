package com.reconcilem.core.normalizer;

import com.reconcilem.core.model.ReconciliationRecord;

public class CounterpartyNameNormalizer implements RecordNormalizer {

    @Override
    public ReconciliationRecord normalize(ReconciliationRecord record) {
        String normalizedCounterpartyName = normalizeName(record.counterpartyName());

        return new ReconciliationRecord(
                record.id(),
                record.source(),
                record.transactionDate(),
                record.amount(),
                record.currency(),
                normalizedCounterpartyName,
                record.reference(),
                record.attributes()
        );
    }

    private String normalizeName(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim().toUpperCase();

        normalized = normalized
                .replace("L.L.C.", "LLC")
                .replace("L.L.C", "LLC")
                .replace("L L C", "LLC")
                .replace("LTD.", "LTD")
                .replace("INC.", "INC");

        normalized = normalized
                .replace(".", " ")
                .replace(",", " ")
                .replace("\"", " ")
                .replace("'", " ")
                .replace("«", " ")
                .replace("»", " ")
                .replaceAll("\\s+", " ")
                .trim();

        normalized = removeLegalForm(normalized, "LLC");
        normalized = removeLegalForm(normalized, "LTD");
        normalized = removeLegalForm(normalized, "LIMITED");
        normalized = removeLegalForm(normalized, "INC");
        normalized = removeLegalForm(normalized, "CORP");
        normalized = removeLegalForm(normalized, "COMPANY");
        normalized = removeLegalForm(normalized, "ООО");
        normalized = removeLegalForm(normalized, "ОАО");
        normalized = removeLegalForm(normalized, "АО");
        normalized = removeLegalForm(normalized, "МЧЖ");

        return normalized.replaceAll("\\s+", " ").trim();
    }

    private String removeLegalForm(String value, String legalForm) {
        return value
                .replaceAll("^" + legalForm + "\\s+", "")
                .replaceAll("\\s+" + legalForm + "$", "")
                .replaceAll("\\s+" + legalForm + "\\s+", " ")
                .trim();
    }
}