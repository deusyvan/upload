package upload.armazenamento;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArmazenamentoArquivoService implements ArmazenamentoService {

    private final Path localDiretorio;

    @Autowired
    public ArmazenamentoArquivoService(ArmazenamentoPropriedade propriedades) {
        this.localDiretorio = Paths.get(propriedades.getLocation());
    }

    @Override
    public void armazenar(MultipartFile arquivo) {
        String nomeArquivo = StringUtils.cleanPath(arquivo.getOriginalFilename());
        try {
            if (arquivo.isEmpty()) {
                throw new ArmazenamentoException("Falha ao armazenar aquivo vazio: " + nomeArquivo);
            }
            if (nomeArquivo.contains("..")) {
                // This is a security check
                throw new ArmazenamentoException(
                        "Não é possível armazenar o arquivo com o caminho relativo fora do diretório atual"
                                + nomeArquivo);
            }
            Files.copy(arquivo.getInputStream(), this.localDiretorio.resolve(nomeArquivo),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new ArmazenamentoException("Falha ao armazenar o arquivo " + nomeArquivo, e);
        }
    }

    @Override
    public Stream<Path> listarTodos() {
        try {
            return Files.walk(this.localDiretorio, 1)
                    .filter(path -> !path.equals(this.localDiretorio))
                    .map(path -> this.localDiretorio.relativize(path));
        }
        catch (IOException e) {
            throw new ArmazenamentoException("Falha ao ler o arquivo armazenado", e);
        }

    }

    @Override
    public Path listarPorNome(String nomeArquivo) {
        return localDiretorio.resolve(nomeArquivo);
    }

    @Override
    public Resource listarPorRecurso(String nomeArquivo) {
        try {
            Path arquivo = listarPorNome(nomeArquivo);
            Resource resource = new UrlResource(arquivo.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new ArmazenamentoArquivoNaoEncontradoException(
                        "Não foi possível ler o arquivo: " + nomeArquivo);

            }
        }
        catch (MalformedURLException e) {
            throw new ArmazenamentoArquivoNaoEncontradoException("Não foi possível ler o arquivo: " + nomeArquivo, e);
        }
    }

    @Override
    public void apagarTodos() {
        FileSystemUtils.deleteRecursively(localDiretorio.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(localDiretorio);
        }
        catch (IOException e) {
            throw new ArmazenamentoException("Não foi possível inicializar o armazenamento.", e);
        }
    }
}
