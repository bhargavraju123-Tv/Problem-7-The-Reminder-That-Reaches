# Decisions

## Project
**Problem 7 – The Reminder That Reaches**

This document records important implementation decisions, rejected alternatives,
trade-offs, edge cases, and limitations as the project is developed.

> This file is a living document. Update it whenever an important decision is made. 

---

## 1. Technology Stack

**Decision:** Use Java 21 with Spring Boot 4.1.1 and Maven.

**Technologies:**
- Spring Web
- Spring Validation
- Spring Boot DevTools
- Spring Boot Test
- Git & GitHub

**Reason:** Spring Boot provides a simple and modular backend for implementing
the reminder, contact-policy, channel-fallback, and validation requirements.

**Database:** Will be decided after inspecting the provided Problem 7 data pack.

**Not using initially:** React, Spring Security, Thymeleaf, AWS, or real
messaging providers because they are not required for Problem 7.

---

## 2. Delivery Interface

**Decision:** Start with a simple runnable interface rather than building a
front end.

**Reason:** Problem 7 explicitly states that command-line delivery is acceptable
and that interface quality is not assessed.

**Rejected:** Building a dashboard or polished web UI before the mandatory
requirements are complete.

**Why rejected:** It would spend time on something that is not part of the
Problem 7 floor.

---

## 3. Reminder Channels

The system must work with the provided mock SMS, voice, and email channels.

**Decision:** Use the supplied mock channels rather than integrating real
messaging providers.

**Reason:** Real messaging-provider integration is explicitly not required.

---

## 4. Channel Fallback

**Decision:** The reminder system will support fallback between eligible
channels.

**Reason:** Channel fallback is a mandatory floor requirement.

**Important:** The final fallback order and stopping rule will be decided after
examining the supplied data and mock-channel behavior.

---

## 5. Stopping Rule

**Decision:** Every reminder attempt sequence must have an explicit stopping
condition.

Possible stopping conditions to evaluate include:
- successful delivery;
- no remaining eligible channel;
- resident/channel opt-out;
- quiet-hours restriction;
- another rule discovered in the problem data.

**Reason:** The problem requires a stopping rule and specifically warns against
unbounded retries.

**Final rule:** To be documented after testing the supplied mock channels.

---

## 6. Quiet Hours and Opt-Outs

**Decision:** Quiet-hour and opt-out rules will be enforced through a central
contact-policy mechanism rather than relying on individual channel callers.

**Reason:** Problem 7 requires these restrictions to be enforced in a way that
future code paths cannot accidentally bypass.

---

## 7. Language Selection

**Decision:** Select reminder content according to each resident's recorded
language preference.

**Decision on message generation:** Use templates rather than natural-language
generation.

**Reason:** Problem 7 states that natural-language generation is not required
and that templates are acceptable.

---

## 8. Shared Contact Points

**Decision:** The system must explicitly handle cases where one contact point
belongs to more than one resident.

**Reason:** Preventing duplicate messages in this situation is a mandatory
floor requirement.

**Final behavior:** To be determined after inspecting the actual contact data
and understanding the appointment relationships.

---

## 9. Success Measurement

**Decision:** Define and implement an explicit measurable definition of
"reached."

**Reason:** The problem states that messages sent is not itself a measure of
success.

**Final metric:** To be documented after examining the delivery behavior of the
provided mock channels.

---

## 10. Edge Cases Found in the Contact Data

This section will be completed while inspecting the supplied data pack.

| Edge case | What we observed | Decision | Implemented? |
|---|---|---|---|
| Incomplete contact record | To be inspected | To be decided | No |
| Shared contact point | To be inspected | To be decided | No |
| Channel-specific limitations | To be inspected | To be decided | No |
| Language preference | To be inspected | To be decided | No |
| Recorded opt-out | To be inspected | To be decided | No |
| Stale contact details | To be inspected | To be decided | No |

Do not remove an edge case from this section just because it was not
implemented. If it was noticed and consciously left out, record that fact and
why.

---

## 11. What We Are Not Building

The following are intentionally out of scope unless the problem requirements
change:

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
- modeling the trade-off between failing to reach someone and over-contacting
  someone.

These will not be prioritized over mandatory requirements.

---

## 13. Day-Two Requirement Change

**Design principle:** Contact-related rules should have a clear, centralized
place in the system.

**Reason:** The organizers state that a new requirement will arrive on day two
and specifically suggest thinking about where a new rule concerning who may be
contacted, how, or how often would be added.

**Goal:** Add the changed rule without rewriting unrelated parts of the system.

---

## 14. Decisions Cut for Time

To be updated during the challenge.

For each cut, record:
- what was not implemented;
- why it was cut;
- what requirement was prioritized instead;
- what would be fixed first with additional time.

---

## 15. Known Limitations

To be updated as implementation progresses.

---

## 16. First Fix After Submission

To be updated near the end of the challenge.

The item should be a concrete improvement that would provide the most value
after the mandatory requirements are complete.
