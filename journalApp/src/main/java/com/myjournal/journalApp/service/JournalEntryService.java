package com.myjournal.journalApp.service;

import com.myjournal.journalApp.dto.JournalEntry;
import com.myjournal.journalApp.entity.JournalEntryEntity;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.exception.ResourceNotFoundException;
import com.myjournal.journalApp.repository.JournalEntryRepository;
import com.myjournal.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class JournalEntryService {
    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository; // Corrected typo

    public JournalEntryService(JournalEntryRepository journalEntryRepository, UserRepository userRepository){
        this.journalEntryRepository = journalEntryRepository;
        this.userRepository = userRepository;
    }

    // N+1 Fix: Fetch all entries for a user in a single query
    public List<JournalEntry> getAllEntries(User user){
        List<ObjectId> journalEntryIds = user.getJournalEntryIds();
        List<JournalEntryEntity> journalEntries = journalEntryRepository.findAllById(journalEntryIds);
        return journalEntries.stream()
                .map(entity -> new JournalEntry(entity.getId(), entity.getTitle(), entity.getContent()))
                .toList();
    }

    public JournalEntry saveEntry(User user, JournalEntry journalEntry){
        JournalEntryEntity journalEntryEntity = new JournalEntryEntity(journalEntry.getContent(), journalEntry.getTitle());
        JournalEntryEntity savedEntry = journalEntryRepository.save(journalEntryEntity);

        user.getJournalEntryIds().add(savedEntry.getId());
        userRepository.save(user); // Save the user with the new entry ID
        return new JournalEntry(savedEntry.getId(), savedEntry.getTitle(), savedEntry.getContent());
    }

    // Fetches a single entry by its ID, throws if not found
    public JournalEntry getJournalEntryById(ObjectId id){
        return journalEntryRepository.findById(id)
                .map(entity -> new JournalEntry(entity.getId(), entity.getTitle(), entity.getContent()))
                .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", id));
    }

    // Fetches a single entry by ID, ensuring it belongs to the given user
    public JournalEntry getJournalEntryByIdAndUser(ObjectId entryId, User user) {
        if (!user.getJournalEntryIds().contains(entryId)) {
            throw new ResourceNotFoundException("JournalEntry", "id for user " + user.getUserName(), entryId);
        }
        return getJournalEntryById(entryId); // Use the existing method to fetch
    }

    public JournalEntry updateJournalEntryById(ObjectId entryId, JournalEntry newJournalEntry, User user){
        // Ensure the entry belongs to the user
        JournalEntryEntity oldJournalEntryEntity = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("JournalEntry", "id", entryId));

        if (!user.getJournalEntryIds().contains(entryId)) {
            throw new ResourceNotFoundException("JournalEntry", "id for user " + user.getUserName(), entryId);
        }

        if(newJournalEntry.getContent() != null && !newJournalEntry.getContent().isEmpty())
            oldJournalEntryEntity.setContent(newJournalEntry.getContent());

        if(newJournalEntry.getTitle() != null && !newJournalEntry.getTitle().isEmpty())
            oldJournalEntryEntity.setTitle(newJournalEntry.getTitle());

        JournalEntryEntity savedEntry = journalEntryRepository.save(oldJournalEntryEntity);
        return new JournalEntry(savedEntry.getId(),savedEntry.getTitle(), savedEntry.getContent());
    }

    @Transactional
    public void deleteJournalEntryById(ObjectId entryId, User user){
        // Ensure the entry belongs to the user before deleting
        if (!user.getJournalEntryIds().contains(entryId)) {
            throw new ResourceNotFoundException("JournalEntry", "id for user " + user.getUserName(), entryId);
        }
        journalEntryRepository.deleteById(entryId);
        // Also remove from the user's list
        user.getJournalEntryIds().remove(entryId);
        userRepository.save(user);
    }

    public void deleteJournalEntryById(ObjectId entryId){
        journalEntryRepository.deleteById(entryId);
    }
}
