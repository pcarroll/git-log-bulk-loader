package com.pcc.loader.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pcc.loader.pojo.FileCommit;

public interface FileCommitRepository extends MongoRepository<FileCommit, String> {

	public List<FileCommit> findByEmail(String email);

}
