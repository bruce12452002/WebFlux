package home.bruce.webfluxPractice.controller;

import home.bruce.webfluxPractice.entity.Animal;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class HelloWebFlux {
    @GetMapping("hello")
    public Flux<String> helloWorld() {
        return Flux.just("Hi WebFlux");
    }

    @GetMapping("mono")
    public Mono<String> helloMono() {
        return Mono.just("Hi Mono");
    }

    @GetMapping("interval")
    public Flux<String> interval() {
        return Flux.interval(Duration.ofSeconds(1)).map(v -> "v=" + v + " ");
    }

    @GetMapping(value = "interval2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> interval2() {
        return Flux.interval(Duration.ofSeconds(1)).map(v -> "v2=" + v);
    }

    @RequestMapping(value = "sse", method = {RequestMethod.GET})
    public Flux<ServerSentEvent<Long>> sse() {
        return Flux.interval(Duration.ofSeconds(1)).map(v ->
                        ServerSentEvent.<Long>builder()
//                        .id("123") // 可為空或 null
//                        .event("evt") // 可為空或 null
                                .data(v)
//                        .retry(Duration.ofSeconds(3)) // 可為空或 null
                                .build()
        );
    }

    private List<Animal> animals = List.of(
            new Animal(3, "虎"),
            new Animal(1, "鼠"),
            new Animal(4, "兔"),
            new Animal(2, "牛"),
            new Animal(5, "龍"));

    @CrossOrigin
    @GetMapping("eventSource")
    public Flux<ServerSentEvent<Long>> eventSource() {
        return Flux.interval(Duration.ofSeconds(1)).map(v ->
                ServerSentEvent.<Long>builder()
                        .event("xxx") // 可為空或 null，前端只會接收 xxx 事件或 onmessage 事件，不會兩個都接收
                        .data(v)
                        .build()
        );
    }

    @GetMapping(value = "animals", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Animal> getAnimals() {
        return Flux.range(1, 3).map(v ->
                animals.stream().filter(a -> a.getId() == v).findFirst().get()
        ).delayElements(Duration.ofSeconds(1));
    }

    @GetMapping("allAnimal")
    public List<Animal> getAllAnimal() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return animals;
    }

    @PostMapping("setAnimal")
    public Mono<Animal> setAnimal(@RequestBody Mono<Animal> animals) {
        // 使用 map
        //        return animals.map(a -> {
        //            a.setName(a.getName() + "xxx");
        //            return a;
        //        });

        // 使用 flatMap
        return animals.flatMap(a -> {
            a.setName(a.getName() + "ooo");
            return Mono.just(a);
        });
    }

    @GetMapping("param")
    public Mono<String> param(@RequestParam int id, @RequestParam String name, ServerHttpRequest req) {
        return Mono.just(req.getURI().toString());
    }

    @GetMapping("param2/{id}/{name}")
    public Mono<String> param2(@PathVariable int id, @PathVariable String name, ServerHttpRequest req) {
        return Mono.just(req.getURI().toString());
    }

    @GetMapping("listParam")
    public Mono<String> listParam(@RequestParam List<String> name, ServerHttpRequest req) {
        name.forEach(System.out::println);
        return Mono.just(req.getURI().toString());
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @GetMapping(value = "except", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Animal> except() {
        return null;
    }

    @PostMapping("log")
    public Mono<Boolean> log() {
        return Mono.just(true);
    }
}
