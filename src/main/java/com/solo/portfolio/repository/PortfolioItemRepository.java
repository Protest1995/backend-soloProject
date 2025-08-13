package com.solo.portfolio.repository;

import com.solo.portfolio.model.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, String> {}


