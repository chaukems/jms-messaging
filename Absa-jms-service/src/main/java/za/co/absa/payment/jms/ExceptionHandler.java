/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.absa.payment.jms;

import org.springframework.stereotype.Service;
import org.springframework.util.ErrorHandler;

@Service
public class ExceptionHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        t.printStackTrace();
    }
}
