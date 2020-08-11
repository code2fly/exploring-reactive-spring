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
			* benefit of reactive stream types is that they give us a way to say "hey i have got a thing thats gonna produce some data and i want to subscribe to
			that data when it is avaialable (async by default)"
			* since we have registered the intent to recieve the data when it is avaialable we dont have to wait for that data and someone else can use that thread.
			* but that is all we get from reactive streams there is no plumbing involved on how we can process streams of data ,that is all up to us ( we generally
			dont work at this level so some toolkit can be used for this purposes like in spring we can use project reactor. )
		
		* web sockets in spring 4.0 was built over project reactor
		* project reactor is a toolkit that allows us to work with reactive stream types and allows us to process data coming from this reactive streams.
		* spring reactive web brings in Spring WebFlux and not MVC, webflux is a new reactive web runtime that allows us to build reactive web services in spring ecosystem.
		
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