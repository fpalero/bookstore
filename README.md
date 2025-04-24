# Bookstore App

Welcome to the **Bookstore App**! This application allows users to browse, purchase, and check the user loyalty points.

## 📌 Features

* Browse Books: View available books with details like title, author, price, and genre.
* Purchase Books: Buy books with automatic discount and loyalty point calculations.
* Loyalty Points: Track and redeem customer loyalty points.
* Swagger UI: Interactive API documentation for easy testing.
* H2 Database: Embedded database for development (accessible via /h2-console).

## 🚀 Installation

### Prerequisites

Java 21
Gradle
Docker (optional)

### Steps
1. Clone the repository:
    ```bash
    git clone git@github.com:fpalero/bookstore.git
    ```
2. Navigate to the project directory:
    ```bash
    cd bookstore
    ```
3. Build application:
    ```bash
    ./gradlew build
    ```

## � Usage
1 Start the development server:

Run on Local environment
    ```bash
    ./gradlew bootRun
    ```  
or run on Container
    ```bash
    docker build -t bookstore .
    docker run -p 8080:8080 bookstore
    ```


## Accessing the Application

* API Documentation: Open Swagger UI to explore endpoints: `http://localhost:8080/swagger-ui/index.html#`.
* H2 Database Console: Visit `http://localhost:8080/h2-console` (JDBC URL: jdbc:h2:mem:bookstore, User: bookstore & Pass: bookstore).

## 📚 API Endpoints

| Endpoint                  | Method | Description                          |
|---------------------------|--------|--------------------------------------|
| `/api/books`              | GET    | Fetch all available books.          |
| `/api/clients/{id}/points`| GET    | Get loyalty points for a client.    |
| `/api/clients/{id}/purchase` | POST   | Purchase books (updates points).    |


## Technologies Used

- **Backend**: Speing Boot, Java 21, Swagger
- **Database**: H2

# Technical Decisions
## Architecture
A layering architecture has been used. 

1 - The first layer is the controller which is resposible of defining the endpoint and 
call the services.

2 - The second layer is the services that contains all the necessary logic for managing the bookstore.

3 - The third layer is the data layer where is persisted the data.

## Modularity
Following the layered architecture the functionality, on each layer have been defined different domains that contains common logic:

1- Controller: divided on Books (recover all the available books) and Clients (purchase books and recover client loyalty points)

2- Serivce: divided on:
    
    - Get Books: return a list of availal books
    - Get Client Loyalty Points: return the current amount of loyalty points
    - Purchase: this service is used for buying books, apply the appropiete discounds and also calculate the loyalty points for the clients.

## TDD
I followed the TDD practices ensuring that the bookstor services worked correctly even if I iterete on changes. This also allowd to have a code coverage of 85%

## Exception Handler
I created custom exception and error code that allows to identify fast which is the main cause of the error that happen duirng the service call.

## Neatness of code
I created descriptive variables and functions to make easy to understand the code.

## Self-documented test
I documented the java classes with clear comment and provide comprehensive usage instructions in the README file.
