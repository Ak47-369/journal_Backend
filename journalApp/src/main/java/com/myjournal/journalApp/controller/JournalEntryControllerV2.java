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
@RequestMapping("/journal/V2/user/{userName}")
public class JournalEntryControllerV2 {
    private final UserService userService;
    private final JournalEntryService journalEntryService;


    public JournalEntryControllerV2(JournalEntryService journalEntryService,UserService userService){
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @GetMapping("/get-all-entries") // It's path is /journal/V2/user/{userName}/get-all-entries
    public ResponseEntity<List<JournalEntry>> getAllEntries(@PathVariable String userName){
        User user = userService.findByUserName(userName);
        if(user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<JournalEntry> allEntries = journalEntryService.getAllEntries(user);
        if(allEntries != null && !allEntries.isEmpty())
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("get/id/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable String userName, @PathVariable ObjectId entryId){
        User user = userService.findByUserName(userName);
        if(user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(!userService.hasJournalEntryWithId(user,entryId))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        JournalEntry journalEntry = journalEntryService.getJournalEntryById(entryId);
        return new ResponseEntity<>(journalEntry, HttpStatus.OK);
    }

    @DeleteMapping("delete/id/{entryId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable String userName,@PathVariable ObjectId entryId){
        User user = userService.findByUserName(userName);
        if(user == null || !userService.hasJournalEntryWithId(user,entryId))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        userService.deleteJournalEntryById(user,entryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("update/id/{entryId}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable String userName,@PathVariable ObjectId entryId, @RequestBody JournalEntry entry){
        User user = userService.findByUserName(userName);
        if(user == null || !userService.hasJournalEntryWithId(user,entryId))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        JournalEntry updatedJournalEntry = userService.updateJournalEntryById(user,entryId, entry);
        if(updatedJournalEntry != null)
            return new ResponseEntity<>(updatedJournalEntry, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("create-entry")
    public ResponseEntity<JournalEntry> createEntry(@PathVariable String userName,@RequestBody JournalEntry entry){
        User user = userService.findByUserName(userName);
        if(user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        JournalEntry createdJournalEntry = journalEntryService.saveEntry(user, entry);
        if(createdJournalEntry != null)
            return new ResponseEntity<>(createdJournalEntry,HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
