package com.myjournal.journalApp.jobs;

import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.repository.UserRepository;
import com.myjournal.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Component
@Slf4j
public class QuotaJobs {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MongoTemplate mongoTemplate;

    @Value("${app.quota.max}")
    private int MAX_QUOTA;

    public QuotaJobs(UserRepository userRepository,
                         EmailService emailService,
                         MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * JOB 1: Sends a weekly quota report to all users.
     * Runs every Monday at 9 AM UTC, as defined in application.yml.
     */
    @Async
    @Scheduled(cron = "${app.jobs.quota-report-cron}", zone = "UTC")
    @Transactional(readOnly = true) // Required for streaming results
    public void sendWeeklyQuotaReport() {
        log.info("JOB_START: Starting weekly quota report...");
        try (Stream<User> users = userRepository.findAllBy()) {
            // We stream users one by one from the DB (no OutOfMemoryError)
            users.forEach(user -> {
                try {
                    int quotaLeft = MAX_QUOTA - user.getMonthlyQuotaUsed();
                    String subject = "Your Weekly Journal Quota Report";
                    String body = "Hi " + user.getUserName() + ",\n\nYou have "
                            + quotaLeft + " / " + MAX_QUOTA + " quota left this month.";
                    // Async Email Sending
                    emailService.sendEmail(
                            user.getEmail(),
                            subject,
                            body,
                            "user:" + user.getId()
                    );
                } catch (Exception e) {
                    // Catch errors for *this* user so the loop continues
                    log.error("Failed to send quota email for user {}", user.getId(), e);
                }
            });

        } catch (Throwable t) {
            // Catch any top-level errors (like DB connection)
            log.error("CRITICAL: Weekly quota report job failed entirely.", t);
        }
        log.info("JOB_END: Weekly quota report finished.");
    }

    /**
     * JOB 2: Resets the monthly quota for ALL users.
     * Runs at 00:00 UTC on the 1st of every month.
     */
    @Async
    @Scheduled(cron = "${app.jobs.quota-reset-cron}", zone = "UTC")
    @Transactional // 2. This is a write operation, so it needs a transaction
    public void resetMonthlyQuotas() {
        log.info("JOB_START: Resetting all user monthly quotas...");
        try {
            // Build an efficient bulk update query
            Query query = new Query(); // An empty query means "match all documents"
            Update update = new Update().set("monthlyQuotaUsed", 0); // Set the field to 0

            // This sends ONE command to MongoDB to update ALL users
            // This is infinitely faster and safer than looping.
            var result = mongoTemplate.updateMulti(query, update, User.class);

            log.info("JOB_END: Monthly quotas reset. {} users updated.", result.getModifiedCount());

        } catch (Throwable t) {
            // Catch potential DB errors
            log.error("CRITICAL: Monthly quota reset job failed!", t);
        }
    }
}
