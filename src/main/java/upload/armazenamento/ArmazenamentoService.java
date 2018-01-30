package upload.armazenamento;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ArmazenamentoService {

    void init();

    void armazenar(MultipartFile file);

    Stream<Path> listarTodos();

    Path listarPorNome(String filename);

    Resource listarPorRecurso(String filename);

    void apagarTodos();

}
