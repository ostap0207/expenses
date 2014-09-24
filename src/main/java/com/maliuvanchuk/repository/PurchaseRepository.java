package com.maliuvanchuk.repository;

import com.maliuvanchuk.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by ostap_000 on 9/24/2014.
 */
public interface PurchaseRepository extends JpaRepository<Purchase,Long> {

    List<Purchase> findByBuyerLikeOrderByDateDesc(String buyer);

    @Query("select sum(p.amount) from Purchase p where p.buyer = :name")
    Float findSumForName(@Param("name") String name);

}
