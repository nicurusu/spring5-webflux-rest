package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;

class CategoryControllerTest {

    public static final String CATEGORY_URL = CategoryController.BASE_URL + "/sameId";
    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    void list() {
        given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Cat1").build(),
                        Category.builder().description("Cat2").build()));

        webTestClient.get().uri(CategoryController.BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        given(categoryRepository.findById("someid"))
                .willReturn(Mono.just(Category.builder().description("Cat").build()));

        webTestClient.get().uri(CATEGORY_URL)
                .exchange()
                .expectBodyList(Category.class);
    }

    @Test
    public void testCreateCategory() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryToSaveMono = Mono.just(Category.builder().description("Categ1").build());

        webTestClient.post()
                .uri(CategoryController.BASE_URL)
                .body(categoryToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateCategory() {
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("Categ2").build());

        webTestClient.put()
                .uri(CATEGORY_URL)
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testPatchCategoryWithChange() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("Categ2").build());

        webTestClient.patch()
                .uri(CATEGORY_URL)
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchCategoryNoChange() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().build());

        webTestClient.patch()
                .uri(CATEGORY_URL)
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository, never()).save(any());
    }

}