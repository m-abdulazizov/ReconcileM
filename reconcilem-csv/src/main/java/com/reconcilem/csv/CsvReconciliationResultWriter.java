package com.reconcilem.csv;

import com.reconcilem.core.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CsvReconciliationResultWriter
{
    public void write(ReconciliationResult result, Path outputDirectory){
        Objects.requireNonNull(result, "Reconciliation result must not be null");
        Objects.requireNonNull(outputDirectory, "Output directory must not be null");

        try{
            Files.createDirectories(outputDirectory);

            writeMatchResults(outputDirectory.resolve("matched.csv"), result.matched());
            writeMatchResults(outputDirectory.resolve("possible_matches.csv"), result.possibleMatches());
            writeMatchResults(outputDirectory.resolve("duplicate_matches.csv"), result.duplicateMatches());
            writeMatchResults(outputDirectory.resolve("conflict_matches.csv"), result.conflictMatches());

            writeRecords(outputDirectory.resolve("unmatched_source.csv"), result.unmatchedSourceRecords());
            writeRecords(outputDirectory.resolve("unmatched_target.csv"), result.unmatchedTargetRecords());

            writeSummary(outputDirectory.resolve("summary.csv"), result.summary());
        }catch (IOException ex){
            throw new CsvWriteException("Failed to write reconciliation result to directory: " + outputDirectory, ex);
        }
    }

    private void writeMatchResults(Path path, List<MatchResult> results) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(String.join(",",
                    "source_id",
                    "target_id",
                    "decision",
                    "total_score",
                    "source_date",
                    "target_amount",
                    "source_currency",
                    "target_currency",
                    "source_counterparty",
                    "target_counterparty",
                    "source_reference",
                    "target_reference",
                    "explanations"));
            writer.newLine();

            for(MatchResult result : results){
                ReconciliationRecord source = result.sourceRecord();
                ReconciliationRecord target = result.targetRecord();

                writer.write(csvLine(
                        source.id(),
                        target == null ? "" :target.id(),
                        result.decision().name(),
                        String.valueOf(result.totalScore()),
                        formatDate(source.transactionDate()),
                        target == null ? "" : formatDate(target.transactionDate()),
                        formatAmount(source.amount()),
                        target == null ? "" : formatAmount(target.amount()),
                        source.currency(),
                        target == null ? "" : target.currency(),
                        source.counterpartyName(),
                        target == null ? "" : target.counterpartyName(),
                        source.reference(),
                        target == null ? "" : target.reference(),
                        explanations(result.scores())
                ));
                writer.newLine();
            }
        }
    }
    private void writeRecords(Path path, List<ReconciliationRecord> records) throws IOException{
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
            writer.write(String.join(",",
                    "id",
                    "source",
                    "transaction_date",
                    "amount",
                    "currency",
                    "counterparty",
                    "reference"));
            writer.newLine();

            for(ReconciliationRecord record : records){
                writer.write(csvLine(
                        record.id(),
                        record.source(),
                        formatDate(record.transactionDate()),
                        formatAmount(record.amount()),
                        record.currency(),
                        record.counterpartyName(),
                        record.reference()
                ));
                writer.newLine();
            }
        }
    }

    private void writeSummary(Path path, ReconciliationSummary summary) throws IOException{
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
            writer.write(String.join(",",
                    "source_count",
                    "target_count",
                    "matched_count",
                    "possible_match_count",
                    "unmatched_source_count",
                    "unmatched_target_count"));
            writer.newLine();

            writer.write(csvLine(
                    String.valueOf(summary.sourceCount()),
                    String.valueOf(summary.targetCount()),
                    String.valueOf(summary.matchedCount()),
                    String.valueOf(summary.possibleMatchCount()),
                    String.valueOf(summary.unmatchedSourceCount()),
                    String.valueOf(summary.unmatchedTargetCount())
            ));
            writer.newLine();
        }
    }

    private String explanations(List<MatchScore> scores){
        if (scores == null || scores.isEmpty()){
            return"";
        }

        return scores.stream()
                .map(score -> score.ruleName() + ":" +score.score() + ":" +
                        score.explanation())
                .collect(Collectors.joining("|"));
    }

    private String csvLine(String... values){
        return java.util.Arrays.stream(values)
                .map(this::escape)
                .collect(Collectors.joining(","));
    }

    private String escape(String value){
        if(value == null) {
            return "";
        }
        boolean mustQuote = value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r");

        String escaped = value.replace("\"", "\"\"");

        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }

    private String formatDate(LocalDate date){
        return date == null ? "" : date.toString();
    }

    private String formatAmount(BigDecimal amount){
        return amount == null ? "" : amount.toPlainString();
    }
}
