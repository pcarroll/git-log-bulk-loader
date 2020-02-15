package com.pcc.loader.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pcc.loader.pojo.Author;
import com.pcc.loader.pojo.ProjectFile;

public interface ProjectFileRepository extends MongoRepository<ProjectFile, String> {

	public Author findByName(String name);

}
