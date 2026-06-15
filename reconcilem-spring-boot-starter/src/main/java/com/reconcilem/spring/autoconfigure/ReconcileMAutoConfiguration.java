package com.reconcilem.spring.autoconfigure;

import com.reconcilem.core.engine.DefaultReconciliationEngine;
import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.csv.CsvReconciliationRecordReader;
import com.reconcilem.csv.CsvReconciliationResultWriter;
import com.reconcilem.jdbc.JdbcReconciliationResultRepository;
import com.reconcilem.jdbc.JdbcReconciliationRecordReader;
import com.reconcilem.spring.factory.ReconcileMJobFactory;
import com.reconcilem.spring.properties.ReconcileMProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@AutoConfiguration
@EnableConfigurationProperties(ReconcileMProperties.class)
public class ReconcileMAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReconciliationEngine reconciliationEngine() {
        return new DefaultReconciliationEngine();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsvReconciliationRecordReader csvReconciliationRecordReader() {
        return new CsvReconciliationRecordReader();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsvReconciliationResultWriter csvReconciliationResultWriter() {
        return new CsvReconciliationResultWriter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReconcileMJobFactory reconcileMJobFactory(ReconcileMProperties properties) {
        return new ReconcileMJobFactory(properties);
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    public JdbcReconciliationRecordReader jdbcReconciliationRecordReader(DataSource dataSource) {
        return new JdbcReconciliationRecordReader(dataSource);
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean
    public JdbcReconciliationResultRepository jdbcReconciliationResultRepository(DataSource dataSource) {
        return new JdbcReconciliationResultRepository(dataSource);
    }
}
