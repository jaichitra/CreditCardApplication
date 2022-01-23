# CreditCardApplication

Sample Application to add and view Credit Cards.

The Credit Card Application consists of following modules.

1. CreditCardApplicationGateway
2. CreditCardDiscoveryServer
3. CreditCardService

Tech-stack used to implement the solutions are - Sping Boot, Microservices architecture, 
Spring Boot Cloud including Eureka Service Registry and Discovery, Ribbon Load Balancer, 
Zuul Api Gateway, H2 In memory database(File based memory), Spring basic Authentication, Junit and Mockito.


**CreditCardApplicationGateway**

A Api gateway to act as a facade for the CreditCardService APIs.

Can be Accessed through the URL:

http://localhost:8081/ccvendor/**

This service fetches the registry for the CreditCardService and directs the calls to the available service
application in round robin fashion, through Ribbon internally.

**CreditCardDiscoveryServer**

Eureka discovery service for registry and discovery of the CreditCardService Application.
The registry is utilized by ApplicationGateway for directing the calls to available instances and by 
CreditCardService application for internal db cache update(discussed in later sections.)

**CreditCardService**

This application encloses the main functionality of adding and fetching of CreditCard data.
Uses a In memory H2 database per instance in a file based db type configuration.
The H2 database created at start is not shared by other instances and each CreditCardService instance has its own
copy of database separate from the database instance of other Application instances.

This application is configured to load on a dynamic port and not intended to be called directly by determining the 
current running port of the individual instance. But to be called through Api gateway application.

Following are the APIs exposed for basic credit card operations:
The below given API can be invoked using the basic authentication per credentials mentioned in the passwords.txt places in src/main/resources.
In a production/ideal scenario these credentials would be loaded from a dedicated passwords vault server or from a database or ldap.
 
**HTTP POST**
/v1/creditcard
Auth Roles Allowed - ROLE_ADMIN, ROLE_USER
Consumes and produces - Json Object in below format: 

Sample Request:

{
	"cardNumber":"4617019281263712",
	"applicantName" : "John",
	"cardLimit": 3013000.0
}

Sample Response:

{
    "cardNumber": "4659427091358961",
    "applicantName": "John",
    "limit": 3013000.0,
    "balance": 0,
    "cvc": "395",
    "validFrom": "01/22",
    "validThru": "01/26"
}

Api Gateway URL : http://localhost:8081/ccvendor/v1/creditcard



**HTTP GET**
/v1/creditcards
Auth Roles Allowed - ROLE_ADMIN

Produces - Json Object in below format:

Response:
[
 {
    "cardNumber": "4659427091358961",
    "applicantName": "John",
    "limit": 1003000,
    "balance": 0,
    "cvc": "395",
    "validFrom": "01/22",
    "validThru": "01/26"
 }
]

Api Gateway URL : http://localhost:8081/ccvendor/v1/creditcards



**HTTP POST** [RESTICTED USE - API not available for call from a User, internally used by app to Update/Sync DB Caches]
/v1/dbsync

Produces the card data stored


**HTTP GET** [RESTICTED USE - API not available for call from a User, internally used by app to Update/Sync DB Caches]
/v1/dbsync

Accepts the card data collection



**Validation**

The request passed to add a credit card data has 3 parameters - Card Number, Card Applicant Name and Card Limit.

Card Number is validated and is expected to be 16-19 Characters long and shall comply with Luhn10 Number format.
Card Limit is validated for a minimum and maximum limit as pre configured in application properties.
Card Applicant is expected to be Not-Empty.

**Maintaining Resilience**

Since every instance has there own in-memory db instance, to maintain resilience and to recover from any data loss dbSync/appSync 
is used internally between multiple instances. Also, to support this CreditCardService shall have more than 1 instance running.

AppSync/DbSync

On adding a card detail to db in a service instance, a async process gets invoked which fetches all the registered service instances
from the discovery client, accomplished through class implementation of AppSyncService. It then broadcast the current state of the 
in-memory database to other available instances. The API invoked through this service (/v1/dbsync) are restricted and
Auth protected with allowed roles - ROLE_SYS_ADMIN.

The FilePasswordManager class internal implementation which loads the User Credential data has a check to avoid loading any external user 
with ROLE as ROLE_SYS_ADMIN. As the API ("/v1/dbsync"") are not intended to be used by any user but the system itself.

Similarly when a new instance is started the AppStartSyncService is invoked on ApplicationReady event and tries to fetch the db cache 
from other available instances and do update its own instance of database data.

By following this approach its guaranteed that a card added to a instance of CreditCardService would be in sync with available instances.

BUT, This approach introduces a limitation.

**Limitation**

As AppSyncService and AppStartSyncService class does the sync up of the database data among available peers. 
There are chances where a fetch request is invoked while the sync is still in process, and the data fetched would not be updated
to the latest.

There are two approaches to solve this shortcomings:

1. By implementing a Custom LoadBalancer at CreditCardApplicationGateway application. Which would select a leader among all the availble
server instances of CreditCardService and direct all the add and fetch request to the selected Leader. On every request it can check
whether the leader instance is alive, and when for some reasons leader instance is not reachable it can select a new leader from the available
instances and start directing requests to this new leader. The functionality in AppSyncService would ensure that its memory instance would
have same up to date data as the previous leader.

2. By using a clustered dedicated database server instances.
