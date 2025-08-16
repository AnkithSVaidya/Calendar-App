#ifndef CONFLICT_DETECTOR_H
#define CONFLICT_DETECTOR_H

#include <vector>
#include <unordered_map>
#include <set>
#include <memory>
#include <chrono>
#include <algorithm>
#include <string>

namespace calendar {

class ConflictDetector {
public:
    struct TimeSlot {
        int event_id;
        long start_time;
        long end_time;
        std::string user_id;
        int priority;
        
        bool overlaps(const TimeSlot& other) const {
            return !(end_time <= other.start_time || start_time >= other.end_time);
        }
    };
    
    struct ConflictResult {
        bool has_conflict;
        std::vector<std::pair<int, int>> conflicting_pairs;
        std::chrono::microseconds detection_time;
        int total_conflicts;
    };
    
    
    ConflictResult detectConflictsOptimized(const std::vector<TimeSlot>& slots);
    
    // Detecting conflicts for a single new event against existing events
    ConflictResult checkNewEvent(const TimeSlot& new_slot, 
                                const std::vector<TimeSlot>& existing_slots);
    
    std::vector<TimeSlot> suggestAlternatives(const TimeSlot& conflicting_slot,
                                             const std::vector<TimeSlot>& existing_slots,
                                             int num_suggestions = 3);
    
private:
    // Interval tree node for advanced conflict detection
    struct IntervalNode {
        TimeSlot slot;
        long max_end;
        std::shared_ptr<IntervalNode> left;
        std::shared_ptr<IntervalNode> right;
        
        IntervalNode(const TimeSlot& s) : slot(s), max_end(s.end_time) {}
    };
    
    // Building interval tree queries
    std::shared_ptr<IntervalNode> buildIntervalTree(std::vector<TimeSlot>& slots,
                                                    int start, int end);
    
    // Querying interval tree for overlaps
    void findOverlaps(std::shared_ptr<IntervalNode> root,
                     const TimeSlot& query,
                     std::vector<int>& overlapping_ids);
    
    // Helper for the sweep line algorithm
    struct Event {
        long time;
        bool is_start;
        int slot_id;
        std::string user_id;
        
        bool operator<(const Event& other) const {
            if (time != other.time) return time < other.time;
            return !is_start && other.is_start; // Process ends before starts
        }
    };
};

} 

#endif 