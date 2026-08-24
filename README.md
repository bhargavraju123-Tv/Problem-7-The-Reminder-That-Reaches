# Problem-7-The-Reminder-That-Reaches
BHARGAV 43110962 BE-CSE  SATHYABAMA UNIVERSITY



# Reach Reminder System (Problem 7)

## Overview
This is a Spring Boot application designed to orchestrate appointment reminders for residents. It dynamically routes messages through fallback channels (SMS, Voice, Email), translates messages based on resident profiles, strictly enforces quiet hours, and guarantees compliance with the Day 2 CR-2026/11 7-day rate limits.

## Prerequisites
To run this project, you will need the following installed on your machine:
* **Java 21** (JDK 21)
* **Maven** (to build the project)
* **Python** (required for the external transport mock; ensure `python` or `py` is in your system PATH)
* used "intelij" software ide
* used Postman software to check api's workflow and op retriving going good or not (if theres no api use terminal command prompt )

_____________________________________________________________________________________________________________________________________________
## Tech Stack
* **Java 21**
* **Spring Boot 4.1.1**
* **In-Memory Data Structures** (Intentionally chosen over a database to ensure zero friction during evaluator setup).
_____________________________________________________________________________________________________________________________________________
## How to Build and Run

bash(script this in terminal or use postman for api checking):


1.cd reach-reminder          (Clone the repository and navigate to the root folder)

2.mvn clean install          ( Build the project using Maven)

3.mvn spring-boot:run          (Run the Spring Boot application)

*******TYPE THIS IF YOU ARE USING COMMAND PROMPT 
4.curl -X POST http://localhost:8080/reminders/run

              (or)
*******TYPE THIS IF YOU ARE USING POSTMAN SOFTWARE
=>go to postman 
=>enable post method
=>type the below mentioned url in search bar 

4.http://localhost:8080/api/reminders/run


********** WE HAVE TO TYPE /SEARCH THIS URL IN TERMINAL OR POSTMAN AFTER THE SPRING BOOT APPLICATION STARTED RUNNING 
 TO RUN AND GET OUTPUT 
 1. RUN SPRING BOOT CODE (APPLICATION)
 2. SEARCH OR TYPE THE MENTIONED URL ,AS I HAVE SAID THAT BASIS ON TERMINAL OR POSTMAN
___________________________________________________________________________________________________________________________________________________________________________
WHILE BEFORE EVALUATING PLEASE GO THROUGH THIS BECAUSE OUTPUT EXPLANATION IS MENTIONED HERE :

OUTPUT VARIES ON TIME (THIS IS ONE OF THE FLOOR BEHAVIOUR FEATURE ,ITS THERE IN PROBLEM STATEMENT )

Operating Window: The system's quiet hours are configured to block messages overnight between 23:00 and 03:00. If you test the system during these hours, all messages will be correctly suppressed.

Day 2 Surprise (CR-2026/11): The 7-day maximum contact limit is fully implemented. The system dynamically tracks attempts and chronologically prioritizes the most urgent appointments to ensure compliance.
