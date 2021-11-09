package config;

import annotations.Bean;
import annotations.Configuration;
import classes.Cat;
import classes.Dog;

@Configuration
public class ContextConfig {

    @Bean
    public Cat cat() {
        return new Cat();
    }

    @Bean
    public Dog dog() {
        return new Dog();
    }

}
