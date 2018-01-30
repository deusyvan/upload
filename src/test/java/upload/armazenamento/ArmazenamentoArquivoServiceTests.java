/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package upload.armazenamento;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import upload.armazenamento.ArmazenamentoArquivoService;
import upload.armazenamento.ArmazenamentoException;
import upload.armazenamento.ArmazenamentoPropriedade;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 *
 */
public class ArmazenamentoArquivoServiceTests {

    private ArmazenamentoPropriedade properties = new ArmazenamentoPropriedade();
    private ArmazenamentoArquivoService service;

    @Before
    public void init() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        service = new ArmazenamentoArquivoService(properties);
        service.init();
    }

    @Test
    public void loadNonExistent() {
        assertThat(service.listarPorNome("foo.txt")).doesNotExist();
    }

    @Test
    public void saveAndLoad() {
        service.armazenar(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        assertThat(service.listarPorNome("foo.txt")).exists();
    }

    @Test(expected = ArmazenamentoException.class)
    public void saveNotPermitted() {
        service.armazenar(new MockMultipartFile("foo", "../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
    }

    @Test
    public void savePermitted() {
        service.armazenar(new MockMultipartFile("foo", "bar/../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
    }

}
