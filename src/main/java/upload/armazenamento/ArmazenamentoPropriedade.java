package upload.armazenamento;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("armazenamento")
public class ArmazenamentoPropriedade {

    /**
     * Localização da pasta para armazenar arquivos
     */
    private String location = ".sistemarquivos";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
