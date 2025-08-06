## RabbitMQ to WebSocket Microservice
This project is a Spring Boot microservice designed to consume events from a RabbitMQ topic exchange and broadcast them in real-time to connected web applications via WebSocket connections using STOMP.

## Features
RabbitMQ Integration: Connects to a RabbitMQ topic exchange to listen for incoming events.
WebSocket Broadcasting: Uses Spring WebSockets with STOMP to push events to subscribed clients.
Real-time Updates: Enables web applications to receive immediate updates as events occur in RabbitMQ.
Scalable Architecture: Provides a foundation for building real-time event-driven systems.

## Technologies Used
Spring Boot: Framework for building standalone, production-grade Spring applications.
Spring AMQP: For integrating with RabbitMQ.
Spring WebSocket: For building WebSocket-based applications.
STOMP: Simple Text Oriented Messaging Protocol over WebSockets for structured messaging.
Maven: Dependency management and build automation tool.
Lombok: (Optional) To reduce boilerplate code in Java classes.

## Prerequisites
Before you begin, ensure you have the following installed:
Java Development Kit (JDK) 17 or higher
Apache Maven
RabbitMQ Server: You can run RabbitMQ using Docker for convenience:
```bash
docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
The RabbitMQ management UI will be available at http://localhost:15672 (default credentials: guest/guest).

## How to Run
Clone the repository (or create a new Spring Boot project and copy the provided files).
Navigate to the project root directory in your terminal.

Build the project using Maven:
```bash
mvn clean install
Run the Spring Boot application:
mvn spring-boot:run
```
or
```bash
run.bat
```
The application will start on http://localhost:8080 by default.

## How to Test
Simulate RabbitMQ Producer
You can send test messages to your RabbitMQ setup using the RabbitMQ management UI:
Open your browser and go to http://localhost:15672.
Log in with guest/guest.
Navigate to Exchanges and click on the event.topic.exchange.
Scroll down to the Publish message section.
Set the Routing Key (e.g., user.event.created, order.event.updated, any.event.type).
In the Payload text area, enter a JSON message matching the Event structure:
{
"type": "USER_CREATED",
"payload": "{"userId": "456", "username": "newuser"}",
"timestamp": "2025-08-05T19:00:00",
"source": "AuthService"
}
Click the Publish message button.
Connect with WebSocket Client
The project includes a simple index.html file that acts as a WebSocket client to display received events.
Ensure your Spring Boot application is running.
Open your web browser and go to http://localhost:8080/index.html.
As you publish messages to RabbitMQ (as described above), you should see them appear in real-time on this HTML page.

## Project Structure
rabbitmq-websocket-microservice/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/
│ │ │ └── equipment/
│ │ │ └── ms/
│ │ │ ├── config/
│ │ │ │ ├── RabbitMQConfig.java # RabbitMQ queue, exchange, binding configuration
│ │ │ │ └── WebSocketConfig.java # WebSocket and STOMP message broker configuration
│ │ │ ├── listener/
│ │ │ │ └── RabbitMQEventListener.java # Listens to RabbitMQ and broadcasts to WebSockets
│ │ │ ├── model/
│ │ │ │ └── EquipmentEvent.java # Data Transfer Object for events
│ │ │ └── MsEquipmentEventsApplication.java # Main Spring Boot application
│ │ └── resources/
│ │ | ├── application.properties # App configuration (RabbitMQ connection details)
│ │ | └── static/
│ │ | | └── index.html # Simple WebSocket client for testing
└── pom.xml # Maven project configuration and dependencies
