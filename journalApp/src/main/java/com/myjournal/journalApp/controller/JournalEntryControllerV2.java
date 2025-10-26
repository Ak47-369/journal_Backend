package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.entity.JournalEntry;
import com.myjournal.journalApp.entity.JournalEntryEntity;
import com.myjournal.journalApp.service.JournalEntryService;
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
    public List<JournalEntry> getAllEntries(){
        return journalEntryService.getAllEntries();
    }

    @GetMapping("get/id/{entryId}")
    public JournalEntry getEntryById(@PathVariable("entryId") String id){
        return journalEntryService.getJournalEntryById(id);
    }

    @DeleteMapping("delete/id/{entryId}")
    public void deleteEntryById(@PathVariable String entryId){
        journalEntryService.deleteJournalEntryById(entryId);
    }

    @PutMapping("update/id/{entryId}")
    public JournalEntry updateEntryById(@PathVariable String entryId, @RequestBody JournalEntry entry){
        return journalEntryService.updateJournalEntryById(entryId, entry);
    }

    @PostMapping("create-entry")
    public JournalEntry createEntry(@RequestBody JournalEntry entry){
        return  journalEntryService.saveEntry(entry);
    }
}
