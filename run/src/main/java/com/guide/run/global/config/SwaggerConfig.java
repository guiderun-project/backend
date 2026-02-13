package com.guide.run.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI guideRunOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GuideRun API")
                        .description("GuideRun 백엔드 API 문서")
                        .version("v1")
                        .license(new License().name("GuideRun"))
                        .termsOfService("https://guidesrun.kr"))
                .components(new Components())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/admin/**")
                .build();
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            String tag = resolveTag(handlerMethod);
            if (operation.getTags() == null || operation.getTags().isEmpty()) {
                operation.addTagsItem(tag);
            }
            if (operation.getSummary() == null || operation.getSummary().isBlank()) {
                operation.setSummary(toMethodSummary(handlerMethod.getMethod().getName()));
            }
            addDefaultResponses(operation);
            return operation;
        };
    }

    @Bean
    public OpenApiCustomizer openApiSecurityCustomizer() {
        return openApi -> {
            openApi.getComponents().addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .in(SecurityScheme.In.HEADER)
                            .name("Authorization"));
        };
    }

    private void addDefaultResponses(Operation operation) {
        if (!operation.getResponses().containsKey("400")) {
            operation.getResponses().addApiResponse("400", new ApiResponse().description("잘못된 요청"));
        }
        if (!operation.getResponses().containsKey("401")) {
            operation.getResponses().addApiResponse("401", new ApiResponse().description("인증 실패"));
        }
        if (!operation.getResponses().containsKey("403")) {
            operation.getResponses().addApiResponse("403", new ApiResponse().description("접근 권한 없음"));
        }
        if (!operation.getResponses().containsKey("500")) {
            operation.getResponses().addApiResponse("500", new ApiResponse().description("서버 오류"));
        }
    }

    private String resolveTag(HandlerMethod handlerMethod) {
        String beanName = handlerMethod.getBeanType().getSimpleName();
        return beanName.replace("Controller", "");
    }

    private String toMethodSummary(String methodName) {
        String spaced = Arrays.stream(methodName.split("(?=[A-Z])"))
                .collect(Collectors.joining(" "));
        return spaced.isBlank() ? methodName : Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
