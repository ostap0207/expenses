package com.maliuvanchuk.controller;

import com.maliuvanchuk.model.Purchase;
import com.maliuvanchuk.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Created by ostap_000 on 9/24/2014.
 */
@Controller
public class PurchaseController {

    @Autowired
    PurchaseRepository repo;

    public static final String NAME1 = "Ostap";
    public static final String NAME2 = "Diego";
    String status = "%s owes %s %s EUR";

    @RequestMapping("/")
    public String index(Model model){
        Float ostapsSum = normalize(repo.findSumForName(NAME1));
        Float diegosSum = normalize(repo.findSumForName(NAME2));
        Float totalDiff = Math.abs(ostapsSum - diegosSum);

        String filledStatus;
        if (ostapsSum > diegosSum){
            filledStatus = formatStatus(status, NAME2, NAME1, totalDiff/2);
        }else{
            filledStatus = formatStatus(status, NAME1, NAME2, totalDiff/2);
        }

        model.addAttribute("status",filledStatus);
        model.addAttribute("totalDiff",totalDiff);
        model.addAttribute("ostapPurchases",repo.findByBuyerLikeOrderByDateDesc(NAME1));
        model.addAttribute("diegoPurchases",repo.findByBuyerLikeOrderByDateDesc(NAME2));
        model.addAttribute("purchase",new Purchase());

        Date towMonthsAgo = Date.from(LocalDate.now().minusMonths(2).withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date monthPrevStart = Date.from(LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date monthStart = Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date monthEnd = Date.from(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        model.addAttribute("ostapSum", normalize(repo.findSumForNameBetweenDates(NAME1, monthStart, monthEnd)));
        model.addAttribute("diegoSum", normalize(repo.findSumForNameBetweenDates(NAME2, monthStart, monthEnd)));

        model.addAttribute("ostapPrevSum", normalize(repo.findSumForNameBetweenDates(NAME1, monthPrevStart, monthStart)));
        model.addAttribute("diegoPrevSum", normalize(repo.findSumForNameBetweenDates(NAME2, monthPrevStart, monthStart)));

        List<Purchase> treeMonthPurchases = repo.findForNameBetweenDates(towMonthsAgo,monthEnd);
        model.addAttribute("weekData",parsePurchaseData(treeMonthPurchases));

        return "index";
    }

    private Map<LocalDate,Float> parsePurchaseData(List<Purchase> purchases){
       return purchases.stream().collect(Collectors.groupingBy(
               (Purchase p) -> p.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
               , Collectors.reducing(
                        0.0f,
                        Purchase::getAmount,
                        Float::sum)));
    }

    private Float normalize(Float number){
        return Optional.ofNullable(number).orElse(0.0f).floatValue();
    }

    private String formatStatus(String status, String name1,String name2,float sum){
        return String.format(status, name1, name2, NumberFormat.getNumberInstance().format(sum));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String index(Purchase purchase){
        purchase.setDate(new Date());
        repo.saveAndFlush(purchase);
        return "redirect:/";
    }

}
