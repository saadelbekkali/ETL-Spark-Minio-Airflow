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
  - [Installation & Setup](#installation--setup)
- [Accessing the Services](#accessing-the-services)
- [Troubleshooting](#troubleshooting)
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

### Installation & Setup

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/your-repo-name.git
   cd your-repo-name
2. **Build and Start the Docker Environment::**

* Navigate to the Airflow-Docker folder:

  ```bash
  cd Airflow-Docker

* Start the Docker containers:

  ```bash
  docker-compose up --build

This command will build the Docker images (for Airflow, Spark, and MinIO) and start all services.

3. **Compile the Scala ETL Code:**

Open a new terminal window.

Navigate to the ETL-Store-Scala folder:

  ```bash
    cd /ETL-Store-Scala

Build the project and generate the JAR file using Maven:

```bash
  mvn clean package

Once the JAR file is generated (typically under the target folder), copy it to the appropriate folder inside the Docker environment (usually a jar directory within Airflow-Docker). Adjust the path as needed.

3. **Triggering the Pipeline:**

* With all services running, access the Airflow UI at http://localhost:8080.
* Monitor and manage the DAGs. You can either trigger them manually or let the scheduled daily run execute automatically.
* Logs and progress can be reviewed directly from the Airflow web interface.


### Accessing the Services

*Airflow:*
UI is accessible at http://localhost:8081. Refer to your Docker Compose configuration for default credentials if required.

*MinIO:*
Accessible via http://localhost:9000. Use the credentials provided in the Docker Compose file to log in.

*Spark:*
UI is accessible at http://localhost:8080.Monitor Spark logs and job status via the Docker container logs.


### Troubleshooting

**Docker Issues:**

* Verify that Docker has enough memory and CPU resources allocated.
* Use docker-compose logs to check for any errors in the services.

**Airflow Problems:**

* Check the logs for the Airflow scheduler and webserver containers.
* Ensure that the DAGs are correctly placed in the expected directory.

**Scala ETL Failures:**

* Confirm that the Scala JAR file is correctly built and placed.
* Inspect the Spark container logs for any execution errors related to the ETL job.

### Contributing

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
