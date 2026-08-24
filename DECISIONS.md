# Decisions

## Project
**Problem 7 – The Reminder That Reaches**

This document records important implementation decisions, rejected alternatives,
trade-offs, edge cases, and limitations as the project is developed.



---

## 1. Technology Stack

**Decision:** Use Java 21 with Spring Boot 4.1.1 and Maven.

**Technologies:** git hub, java spring boot and the dependencies are 
- Spring Validation
- Spring Boot DevTools
- Spring Boot Test
- jackson-databind
- spring-boot-starter-test
- spring-boot-starter-web

**Reason:** Spring Boot provides a simple and modular backend for implementing the reminder, contact-policy, channel-fallback, and validation requirements. moreover spring boot is robust, enterprise level standards to build scalable backend system logic.

**Database:** no database included because if suppouse we include mysql then evaluator must go through some process in repoistory layer providing root name and pass since 
root name, pass will be my name and my personal pass that wont suppourt in evaluator pc, which leads errors, so i avoided to put database indulging into project.

**Not required initially:** React, Spring Security, Thymeleaf, AWS, or real messaging providers because they are not required for Problem 7.

---

## 2. Channel Fallback

**Decision:** The reminder system will support fallback between eligible channels sequentially: SMS, then Voice (Mobile/Landline), then Email.

**Reason:** Channel fallback is a mandatory floor requirement to maximize reach without unnecessary spam.

**Important:** The fallback order prioritizes less intrusive channels (SMS) before escalating to Voice, ending with Email as a final catch-all if explicitly opted in.

---

## 5. Stopping Rule

**Decision:** Every reminder attempt sequence must have an explicit stopping condition.

**Final rule:** The sequence stops immediately when one of the following occurs:
- A successful delivery is confirmed by the transport layer.
- All eligible channels are exhausted.
- The resident has opted out of all available channels.
- The system is operating within the 23:00 to 03:00 Quiet Hours window.
- **(Day 2 Rule):** The resident has already received 2 contacts within the rolling 7-day period[cite: 1].

**Reason:** The problem requires a stopping rule and specifically warns against unbounded retries. The 7-day limit enforces regulatory compliance[cite: 1].

---

## 6. Quiet Hours and Opt-Outs

**Decision:** Quiet-hour (23:00 to 03:00) and opt-out rules are strictly enforced through a central `ReminderPolicyEngine` rather than relying on individual channel callers.

**Reason:** Problem 7 requires these restrictions to be enforced in a way that future code paths cannot accidentally bypass. Centralization guarantees 100% compliance before message generation begins.

---

## 7. Language Selection

**Decision:** Select reminder content according to each resident's recorded language preference (English, Spanish, French). Unrecognized languages default to English.

**Decision on message generation:** Use string templates rather than natural-language generation.

**Reason:** Problem 7 states that natural-language generation is not required and that templates are acceptable. This ensures predictability and accuracy in translations.

---

## 8. Shared Contact Points

**Decision:** The system maintains a globally tracked `Set` of shared contact endpoints upon initialization. Any endpoint belonging to more than one resident is flagged as "unsafe" and permanently disqualified from use.

**Reason:** Preventing duplicate messages in this situation is a mandatory floor requirement to ensure data privacy and prevent spamming shared household devices.

---

## 9. Success Measurement

**Decision:** Define and implement an explicit measurable definition of "reached."

**Final metric:** A resident is considered "reached" only when the transport mock returns a definitive success status (e.g., `carrier confirmed`, `human`, or `smtp ok`). Mere attempts do not increment the "Confirmed human reach" metric.

**Reason:** The problem states that messages sent is not itself a measure of success.

---

## 10. Edge Cases Found in the Contact Data
### Incomplete contact details

Some residents are missing phone numbers or email addresses.
**Decision:** Skip the unavailable channels without stopping the whole process.
**Status:** Implemented.

### Shared contact points

Some phone numbers or email addresses are shared by multiple residents.
**Decision:** Treat shared contact points as unsafe and block them to avoid sending personal appointment details incorrectly.
**Status:** Implemented.

### Channel limitations

Not every contact method can be used for every channel.
**Decision:** Check channel availability and opt-out rules before adding a channel to the eligible list.
**Status:** Implemented.

### Language preference

Residents have different language preferences.
**Decision:** Use the resident's preferred language and fall back to English when a template is unavailable.
**Status:** Implemented.

### Opt-outs

Some residents have opted out of specific channels or all channels.
**Decision:** Skip opted-out channels. If no safe channel remains, mark the reminder as suppressed.
**Status:** Implemented.

### Stale contact details

Some contact details may be outdated based on their verification date or delivery behavior.
**Decision:** I identified this edge case but ran out of time to implement proper stale-contact detection and handling.
**Status:** Not implemented.


---

## 11. What We Are Not Building

The following are intentionally out of scope unless the problem requirements change:

- User interface/dashboard
- Real messaging-provider integration
- Natural-language generation for reminder content
- Appointment booking
- Appointment rescheduling
- Appointment cancellation
- Large-scale/national infrastructure

**Reason:** These are listed as not required by the Problem 7 document.

---

## 12. Optional Features

Only after every floor requirement is working, consider:

- adaptive stopping based on resident/channel information;
- detecting stale contact details from delivery behavior;
- modeling the trade-off between failing to reach someone and over-contacting someone.

*(Note: Partially cut for time—see section 14).*

---

## 13. Day-Two Requirement Change

**Design principle:** Contact-related rules should have a clear, centralized place in the system.

**Reason:** The organizers state that a new requirement will arrive on day two[cite: 2].

**Goal:** Add the changed rule without rewriting unrelated parts of the system.

**Implementation (CR-2026/11):** 
*   **Prioritization:** Implemented chronological sorting in `ReminderOrchestrator.java`. Appointments scheduled soonest are processed first to ensure urgent reminders are delivered before the limit is exhausted[cite: 1].
*   **Counting:** Added a `residentContactCounts` Map to track attempts per resident. We increment this count before verifying if the gateway attempt succeeded or failed[cite: 1].
*   **Evidence:** Reminders blocked by this rule are explicitly logged under the `RATE_LIMIT_EXCEEDED` suppression reason[cite: 1].

---

## 14. Decisions Cut for Time

*   **What was not implemented:** Adaptive stopping and tracking for stale/dead contact details.
*   **Why it was cut:** I saw this requirement and honestly ran out of time. I consciously chose to prioritize strictly enforcing the mandatory CR-2026/11 limit[cite: 1].
*   **What requirement was prioritized instead:** Chronological sorting and the 7-day attempt tracking map[cite: 1].
*   **What would be fixed first with additional time:** Build a "dead-letter" exclusion list to permanently skip endpoints that return hard bounces.

---

## 15. Known Limitations

*   **In-Memory Volatility:** The 7-day rate limit tracker (`residentContactCounts`) is stored in memory. If the application restarts, this history is wiped unless pre-loaded.
*   **Implicit Cost of Errors:** We prioritized urgent appointments by date, but we lack a weighted mathematical model to compare the clinical cost of missing a health assessment versus a debt meeting.

---

## 16. First Fix After Submission

*   **Data Persistence Layer:** The immediate first fix would be integrating a lightweight, embedded database (like H2 or SQLite) to persist the `residentContactCounts` and stale endpoints. This avoids evaluator setup errors while allowing the system to learn from failures and scale safely across multiple days.
