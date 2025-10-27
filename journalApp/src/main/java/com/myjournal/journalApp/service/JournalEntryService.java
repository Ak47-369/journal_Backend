package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.JournalEntry;
import com.myjournal.journalApp.entity.JournalEntryEntity;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.repository.JournalEntryRepository;
import com.myjournal.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {
    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepositry;


    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserRepository userRepositry){
        this.journalEntryRepository = journalEntryRepository;
        this.userRepositry = userRepositry;
    }

    public List<JournalEntry> getAllEntries(){
        List<JournalEntry> allEntries;
        allEntries = journalEntryRepository.findAll().stream().map(journalEntryEntity -> new JournalEntry(journalEntryEntity.getId(),journalEntryEntity.getContent(), journalEntryEntity.getTitle())).toList();
        return allEntries;
    }

    public JournalEntry saveEntry(User user, JournalEntry journalEntry){
        JournalEntryEntity journalEntryEntity = new JournalEntryEntity(journalEntry.getContent(), journalEntry.getTitle());
        JournalEntryEntity savedEntry = journalEntryRepository.save(journalEntryEntity);
        user.getJournalEntryIds().add(savedEntry.getId());
        userRepositry.save(user);
        return new JournalEntry(savedEntry.getId(), savedEntry.getTitle(), savedEntry.getContent());
    }

    public JournalEntry getJournalEntryById(ObjectId id){
        Optional<JournalEntryEntity>  optionalJournalEntryEntity = journalEntryRepository.findById(id);
        if(optionalJournalEntryEntity.isPresent()){
            JournalEntryEntity journalEntryEntity = optionalJournalEntryEntity.get();
            return new JournalEntry(journalEntryEntity.getId(), journalEntryEntity.getTitle(), journalEntryEntity.getContent());
        }
        return null;
    }

    public JournalEntry updateJournalEntryById(ObjectId id, JournalEntry newJournalEntry){
        JournalEntryEntity oldjournalEntryEntity = journalEntryRepository.findById(id).orElse(null);
        if(oldjournalEntryEntity != null){
            if(oldjournalEntryEntity.getContent() != null && !oldjournalEntryEntity.getContent().isEmpty())
                oldjournalEntryEntity.setContent(newJournalEntry.getContent());

            if(oldjournalEntryEntity.getTitle() != null && !oldjournalEntryEntity.getTitle().isEmpty())
                oldjournalEntryEntity.setTitle(newJournalEntry.getTitle());
            JournalEntryEntity savedEntry = journalEntryRepository.save(oldjournalEntryEntity);
            return new JournalEntry(savedEntry.getId(),savedEntry.getTitle(), savedEntry.getContent());
        }
        return null;
    }

    public void deleteJournalEntryById(ObjectId id){
        journalEntryRepository.deleteById(id);
    }
}
