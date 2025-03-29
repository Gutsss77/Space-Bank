# Database Setup for SpaceBank Project

## Introduction

The **SpaceBank** project uses a MySQL database to store user and transaction data. This document will guide you through setting up the database and tables required for the project to function correctly.

## Prerequisites

- **MySQL** should be installed on your machine. You can download MySQL from the official [MySQL website](https://dev.mysql.com/downloads/installer/).
- Ensure you have a **MySQL client** (like MySQL Workbench, phpMyAdmin, or command line) to interact with the database.

## Steps to Set Up the Database

### 1. Create the Database

First, create the database `space_bank` in MySQL by running the following SQL command:

```sql
CREATE DATABASE IF NOT EXISTS space_bank;
USE space_bank;
