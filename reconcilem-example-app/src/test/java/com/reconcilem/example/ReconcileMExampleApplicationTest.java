package com.reconcilem.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReconcileMExampleApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRunCsvDemoEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/demo/reconcile-csv", String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("summary");
        assertThat(response.getBody()).contains("matchedCount");
    }

    @Test
    void shouldRunJdbcPersistenceDemoEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/demo/reconcile-jdbc/persist", String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("runId");
        assertThat(response.getBody()).contains("savedMatches");
    }
}
