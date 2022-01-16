# KATE, a small framework for event driven request/ response with Kafka

## Copyright
The KAfka requesT/ response framework with Events is developed by Kees Broenink in 2021.
It can be freely used but cannot be adjusted or forked. Contact me for more information keesbroenink@gmail.com

## Basic concepts event driven request/ response
Most web applications talk to the server using HTTP which is a synchronous protocol. Also a lot of webservices (microservices)
talk to each other using HTTP. Kate will help you to change this and make your software landscape truly asynchronous.

The current approaches we see most of the time are a combination of request-response and event driven where the latter is
used to 'let the world know that something has happened'. What is missing here is the combination of request-response with
an asynchronous message communication system. It is really hard to build software systems that only react on events to do some work. 
We always have a need for commands and queries that should be answered. Kate will provide this missing part.

The architecture of Kate is very straightforward. Requests are delivered to the request bulletin board topic. Back-end services
will subscribe to the topic and Kate calls the callback in which the 3GL developer can write the code to answer the request.
The 3GL calls a Kate API sender that will post the answer to the response bulletin board topic.
The back-end service that submitted the original request will write the callback to receive the answer. If the
request originated from a webclient the answer will then be delivered to the client.

The framework tries to impact your microservice code to the very minimum. When using a software architecture where the 
domain layer is central and technology-agnostic (e.g. onion or hexagonal) you are able to use Kate
without many changes. The main change will be that the business services will deliver their answers not by returning
them with the service method. Services will have void return values (errors should still be returned or raised).
Requests will be put on Kafka (or similar middleware technology with publish/ subscribe support). Responses will come in 
on a different thread and the microservice will handle them in callback methods enabled by Kate. The microservice developer 
will be responsible to handle the response but Kate will help with convenience utilities to send messages and to remember 
requests when the developer has a need for them. 
Services that have no outbound needs (do not call other services) will not implement response callbacks. 
The original caller of the service will implement the callback and operate on the response, possibly by delivering the 
response to the original webclient. 

The Kate architecture is fully compliant with a multi-instance micro-service architecture. A request consumer for a certain
domain service will always use the same Kafka consumer group name. In this way every request will be handled by exactly one instance.
The responses on the other hand are read by all instances of the domain services that could need the answer. 
Kate wants to be flexible and light-weight and doesn't manage an administration which instance handled the request. 
But Kate makes sure that only the callback of the instance that handled the request will be triggered to handle the response. 
Other instances will ignore the response. The consumer group name for the responses is appended with the OS process id to 
make it unique.

## Reactive Manifesto

The Reactive Manifesto https://www.reactivemanifesto.org/ sets the stage for modern software architectures to support high 
performant reliable systems with many users.
The Kate framework fits in here very well. It addresses the Message Driven principle because request and responses will 
be real asynchronous messages.
Kate uses Kafka. Kafka is a robust reliable message system that scales very well. If you combine this with microservices 
on e.g. Kubernetes, the whole server landscape will become Responsive, Resilient and Elastic. 
When a microservice (Kafka consumer) crashes, another instance will pick up the message again
because messages will stay on Kafka. Only on successful processing the consumer will commit the new read offset.
Kate could easily be extended with support for similar systems like Kafka.

## Technical dependencies

See build.gradle.kts

- JVM Java 8 or newer
- Spring Boot 
- Spring Boot Web (DeferredResult)
- Spring Kafka
- Kafka

Used in the examples
- JUnit 5 (Jupiter)
- springdoc-openapi-ui

## Events

An alternative to request/ response is to notify that something has happened. With Kate you can publish an Event message on 
the event bulletin board topic.
Microservices can write a callback handler to react on messages of a certain type.

## Errors

Kate supports notifying error events. It works the same as Kate events but uses a different topic: the error bulletin board topic.
Microservices can write a callback handler to react on error messages. The following setting is needed to make this work:

```
kate.consumer.event-topics: ${kate.default-event-topic}, ${kate.default-error-topic}
```

## Examples

### Car sell advice
The test directory contains a cars example to help you build microservices with Kate. Package: `org.kate.examples.cars`

If offers a simple HTML webclient by means of Swagger on http://localhost:9090/cars/swagger-ui.html

The cars example consists of four microservices:
- web-svc: the web layer to receive HTTP car requests, call a service to handle the request and deliver the response to the web-client
- car-advice-svc: the calculation of the value of a car and the advice if you should sell or not
- car-value-svc: the value of a certain car (used by car-advice-svc)
- car-bonusvalue-svc: certain car types have extra value (used by car-advice-svc)

Note that the examples are very basic and have no clear domain datatypes and validation. Its purpose is to show how you can
build microservices using Kate. The car-value-svc is a bit more elaborate and follows the software architectural pattern
onion/ hexagonal (but without validation). Also no security aspects are taken into account. It is educational software.

The web service uses DeferredResult to communicate in an asynchronous way with the web client. When the request is not answered
within a given amount of microseconds the web client receives an HTTP status 408 (TIMEOUT). It is easy to extend Kate with other
asynchronous protocols and frameworks (websockets, Flux).

The car advice service has an extra challenge because it needs to call two services in parallel. And only when both answers
are received, it can deliver the advice to the Kafka response bulletin board. Study the code carefully to learn how this can be done. 

The car value and car bonus value service are very simple services that register a request callback and return the car value
or car bonus value to the reply topic that is provided with the request. So even if these services are called from another
domain with possibly a different Kafka response bulletin board, the answers will be delivered to the right place.

The car example comes with a Spring Boot main and can be executed by running the main. Note that a better way of using and 
testing Kate is to deploy the four services as separate applications (with multiple instances). TODO: create four JAR files 
and Docker files. Also create a separate JAR with all request and response objects. 
 
