# currency-app

This is a full-stack web application built with a Java Spring Boot REST backend and an Angular TypeScript frontend.

The application:

- Fetches and displays historical exchange rates against EUR.
- Provides a table and a line chart to visualize the exchange rate history.
- Allows users to convert amounts between EUR and a selected currency.

## Setting Up the Backend (Java)

1. Navigate to the `currency-app-backend` directory.

```bash
   cd currency-app-backend
```

2. Install the required dependencies using Gradle.

```bash
./gradlew build
```

3. Run the backend server

```bash
./gradlew bootRun
```

## Setting Up the Frontend (Angular)

1. Navigate to the `currency-app-frontend` directory.

```bash
   cd currency-app-frontend
```

2. Install the required dependencies using npm.

```bash
   npm install
```

3. Run the frontend development server

```bash
   ng serve
```

## Running the Application

1. Ensure that the backend is running on http://localhost:8080 (or another port, depending on your setup).
2. Ensure that the frontend is running on http://localhost:4200.
3. Open your browser and go to http://localhost:4200. The frontend should be able to make API calls to the backend and display the data.

## Credits

- Currency List: The list of currencies (with symbols and flags) was sourced from this [gist by A. Valerian](https://gist.github.com/avaleriani/2ce5d24f905825ce0e2f8489c9fda4c3).
