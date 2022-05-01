/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.service;

import com.anthill.OptimumBet.util.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author truth
 */
@Service("optimumService")
public class OptimumBetService {

    public String readMaliciousUrl(boolean isToPathSystem) throws IOException {
        File doc = new File(Utils.MALICIOUS_URL_DATA_IN_PATH_SYSTEM);
        Resource resource = new ClassPathResource(Utils.MALICIOUS_URL_DATA_IN_PATH_RESOURCES);
        
        Scanner obj =(isToPathSystem) ? new Scanner(doc) : new Scanner(resource.getFile());
        
        int count = 0;
        StringBuilder sb = new StringBuilder();
        while (obj.hasNextLine() && count < 10) {
            sb.append(obj.nextLine());
            sb.append("\n");
            count++;
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}
