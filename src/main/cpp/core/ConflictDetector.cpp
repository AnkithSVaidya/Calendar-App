#include "ConflictDetector.h"
#include <algorithm>
#include <set>
#include <iostream>

namespace calendar {

ConflictDetector::ConflictResult ConflictDetector::detectConflictsBasic(
    const std::vector<TimeSlot>& slots) {
    
    auto start_time = std::chrono::high_resolution_clock::now();
    ConflictResult result;
    result.has_conflict = false;
    result.total_conflicts = 0;
    
    
    for (size_t i = 0; i < slots.size(); ++i) {
        for (size_t j = i + 1; j < slots.size(); ++j) {
            // Only check conflicts for same user
            if (slots[i].user_id == slots[j].user_id && 
                slots[i].overlaps(slots[j])) {
                result.has_conflict = true;
                result.conflicting_pairs.push_back({slots[i].event_id, 
                                                   slots[j].event_id});
                result.total_conflicts++;
            }
        }
    }
    
    auto end_time = std::chrono::high_resolution_clock::now();
    result.detection_time = std::chrono::duration_cast<std::chrono::microseconds>(
        end_time - start_time);
    
    return result;
}

ConflictDetector::ConflictResult ConflictDetector::detectConflictsOptimized(
    const std::vector<TimeSlot>& slots) {
    
    auto start_time = std::chrono::high_resolution_clock::now();
    ConflictResult result;
    result.has_conflict = false;
    result.total_conflicts = 0;
    
    Grouping slots by user for more efficient processing
    std::unordered_map<std::string, std::vector<TimeSlot>> user_slots;
    for (const auto& slot : slots) {
        user_slots[slot.user_id].push_back(slot);
    }
    
    // sweep line algorithm
    for (auto& [user_id, user_events] : user_slots) {
        // Sort events by start time
        std::sort(user_events.begin(), user_events.end(),
                 [](const TimeSlot& a, const TimeSlot& b) {
                     return a.start_time < b.start_time;
                 });
        
        // Sweep line algorithm takes only n log n per user
        std::vector<Event> events;
        for (const auto& slot : user_events) {
            events.push_back({slot.start_time, true, slot.event_id, slot.user_id});
            events.push_back({slot.end_time, false, slot.event_id, slot.user_id});
        }
        
        std::sort(events.begin(), events.end());
        
        std::set<int> active_events;
        for (const auto& event : events) {
            if (event.is_start) {
                // Checking conflicts with currently active events
                for (int active_id : active_events) {
                    result.has_conflict = true;
                    result.conflicting_pairs.push_back({active_id, event.slot_id});
                    result.total_conflicts++;
                }
                active_events.insert(event.slot_id);
            } else {
                active_events.erase(event.slot_id);
            }
        }
    }
    
    auto end_time = std::chrono::high_resolution_clock::now();
    result.detection_time = std::chrono::duration_cast<std::chrono::microseconds>(
        end_time - start_time);
    
    return result;
}

ConflictDetector::ConflictResult ConflictDetector::checkNewEvent(
    const TimeSlot& new_slot, 
    const std::vector<TimeSlot>& existing_slots) {
    
    auto start_time = std::chrono::high_resolution_clock::now();
    ConflictResult result;
    result.has_conflict = false;
    result.total_conflicts = 0;
    
    // Checking new event against all existing events for the same user
    for (const auto& existing : existing_slots) {
        if (existing.user_id == new_slot.user_id && 
            existing.overlaps(new_slot)) {
            result.has_conflict = true;
            result.conflicting_pairs.push_back({new_slot.event_id, existing.event_id});
            result.total_conflicts++;
        }
    }
    
    auto end_time = std::chrono::high_resolution_clock::now();
    result.detection_time = std::chrono::duration_cast<std::chrono::microseconds>(
        end_time - start_time);
    
    return result;
}

std::vector<ConflictDetector::TimeSlot> ConflictDetector::suggestAlternatives(
    const TimeSlot& conflicting_slot,
    const std::vector<TimeSlot>& existing_slots,
    int num_suggestions) {
    
    std::vector<TimeSlot> suggestions;
    
    // Getting all slots for the same user, sorted by time
    std::vector<TimeSlot> user_slots;
    for (const auto& slot : existing_slots) {
        if (slot.user_id == conflicting_slot.user_id) {
            user_slots.push_back(slot);
        }
    }
    
    std::sort(user_slots.begin(), user_slots.end(),
             [](const TimeSlot& a, const TimeSlot& b) {
                 return a.start_time < b.start_time;
             });
    
    long duration = conflicting_slot.end_time - conflicting_slot.start_time;
    long one_hour = 3600000; 
    
    // For finding gaps between existing events
    for (size_t i = 0; i <= user_slots.size() && suggestions.size() < num_suggestions; ++i) {
        long gap_start, gap_end;
        
        if (i == 0) {
            // Before first event
            gap_end = user_slots.empty() ? conflicting_slot.start_time + one_hour * 24 
                                         : user_slots[0].start_time;
            gap_start = gap_end - duration;
        } else if (i == user_slots.size()) {
            // After last event
            gap_start = user_slots[i-1].end_time;
            gap_end = gap_start + duration;
        } else {
            // Between events
            gap_start = user_slots[i-1].end_time;
            gap_end = user_slots[i].start_time;
        }
        
        // Checking if the gap is large enough
        if (gap_end - gap_start >= duration) {
            TimeSlot suggestion = conflicting_slot;
            suggestion.start_time = gap_start;
            suggestion.end_time = gap_start + duration;
            suggestions.push_back(suggestion);
        }
    }
    
    return suggestions;
}

std::shared_ptr<ConflictDetector::IntervalNode> ConflictDetector::buildIntervalTree(
    std::vector<TimeSlot>& slots, int start, int end) {
    
    if (start > end) return nullptr;
    
    int mid = start + (end - start) / 2;
    auto node = std::make_shared<IntervalNode>(slots[mid]);
    
    node->left = buildIntervalTree(slots, start, mid - 1);
    node->right = buildIntervalTree(slots, mid + 1, end);
    
    // Update max_end
    if (node->left) {
        node->max_end = std::max(node->max_end, node->left->max_end);
    }
    if (node->right) {
        node->max_end = std::max(node->max_end, node->right->max_end);
    }
    
    return node;
}

void ConflictDetector::findOverlaps(std::shared_ptr<IntervalNode> root,
                                   const TimeSlot& query,
                                   std::vector<int>& overlapping_ids) {
    if (!root) return;
    
    // Check if current node overlaps
    if (root->slot.overlaps(query)) {
        overlapping_ids.push_back(root->slot.event_id);
    }
    
    // Check left subtree if it might contain overlaps
    if (root->left && root->left->max_end >= query.start_time) {
        findOverlaps(root->left, query, overlapping_ids);
    }
    
    // Check right subtree if it might contain overlaps
    if (root->right && root->slot.start_time <= query.end_time) {
        findOverlaps(root->right, query, overlapping_ids);
    }
}

} 