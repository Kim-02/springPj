package com.dasolsystem.core.jparepository;

import com.dasolsystem.core.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<Image,Long> {
}
