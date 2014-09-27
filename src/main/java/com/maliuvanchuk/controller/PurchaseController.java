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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

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
        Float ostapsSum = Optional.ofNullable(repo.findSumForName(NAME1)).orElse(0.0f).floatValue();
        Float diegosSum = Optional.ofNullable(repo.findSumForName(NAME2)).orElse(0.0f).floatValue();
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


        Date monthStart = Date.from(LocalDate.now().withDayOfMonth(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date monthEnd = Date.from(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(monthStart.toString());

        model.addAttribute("ostapSum", repo.findSumForNameBetweenDates(NAME1, monthStart, monthEnd));
        model.addAttribute("diegoSum",repo.findSumForNameBetweenDates(NAME2, monthStart, monthEnd));

        return "index";
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
