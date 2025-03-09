package com.glucovision.historyservice.repository;

import com.glucovision.historyservice.model.History;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoryRepository extends MongoRepository<History, String> {
}
