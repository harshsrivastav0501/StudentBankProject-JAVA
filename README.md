# Student Bank - Java Mini Project

A console and Swing desktop banking application built with core Java and object-oriented programming concepts.

## Features
- Open savings and current accounts with an initial deposit.
- Automatically assign account numbers.
- List accounts and search by account number or holder name.
- Deposit and withdraw money with validation.
- Transfer money between accounts in console mode.
- Calculate total balance.
- Save and load account data using Java object serialization (`bank-data.ser`).
- Swing GUI with account creation, deposits, withdrawals, balance checking, account listing, and saving.

## Requirements
- JDK 11 or newer.
- No external libraries required.

## Compile
Run these commands from the `StudentBankProject` folder:

```bash
javac -d out $(find src -name "*.java")
```

## Run the console application

```bash
java -cp out bank.app.Main
```

## Run the desktop GUI

```bash
java -cp out bank.app.Main gui
```

The program saves account data to `bank-data.ser` in the current working directory. The first run creates two sample accounts if no saved accounts are found.
