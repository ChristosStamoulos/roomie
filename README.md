# roomie

Roomie is an airbnb booking system developed in Java and Kotlin that operates distributed. 
Using the Map Reduce framework, it analyzes, process and stores data in an eficcient and quick manner.
Roomie consists of two parts: 
  * backend, for the data operations and
  * frontend, an Android application for hosting airbnbs.

Roomie also supports manager and customer functionality, through a console and android application respectively.

## Features

### Manager

 * Add an airbnb for hosting.
 * Add availability to an airbnb you own.
 * See the reservations of your airbnb(s).
 * See total reservation by area for a selected period of time

### Customer

 * Search airbnbs trough filters (area, date range, price range, rating, number of people staying)
 * See details of the airbnb (photos, map area, number of room, bathrooms etc.)
 * Book an airbnb
 * Rate the airbnb (1-5 star rating)
 * View your bookings.
 * View your account details.

## Compile and Run

 1. Go to backend/src/main/java/com/example/roomie/backend/config and update the the hosts correctly.
 2. Compile and run the files
  ```sh
  >> javac Master.java
  >> javac Worker.java
  >> javac Reducer.java
  ```

  ```sh
  >> java Reducer
  >> java Worker
  >> java Master
  ```
3. For the manager console
   ```sh
   >> javac ConsoleApp.java
   >> java ConsoleApp
   ```
4. For android app

   4.1 locate BackendCommunicator and update the host for the Master

   4.2 download and run the app

**Note: you can run as many Workers as you want but update the configurations for its one correctly.

## Collaborators

- [Eleni Kechrioti](https://github.com/EleniKechrioti)
- [Maria Schoinaki](https://github.com/MariaSchoinaki)

You can find a demo video [here](https://player.vimeo.com/video/998856180?badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=58479).
