# ETL Pipeline using Airflow, Spark, MinIO, and Scala

A modular data pipeline that ingests data daily from the [Fake Store API](https://fakestoreapi.com/), processes it through multiple transformation layers (bronze, silver, and gold), and stores the results as Apache Iceberg tables. This project leverages Docker Compose to seamlessly integrate Airflow for orchestration, Spark for data processing, and MinIO for object storage. The ETL logic is implemented in Scala using Spark and follows a Maven-based project structure, providing an excellent playground for both Scala/Spark enthusiasts and those looking to learn Airflow.

---

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Motivation](#motivation)
- [Architecture & Workflow](#architecture--workflow)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [VS Code Configuration](#vs-code-configuration)
  - [Installation & Setup](#installation--setup)
- [Accessing the Services](#accessing-the-services)
- [Troubleshooting](#troubleshooting)
- [Improvements & Future Work](#improvements--future-work)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgements](#acknowledgements)

---

## Overview

This project creates an end-to-end data pipeline that:

1. **Ingests data** from the Fake Store API daily.
2. **Stores raw data** as JSON in a "bronze" bucket within MinIO.
3. **Transforms the raw data** into structured formats:
   - **Silver layer:** Applies initial transformations and writes data as Iceberg tables.
   - **Gold layer:** Performs further aggregations and creates final aggregated Iceberg tables.
4. **Orchestrates the entire process** using Apache Airflow.
5. **Executes ETL processes** via a Scala-based Spark job built with Maven.

---

## Project Structure

The repository is organized into two primary folders:

- **`Airflow-Docker`**  
  Contains all Docker-related files needed to run the integrated environment:
  - **DAGs:** Airflow DAGs that schedule and manage the ETL pipeline.
  - **Dockerfiles:** For building container images.
  - **docker-compose.yml:** Defines and runs the following services:
    - **Apache Airflow:** The orchestration engine.
    - **Apache Spark:** The processing engine.
    - **MinIO:** An object storage service for storing raw and processed data.
  
- **`ETL-Store-Scala`**  
  Contains the Scala code for the ETL process using Spark:
  - This module is built with Maven. After building, the resulting JAR file is placed into a designated `jar` folder (within the Airflow-Docker context) so that it can be executed by Spark as part of the workflow.

---

## Motivation

The project was developed with the following goals in mind:

- **Skill Enhancement:** To continue developing with Scala and Spark.
- **Learning Opportunity:** To gain hands-on experience with Apache Airflow.
- **Innovation:** To provide a Docker Compose configuration that integrates Airflow, Spark, and MinIO in one environment—a setup not readily available in public resources.

---

## Architecture & Workflow

1. **Data Ingestion (Bronze Layer):**
   - A daily API call retrieves data from the Fake Store API.
   - The raw JSON data is stored in a designated "bronze" bucket in MinIO.

2. **Initial Transformation (Silver Layer):**
   - The raw JSON is read and undergoes basic transformations.
   - The transformed data is saved as Apache Iceberg tables in the silver layer.

3. **Aggregation and Final Transformation (Gold Layer):**
   - Data from the silver layer is further processed to compute aggregated metrics.
   - The final aggregated data is stored as Iceberg tables in the gold layer.

4. **Orchestration with Airflow:**
   - Airflow DAGs manage the scheduling and execution of ingestion and transformation tasks.
   - Task dependencies ensure a seamless flow from data ingestion to final aggregation.

5. **Processing with Scala & Spark:**
   - The ETL code written in Scala is compiled into a JAR file using Maven.
   - This JAR is executed within the Spark service to perform data transformations.

---

## Getting Started

### Prerequisites

- **Docker & Docker Compose:** Ensure that both are installed on your system.
- **Git:** To clone the repository.
- **Maven:** To build the Scala project.

### VS Code Configuration

To work optimally with this Java project (originally created in IntelliJ) in Visual Studio Code (VS Code), you'll need to install certain extensions and configure the Java SDK.

1. **Install Required Extensions:**

Make sure to install the following VS Code extensions for complete Java, Scala, and Maven support:

- **Extension Pack for Java**: [Link](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) - Includes basic tools for Java development.
- **Metals**: [Link](https://marketplace.visualstudio.com/items?itemName=scalameta.metals)  - Complete Scala support.
- **Maven for Java**: [Link](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven) - Tools for working with Maven projects.
- **Project Manager for Java**: (Included in "Extension Pack for Java" or install separately if needed) - Facilitates Java project management.
- **Test Runner for Java**: (Included in "Extension Pack for Java" or install separately if needed) - Allows running and debugging unit tests.

 2. **Configure Azul Zulu 11 SDK:**

Since the project was originally developed with Azul Zulu 11, it's important to configure VS Code to use the same SDK.

#### Install Azul Zulu 11:
If you haven't already, download and install Azul Zulu 11 from a trusted source (e.g., Azul's website).

#### Configure the SDK in VS Code:

1. Open VS Code settings: `File -> Preferences -> Settings` .
2. Search for `java.configuration.runtimes`.
3. Edit the `settings.json` file (click "Edit in settings.json" if necessary) and add the following configuration, adjusting the path to your Azul Zulu 11 installation directory:

```json
"java.configuration.runtimes": [
    {
        "name": "JavaSE-11",
        "path": "/path/to/your/azul-zulu-11",
        "default": true
    }
]
```
 3. **Configure JAVA_HOME Environment Variable (Optional but recommended):**

While configuring `settings.json` is usually sufficient, you can also set up the `JAVA_HOME` environment variable to ensure VS Code and other tools use the correct SDK.

   1. Open your operating system's environment variables settings. The process varies by operating system (Windows, macOS, Linux).
   2. Create or edit the `JAVA_HOME` variable:
       - Set `JAVA_HOME` to your Azul Zulu 11 installation directory (same path used in `settings.json`).
   3. Add `$JAVA_HOME/bin` to the `PATH` variable:
       - This ensures `java` and `javac` commands use the correct version. Please make sure where it is your Zulu Path!

        ```text
          export JAVA_HOME=$HOME/Library/Java/JavaVirtualMachines/azul-11/Contents/Home
          export PATH=$JAVA_HOME/bin:$PATH
        ```  
   4. Restart VS Code and your terminal.


4. **Verify Configuration:**

    1.  Open an integrated terminal in VS Code (`View -> Terminal`).

    2. Run the command: `java -version`

    You should see information about Azul Zulu 11, confirming that VS Code is using the correct SDK. Example output:

  ```text
  openjdk version "11.0.25" 2021-04-20
  OpenJDK Runtime Environment (Zulu 11.48+21-CA) (build 11.0.25+10-LTS)
  OpenJDK 64-Bit Server VM (Zulu 11.48+21-CA) (build 11.0.25+10-LTS, mixed mode)
  ```
### Installation & Setup

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/your-repo-name.git
   cd your-repo-name
   ```
   
2. **Build and Start the Docker Environment::**

* Navigate to the Airflow-Docker folder:

  ```bash
  cd Airflow-Docker
  ```
  
* Start the Docker containers:

  ```bash
  docker-compose up --build
  ```
This command will build the Docker images (for Airflow, Spark, and MinIO) and start all services.

3. **Compile the Scala ETL Code:**

- Open a new terminal window.

- Navigate to the ETL-Store-Scala folder:

  ```bash
  cd /ETL-Store-Scala
  ```

- Build the project and generate the JAR file using Maven:

  ```bash
  mvn clean package
  ```
Once the JAR file is generated (typically under the target folder), copy it to the appropriate folder inside the Docker environment (usually a jar directory within Airflow-Docker). Adjust the path as needed.

3. **Triggering the Pipeline:**

* With all services running, access the Airflow UI at http://localhost:8080.
* Monitor and manage the DAGs. You can either trigger them manually or let the scheduled daily run execute automatically.
* Logs and progress can be reviewed directly from the Airflow web interface.


### Accessing the Services

- *Airflow:*
UI is accessible at http://localhost:8081. Refer to your Docker Compose configuration for default credentials if required. Configuration of Spark connection:

   1. Access Airflow UI at http://localhost:8081
   2. Navigate to Admin > Connections
   3. Find or create 'spark_default' connection
   4. Configure with the following settings:
     - Connection Type: Spark
     - Host: spark://spark
     - Port: 7077 (In this case)
   5. Save the connection

- *MinIO:*
Accessible via http://localhost:9000. Use the credentials provided in the Docker Compose file to log in.

- *Spark:*
UI is accessible at http://localhost:8080.Monitor Spark logs and job status via the Docker container logs.


### Troubleshooting

**Docker Issues:**

- Verify that Docker has enough memory and CPU resources allocated.
- Use docker-compose logs to check for any errors in the services.

**Airflow Problems:**

- Check the logs for the Airflow scheduler and webserver containers.
- Ensure that the DAGs are correctly placed in the expected directory.

**Scala ETL Failures:**

- Confirm that the Scala JAR file is correctly built and placed.
- Inspect the Spark container logs for any execution errors related to the ETL job.

## Improvements & Future Work

- **Enhanced Catalog Access:**  
  Improve the data catalog so that it is accessible via standard SQL interfaces instead of relying solely on Spark SQL. This change will offer a more familiar querying experience and simplify integration with various BI tools.

- **Cloud Deployment:**  
  Explore deploying the entire pipeline in the cloud to take advantage of scalable resources, managed services, and global accessibility. This includes setting up cloud-native services for Airflow, Spark, and MinIO (or their alternatives).

- **Enhanced Monitoring:**  
  Integrate detailed logging and monitoring within Airflow to better track ETL job performance and quickly diagnose failures.
  
- **Error Handling & Notifications:**  
  Implement robust error handling mechanisms and set up alert notifications for pipeline failures.

- **Modularization:**  
  Further modularize the ETL code for improved maintainability and testing, including adding unit and integration tests.

- **Scalability Enhancements:**  
  Investigate scaling the pipeline components (e.g., using Kubernetes for container orchestration) to handle larger data volumes.


## Contributing

We welcome contributions! If you'd like to contribute:
1. Fork the repository
2. Create a new branch
3. Submit a pull request

For bugs or feature requests, please open an issue.

## License

This project is distributed under the MIT License. See `LICENSE` file for more information.

## Acknowledgements

- [Apache Airflow](https://airflow.apache.org/)
- [Apache Spark](https://spark.apache.org/)
- [MinIO](https://min.io/)
- [Fake Store API](https://fakestoreapi.com/)
