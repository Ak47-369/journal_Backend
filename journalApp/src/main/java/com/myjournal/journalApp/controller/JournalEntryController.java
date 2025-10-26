package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.JournalApplication;
import com.myjournal.journalApp.entity.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/_journal")
public class JournalEntryController {
    private HashMap <Long, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping("/get-all-entries") // It's path is /journal/get-all-entries
    public List<JournalEntry> getAll(){
        return new ArrayList<>(journalEntries.values());
    }

    @GetMapping("get/id/{entryId}")
    public JournalEntry getEntryById(@PathVariable("entryId") long id){
        return journalEntries.get(id);
    }

    @DeleteMapping("delete/id/{entryId}")
    public JournalEntry deleteEntryById(@PathVariable long entryId){
        return journalEntries.remove(entryId);
    }

    @PutMapping("update/id/{entryId}")
    public JournalEntry updateEntryById(@PathVariable long entryId, @RequestBody JournalEntry entry){
        return journalEntries.put(entryId,entry);
    }

    @PostMapping("create-entry")
    public boolean createEntry(@RequestBody JournalEntry entry){
        journalEntries.put(entry.getId(), entry);
        return true;
    }
}
