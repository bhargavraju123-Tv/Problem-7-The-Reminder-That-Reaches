# AI Usage


**Problem 7 – The Reminder That Reaches**.


---


Tool:Gemini Ai
# AI Usage Declaration

## General Usage
I utilized an AI assistant (Google Gemini) as a pair-programming partner and sounding board throughout this project. It helped accelerate development, validate architectural decisions, and troubleshoot framework-specific errors, while I maintained full control over the business logic and final implementation.

## Requirement Analysis
* **Interpreting CR-2026/11:** Used AI to discuss the implications of the Day 2 surprise rule (7-day rate limit). We brainstormed defensible prioritization strategies and decided that sorting appointments chronologically was the fairest and most logical approach.
* **Feature Mapping:** Broke down the "Floor" requirements (multi-channel fallback, quiet hours, translations) into independent Java components.

## Coding Assistance
* **Core Logic:** Leveraged AI to generate standard Java boilerplate, including the `switch` statement for multi-language translations (English, Spanish, French).
* **Time API:** Collaborated on writing the `java.time.LocalTime` logic to ensure the quiet hours accurately handled overnight boundaries.
* **Rate Limiting:** Discussed data structures to track contacts, ultimately implementing a `HashMap` to record resident attempts and enforce the limit.

## Testing
* **Output Validation:** Used the AI to verify the mathematical correctness of the final `REMINDER RUN REPORT`. We confirmed that successful attempts, rate-limited suppressions (152), and opt-out suppressions (71) perfectly totaled the 940 input records. 

## Debugging
* **Property Binding:** Debugged a Spring Boot `@ConfigurationProperties` issue where the `application.properties` prefix did not match the Java class, preventing the custom quiet hours from loading.
* **Time Boundary Bugs:** Fixed a logic bug where the system blocked all messages because it miscalculated the daytime window when the quiet hours spanned across midnight.

## Data Cleaning or Inspection
* **Data Mapping:** Used AI to help inspect how the resident profile fields (e.g., language tags, missing endpoints) dictate the behavior of the fallback engine and message generator, ensuring the system fails gracefully when data is missing.
