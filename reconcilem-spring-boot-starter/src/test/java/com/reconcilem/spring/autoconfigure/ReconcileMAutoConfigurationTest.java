package com.reconcilem.spring.autoconfigure;

import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationSummary;
import com.reconcilem.csv.CsvReconciliationRecordReader;
import com.reconcilem.csv.CsvReconciliationResultWriter;
import com.reconcilem.jdbc.JdbcReconciliationResultRepository;
import com.reconcilem.jdbc.JdbcReconciliationRecordReader;
import com.reconcilem.spring.factory.ReconcileMJobFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import java.util.List;

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
            assertThat(context).doesNotHaveBean(JdbcReconciliationRecordReader.class);
            assertThat(context).doesNotHaveBean(JdbcReconciliationResultRepository.class);
        });
    }

    @Test
    void shouldCreateJdbcBeansWhenDataSourceExists() {
        contextRunner
                .withBean(DataSource.class, this::dataSource)
                .run(context -> {
                    assertThat(context).hasSingleBean(JdbcReconciliationRecordReader.class);
                    assertThat(context).hasSingleBean(JdbcReconciliationResultRepository.class);
                });
    }

    @Test
    void shouldRespectCustomReconciliationEngineBean() {
        ReconciliationEngine customEngine = (sourceRecords, targetRecords, job) -> new ReconciliationResult(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new ReconciliationSummary(0, 0, 0, 0, 0, 0)
        );

        contextRunner
                .withBean(ReconciliationEngine.class, () -> customEngine)
                .run(context -> assertThat(context.getBean(ReconciliationEngine.class)).isSameAs(customEngine));
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

    private DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:reconcilem_starter_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
