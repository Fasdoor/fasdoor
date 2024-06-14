package com.os.onestopper.repository;

import com.os.onestopper.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepo extends JpaRepository<Logs, Long> {
}
