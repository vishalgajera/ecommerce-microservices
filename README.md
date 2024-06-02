## Ecommerce Microservices APIs

I've created the following APIs to cover the basic use cases of the e-commerce application.

- [API Gateway] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/api-gateway)
- [Discovery Service] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/service-registry)
- [Auth Server] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/auth-server)
- [User Management Service] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/user-service)
- [Product Management Service] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/product-service)
- [Shopping Cart Service] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/shopping-cart-service)
- [Order Placement Service] (https://github.com/vishalgajera/ecommerce-microservices/tree/main/order-service)

## Architecture Diagram



![ecommerce-microservice-diagram-1x](https://github.com/vishalgajera/ecommerce-microservices/assets/12990664/0425f314-5270-4a93-b060-e4ced5fa6a47)


## Description of each Service

### API Gateway
- Entry Gateway from where user's request will enter into the our entire microservices word (Assume, our APIs are now for the internal use only & will not have any direct access from the ourside world except authorized server for the security reason)
- Each service will be accessible via this service since we've configured necessary path/predicates with load balance feature ON. so that any service goes down then load should get balanced automatically. High Availablity.
- Each service is accessible via it's "service id/name". that means it's nutral from service's IP address, Port, hard-coded URL etc. which help our architecture to be fault tolerance & easy to maintain

### Discovery Server
- Each Service will treat as "Discovery client" & will register with "Discovery server" to maintain the central place of availablity & load balancing via services name.
- we've also disablied to do a self registration

### Auth Server
- Central place for the Authenticate & Authorization purpose
- It'll issue JWT token while successfully login (within response under x-authentication header-name)
- This JWT token we can pass within Http Request under "Authorization" header to call any other API & each API will validate the incoming request via Auth-Server

### User Service
- User service will be responsible for registration, login validation, user-level address maintaining & other personal details

### Product Service 
- It will be responsible for Add/Update/Delete Products including it's available-qty attribute which is crucial for "concurrance control" feature in this project which we've achieved via "Optimistic locking" technique.

### Shopping Cart Service
- It'll present current shopping-cart status at user level. so that whenver user'll go for a checkout. at that time all of the cart products will participate to place an order.

### Order Placement Service
- It'll be in a picture once user decide to place an order.
- Certain Validations are kept as of now but it's not limited to the scope of this application. for now, I've shared very short & mandatory use-case based validations which are as follows:
  -- Check each cart Item is available (not in out-of-stock status)
  -- Place order with given items & reduce the placed quantify from the total preset against each product. so that "Net Available Quantity" proposition maintains the accuracy.
  -- After successfully done above validations. I've cleared cart with consideration that, next time new order will going to be place.
- I've introduced multiple Order Status e.g. Placed, In Transit, Delivered, Cancelled. but it's not limited to this scope. in real-world we may have even more from the defined list.


## Suggesting further improvemetns on top of current implementation

### API Gateway
- we can introduce Rate limit here itself based upon analysing real use case & demand/traffic
- Multiple instances of this api

### Auth Server
- we should define multiple strategy to define the authentication, authorization path e.g. Key Clock framework

### User Service
- Multiple validation we can introduce at each field & personalized experience we can introduce here.

### Product Service
- Multiple validation we can troduce against each field & can separate out "Available Quanity" feature from product entity to different level like, area(warehouse) level available Quanity & many more
- we can apply multiple government complinace here as well. e.g. restricted products like Drugs etc. must be blocked immediately
- we can also put checks about each product expiry related details, country-of-origin etc
  -- Ratings/Like/Comments against each product, this is another BIG topic which comes under product. so that our end user can have more trust over their purchase & we can gain more trust for our platform
  -- Discount/Sale etc can comes at any time on our platform so according to it many things would take place into our entire accounting areas including Profit/Loss.

### Order Placement Service
- Order Cancellation I've not covered in existing code but due in the interest of time, I've not covered. but it's definately "mandatory" condition. Against each cancellation(if it's not yet delivered) then we should have to add those Quanity back to available once it reach back to warehouse.
- we can split the order according to the early delivery based upon items (when multi item present into the order)
  -- Delivery service is another biggest topic to be consider incluging multiple delivery services e.g. 1-day(Fastest), 2-day(Standard) mode of a delivery

### Notification
- Mobile/Web Push Notification
- Email
- SMS
  

## Improvements : Technology Stand point on top of current architecture

- Docker, Kubernates
- Asynchronouse non-blocking calls, Event Driven architecture (Apache Kafka, AmazongMQ ...etc), Reactive implementation
 
  
