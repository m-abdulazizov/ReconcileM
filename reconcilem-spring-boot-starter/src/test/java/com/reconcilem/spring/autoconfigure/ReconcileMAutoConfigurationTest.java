package com.reconcilem.spring.autoconfigure;

import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.csv.CsvReconciliationRecordReader;
import com.reconcilem.csv.CsvReconciliationResultWriter;
import com.reconcilem.spring.factory.ReconcileMJobFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ReconcileMAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ReconcileMAutoConfiguration.class));

    @Test
    void shouldCreateDefaultBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ReconciliationEngine.class);
            assertThat(context).hasSingleBean(CsvReconciliationRecordReader.class);
            assertThat(context).hasSingleBean(CsvReconciliationResultWriter.class);
            assertThat(context).hasSingleBean(ReconcileMJobFactory.class);
        });
    }

    @Test
    void shouldBindProperties() {
        contextRunner
                .withPropertyValues(
                        "reconcilem.thresholds.matched-score=90",
                        "reconcilem.thresholds.possible-match-score=60",
                        "reconcilem.rules.amount-tolerance=2500.00"
                )
                .run(context -> {
                    ReconcileMJobFactory factory = context.getBean(ReconcileMJobFactory.class);

                    assertThat(factory.defaultThresholds().matchedScore()).isEqualTo(90);
                    assertThat(factory.defaultThresholds().possibleMatchScore()).isEqualTo(60);
                    assertThat(factory.defaultRules()).hasSize(5);
                });
    }
}