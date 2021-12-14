# KATE, a small framework for event driven request/ response with Kafka

## Copyright
The KAfka requesT/ response framework with Events is developed by Kees Broenink in 2021.
It can be freely used but cannot be adjusted or forked. Contact me for more information keesbroenink@gmail.com

## Basic concepts event driven request/ response
Most webapplications talk to the server using HTTP which is a synchronous protocol. Also a lot of webservices (microservices)
talk to each other using HTTP. Kate will help you to change this and make your software landscape truly asynchronous.

The current approaches we see most of the time are a combination of request-response and event driven where the latter is
used to 'let the world know that something has happened'. What is missing here is the combination of request-response with
an asynchronous message communication system. It is really hard to build software systems that only react on events to do some work. 
We always have a need for commands and queries that should be answered. Kate will provide this missing part.

The architecture of Kate is very straightforward. Requests are delivered to a generic topic (request bulletin board). Back-end services
will subscribe to the topic and (enabled by Kate) check if they can answer the request. If so a request handling callback is called
and the 3Gl developer can write the code to answer the request and (enabled by Kate) post the answer to another generic topic (response bulletin board).
The back-end service that submitted the original request will write a callback (enabled by Kate) to receive the answer. If the
request originated from a webclient the answer will then be delivered to the client.

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

The Reactive Manifesto https://www.reactivemanifesto.org/ sets the stage for modern software architectures to support high performant reliable systems with many users.
The Kate framework fits in here very well. It addresses the Message Driven  principle because now even request and responses are real asynchronous messages.
Kate uses Kafka. Kafka is a robust reliable message system that scales very well. If you combine this with microservices on e.g. Kubernetes
the whole server landscape will become Responsive, Resilient and Elastic. When a microservice (Kafka consumer) crashes another instance will pick up the message again
because messages will stay on Kafka. Only on successful processing the consumer will commit the new read offset.

## Technical dependencies

See build.gradle.kts

- JVM Java 8 or newer
- Spring Boot 
- Spring Boot Web (DeferredResult)
- Spring Kafka
- Kafka

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
