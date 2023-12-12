package com.devlamq.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication

public class BooksAPIApplication  {

	public static void main(String[] args) {
		SpringApplication.run(BooksAPIApplication.class, args);
	}

}
