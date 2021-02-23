# exunion

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8f2a3cdd2123424babc2a1d5e2806e01)](https://www.codacy.com/manual/robothyluo/exunion?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Robothy/exunion&amp;utm_campaign=Badge_Grade)

exunion is a Java lib that aggregate the main cryptocurrency exchanges' API and provide uniform interfaces. 
Based on exunion, you can easily apply your excellent quantitative trading program on different cryptocurrency exchanges.

## Installation

todo

## Usages

`ExchangeServiceProvider` is the core API of exunion, it takes the exchange service generation responsibility. 
You can get most services' instance through `ExchangeServiceProvider.newInstance(exchange, serviceClazz, options)`. 

The `newInstance` method has three parameters:

+ exchange – the exchange that provide the service.
+ serviceClazz – the exchange service clazz. For example: DepthService.class
+ options – the options to initialize the exchange service instance.

Here are more concrete details and samples about the usages of exunion. 

### Account

```java
```

### Market Data

```java
```

## Contribution

todo