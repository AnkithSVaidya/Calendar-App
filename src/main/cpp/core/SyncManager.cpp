#include "SyncManager.h"
#include <iostream>

namespace calendar {

SyncManager::SyncManager() {}

SyncManager::~SyncManager() {
    stopRealtimeSync();
}

void SyncManager::startRealtimeSync() {
    if (running.exchange(true)) return;
    
    sync_thread = std::thread(&SyncManager::syncLoop, this);
}

void SyncManager::stopRealtimeSync() {
    if (!running.exchange(false)) return;
    
    sync_cv.notify_all();
    if (sync_thread.joinable()) {
        sync_thread.join();
    }
}

void SyncManager::syncLoop() {
    while (running) {
        std::unique_lock<std::mutex> lock(sync_mutex);
        sync_cv.wait_for(lock, std::chrono::milliseconds(100));
        
        if (!running) break;
        
        // Process pending updates for all clients
        {
            std::lock_guard<std::mutex> client_lock(clients_mutex);
            auto now = std::chrono::steady_clock::now();
            
            for (auto& [client_id, connection] : clients) {
                if (!connection.is_active) continue;
                
                // Send all pending updates to client
                while (!connection.pending_updates.empty()) {
                    std::string update = connection.pending_updates.front();
                    connection.pending_updates.pop();
                    
                    // In real implementation, this would use WebSocket/TCP
                    // For now, just log it
                    std::cout << "Sync[" << client_id << "]: " << update << std::endl;
                    
                    connection.last_sync = now;
                }
            }
        }
    }
}

void SyncManager::registerClient(const std::string& client_id) {
    std::lock_guard<std::mutex> lock(clients_mutex);
    
    clients[client_id] = ClientConnection{
        client_id,
        std::chrono::steady_clock::now(),
        std::queue<std::string>(),
        true
    };
    
    std::cout << "Client registered for real-time sync: " << client_id << std::endl;
}

void SyncManager::disconnectClient(const std::string& client_id) {
    std::lock_guard<std::mutex> lock(clients_mutex);
    
    auto it = clients.find(client_id);
    if (it != clients.end()) {
        it->second.is_active = false;
        std::cout << "Client disconnected from sync: " << client_id << std::endl;
    }
}

void SyncManager::broadcastUpdate(const std::string& update) {
    std::lock_guard<std::mutex> lock(clients_mutex);
    
    // Queue update for all active clients
    for (auto& [client_id, connection] : clients) {
        if (connection.is_active) {
            connection.pending_updates.push(update);
        }
    }
    
    // Wake up sync thread to process updates
    sync_cv.notify_one();
}

void SyncManager::sendUpdateToClient(const std::string& client_id, 
                                     const std::string& update) {
    std::lock_guard<std::mutex> lock(clients_mutex);
    
    auto it = clients.find(client_id);
    if (it != clients.end() && it->second.is_active) {
        it->second.pending_updates.push(update);
        sync_cv.notify_one();
    }
}

size_t SyncManager::getActiveClientCount() const {
    std::lock_guard<std::mutex> lock(clients_mutex);
    
    size_t count = 0;
    for (const auto& [id, connection] : clients) {
        if (connection.is_active) count++;
    }
    return count;
}

}