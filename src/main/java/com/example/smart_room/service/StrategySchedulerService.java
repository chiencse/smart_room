package com.example.smart_room.service;

import com.example.smart_room.model.Strategy;
import com.example.smart_room.repository.StrategyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class StrategySchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(StrategySchedulerService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String REPEAT_DAILY = "REPEAT_DAILY";
    private static final String ONE_TIME = "ONE_TIME";

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    @Lazy
    private DeviceService strategyService;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    @PostConstruct
    public void init() {
        logger.info("Initializing scheduler for existing strategies");
        List<Strategy> strategies = strategyRepository.findAll();
        for (Strategy strategy : strategies) {
            if (isSchedulable(strategy)) {
                try {
                    scheduleStrategy(strategy.getId(), strategy.getStartTime(), strategy.getRepeatStatus(), 1L); // Replace 1L with actual userId
                    logger.info("Scheduled strategy ID: {} on startup", strategy.getId());
                } catch (Exception e) {
                    logger.error("Failed to schedule strategy ID: {} on startup: {}", strategy.getId(), e.getMessage());
                }
            }
        }
    }
    @Transactional
    public void scheduleStrategy(Long strategyId, String startTime, String repeatStatus, Long userId) {
        // Validate inputs
        if (startTime == null || repeatStatus == null) {
            logger.error("Cannot schedule strategy ID: {}. Start time or repeat status is null", strategyId);
            throw new IllegalArgumentException("Start time and repeat status must not be null");
        }

        if (!REPEAT_DAILY.equals(repeatStatus) && !ONE_TIME.equals(repeatStatus)) {
            logger.error("Invalid repeat status for strategy ID: {}. Must be REPEAT_DAILY or ONE_TIME", strategyId);
            throw new IllegalArgumentException("Repeat status must be REPEAT_DAILY or ONE_TIME");
        }

        // Cancel any existing scheduled task
        cancelScheduledStrategy(strategyId);

        // Parse startTime
        LocalTime time;
        try {
            time = LocalTime.parse(startTime, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Invalid start time format for strategy ID: {}. Expected HH:mm, got: {}", strategyId, startTime);
            throw new IllegalArgumentException("Invalid start time format. Expected HH:mm");
        }

        LocalDateTime nextRun = LocalDateTime.now().with(time);

        // If the time is in the past, adjust for next day
        if (nextRun.isBefore(LocalDateTime.now())) {
            nextRun = nextRun.plusDays(1);
        }

        // Schedule the task
        ScheduledFuture<?> scheduledTask;
        try {
            if (REPEAT_DAILY.equals(repeatStatus)) {
                // Daily repeating task
                scheduledTask = taskScheduler.scheduleAtFixedRate(
                        () -> {
                            try {
                                strategyService.runStrategy(strategyId, userId);
                                logger.info("Successfully executed strategy ID: {}", strategyId);
                            } catch (Exception e) {
                                logger.error("Failed to execute strategy ID: {}: {}", strategyId, e.getMessage());
                            }
                        },
                        nextRun.atZone(java.time.ZoneId.systemDefault()).toInstant(),
                        Duration.ofDays(24 * 60 * 60 * 1000) // Repeat every 24 hours
                );
                logger.info("Scheduled daily task for strategy ID: {} at {}", strategyId, nextRun);
            } else {
                // One-time task
                scheduledTask = taskScheduler.schedule(
                        () -> {
                            try {
                                strategyService.runStrategy(strategyId, userId);
                                logger.info("Successfully executed one-time strategy ID: {}", strategyId);
                                cancelScheduledStrategy(strategyId); // Clean up after one-time execution
                            } catch (Exception e) {
                                logger.error("Failed to execute one-time strategy ID: {}: {}", strategyId, e.getMessage());
                            }
                        },
                        nextRun.atZone(java.time.ZoneId.systemDefault()).toInstant()
                );
                logger.info("Scheduled one-time task for strategy ID: {} at {}", strategyId, nextRun);
            }

            // Store the scheduled task
            scheduledTasks.put(strategyId, scheduledTask);
        } catch (Exception e) {
            logger.error("Failed to schedule strategy ID: {}: {}", strategyId, e.getMessage());
            throw new RuntimeException("Failed to schedule strategy: " + e.getMessage());
        }
    }

    public void cancelScheduledStrategy(Long strategyId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(strategyId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            scheduledTasks.remove(strategyId);
            logger.info("Cancelled scheduled task for strategy ID: {}", strategyId);
        }
    }

    public void updateStrategySchedule(Long strategyId, String startTime, String status, String repeatStatus, Long userId) {
        if ("ACTIVE".equals(status) && startTime != null && repeatStatus != null) {
            scheduleStrategy(strategyId, startTime, repeatStatus, userId);
        } else {
            cancelScheduledStrategy(strategyId);
            logger.info("No scheduling needed for strategy ID: {} (inactive or missing startTime/repeatStatus)", strategyId);
        }
    }

    private boolean isSchedulable(Strategy strategy) {
        return "ACTIVE".equals(strategy.getStatus()) &&
                strategy.getStartTime() != null &&
                strategy.getRepeatStatus() != null;
    }
}