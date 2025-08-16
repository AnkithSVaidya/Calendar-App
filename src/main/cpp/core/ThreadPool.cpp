#include "ThreadPool.h"
#include <iostream>

namespace calendar {

ThreadPool::ThreadPool(size_t num_threads) : stop(false) {
    for(size_t i = 0; i < num_threads; ++i) {
        workers.emplace_back([this] {
            for(;;) {
                std::function<void()> task;
                
                {
                    std::unique_lock<std::mutex> lock(this->queue_mutex);
                    this->condition.wait(lock, [this]{ 
                        return this->stop || !this->tasks.empty(); 
                    });
                    
                    if(this->stop && this->tasks.empty()) {
                        return;
                    }
                    
                    task = std::move(this->tasks.front());
                    this->tasks.pop();
                }
                
                // Execute task
                task();
                tasks_completed++;
            }
        });
    }
}

ThreadPool::~ThreadPool() {
    {
        std::unique_lock<std::mutex> lock(queue_mutex);
        stop = true;
    }
    
    condition.notify_all();
    
    for(std::thread &worker: workers) {
        if(worker.joinable()) {
            worker.join();
        }
    }
}

size_t ThreadPool::getQueueSize() const {
    std::unique_lock<std::mutex> lock(queue_mutex);
    return tasks.size();
}

} 