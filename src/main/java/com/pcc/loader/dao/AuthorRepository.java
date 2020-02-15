package com.pcc.loader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pcc.loader.pojo.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {

	public Author findByEmail(String email);
	public Author findByName(String name);

}
