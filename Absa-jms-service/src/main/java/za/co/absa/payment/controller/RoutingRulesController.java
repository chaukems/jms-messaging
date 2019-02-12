/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.absa.payment.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.absa.payment.bean.RoutingRule;
import za.co.absa.payment.service.RoutingRulesService;


@RestController
@RequestMapping("/rulesEngine")
public class RoutingRulesController {

    @Autowired
    private RoutingRulesService routingRulesService;

    @GetMapping("/rules")
    public List getRoutingRules() {
        return routingRulesService.getRules();
    }

    @GetMapping("/rule/{id}")
    public RoutingRule getRoutingRules(@PathVariable("id") long id) {
        return routingRulesService.findRuleById(id);
    }

}
