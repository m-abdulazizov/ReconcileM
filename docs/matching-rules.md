# Matching Rules

Matching rules are the main extension point in ReconcileM. A rule compares one source record with one target record and returns a `MatchScore`.

```java
public interface MatchingRule {
    MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target);
}
```

Each score contains:

```text
rule name
numeric score
human-readable explanation
```

## Built-In Rules

| Rule | Purpose |
|---|---|
| `AmountExactMatchRule` | Scores when amounts are exactly equal |
| `AmountToleranceRule` | Scores when amount difference is within a configured tolerance |
| `CurrencyExactMatchRule` | Scores when currencies are equal |
| `DateToleranceRule` | Scores when transaction dates are close enough |
| `ReferenceExactMatchRule` | Scores when references are equal |
| `ReferenceContainsRule` | Scores when one reference contains the other after normalization |
| `CounterpartyContainsRule` | Scores when one counterparty name contains the other after normalization |

## Decision Thresholds

The engine adds all rule scores and compares the total against job thresholds:

```text
score >= matchedScore        -> MATCHED
score >= possibleMatchScore  -> POSSIBLE_MATCH
score < possibleMatchScore   -> UNMATCHED
```

Example:

```java
ReconciliationThresholds thresholds = new ReconciliationThresholds(80, 50);
```

## Custom Rule

Applications can add domain-specific rules without changing the framework.

```java
public class PaymentGatewayFeeRule implements MatchingRule {

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        BigDecimal difference = source.amount().subtract(target.amount()).abs();

        if (difference.compareTo(new BigDecimal("2500.00")) <= 0) {
            return new MatchScore(
                    "PAYMENT_GATEWAY_FEE",
                    20,
                    "Amounts are within allowed payment gateway fee"
            );
        }

        return new MatchScore(
                "PAYMENT_GATEWAY_FEE",
                0,
                "Amount difference is greater than allowed payment gateway fee"
        );
    }
}
```

Use the custom rule in a job:

```java
ReconciliationJob job = new ReconciliationJob(
        "GATEWAY_TO_ORDERS",
        "payment-gateway",
        "orders",
        List.of(
                new AmountToleranceRule(new BigDecimal("2500.00"), 40),
                new CurrencyExactMatchRule(),
                new ReferenceContainsRule(),
                new PaymentGatewayFeeRule()
        ),
        new ReconciliationThresholds(80, 50)
);
```

## Rule Design Guidelines

- Keep rules small and focused.
- Return `0` instead of throwing for normal business mismatch.
- Throw only when the rule cannot execute because data is invalid.
- Use clear explanations because they appear in reports and saved results.
- Prefer configurable constructor values over hardcoded business constants.
