package upload;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import upload.armazenamento.ArmazenamentoArquivoNaoEncontradoException;
import upload.armazenamento.ArmazenamentoService;

@Controller
public class ArquivoUploadController {

    private final ArmazenamentoService armazenamentoService;

    @Autowired
    public ArquivoUploadController(ArmazenamentoService armazenamentoService) {
        this.armazenamentoService = armazenamentoService;
    }

    @GetMapping("/")
    public String listaArquivosCarregados(Model model) throws IOException {

        model.addAttribute("arquivos", armazenamentoService.listarTodos().map(
                path -> MvcUriComponentsBuilder.fromMethodName(ArquivoUploadController.class,
                        "servidorArquivo", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "formulario-upload";
    }

    @GetMapping("/arquivos/{nomeArquivo:.+}")
    @ResponseBody
    public ResponseEntity<Resource> servidorArquivo(@PathVariable String nomeArquivo) {

        Resource arquivo = armazenamentoService.listarPorRecurso(nomeArquivo);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "anexo; nomeArquivo=\"" + arquivo.getFilename() + "\"").body(arquivo);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("arquivo") MultipartFile arquivo,
            RedirectAttributes redirectAttributes) {

        armazenamentoService.armazenar(arquivo);
        redirectAttributes.addFlashAttribute("message",
                "Seu arquivo: " + arquivo.getOriginalFilename() + " foi carregado com sucesso!");

        return "redirect:/";
    }

    @ExceptionHandler(ArmazenamentoArquivoNaoEncontradoException.class)
    public ResponseEntity<?> handleStorageFileNotFound(ArmazenamentoArquivoNaoEncontradoException exc) {
        return ResponseEntity.notFound().build();
    }

}
