/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.anthill.OptimumBet.util;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author truth
 */
public class Message {
    @Getter @Setter
    private String message;
    
    public Message(String message){
        this.message = message;
    }
}
