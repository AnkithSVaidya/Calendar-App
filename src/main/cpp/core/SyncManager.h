#ifndef SYNC_MANAGER_H
#define SYNC_MANAGER_H

#include <thread>
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <unordered_map>
#include <queue>
#include <chrono>
#include <string>

namespace calendar {

class SyncManager {
private:
    struct ClientConnection {
        std::string client_id;
        std::chrono::steady_clock::time_point last_sync;
        std::queue<std::string> pending_updates;
        bool is_active;
    };
    
    std::unordered_map<std::string, ClientConnection> clients;
    std::mutex clients_mutex;
    
    std::thread sync_thread;
    std::atomic<bool> running{false};
    std::condition_variable sync_cv;
    std::mutex sync_mutex;
    
    void syncLoop();
    
public:
    SyncManager();
    ~SyncManager();
    
    void startRealtimeSync();
    void stopRealtimeSync();
    
    
    void registerClient(const std::string& client_id);
    void disconnectClient(const std::string& client_id);
    
    // For broadcasting updates to all connected clients
    void broadcastUpdate(const std::string& update);
    void sendUpdateToClient(const std::string& client_id, const std::string& update);
    
    size_t getActiveClientCount() const;
};

}

#endif