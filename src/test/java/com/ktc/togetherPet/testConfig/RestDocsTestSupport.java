package com.ktc.togetherPet.testConfig;

import com.google.gson.Gson;
import com.ktc.togetherPet.config.GsonConfig;
import javax.management.Attribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Disabled
@Import({RestDocsConfiguration.class, GsonConfig.class})
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest
public class RestDocsTestSupport {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private Gson gson;

    protected static Attribute constraints(
        final String value
    ) {
        return new Attribute("constraints", value);
    }


    @BeforeEach
    void setUp(final WebApplicationContext context,
        final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
            .alwaysDo(MockMvcResultHandlers.print())
            .alwaysDo(restDocs)
            .build();
    }

    protected String toJson(final Object obj) {
        return gson.toJson(obj);
    }
}
