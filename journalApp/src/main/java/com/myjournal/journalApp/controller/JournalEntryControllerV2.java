package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.JournalEntry;
import com.myjournal.journalApp.service.JournalEntryService;
import com.myjournal.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal/V2/user/{userName}")
public class JournalEntryControllerV2 {
    private  final UserService userService;


    public JournalEntryControllerV2(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/get-all-entries") // It's path is /journal/V2/user/{userName}/get-all-entries
    public ResponseEntity<List<JournalEntry>> getAllEntries(@PathVariable String userName){
        List<JournalEntry> allEntries = userService.getAllEntries(userName);
        if(allEntries != null && !allEntries.isEmpty())
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("get/id/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable String userName, @PathVariable ObjectId entryId){
        JournalEntry journalEntry = userService.getJournalEntryById(userName, entryId);
        if(journalEntry != null){
            return new ResponseEntity<>(journalEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("delete/id/{entryId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable String userName,@PathVariable ObjectId entryId){
        userService.deleteJournalEntryById(userName,entryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("update/id/{entryId}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable String userName,@PathVariable ObjectId entryId, @RequestBody JournalEntry entry){
        JournalEntry updatedJournalEntry = userService.updateJournalEntryById(userName,entryId, entry);
        if(updatedJournalEntry != null)
            return new ResponseEntity<>(updatedJournalEntry, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("create-entry")
    public ResponseEntity<JournalEntry> createEntry(@PathVariable String userName,@RequestBody JournalEntry entry){
        JournalEntry createdJournalEntry = userService.createEntry(userName,entry);
        if(createdJournalEntry != null)
            return new ResponseEntity<>(createdJournalEntry,HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
