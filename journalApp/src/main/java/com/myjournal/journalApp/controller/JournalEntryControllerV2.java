package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.JournalEntry;
import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.service.JournalEntryService;
import com.myjournal.journalApp.service.RateLimiterService;
import com.myjournal.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal/v2/user/entries") // Consistent casing
public class JournalEntryControllerV2 {
    private final UserService userService;
    private final JournalEntryService journalEntryService;
    private final RateLimiterService rateLimiterService;

    public JournalEntryControllerV2(JournalEntryService journalEntryService, UserService userService,
                                    RateLimiterService rateLimiterService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllEntries(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        User user = userService.findByUserName(userName);
        if(!rateLimiterService.isRequestAllowed(user.getId()))
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        List<JournalEntry> allEntries = journalEntryService.getAllEntries(user);
        return new ResponseEntity<>(allEntries, HttpStatus.OK); // Always return 200 OK, even for an empty list
    }

    @GetMapping("{entryId}")
    public ResponseEntity<JournalEntry> getEntryById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable ObjectId entryId) {
        String userName = userDetails.getUsername();
        User user = userService.findByUserName(userName);
        // Ensure the entry belongs to the user
        if (!userService.hasJournalEntryWithId(user, entryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        JournalEntry journalEntry = journalEntryService.getJournalEntryByIdAndUser(entryId, user);
        return new ResponseEntity<>(journalEntry, HttpStatus.OK);
    }

    @DeleteMapping("{entryId}")
    public ResponseEntity<?> deleteEntryById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable ObjectId entryId) {
        String userName = userDetails.getUsername();
        User user = userService.findByUserName(userName);
        if(!userService.hasJournalEntryWithId(user, entryId))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        journalEntryService.deleteJournalEntryById(entryId, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{entryId}")
    public ResponseEntity<JournalEntry> updateEntryById(@AuthenticationPrincipal UserDetails userDetails , @PathVariable ObjectId entryId, @RequestBody JournalEntry entry) {
        String userName = userDetails.getUsername();
        User user = userService.findByUserName(userName);
        if(!userService.hasJournalEntryWithId(user, entryId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        JournalEntry updatedJournalEntry = journalEntryService.updateJournalEntryById(entryId, entry, user);
        return new ResponseEntity<>(updatedJournalEntry, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@AuthenticationPrincipal UserDetails userDetails ,@RequestBody JournalEntry entry) {
        String userName = userDetails.getUsername();
        User user = userService.findByUserName(userName);
        JournalEntry createdJournalEntry = journalEntryService.saveEntry(user, entry);
        return new ResponseEntity<>(createdJournalEntry, HttpStatus.CREATED);
    }
}
