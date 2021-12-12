# KATE, a small framework for event driven request/ response with Kafka

## Copyright
The KAfka requesT/ response framework with Events is developed by Kees Broenink in 2021.
It can be freely used but cannot be adjusted or forked.

## Basic concepts event driven request/ response
The framework tries to impact your microservice code to the very minimum. When using a software architecture where the 
domain layer is central and technology-agnostic (e.g. onion or hexagonal) you are able to use Kate
without many changes. The main change will be that the business services will deliver their answers not by returning
them with the service method. Services will have void return values (errors should still be returned or raised).
Requests will be put on Kafka (or similar middleware technology with publish/ subscribe support). Responses will come in 
on a different thread and the microservice will handle them in callback methods enabled by Kate. The microservice developer 
will be responsible to handle the response but Kate will help with convenience utilities to remember requests when the 
developer has a need for them. 
Services that have no outbound needs (do not call other services) will probably not implement a callback for the response. 
The original caller of the service will implement the callback and operate on the response, possibly by delivering the 
response to the original webclient. 

## Reactive Manifesto

TODO

## Technical dependencies

See build.gradle.kts

- JVM Java 8 or newer
- Spring Boot 
- Spring Boot Web (DeferredResult)
- Spring Kafka

Also defined but only used in the examples
- JUnit
- Spring Boot dev tools
- Spring Boot starter test
- Spring Boot Actuator
- Spring Boot data JPA
- Springdoc open api
- H2

## Events

TODO

## Errors

TODO

## Examples

TODO
