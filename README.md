# Inventory App, Stage 2 

Project Overview 

You've made it to the end! This is the final part of project where you'll create a single amazing app that will pull together many of the components you've learned in this Nanodegree program! 

The goal is to design and create the structure of an Inventory App which would allow a store to keep track of its inventory. In Stage 1, you designed and implemented the backend database that stores information about the products and suppliers. In this stage, you'll build out the app UI for users to add and remove inventory. 

Why this project? 

This is the capstone project of the Nanodegree program that allows you to demonstrate all the components you've learned throughout the program in a single final product. 

Imagine that you are the Android developer at a merchandise startup, tasked with building out their inventory app. Consider the user workflow, user needs, and backend database storage needs as you plan. 

You will need to pull together your knowledge of app planning, navigating between multiple activities, databases, networking, and UI development. A strong foundation in these skills are foundational to Android development worldwide. By practicing these skills and building this app, you will have the foundation to build similar apps for any kind of business. 

What will I Iearn? 

In addition to skills covered in previous courses, this project is about combining various ideas and skills we’ve been practicing throughout the course. They include: 

* Storing information in a SQLite database 
* Integrating Android’s file storage systems into that database 
* Setting up a Content Provider and use it to access a repository of data 
* Presenting information from files and SQLite databases to users 
* Updating information based on user input 
* Creating intents to other apps using stored information 

Note: As the focus of this program is Java, only projects completed with Java as the source code will be accepted. Projects using Kotlin as the source code will not be accepted. 

Build Your Project 

You must have passed Inventory App, Stage 1 before submitting this stage. 

For this stage, you'll build upon the database scheme you developed in Stage 1 to develop the UI that will allow users to add and remove products from the inventory, review details about a particular product, and see all the currently available products in stock. 

Here are some suggested steps: 

* Data Persistence 

This app will use a ContentProvider backed by a SQLite database. This ContentProvider is updated whenever changes are made to the database (remember the CRUD methods). 

Map out the interactions between the data, the Content Provider, Loader, Adapter, Activities/Fragments. 

* User Interface - Layout and Function 

Mock up the app's navigation flow and the activities' layouts. How will users access the activities to Add Inventory, See Product Details, Edit Product Details, and See the a Catalog of all Inventory? (Hint: It can be helpful in designing to consider each of the tasks above its own separate activity or fragment and the different navigation experiences we've covered - Navigation drawer, ViewPager, Up/Back Navigation, Intents) 

Build out the activities and/or fragments that will handle adding, removing, and editing items from the inventory database. 

* Note About Adding Images 

In the Product Details Activity, adding the functionality for the user to pick an image from the gallery is not required as it was not covered in the course. For students interested in implementing it, resources are available in the forum here. 
