# Design Analysis

An analysis of how this solution addresses the four design criteria outlined in the coding test specification.

---

## 1. Extensibility

The solution is highly extensible thanks to a data-driven, configuration-first approach:

- **Age bands are defined in YAML, not code.** Adding a new ticket type (e.g., "Student" for ages 18-25) requires only a new entry in `application.yml` under `age-ranges` and `ticket-prices`, plus a new `TicketType` enum value -- no changes to `MovieTicketsService` logic.
- **The `Range` record** is a generic, reusable age-range abstraction. The service builds a `Map<Range, TicketType>` from config at construction time, so the age-to-ticket-type resolution is entirely driven by configuration -- not hardcoded `if/else` chains.
- **Discount parameters are externalized** (`children-group-discount-threshold`, `children-group-discount-rate`), so discount thresholds and rates can be tuned without recompilation.
- **Custom Jackson serialization** for `MonetaryAmount` is implemented via Spring's `ObjectMapper` customization, making it straightforward to add new custom types.

**Where it could go further:** The discount logic itself (apply to children when qty >= threshold) is still procedural inside `MovieTicketsService`. A more extensible design could introduce a discount strategy interface, allowing multiple discount rules to be composed. That said, for the scope of this problem, the current approach avoids over-engineering.

---

## 2. Separation of Concerns

The solution follows a clean layered architecture with strict unidirectional dependency flow:

| Layer | Class | Responsibility |
|---|---|---|
| **Controller** | `MovieTicketsController` | HTTP routing, request validation (`@Valid`), delegation |
| **Service** | `MovieTicketsService` | All business logic: grouping, pricing, discounts, sorting |
| **Model** | `request/` and `response/` packages | Separate request DTOs from response DTOs -- no shared mutable state |
| **Config** | `MovieTicketsProperties`, `JacksonConfig` | Externalized config binding, serialization concerns |
| **Exception** | `GlobalExceptionHandler` | Centralized error formatting (RFC 7807 Problem Details) |

Key observations:

- The **controller does zero business logic** -- it validates input and delegates to the service.
- **Request and response models are in separate packages**, preventing accidental coupling between inbound and outbound contracts.
- **Error handling is fully decoupled** from business logic via `@RestControllerAdvice`, so the service never formats HTTP responses.
- **Serialization concerns** (MonetaryAmount to JSON) live in the config layer, not in the DTOs themselves.
- **Internal models** (`Range`, `TicketTypeSummary`) are not exposed in the API -- they exist only to support the service layer's calculations.

---

## 3. Configurability

Every business rule parameter is externalized in `application.yml`:

```yaml
movietickets:
  pricing:
    children-group-discount-threshold: 3
    children-group-discount-rate: 0.75
    age-ranges:
      - { min: 1, max: 10, ticket-type: CHILDREN }
      - { min: 11, max: 17, ticket-type: TEEN }
      - { min: 18, max: 64, ticket-type: ADULT }
      - { min: 65, max: 100, ticket-type: SENIOR }
    ticket-prices:
      CHILDREN: { amount: 5, currency: AUD }
      TEEN: { amount: 12, currency: AUD }
      ADULT: { amount: 25, currency: AUD }
      SENIOR: { amount: 17.50, currency: AUD }
```

- **`MovieTicketsProperties`** uses nested Java records with `@ConfigurationProperties`, giving type-safe, validated config binding at startup.
- Config can be **overridden per environment** (dev/staging/prod) using Spring profiles or environment variables -- no code changes needed.
- **Currency is configurable** per ticket type (not hardcoded to AUD), supporting potential multi-currency scenarios.
- The use of **Moneta (JSR-354)** for monetary calculations ensures precision via `BigDecimal` -- avoiding floating-point rounding errors that would be a real production concern.

---

## 4. Appropriate Test Coverage

56 tests across 4 testing layers, all passing:

| Layer | Tests | What's Covered |
|---|---|---|
| **Unit (Service)** | 36 | Core business logic -- no-ticket edge case, single ticket per type (with parameterized age boundaries), multi-ticket without discount, multi-ticket with discount |
| **Integration (MockMvc)** | 8 | Input validation (5 tests for each constraint) + the 3 sample scenarios from the spec |
| **Component (Full stack)** | 3 | Real HTTP calls against embedded server -- end-to-end verification |
| **Serialization** | 8 | Round-trip JSON for all DTOs, verifying MonetaryAmount custom format |
| **Context** | 1 | Spring context loads successfully |

Notable testing practices:

- **Parameterized tests** cover age boundary conditions (e.g., testing ages 18, 30, 64 for Adult), catching off-by-one errors.
- **Nested `@Nested` test classes** organize scenarios logically (NoTicket -> SingleTicket -> MultiTicket -> WithDiscounts), making the test suite readable as a specification.
- **Fixture-based assertions** using JSON files in `src/test/resources/fixtures/` ensure response payloads exactly match expected output.
- **Test helpers** (`ModelFixturesHelper`, `MonetaryAmountHelper`, `FixturesHelper`) reduce boilerplate without obscuring test intent.
- **The 3 sample scenarios from the PDF** are explicitly tested in `ExampleTestsFromSpec`, directly validating against the requirements.
- **Load testing** (60 parallel requests) was also performed, confirming thread safety of the stateless design.

The test coverage satisfies the spec's requirement that "any domain logic has associated unit tests" and goes well beyond it with integration and component tests.

---

## Summary

The solution demonstrates strong software design across all four criteria. The standout strength is configurability -- the entire pricing model is data-driven. The main area where the design could be pushed further is making the discount logic itself pluggable (strategy pattern for discount rules), but that would arguably be over-engineering for the stated requirements.
