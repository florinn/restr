Restr [![build badge](https://travis-ci.org/florinn/restr.svg?branch=master)](https://travis-ci.org/florinn/restr)
===================

The project provides some helpers for creating REST APIs. It enables declarative mapping of Java exceptions to REST errors and building REST representations following [HATEOAS](http://en.wikipedia.org/wiki/HATEOAS) principles.

----------


Features
-------------
* Map Java exceptions to REST errors using a config file
* A REST error includes: 

> - HTTP status code
> - specific error code (useful to discriminate between errors having the same HTTP status code)
> - end used friendly message
> - developer oriented information about the error

* Replace exception details with null or static messages (useful in production to hide runtime error messages)
* Associate entity classes to resource representation classes
* Customize resource representation classes 
* Support nested representations and entity references
* Get JSON from a resource representation instance


Installing
-------------
Add this Maven dependency to your build:
```xml
<dependencies>
    <dependency>
        <groupId>com.github.florinn</groupId>
        <artifactId>restr</artifactId>
        <version>${restr.version}</version>
    </dependency>
</dependencies>
```


Usage
-------------

### Map Java exceptions to REST errors

To configure the mapping you need to add a file called `rest-errors.config` as a resource to your project (usually in a directory called `/src/resources`), with content similar to:

```
# 400
java.lang.IllegalAccessException =       400 | 01 | Bad request. | Bad request.
java.lang.InstantiationError =           400 | code=02 | msg=Bad request. | devMsg=Request is missing header x.
java.lang.IllegalArgumentException =     400 | 03 | Bad request. | <exmsg>
java.lang.IllegalStateException =        400 | Bad request. | <exmsg>
java.lang.IllegalMonitorStateException = 400 | <exmsg>

# 403
java.lang.SecurityException =            403 | 05 | Credentials authenticating this request are not authorized to run this operation. \
                                                  | Credentials authenticating this request are not authorized to run this operation.

# 412
java.lang.OutOfMemoryError =             412 | 01 | null

# 500
Throwable =                              500 | Server unexpected error, retry request. | <exmsg>
```

** Note: **

- mapping order is top to bottom
- nested exceptions are matched by first found parent exception
- comment lines start with `#`
- values should be delimited by `|` and specified using either as **key=value** (possible keys: `code`, `msg`, `devMsg`) or only as **value** omitting the key
- `<exmsg>` inserts the exception message 
- `code` can be overriden when calling `RestExceptionMapper.getRestError`
- `null` means a null value
- `\` allows continuation on the next line

To get the matching REST error:

```java
RestError error = RestExceptionMapper.getRestError(exception);
```

** Note: **

- `RestExceptionMapperProvider` is a JAX-RS exception mapper available to use in your project. This exception mapper considers the innermost exception when trying to match a REST error.
- to be able to pass a `code` value to the `RestExceptionMapperProvider`, you may subclass any custom exception from the provided `BaseException` or `BaseRuntimeException`


### Build REST representations

###### Resource representation class

To create a representation class for an entity:

```java
public class UserRepresentation extends Link<User> {

    public UserRepresentation(String fqBasePath, User user) {
        super(fqBasePath, user);
    }

}
```

** Note: ** `fqBasePath` is the fully qualified base path

The representation class is a map of **(entity property name, entity property value)**, so you may customize it rather easily. E.g. 

```java
public class LocationRepresent extends Link<Location> {

    public LocationRepresent(String fqBasePath, Location location) {
        super(fqBasePath, location);
        
        Map<String, Object> links = new LinkedHashMap<String,Object>();
        
        URI calendarUri = UriBuilder.fromPath(
                RestResourceDefinitionRegistry.getResourceDefinition(Calendar.class).getPathTemplate())
                .build(location.getUser().getId());
        Link<?> calendarLink = Link.from(fqBasePath, calendarUri.getPath());
        links.put("calendar", calendarLink);
        
        this.put("meta", links);
    }

}
```

###### Entity-representation association

To define an association between an entity class and a default REST representation class:

```java
String usersPath = "/users";
RestResourceDefinition<User, UserRepresentation> userResourceDefinition = 
                new RestResourceDefinition<User, UserRepresentation>(User.class, UserRepresentation.class, usersPath) {

            @Override
            public URI getPath(User user) {
                URI path = UriBuilder.fromPath(getPathTemplate()).build();
                return path;
            }

        };
```

** Note: ** `getPath` is used to construct the `href` field pointing to the `uri` for the resource. You may use the path template (and corresponding values for any placeholders) as shown above.

**The association above needs to be registered with `RestResourceDefinitionRegistry`:**

```java
RestResourceDefinitionRegistry.registerResourceDefinition(userResourceDefinition);
```

###### Resource representation instance

To get a resource representation instance from an entity:

```java
User user = new User("jdoe", "John Doe");
EntityRef<String, User> userRef = EntityRef.from(user);
Location.GeoCoordinate geoCoordinate = new Location.GeoCoordinate(123456, 654321);
Location location = new Location("xyz", userRef, geoCoordinate);

Link<Location> locationLink = LinkFactory.getLink(fqBasePath, location);
```

To get the JSON corresponding to a representation instance:

```java
userLink.asJSON();
```

```json
{
    "href": "http://localhost/users/jdoe/locations/xyz",
    "id": "xyz",
    "user": {
        "href": "http://localhost/users/jdoe"
    },
    "geoCoordinate": {
        "latitude": 123456.0,
        "longitude": 654321.0
    },
    "meta": {
        "calendars": {
            "href": "http://localhost/users/jdoe/calendars"
        }
    }
}
```

** Note: **

- `EntityRef` may be used as a reference to an entity instance, it contains the class name and the id of the resource instance
- `EntityRef` types nested in `Entity` types get represented as `href` links
- Nested `Entity` types get represented using their default representation class

To get an `href` link (which could be used to implement representation expansion for resource listing):

```java
Link<User> userLink = Link.from(fqBasePath, user);
```

```json
{
    "href": "http://localhost/users/jdoe"
}
```

** Note: ** Any valid JSON within entities gets represented as it is