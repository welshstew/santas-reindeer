/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.swinchester.hackathons;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Service;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(
            new CamelHttpTransportServlet(), "/*");
        servlet.setName("CamelServlet");
        return servlet;
    }

    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {
            restConfiguration()
                .contextPath("/").apiContextPath("/api-doc")
                    .apiProperty("api.title", "Reindeer/Camel REST API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "true")
                    .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

            rest("/reindeerservice").description("Reindeer REST service")
                .post("/").outType(ServiceResponse.class).description("The list of all the teams")
                    .route().routeId("reindeer-api")
                    .to("log:stuff?showAll=true")
                    .setProperty("something").groovy("resource:classpath:groovy/loadConfigMap.groovy")
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            ServiceResponse toPost = (ServiceResponse) exchange.getIn().getBody();
                            RestTemplate restTemplate = new RestTemplate();
                            ServiceResponse resp = restTemplate.postForObject("http://proxy-api/api/service/proxy", toPost, ServiceResponse.class);
                            exchange.getIn().setBody(resp);
                        }
                    })
                    .endRest()
                .get("/ping").description("Simple ping")
                    .route().routeId("ping-api")
                    .to("log:stuff?showAll=true")
                    .setBody(constant("{ 'ping': 'success!' }"));
        }
    }

    @Component
    class Backend extends RouteBuilder {

        @Override
        public void configure() {
            // A first route generates some orders and queue them in DB
            from("timer:hello?period=60000").routeId("timer")
                .log("Just saying i'm working...!");


        }
    }
}