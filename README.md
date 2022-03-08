# Data Collector Service

## How to Run

Please note that the service runs on default port **10001**. Two options to start the application

1. To make the service up this port needs to be free
2. Change ```server.port``` property of ```data-collector-service/src/main/resources/application.properties``` file to
   up in any port.

### Get the source code

1. Execute command ```git clone git@github.com:mbzshajib/data-collector-service.git (ssh)```
2. Go to data-collector-service directory
3. Checkout main Branch ```git checkout main```

### Deploy in Local Machine

- Execute following command to start the application in local machine ```./mvnw spring-boot:run```

**Note:** _To change the port change the property ```server.port```
of ```data-collector-service/src/main/resources/application.properties``` file_

### Deploy in container

Execute following commands to start the application in docker container.

1. Go to data-collector-service
2. Execute ```docker build -t mbzshajib/data-collector-service .```
3. Execute ```docker run -p 8080:8080 mbzshajib/data-collector-service```

## Description

#### Current Diagram

![component diagram](./diagram/components.png)

## Assumptions

## Further Improvement

![component diagram](./diagram/improvement_proposal.png)

## Regarding the Challenge
