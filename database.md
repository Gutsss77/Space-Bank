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
```

Create the Users Table
The users table stores user information, including the username, email, password, and user type (admin or regular user).
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    userType ENUM('admin', 'user') NOT NULL DEFAULT 'user',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
Create the Account Users Table
The account_users table stores details for each account holder, including their personal information, account number, and balance.

```sql
CREATE TABLE IF NOT EXISTS account_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    f_name VARCHAR(255) NOT NULL,  -- Father's name
    email VARCHAR(255) NOT NULL,   -- Email field remains in the table
    accountNumber VARCHAR(10) NOT NULL UNIQUE,
    adhaar VARCHAR(12) NOT NULL,
    mobileno VARCHAR(15) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    ibalance DECIMAL(10, 2) NOT NULL CHECK (ibalance >= 0),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
Create the Transactions Table
The transactions table stores the transaction details for fund transfers between accounts, including the sender and receiver account numbers, the amount transferred, and the transaction type (credit or debit).
```sql
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    senderAccountNumber VARCHAR(10) NOT NULL,
    receiverAccountNumber VARCHAR(10),
    amount DECIMAL(10, 2) NOT NULL,
    transactionType ENUM('credit', 'debit') NOT NULL,
    transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (senderAccountNumber) REFERENCES account_users(accountNumber) ON DELETE CASCADE,
    FOREIGN KEY (receiverAccountNumber) REFERENCES account_users(accountNumber) ON DELETE CASCADE
);
```

