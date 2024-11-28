package com.os.fasdoor.repository;

import com.os.fasdoor.model.Logs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogsRepo extends JpaRepository<Logs, Long> {
}
