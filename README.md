# Piper - System for automating smarthome appliances
## Engineering Thesis | Poznań University of Technology

## Preface
Modern smart home solutions provide a way to automate almost everything. Unfortu-nately it’s nearly impossible to handle all possible scenarios by manually created routines.There should be a way for houses to automatically adjust to human behaviour.

## Abstract
Piper is a machine learning based system, that is able to enhance smart homes with auto-mated creation of routines based on real life events. This work presents key aspects of thesystem, describes its development process and provides guides for prospective integratorsand users.  First chapter discusses motivation for the project and defines its purpose.It also compares Piper idea with existing smart-home solutions. The next one depictsarchitectural concepts used in the system. The third chapter documents characteristicsand implementation of Piper’s modules. It is followed by the section about deployingthe system. Next two chapters are guides for integrators and end users. They are fol-lowed by report from tests of the system. The paper is ended with conclusions designing,developing and testing Piper.

## Architecture
![schema](https://user-images.githubusercontent.com/32958017/118175799-ddf23500-b430-11eb-8c55-b4f03da5f3ff.png)

## Neural Network
![dataflow](https://user-images.githubusercontent.com/32958017/118175864-f19d9b80-b430-11eb-9f1c-2b1a3e22bc24.png)

## Division of work
* **Jakub Riegel:** Implementation of core back-end of the project, including public RESTendpoints for front-end and house clients, database management and integrating thedata across whole system. He also maintained the deployment server.
* **Wojciech Kasperski:** Creation of machine learning model and implementation of aservice responsible for building models used and service responsible for predictionand proposing routines that automate home. Designing the frontend layout for thebest user experience.
* **Rafał Ewiak:** Implementation of front-end application as client interface; Preparationof data models in front-end application: Creation of user interface components;Utilization of Home Service REST API in front-end application; Initialization ofREST API for Machine Learning.
* **Michał Kalinowski:** Creation of sample household required for testing the entire sys-tem, also as a template for future customers; Delivery of household asDocker1container; Handling errors for REST API of Machine Learning model serving service.
