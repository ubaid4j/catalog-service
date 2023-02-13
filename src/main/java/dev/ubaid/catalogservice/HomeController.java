package dev.ubaid.catalogservice;

import dev.ubaid.catalogservice.config.ApplicationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    private final ApplicationProperties props;

    public HomeController(ApplicationProperties props) {
        this.props = props;
    }

    @GetMapping("/")
    String welcome() {
        return props.getGreeting();
    }
}
