package com.maliuvanchuk.controller;

import com.maliuvanchuk.model.Purchase;
import com.maliuvanchuk.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by ostap_000 on 9/24/2014.
 */
@Controller
public class PurchaseController {

    @Autowired
    PurchaseRepository repo;

    public static final String NAME1 = "Ostap";
    public static final String NAME2 = "Diego";
    String status = "%s owes %s %f EUR";

    @RequestMapping("/")
    public String index(Model model){
        Float ostapsSum = repo.findSumForName(NAME1);
        ostapsSum = ostapsSum == null ? 0 : ostapsSum;
        Float diegosSum = repo.findSumForName(NAME2);
        diegosSum = diegosSum == null ? 0 : diegosSum;
        String filledStatus;
        if (ostapsSum > diegosSum){
            filledStatus = String.format(status, NAME2, NAME1,ostapsSum - diegosSum);
        }else{
            filledStatus = String.format(status, NAME1, NAME2, -ostapsSum + diegosSum);
        }

        model.addAttribute("status",filledStatus);
        model.addAttribute("ostapPurchases",repo.findByBuyerLikeOrderByDateDesc(NAME1));
        model.addAttribute("diegoPurchases",repo.findByBuyerLikeOrderByDateDesc(NAME2));
        model.addAttribute("purchase",new Purchase());
        return "index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String index(Purchase purchase){
        purchase.setDate(new Date());
        repo.saveAndFlush(purchase);
        return "redirect:/";
    }

}
