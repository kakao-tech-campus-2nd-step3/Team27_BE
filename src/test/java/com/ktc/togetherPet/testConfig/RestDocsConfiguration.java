package com.ktc.togetherPet.testConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

@TestConfiguration
public class RestDocsConfiguration {

    @Bean
    public RestDocumentationResultHandler restDocumentationResultHandler() {
        return MockMvcRestDocumentation.document(
            "{class-name}/{method-name}",
            Preprocessors.preprocessRequest(
                Preprocessors.modifyHeaders()
                    .remove("Content-Length")
                    .remove("Host"),
                Preprocessors.prettyPrint()
            ),
            Preprocessors.preprocessResponse(
                Preprocessors.modifyHeaders()
                    .remove("Content-Length")
                    .remove("X-Content-Type-Options")
                    .remove("X-XSS-Protection")
                    .remove("Cache-Control")
                    .remove("Pragma")
                    .remove("Expires")
                    .remove("X-Frame-Options"),
                Preprocessors.prettyPrint())
        );
    }
}
