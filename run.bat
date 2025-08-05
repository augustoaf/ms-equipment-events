echo Packaging and running the application
call mvn clean package
call java -jar target/ms-equipment-events-1.0.0.jar
