# TicketingSystem

The TicketingSystem project is a real-time event ticketing system with a producer-consumer implementation. It consists of a Spring Boot backend and a React frontend.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies](#technologies)
- [Setup Instructions](#setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Usage Instructions](#usage-instructions)
  - [Configuring and Starting the System](#configuring-and-starting-the-system)
  - [UI Controls](#ui-controls)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The TicketingSystem is designed to manage event ticketing operations in real-time. It supports vendor and customer simulations, VIP customer handling, event management, and logging. The system is divided into a backend service built with Java, Spring Boot, and Maven, and a frontend application built with TypeScript, React, and Vite.

## Features

- Real-time ticketing system
- Vendor and customer simulations
- VIP customer handling
- Event management
- Logging and simulation control

## Technologies

### Backend

- Java
- Spring Boot
- Maven

### Frontend

- TypeScript
- React
- Vite
- npm

## Setup Instructions

### Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- Node.js 14 or higher
- npm 6 or higher

### Backend Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/PamuduW/TicketingSystem.git
    cd TicketingSystem
    ```

2. Navigate to the backend directory:
    ```sh
    cd backend
    ```

3. Build the project using Maven:
    ```sh
    mvn clean install
    ```

4. Run the Spring Boot application:
    ```sh
    mvn spring-boot:run
    ```

### Frontend Setup

1. Navigate to the frontend directory:
    ```sh
    cd frontend
    ```

2. Install the dependencies:
    ```sh
    npm install
    ```

3. Start the React application using Vite:
    ```sh
    npm run dev
    ```

## Usage Instructions

### Configuring and Starting the System

1. Access the backend API at `http://localhost:8080/api`.
2. Access the frontend application at `http://localhost:5173`.

### UI Controls

- **Event Management**: Create, update, and delete events.
- **Vendor Management**: Assign vendors to events.
- **Ticket Management**: Add and buy tickets for events.
- **Simulation Control**: Start and stop simulations for events.
- **Logging**: View and save logs for events.

## Running Tests

### Backend

1. Navigate to the backend directory:
    ```sh
    cd backend
    ```

2. Run the tests using Maven:
    ```sh
    mvn test
    ```

### Frontend

1. Navigate to the frontend directory:
    ```sh
    cd frontend
    ```

2. Run the tests using npm:
    ```sh
    npm test
    ```

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add some feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.