package upload;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import upload.armazenamento.ArmazenamentoPropriedade;
import upload.armazenamento.ArmazenamentoService;

@SpringBootApplication
@EnableConfigurationProperties(ArmazenamentoPropriedade.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(ArmazenamentoService armazenamentoService) {
        return (args) -> {
            armazenamentoService.apagarTodos();
            armazenamentoService.init();
        };
    }
}
