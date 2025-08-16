#include "EventScheduler.h"
#include <algorithm>
#include <iostream>
#include <chrono>

namespace calendar {

EventScheduler::EventScheduler(size_t num_threads) {
    // Initializing worker threads for concurrent request handling
    for (size_t i = 0; i < num_threads; ++i) {
        worker_threads.emplace_back(&EventScheduler::worker_thread_function, this);
    }
}

EventScheduler::~EventScheduler() {
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        stop_flag = true;
    }
    cv.notify_all();
    
    // Waits for all threads to complete
    for (auto& thread : worker_threads) {
        if (thread.joinable()) {
            thread.join();
        }
    }
}

void EventScheduler::worker_thread_function() {
    while (true) {
        std::function<void()> task;
        
        {
            std::unique_lock<std::mutex> lock(queue_mutex);
            cv.wait(lock, [this] { return stop_flag || !task_queue.empty(); });
            
            if (stop_flag && task_queue.empty()) {
                return;
            }
            
            task = std::move(task_queue.front());
            task_queue.pop();
        }
        
        task();
    }
}

bool EventScheduler::hasConflict(const Event& new_event) const {
    // Checking for time conflicts with existing events for the same user
    for (const auto& existing : events) {
        if (existing.user_id == new_event.user_id) {
            if (existing.overlaps(new_event)) {
                return true;
            }
        }
    }
    return false;
}

std::future<bool> EventScheduler::addEventAsync(const Event& event) {
    auto promise = std::make_shared<std::promise<bool>>();
    auto future = promise->get_future();
    
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        task_queue.emplace([this, event, promise]() {
            total_requests++;
            bool result = addEvent(event);
            promise->set_value(result);
        });
    }
    
    cv.notify_one();
    return future;
}

bool EventScheduler::addEvent(const Event& event) {
    std::unique_lock<std::shared_mutex> lock(events_mutex);
    
    // Checking for the conflicts before adding
    if (hasConflict(event)) {
        conflict_count++;
        return false;
    }
    
    // Adding event if theres no conflicts
    events.push_back(event);
    successful_adds++;
    return true;
}

std::future<std::vector<Event>> EventScheduler::getEventsAsync(const std::string& user_id) {
    auto promise = std::make_shared<std::promise<std::vector<Event>>>();
    auto future = promise->get_future();
    
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        task_queue.emplace([this, user_id, promise]() {
            total_requests++;
            auto result = getEvents(user_id);
            promise->set_value(result);
        });
    }
    
    cv.notify_one();
    return future;
}

std::vector<Event> EventScheduler::getEvents(const std::string& user_id) const {
    std::shared_lock<std::shared_mutex> lock(events_mutex);
    std::vector<Event> user_events;
    
    // Filtering events for specific user
    std::copy_if(events.begin(), events.end(), 
                 std::back_inserter(user_events),
                 [&user_id](const Event& e) { return e.user_id == user_id; });
    
    // Sorting by start time
    std::sort(user_events.begin(), user_events.end(),
              [](const Event& a, const Event& b) {
                  return a.start_timestamp < b.start_timestamp;
              });
    
    return user_events;
}

std::vector<Event> EventScheduler::getAllEvents() const {
    std::shared_lock<std::shared_mutex> lock(events_mutex);
    return events;
}

std::future<bool> EventScheduler::removeEventAsync(int event_id) {
    auto promise = std::make_shared<std::promise<bool>>();
    auto future = promise->get_future();
    
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        task_queue.emplace([this, event_id, promise]() {
            total_requests++;
            bool result = removeEvent(event_id);
            promise->set_value(result);
        });
    }
    
    cv.notify_one();
    return future;
}

bool EventScheduler::removeEvent(int event_id) {
    std::unique_lock<std::shared_mutex> lock(events_mutex);
    
    auto it = std::find_if(events.begin(), events.end(),
                          [event_id](const Event& e) { return e.id == event_id; });
    
    if (it != events.end()) {
        events.erase(it);
        return true;
    }
    
    return false;
}

std::future<bool> EventScheduler::updateEventAsync(const Event& event) {
    auto promise = std::make_shared<std::promise<bool>>();
    auto future = promise->get_future();
    
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        task_queue.emplace([this, event, promise]() {
            total_requests++;
            
            // Remove old event and add new one
            bool removed = removeEvent(event.id);
            if (removed) {
                bool added = addEvent(event);
                promise->set_value(added);
            } else {
                promise->set_value(false);
            }
        });
    }
    
    cv.notify_one();
    return future;
}

std::vector<bool> EventScheduler::addEventsBatch(const std::vector<Event>& events) {
    std::vector<bool> results;
    results.reserve(events.size());
    
    std::unique_lock<std::shared_mutex> lock(events_mutex);
    
    for (const auto& event : events) {
        total_requests++;
        if (!hasConflict(event)) {
            this->events.push_back(event);
            successful_adds++;
            results.push_back(true);
        } else {
            conflict_count++;
            results.push_back(false);
        }
    }
    
    return results;
}

}