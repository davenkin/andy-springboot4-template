- package: common/integration/api
- 不用webclient，因为没有必要引入一个reactive的库来做rest api调用，rest client已经足够了
- 2 types of rest client: service account rest client and token relay rest client
- Use [RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient) for
    calling remote APIs. Do not use Webclient as it's from the Webflux ecosystem. Do not use RestTemplate as it's already
    marked as deprecated. Do not use HTTP Service Clients (`@HttpExchange` etc.) as it requires extra configuration.
