# Online IDE Backend

This repository contains the **backend application** for the Online IDE. It handles code execution requests and provides the necessary API endpoints for the frontend.

## 🚀 Features
- Execute code in **Java**, **C++**, and **Python**
- Manage containerized environments using Docker
- Provide secure and isolated code execution
- Unit and integration testing using **JUnit**

## 🛠️ Tech Stack
- **Spring Boot 3**: For building the backend services
- **Docker**: For running code in isolated containers

## 🧑‍💻 Frontend Repository
The frontend application providing the UI for writing and running code is available here:
[Online IDE Frontend](https://github.com/Aviral-Gupta101/online-ide-frontend)

## 🚀 Starting the Application
To start the backend using Docker Compose, follow these steps:
1. Ensure Docker and Docker Compose are installed.
2. Run the following command in the project directory:
    ```bash
    docker-compose up -d
    ```

## ⚠️ Note
- When a user tries to run code for the **first time** and the required Docker image is **not available** on the server, the backend will attempt to **download the image**.
- This download process can take some time depending on the image size.
- During this period, the client request will be **dropped** and an **error** will be returned in the response.
- Once the image is downloaded, the user can rerun the code without issues.

## 🧪 API Example
Once the application is running, you can test the code execution using the following `curl` request:

```bash
curl --location 'http://localhost:8080/online-compiler/run-code' \
--header 'Content-Type: application/json' \
--data '{
  "compilerType": "CPP",
  "code": "#include <iostream>\nusing namespace std;\n\nint main() {\n  int a = 10; \n  int b = 20;\n  cin>>a>>b;\n  cout<<\"A: \"<<a<<endl;\n  cout<<\"B: \"<<b<<endl;\n  cout<<\"SUM: \" <<a+b<<endl;\n  return 0;\n}",
  "input": "10 20"
}'
```

### Explanation:
- **compilerType**: Can be set to one of the following values:
  - `CPP` for C++ code
  - `PYTHON` for Python code
  - `JAVA` for Java code
- **code**: The code to be executed. Ensure special characters like `\n`, `\"`, etc., are properly **escaped**.
- **input**: (Optional) Provide input values if the code requires standard input.

## 🖼️ Live URL
The backend is integrated with the frontend and can be accessed through the live frontend URL: 
[Online IDE Frontend](https://online-ide-frontend.netlify.app)

