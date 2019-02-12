/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.absa.payment.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import za.co.absa.payment.bean.RoutingRule;


@Service
public class RoutingRulesServiceImpl implements RoutingRulesService {

    @Override
    public List<RoutingRule> getRules() {
        List<RoutingRule> rules = new ArrayList();

        return rules;
    }

    @Override
    public RoutingRule findRuleById(long id) {
        RoutingRule routingRule = new RoutingRule();
        return routingRule;
    }
}
