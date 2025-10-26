package com.myjournal.journalApp.repository;

import com.myjournal.journalApp.entity.JournalEntryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends MongoRepository <JournalEntryEntity, String> {

}
