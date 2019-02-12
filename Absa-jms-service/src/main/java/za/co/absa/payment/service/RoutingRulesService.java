/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.absa.payment.service;

import java.util.List;
import za.co.absa.payment.bean.RoutingRule;

public interface RoutingRulesService {

    List<RoutingRule> getRules();

    RoutingRule findRuleById(long id);

}
