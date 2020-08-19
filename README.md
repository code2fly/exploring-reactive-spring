## Reactive Spring - 

#### Main question, how do we handle more users/ how do we handle more api requests - 
  - reactive programming is a way to make more efficient use of threads .
	
	* could we do better with our existing nodes?? say in a microservices placed behind load balancer and scaled out but could we do better with our existing nodes?
		* how to handle more request per node? where are we being bottlenecked and why???
			* though it depends on different things in different techstack but for JVM developers the answer is threads
			* lot of things we do in apps today deal with IO so even if app goes out of thread the cpu usage is still low, the traditional impl with IO streams does not lend itself well
			to this since they are blocking and the thread is sitting idle on waiting state doing nothing so that is what we are trying to solve with reactive programming model
			a way to say let me know once this IO task is done instead of the main request thread waiting for it to complete.
	
	* question is if we have had these concepts of NIO since java 1.4 then why aren't we using it as a default if we already know most of the time JVM is idle waiting for some IO response - 
		* people have tendencies to fall for the fallacies/misbelief of distributed computing like - 
			- misconceptions like assuming data in n/w is infinitely fast and it will never fail, assuming next byte is right around the corner, once the data is flowing in then we will get all data and nothing is going to stop it from coming.
		* using java nio is not easy or natural
		* we dont work at that level, we don't write code working with IO streams or NIO directly. we work in terms of higher abstraction
	
	* no. of organisations have tried to create api that helps us describe asynchronous computational processing/async multithreaded processing.
	* in 2015 few major players got together and decided to extract out for simple interfaces that are part of reactive streams specifications that describe
	async unbounded streams of data.(reactive-streams.org)
		* reactive stream spec provides us foundational types that we can depend upon like common currency that can use to describe asynchronous streams of data
		potentially unbounded latent streams of data.
			* benefit of reactive stream types is that they give us a way to say "hey i have got a thing that's gonna produce some data and i want to subscribe to
			that data when it is avaialable (async by default)"
			* since we have registered the intent to recieve the data when it is avaialable we dont have to wait for that data and someone else can use that thread.
			* but that is all we get from reactive streams there is no plumbing involved on how we can process streams of data ,that is all up to us ( we generally
			dont work at this level so some toolkit can be used for this purposes like in spring we can use project reactor. )
		
		* web sockets in spring 4.0 was built over project reactor
		* project reactor is a toolkit that allows us to work with reactive stream types and allows us to process data coming from this reactive streams.
		* spring reactive web brings in Spring WebFlux and not MVC, webflux is a new reactive web runtime that allows us to build reactive web services in spring ecosystem.
		
	* Check this for reactive https://dzone.com/articles/5-things-to-know-about-reactive-programming
	 
#### Basics of Reactive -  
  * The 4 main types reactive types are - 
    - Publisher - it broadcasts data to subscribers 
    - Processor - its a bridge.(a publisher and a subscriber)
    - Subscriber - it consumes the data. (in onNext method). When subscriber first subscribes to a publisher a new subscription is created. 
    - Subscription - this is the thing that we use to request more data from the publisher. i.e. it drives the rate of consumption. so publisher cannot overwhelm 
    subscriber with data since we are managing how much data we want. (this flow control of taking as many as we can and cancelling if we cannot consume more is called backpressure)
    
  * we don't directly implement these interfaces and in order to do seemless use of rate limiting or other things on streams of data, for this we can use tools like project reactor,
  it builds on top of reactive stream specification and provides 2 other types .
    * **Flux** - it is a thing that produces zero or more values. (its ultimately Publisher but supports high level operations such 
    as map or flatmap or filter etc.)
    * **Mono** - a thing that produces at most one value. It is like a CompletableFuture.
    
  * mongodb has a concept of `tailable query` if db is started in replicated mode. This is particularly useful when working with reactive application.
    *  since mongo can be distributed db they prefer UUID for id field over monotonically incrementing numbers.
    
    
  * **Transactions in reactive world** - 
    * since we are no more staying on the same thread during a transaction we cannot rely on the traditional approach of 
    ThreadLocal(map of key value stored on a current thread, with key being currentthread name and value being any value) that spring and everything until now used.
    
    * so we need some other way to propagate that state associated with  current ongoing transaction (and we cannot rely on threadlocal now with reactive)
    so reactive team created `Context`. its like a dictionary that gets propagated for the entirety of the pipeline.
    *  anywhere in the pipeline we can get access to the context say using `doOnEach(signal -> signal.getContext()`
    * spring uses this Context heavily in security while propagating the Authentication principal, in distributed tracing,
    transaction propagation (we can also use it for MDC)
    
  * **Http service** -
    * in spring reactive REST api can be designed in 2 ways - MVC style and Functional reactive style 
    * apart from creating normal REST api that we were also building with spring MVC , ***what reactive spring helps us in cases we 
    are at a risk of monopolizing a thread*** . e.g. whereever we want to keep a socket open(using traditional java i/o)
      * instead of returning one since value from a REST api, suppose we want to get access to latest data as quickly as possible.
      * this is a common scenario and a common implementation for such a case is say we might have something that uses a server socket that just keeps that socket open
      and we keep reading from that socket all the time (that way we dont have to connect again, or latency of setting up network call with acking, connecting, teardown etc.).
      * i.e. in above approach the socket remains open and a thread is assigned to that socket so that we can process new updates as soon as they arrive instead of repolling or reinvoking REST api.
      *  use cases for such kind of requirement or style - chat messaging, stock ticker(high speed low latency trading), status/presence notification
      * to do this in Http we can build a ServerSendEvent endpoint, this endpoint will give us a infinite stream of values.
        * `Server-Sent event` is a protocol that is based on Http, but when the client knows about the special content type it does not disconnect and keep pointing to that socket live.
    * Spring uses Scheduler behind the scenes for doing everything in reactive which is essentially a threadpool and Clock.
      * the threadpool by default has threads equal to number of cores and they are all individual event loop.
      * if there is any blocking call that hurts reactive api heaving hence in that case blocking code should be moved to different scheduler.
      * use project blockinghound to verify there is no blocking code and use `Schedulers.elastic()` if there is blocking IO that cannot be avoided .
      
  * **Web Sockets** - 
    * it is binary protocol(hence efficient) that supports bi-directional communication .
    * they are often used in tandem with http so basically they are an extension to our http interface and our http application and not a replacement.
    * they go really well with reactive stream specifications
    * spring webflux makes it very easy to write WebSocket application
    * http is good but it does not do well when it comes to bi-directional communication, so websockets are a slightly better option.
    * can be an alternative to server-sent event stream.
    * for someone to start using websocket they have to connect to an http endpoint that then gets upgraded to WebSocket protocol.
    
    
  * **Http Client** - 
    * `WebCleint` is used to perform http client request like RestTemplate.
    * webclient also provides many options like `onErrorResume` etc.. for ***graceful degradation*** for scenarios where the api we are trying to call 
    is down , or slow or failing.
      * graceful degradation - ability of system to maintain limited functionality even when large part of it has been destroyed to avoid catastropic failure, 
      i.e. to avoid minimal effect to reach client and try to mitigate it someway either getting data from cache , returning default value, loadbalancing request to other servers.
    * but sometimes these operators to make our code robust in case server side topology changes are not helpful like in cases we know the service 
    is not going to come up for sometime they is no point in retrying etc.. this is where ***circuit breaker*** comes in.
    * it is the mechanism by which prevents our service from overwhelming other downstream services or being overwhelmed themselves by doing 
    needless work of calling or retrying even if say service is down or not even present in discovery.
    * circuit breaker maintains a state i.e. say a api fails the flag against it is set and next time call to it will be avoided but suppose we 
    are using service registry based client side loadbalancing spring autoconfigures things for us.. say service is not avaialable for the first time
     for us say if there is a change in service discovery and we get a heartbeat telling that the table has new entry for the service we were looking for(i.e. that 
    service is up again) then the flag set in circuit breaker will automatically be set to false even if last interaction was a failure to make smart decision.
      * we can also set this flag of circuit breaker manually by configuring  `ReactiveCircuitBreakerFactory` (but it requires downcasting to 
      undelying reselience4j or hystrix types) // TODO check this how to do it.
    * ***Service Hedging*** - for ***idempotent services*** we can use the concept of hedging, i.e. getting multiple instances of the target service
    from discovery and then calling each of them in parallel and the first one to respond will be accepted and then backpressure will be applied on the other two.
      * this can useful in case we have a SLA on our service and the service below us needs to run half of our sla to make sure we have scope of retry etc so instead
      of us timing out the response we can say call all of them and pick the response from the one that replied the fastest.
      * this concept can be easily achieved in reactive with `Flux.first(flux1, flux2...)`
      
      
  * **RSocket** -
    * it is a binary protocol that supports full duplex (bi-directional) communication , meant to be an ideal technology/protocol for service to 
    service communication.
    * integrated into spring5.2 at different layers of the stack like security, gateway etc..
    * comparing rsocket with grpc - https://medium.com/netifi/differences-between-grpc-and-rsocket-e736c954e60
    
    * <ins>Motivations behind RSocket</ins> - 
      * in http client section we looked at different patterns to make our interactions with http services a bit more reliable , since we know services
          are going to fail , topology changes will happen (with http we can not do much about it, we just have to try again) 
      * we want services to be exposed such a way that they can be communicated in a natively reactive fashion i.e. we want clients to have options for control degradation
        of their service and behavior , see topology changes, better speed.
      * with http some of these options are not available , http1.0 is slow, we can use http2.0 which is
      binary hence efficient that http1.1 over the wire with other benefits but basically http is request response, it is not asynchronous(we can get the effect
      with server-sent events but its better to use a protocol that is built for this kind fo commn. like websocket).
        * websockets though being bi-directional protocol have their own limitation that we figure out security on our own(no native on the wire security, STOMP can be used in spring to carry security headers).
        * though http is great but it is not the best fit for high performance, low latency kind of microservice interactions particularly in a homogeneous datacenter env where 
        we have control over the nature of the services.
        * grpc though fast has its own limitations like every payload needs to be a protobuf, and using protobuf
        means we need code generation and grpc services that are code generated are async by default but not reactive by default.
          * one option is to do reactive grpc is to use a grpc plugin from salesforce (plugin for grpc compiler which transpiles
          grpc service definitions into language of our choice).
          * other option is RSocket, which provide reactive stream semantics.
        * check grpc vs rsocket https://dzone.com/articles/rsocket-vs-grpc-benchmark
      * rsocket provides 4 kinds of interaction model ***over single connection*** (http is pipelined while rsocket is multiplexed) - 
        * request/response - stream of 1
        * request/stream - finite stream of many in response
        * fire-and-forget 
        * channel - bi-directional stream
      * it even supports session resumptions to allow long lived streams across different transport 
      connections. (say we request first 10 records and then get disconnected say driving through a tunnel
      then once we are out it connected and we can get next 10 records.)
     
    * unlike grpc , rsocket is payload agnostic, it does not care if its json or protobuf etc...
    * apart from 4 main interaction model rsocket also supports metadata (like actuator in spring which gives health,uptime,metrics etc.. info about a service and we can 
    connect to it as a client to see health of a service to make smart client side loadbalancing decisions). but actuators are not a standard they
    are specific to spring boot and some other languages and frameworks have their own ways of doing this. 
      * we don't have any specs in http that tells us to have a /actuator for a service, but with rsocket we have this, `metadata push` is 
      where we as a service can advertise information for the client.
        * we know client can then use flow control or backpressure to demand that service can give it only enough data that it can process at a time.
        * this metadata push is similar functionality for server side, where service can say 'hey i am little busy now go bother someone else' etc..
        * way to signal availability to the client.
    
    * we can run rocket on different transports/protocols  like tcp using `TcpServerTransport` or websocket or aeron(high speed , memory efficient wire protocol.. i.e. rsocket on top of aeron)
      
    * with spring using rsocket becomes more easy and comes on with additional features like supplying 
    destination variable like path variable, routes to trigger different handlers etc.
    * an application can expose http endpoint and rsocket at the same time as shown in example in this repo.
    
    
  * **Security** - in reactive context
    * the basic motive here is since in reactive application the number of threads available are very finite and precious 
    we cannot keep them blocked for any crypto related work, and crypto being cpu intensive does not have any reactive meaning to it.
    * so all we are looking for is to make that exchange or interaction off the reactive thread pool.
    * in context of reactive apps this is what spring 5.x does, with ReactiveUserDetailsService and ReactiveAuthenticationManager these 
    things ultimately end up moving the brief time we are doing  cryptography part on to  a separate thread.
    * check Bcrypt - password encoding mechanism but computationally inefficient. (used widely today)
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    