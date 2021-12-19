package io.javabrains.moviecatalogservice.resources;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.javabrains.moviecatalogservice.models.CatalogItem;
import io.javabrains.moviecatalogservice.models.Movie;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog") // tells Hystrix what to break; sets fallback mechanism
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // 1 - get all movies that were rated by the user
        UserRating ratings = restTemplate.getForObject(
                "http://ratings-data-service/ratingsdata/users/" + userId, // not a real url - protocol://<service name in Eureka service discovery>/<endpoint>
                UserRating.class // If response type is a list, we have to set type to sth like ParametrizedType<List<Rating>> - can't use List<Rating>
        );

        // 3 - bind and return data
        return ratings.getUserRating().stream().map(rating -> {
            // 2 - get rated movies details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class); // not a real url - protocol://<service name in Eureka service discovery>/<endpoint>
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());
        }).collect(Collectors.toList());
    }

    // fallback response should be simple, hardcoded, at most taken from the cache
    // we shouldn't do any other call in fallback, because it can also fail and we would have to handle that too
    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
        return Arrays.asList(new CatalogItem("No movie", "", 0));
    }
}
