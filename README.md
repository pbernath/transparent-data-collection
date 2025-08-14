# Making Data Collection Transparent: A Focus on Consent and Transparency

### The Development of a Prototype Mobile Application Centered Around Transparent Data Collection

This repository contains the source code for the mobile application prototype developed as part of the Bachelor's thesis at KTH Royal Institute of Technology.

---

## Table of Contents

- [About The Project](#about-the-project)
  - [Key Features](#key-features)
  - [Built With](#built-with)
- [Future Work](#future-work)
- [Thesis Information](#thesis-information)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## About The Project

This project is a mobile application prototype designed to investigate and address the lack of transparency in data collection. It serves as a potential solution and an example for how to enhance user consent and autonomy in the digital age.

The core of the application is a feature called **"Contracts"**. These are virtual agreements presented to the user that explicitly detail:
*   **What** data is being collected.
*   **Why** the data is being collected (the purpose).
*   **How long** the data will be collected for.

Users are given full autonomy to accept or decline these contracts. To encourage participation, an incentive system using a fictional virtual currency is included. Most importantly, users retain control and can withdraw their consent for any active contract at any time.

The prototype is built for the Android platform and utilizes Firebase for its backend services, including authentication and database management.

### Key Features

*   **User Authentication**: Secure user registration and login functionality powered by Firebase Authentication.
*   **Backend Communication**: Seamless data synchronization with a cloud backend (Firebase).
*   **Transparent "Contracts"**: The novel solution for managing data consent.
    *   Explicitly informs users about data collection details.
    *   Grants users autonomy to grant or deny consent.
    *   Provides the ability to withdraw consent at any time.
    *   Includes an incentive system to reward users for sharing their data.

### Built With

This project was built using the following technologies:

*   [Android Studio](https://developer.android.com/studio) - The official IDE for Android app development.
*   [Kotlin](https://kotlinlang.org/) - The modern programming language for Android development.
*   [Firebase](https://firebase.google.com/)
    *   Firebase Authentication
    *   Firestore Database
    *   Cloud Functions
*   [Material Design](https://material.io/) - For UI components and design principles.
*   [Node.js](https://nodejs.org/) - For server-side scripting and services.
*   [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript) - For cloud function scripting.
*   [Python](https://www.python.org/) - For cloud function scripting.

## Future Work

Future development could expand upon this prototype in several ways:

*   Implement the necessary features to facilitate real-world use cases.
*   Use the application as a tool to evaluate different research topics within data transparency.
*   Conduct market research to evaluate the feasibility and impact of contract-based rewards.

## Thesis Information

This project was submitted in fulfillment of the requirements for the Degree Programme in Computer Engineering and can be read in full at [KTH Royal Institute of Technology](https://kth.diva-portal.org/smash/record.jsf?pid=diva2:1986383).

*   **Title**: Making Data Collection Transparent: A Focus on Consent and Transparency
*   **Swedish Title**: Förtydligande av datainsamling: Ett fokus på samtycke och transparens
*   **Institution**: KTH Royal Institute of Technology (Kungliga Tekniska högskolan)
*   **School**: School of Electrical Engineering and Computer Science
*   **Date**: June 8, 2025
*   **Supervisor**: Zhenyu Li
*   **Examiner**: Ki Won Sung

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Acknowledgments

*   Zhenyu Li
*   Ki Won Sung
*   KTH Royal Institute of Technology
