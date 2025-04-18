package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan(basePackages = "ru.yandex.practicum")
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}
