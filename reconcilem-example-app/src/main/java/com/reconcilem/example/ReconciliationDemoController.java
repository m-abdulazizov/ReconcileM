package com.reconcilem.example;
import com.reconcilem.core.engine.DefaultReconciliationEngine;
import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationThresholds;
import com.reconcilem.core.rule.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class ReconciliationDemoController
{
    private final ReconciliationEngine reconciliationEngine = new DefaultReconciliationEngine();

    @GetMapping("/demo/reconcile")
    public ReconciliationResult demoReconcile() {
        ReconciliationRecord firstBankTransaction = new ReconciliationRecord(
                "BANK_TX_1001",
                "bank",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("999000.00"),
                "UZS",
                "ACME LLC",
                "Payment for Invoice INV 889",
                Map.of()
        );

        ReconciliationRecord secondBankTransaction = new ReconciliationRecord(
                "BANK_TX_1002",
                "bank",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("999000.00"),
                "UZS",
                "ACME LLC",
                "Payment for Invoice INV 889",
                Map.of()
        );

        ReconciliationRecord invoice = new ReconciliationRecord(
                "INV_889",
                "invoice-system",
                LocalDate.of(2026, 5, 30),
                new BigDecimal("1000000.00"),
                "UZS",
                "ACME Limited",
                "INV-889",
                Map.of()
        );

        ReconciliationJob job = new ReconciliationJob(
                "BANK_TO_INVOICE",
                "bank",
                "invoice-system",
                List.of(
                        new AmountToleranceRule(new BigDecimal("1000.00"), 40),
                        new CurrencyExactMatchRule(),
                        new DateToleranceRule(),
                        new ReferenceContainsRule(),
                        new CounterpartyContainsRule()
                ),
                new ReconciliationThresholds(80, 50)
        );

        return reconciliationEngine.reconcile(
                List.of(firstBankTransaction, secondBankTransaction),
                List.of(invoice),
                job
        );
    }

}