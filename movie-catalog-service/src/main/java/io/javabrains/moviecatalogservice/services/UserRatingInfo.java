package io.javabrains.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.javabrains.moviecatalogservice.models.Rating;
import io.javabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {

    @Autowired
    private RestTemplate restTemplate;

//    Hystrix tworzy proxy class. Przychodzący request tak naprawdę woła metodę proxy. Jak wszystko jest ok, proxy woła główną metodę, a jak nie, metodę fallback.
//    Kiedy wołamy metodę oznaczoną @HystrixCommand z wewnątrz naszej klasy, Hystrix proxy jej nie wyłapuje.
//    Rozwiązaniem na to jest przeniesienie tych metod do nowej klasy (każda klasa ma swoje proxy).

    @HystrixCommand(fallbackMethod = "getFallbackUserRating") // tells Hystrix what to break; sets fallback mechanism
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        UserRating ratings = restTemplate.getForObject(
                "http://ratings-data-service/ratingsdata/users/" + userId, // not a real url - protocol://<service name in Eureka service discovery>/<endpoint>
                UserRating.class
        );
        return ratings;
    }

    public UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setUserRating(Arrays.asList(
                new Rating("0", 0)
        ));
        return userRating;
    }
}
