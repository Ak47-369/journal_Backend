package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.JournalEntry;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.service.JournalEntryService;
import com.myjournal.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal/v2/user/{userName}") // Consistent casing
public class JournalEntryControllerV2 {
    private final UserService userService;
    private final JournalEntryService journalEntryService;

    public JournalEntryControllerV2(JournalEntryService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @GetMapping("/entries") // More RESTful path
    public ResponseEntity<List<JournalEntry>> getAllEntries(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        List<JournalEntry> allEntries = journalEntryService.getAllEntries(user);
        return new ResponseEntity<>(allEntries, HttpStatus.OK); // Always return 200 OK, even for an empty list
    }

    @GetMapping("/entries/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable String userName, @PathVariable ObjectId entryId) {
        User user = userService.findByUserName(userName);
        JournalEntry journalEntry = journalEntryService.getJournalEntryByIdAndUser(entryId, user);
        return new ResponseEntity<>(journalEntry, HttpStatus.OK);
    }

    @DeleteMapping("/entries/{entryId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable String userName, @PathVariable ObjectId entryId) {
        User user = userService.findByUserName(userName);
        journalEntryService.deleteJournalEntryById(entryId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/entries/{entryId}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable String userName, @PathVariable ObjectId entryId, @RequestBody JournalEntry entry) {
        User user = userService.findByUserName(userName);
        JournalEntry updatedJournalEntry = journalEntryService.updateJournalEntryById(entryId, entry, user);
        return new ResponseEntity<>(updatedJournalEntry, HttpStatus.OK);
    }

    @PostMapping("/entries")
    public ResponseEntity<JournalEntry> createEntry(@PathVariable String userName, @RequestBody JournalEntry entry) {
        User user = userService.findByUserName(userName);
        JournalEntry createdJournalEntry = journalEntryService.saveEntry(user, entry);
        return new ResponseEntity<>(createdJournalEntry, HttpStatus.CREATED);
    }
}
