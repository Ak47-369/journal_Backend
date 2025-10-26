package com.myjournal.journalApp.service;

import com.myjournal.journalApp.entity.JournalEntry;
import com.myjournal.journalApp.entity.JournalEntryEntity;
import com.myjournal.journalApp.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryService(JournalEntryRepository journalEntryRepository){
        this.journalEntryRepository = journalEntryRepository;
    }

    public List<JournalEntry> getAllEntries(){
        List<JournalEntry> allEntries;
        allEntries = journalEntryRepository.findAll().stream().map(journalEntryEntity -> new JournalEntry(journalEntryEntity.getId(),journalEntryEntity.getContent(), journalEntryEntity.getTitle())).toList();
        return allEntries;
    }

    public JournalEntry saveEntry(JournalEntry journalEntry){
        // Convert Journal Entry POJO/DTO to JournalEntry Entity
        JournalEntryEntity journalEntryEntity = new JournalEntryEntity(journalEntry.getContent(), journalEntry.getTitle());
        JournalEntryEntity savedEntry = journalEntryRepository.save(journalEntryEntity);
        return new JournalEntry(savedEntry.getId(), savedEntry.getTitle(), savedEntry.getContent());
    }

    public JournalEntry getJournalEntryById(String id){
        Optional<JournalEntryEntity>  optionalJournalEntryEntity = journalEntryRepository.findById(id);
        if(optionalJournalEntryEntity.isPresent()){
            JournalEntryEntity journalEntryEntity = optionalJournalEntryEntity.get();
            return new JournalEntry(journalEntryEntity.getId(), journalEntryEntity.getTitle(), journalEntryEntity.getContent());
        }
        return null;
    }

    public JournalEntry updateJournalEntryById(String id, JournalEntry journalEntry){
        Optional<JournalEntryEntity> optionalJournalEntryEntity = journalEntryRepository.findById(id);
        if(optionalJournalEntryEntity.isPresent()){
            JournalEntryEntity journalEntryEntity = optionalJournalEntryEntity.get();
            journalEntryEntity.setContent(journalEntry.getContent());
            journalEntryEntity.setTitle(journalEntry.getTitle());
            JournalEntryEntity savedEntry = journalEntryRepository.save(journalEntryEntity);
            return new JournalEntry(savedEntry.getId(),savedEntry.getTitle(), savedEntry.getContent());
        }
        return null;
    }

    public void deleteJournalEntryById(String id){
        Optional<JournalEntryEntity> optionalJournalEntryEntity = journalEntryRepository.findById(id);
        if(optionalJournalEntryEntity.isPresent()) {
            journalEntryRepository.deleteById(id);
        }
//        System.out.println("Record with Id: " + id + " not present");
    }
}
