package home.bruce.webfluxPractice.controller;

import home.bruce.webfluxPractice.entity.Animal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class WebClientTest {
    @Value("${server.port}")
    private String port;

    @GetMapping(value = "animals2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Animal> getAnimals2() {
        WebClient.RequestHeadersUriSpec<?> r = WebClient.create("localhost:" + port + "/animals").get(); // 使用 GET 方法
        return r.retrieve().bodyToFlux(Animal.class); // Animal 類別必需有預設建構子
    }

    @PostMapping("configureAnimal")
    public Mono<Animal> setAnimal(@RequestBody Mono<Animal> animals) {
        return WebClient.create("localhost:" + port + "/setAnimal")
                .post()
                .body(animals, Animal.class)
                .retrieve()
                .bodyToMono(Animal.class);
    }

    @GetMapping("paramTest")
    public Mono<String> param(@RequestParam int id, @RequestParam String name) {
        return WebClient.create()
                .get()
                .uri(u -> u.scheme("http")
                        .host("localhost")
                        .port(port)
                        .path("/param")
                        .queryParam("id", id)
                        .queryParam("name", name)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("paramTest2/{id}/{name}")
    public Mono<String> param2(@PathVariable int id, @PathVariable String name) {
        return WebClient.create("http://localhost:" + port)
                .get()
                .uri(u -> u.path("/param2/{id}/{name}")
                        .build(id, name))
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("listParamTest")
    // http://localhost:9000/listParamTest?names=ss,qe,er 或 http://localhost:9000/listParamTest?names=ss&names=qe&names=er
    public Mono<String> listParamTest(@RequestParam List<String> names) {
        return WebClient.create()
                .get()
                .uri(u -> u.scheme("http")
                        .host("localhost")
                        .port(port)
                        .path("/listParam")
                        .queryParam("name", names) // key 名稱要對應 /listParam 的參數名稱
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping(value = "exceptionTest", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Animal> exceptionTest() {
        WebClient.RequestHeadersUriSpec<?> r = WebClient.create("localhost:" + port + "/except").get(); // 使用 GET 方法
        return r.retrieve()
                .onStatus(HttpStatus::is5xxServerError, s ->
                        Mono.error(new MyException(s.statusCode().value() + ",我錯了"))
                )
                .bodyToFlux(Animal.class); // Animal 類別必需有預設建構子
    }

    class MyException extends Exception {
        MyException(String msg) {
            super(msg);
        }
    }
}
