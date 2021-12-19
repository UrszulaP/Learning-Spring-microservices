package io.javabrains.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfo {

    @Autowired
    private RestTemplate restTemplate;

//    Hystrix tworzy proxy class. Przychodzący request tak naprawdę woła metodę proxy. Jak wszystko jest ok, proxy woła główną metodę, a jak nie, metodę fallback.
//    Kiedy wołamy metodę oznaczoną @HystrixCommand z wewnątrz naszej klasy, Hystrix proxy jej nie wyłapuje.
//    Rozwiązaniem na to jest przeniesienie tych metod do nowej klasy (każda klasa ma swoje proxy).

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject(
                "http://movie-info-service/movies/" + rating.getMovieId(),
                Movie.class
        ); // not a real url - protocol://<service name in Eureka service discovery>/<endpoint>
        return new CatalogItem(movie.getName(), "Desc", rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie name not found", "", rating.getRating());
    }

}
