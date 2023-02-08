package dev.ubaid.catalogservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping("/")
    String welcome() {
        return "Welcome to the book catalog";
    }
}
