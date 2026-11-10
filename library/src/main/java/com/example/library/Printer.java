package com.example.library;


import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class MyBeanFactoryInitializationAotProcessor
        implements BeanFactoryInitializationAotProcessor {

    @Override
    public @Nullable BeanFactoryInitializationAotContribution processAheadOfTime(
            ConfigurableListableBeanFactory beanFactory) {
        IO.println("HIIIIIII!");
        return null;
    }
}

@Configuration
class PrinterAutoConfiguration {


    @Bean
    static MyBeanFactoryInitializationAotProcessor myBeanFactoryInitializationAotProcessor() {
        return new MyBeanFactoryInitializationAotProcessor();
    }

    @Bean
    Printer printer() {
        return new Printer();
    }

}

public class Printer
        implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IO.println("hi");
    }
}


