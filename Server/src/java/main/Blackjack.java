/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Random;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author ambitos
 */
@WebService(name = "Blackjack", serviceName = "BlackjackService")
public class Blackjack {
    
    private @Resource WebServiceContext webServiceContext;
    private MessageContext messageContext;
    private HttpSession session;
    
    @WebMethod(operationName = "DealCard")
    public String DealCard()
    {
        String card = "";
        ArrayList<String> deck = (ArrayList<String>)session.getAttribute("deck");
        
        card = deck.get(0);
        deck.remove(0);
        return card;
    }
    
    @WebMethod(operationName = "Shuffle")
    public void Shuffle()
    {
        //Get the HttpSession to saving deck for current client
        messageContext = webServiceContext.getMessageContext();
        session = ((HttpServletRequest)messageContext.get(MessageContext.SERVLET_REQUEST)).getSession();
        
        ArrayList<String> deck = new ArrayList<String>();
        
        for(int face = 1;face <= 13; face++)
        {
            for(int suit = 0;suit<=3;suit++)
            {
                deck.add(face + " " + suit); //Add all cards to deck
            }
        }
        
        String tempCard;
        Random randomObject = new Random();
        int index; //index of random selected card 
        
        for(int i=0; i<deck.size();i++)
        {
            index = randomObject.nextInt(deck.size() - 1);
            tempCard = deck.get(i);
            deck.set(i,deck.get(index));
            deck.set(index, tempCard);
        }
        
        session.setAttribute("deck", deck);
    }
    
    @WebMethod(operationName="GetHandValue")
    public int GetHandValue(@WebParam(name = "hand") String hand)
    {
        String[] cards = hand.split("\t");
        int total = 0;
        int face;
        int aceCount = 0;
        
        for(int i=0;i<cards.length;i++)
        {
            face = Integer.parseInt(cards[i].substring(0, cards[i].indexOf(" ")));
            
            switch(face)
            {
                case 1://Ace
                    ++aceCount;
                    break;
                case 11://vales Prince
                case 12://vasilissa Queen
                case 13://vasilias King
                    total+=10;
                    break;
                default:
                    total+= face;
                    break;
            }
        }
        
        if(aceCount > 0)
        {
            if(total + 11 + aceCount - 1 <= 21)
            {
                total += 11 + aceCount - 1;
            }
            else
            {
                total += aceCount;
            }
        }
        return total;
    }
}
