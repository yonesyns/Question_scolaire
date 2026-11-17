package com.gestetud;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@SpringBootApplication @EnableAsync public class GestetudApplication { public static void main(String[] args) { SpringApplication.run(GestetudApplication.class, args); } }
