package dev.ubaid.catalogservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import dev.ubaid.catalogservice.domain.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {
    
    private static KeycloakToken bjornToken; //is not an employee
    private static KeycloakToken isabellaToken;

    @Autowired
    private WebTestClient webTestClient;
    
    @Container
    private static final KeycloakContainer keycloakContainer = 
            new KeycloakContainer("quay.io/keycloak/keycloak:22.0.1-0")
                .withRealmImportFile("test-realm-config.json");
    
    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", 
                () -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
    }
    
    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        isabellaToken = authenticateWith("isabelle", "password", webClient);
        bjornToken = authenticateWith("bjorn", "password", webClient);
    }
    
    @Test
    void contextLoads() {
    }
    
    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1234567890", "Title", "Author", 10.11);
        
        webTestClient
            .post()
            .uri("/api/books")
                .headers(headers -> headers.setBearerAuth(isabellaToken.accessToken()))
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class).value(actualBook -> {
                assertThat(actualBook).isNotNull();
                assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
            });
    }
    
    @Test
    void whenPostRequestUnauthorizedThen403() {
        Book book = Book.of("1234567890", "Title", "Ubaid", 11.01, "Ubaid pub");
        webTestClient
                .post()
                .uri("/api/books")
                .headers(headers -> headers.setBearerAuth(bjornToken.accessToken()))
                .bodyValue(book)
                .exchange()
                .expectStatus().isForbidden();
                
    }
    
    @Test
    void whenPostRequestUnauthenticatedThen401() {
        Book book = Book.of("1234567890", "Title", "ubaid", 111.10, "Ubaid pub");
        webTestClient
                .post()
                .uri("/api/books")
                .bodyValue(book)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }
    
    private record KeycloakToken(String accessToken) {
        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
