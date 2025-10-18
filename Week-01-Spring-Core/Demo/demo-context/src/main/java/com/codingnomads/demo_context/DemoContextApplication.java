package com.codingnomads.demo_context;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class DemoContextApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(DemoContextApplication.class, args);

        printTree("PC", 0, configurableApplicationContext.getBeanFactory());
    }

    private static void printTree(String bean, int level, ConfigurableListableBeanFactory beanFactory) {
        String prefix = "";
        if (level > 0) {
            prefix = "  |".repeat(level);
        }

        System.out.printf(
                "%-30s required by: %-30s, requires: %-30s bean: %s\n",
                prefix + " -> " + bean,
                Arrays.toString(beanFactory.getDependentBeans(bean)),
                Arrays.toString(beanFactory.getDependenciesForBean(bean)),
                beanFactory.getBean(bean)
        );

        String[] dependenciesForBean = beanFactory.getDependenciesForBean(bean);
        for (String dependency : dependenciesForBean) {
            printTree(dependency, level + 1, beanFactory);
        }
    }
}
