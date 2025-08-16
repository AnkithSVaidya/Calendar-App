#ifndef EVENT_SCHEDULER_H
#define EVENT_SCHEDULER_H

#include <vector>
#include <thread>
#include <mutex>
#include <queue>
#include <future>
#include <condition_variable>
#include <memory>
#include <functional>
#include <atomic>
#include <shared_mutex>
#include <string>
#include <chrono>

namespace calendar {

struct Event {
    int id;
    std::string title;
    std::string description;
    long start_timestamp;  
    long end_timestamp;    
    std::string user_id;
    int priority;
    bool is_recurring;
    
    bool overlaps(const Event& other) const {
        return !(end_timestamp <= other.start_timestamp || 
                start_timestamp >= other.end_timestamp);
    }
};

class EventScheduler {
private:
    // Thread pool for handling concurrent requests
    std::vector<std::thread> worker_threads;
    std::queue<std::function<void()>> task_queue;
    std::mutex queue_mutex;
    std::condition_variable cv;
    std::atomic<bool> stop_flag{false};
    
    // Storing Events with thread-safe access
    std::vector<Event> events;
    mutable std::shared_mutex events_mutex;
    
    // Performance metrics
    std::atomic<size_t> total_requests{0};
    std::atomic<size_t> successful_adds{0};
    std::atomic<size_t> conflict_count{0};
    
    void worker_thread_function();
    bool hasConflict(const Event& new_event) const;
    
public:
    explicit EventScheduler(size_t num_threads = 4);
    ~EventScheduler();
    
    // Async operations for concurrent access
    std::future<bool> addEventAsync(const Event& event);
    std::future<std::vector<Event>> getEventsAsync(const std::string& user_id);
    std::future<bool> removeEventAsync(int event_id);
    std::future<bool> updateEventAsync(const Event& event);
    
    // Synchronous operations 
    bool addEvent(const Event& event);
    std::vector<Event> getEvents(const std::string& user_id) const;
    std::vector<Event> getAllEvents() const;
    bool removeEvent(int event_id);
    
    // Performance monitoring
    size_t getTotalRequests() const { return total_requests.load(); }
    size_t getSuccessfulAdds() const { return successful_adds.load(); }
    size_t getConflictCount() const { return conflict_count.load(); }
    
    // Batch operations for efficiency
    std::vector<bool> addEventsBatch(const std::vector<Event>& events);
};

} 

#endif 