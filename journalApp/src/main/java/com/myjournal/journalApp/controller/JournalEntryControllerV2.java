package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.entity.JournalEntry;
import com.myjournal.journalApp.entity.JournalEntryEntity;
import com.myjournal.journalApp.service.JournalEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal/V2")
public class JournalEntryControllerV2 {
    private  final JournalEntryService journalEntryService;

    public JournalEntryControllerV2(JournalEntryService journalEntryService){
        this.journalEntryService = journalEntryService;
    }

    @GetMapping("/get-all-entries") // It's path is /journal/V2/get-all-entries
    public ResponseEntity<List<JournalEntry>> getAllEntries(){
        List<JournalEntry> allEntries = journalEntryService.getAllEntries();
        if(allEntries != null && !allEntries.isEmpty())
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("get/id/{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable("entryId") String id){
        JournalEntry journalEntry = journalEntryService.getJournalEntryById(id);
        if(journalEntry != null){
            return new ResponseEntity<>(journalEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("delete/id/{entryId}")
    public ResponseEntity<?> deleteEntryById(@PathVariable String entryId){
        journalEntryService.deleteJournalEntryById(entryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("update/id/{entryId}")
    public ResponseEntity<JournalEntry> updateEntryById(@PathVariable String entryId, @RequestBody JournalEntry entry){
        JournalEntry updatedJournalEntry = journalEntryService.updateJournalEntryById(entryId, entry);
        if(updatedJournalEntry != null)
            return new ResponseEntity<>(updatedJournalEntry, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("create-entry")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry entry){
        JournalEntry createdJournalEntry = journalEntryService.saveEntry(entry);
        if(createdJournalEntry != null)
            return new ResponseEntity<>(createdJournalEntry,HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
