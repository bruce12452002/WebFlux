package home.bruce.webfluxPractice.controller;

import home.bruce.webfluxPractice.entity.Animal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
public class WebClientAndRestTemplate {
    @Value("${server.port}")
    private String port;

    @GetMapping("useRestTemplate")
    public List<Animal> getAnimals3() {
        Instant start = Instant.now();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Animal[]> entity = restTemplate.getForEntity("http://localhost:" + port + "/allAnimal", Animal[].class); // http:// 也得寫
        List<Animal> animals = List.of(entity.getBody());

        Duration du = Duration.between(start, Instant.now());
        System.out.println("RestTemplate 花了=" + du.toMillis()); // 測試結果為 3352 ，為阻塞

        return animals;
    }

    @GetMapping("useWebClient")
    public Flux<Animal> getAnimals2() {
        Instant start = Instant.now();

        WebClient.RequestHeadersUriSpec<?> r = WebClient.create("localhost:" + port + "/allAnimal").get(); // 使用 GET 方法
        Flux<Animal> animals = r.retrieve().bodyToFlux(Animal.class);// Animal 類別必需有預設建構子

        Duration du = Duration.between(start, Instant.now());
        System.out.println("WebClient 花了=" + du.toMillis()); // 測試結果為 31 ，為非阻塞

        return animals;
    }
}
