/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blackjackclient;

//import com.deitel.java.blackjack.Blackjack;
//import com.deitel.java.blackjack.BlackjackService;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author ambitos
 */
public class BlackjackClientJFrame extends javax.swing.JFrame {

    private String playerCards;
    private String dealerCards; 
    private ArrayList<JLabel> cardBoxes;
    private int currentPlayerCard;
    private int currentDealerCard;
    private BlackjackService blackjackService;
    private Blackjack blackjackProxy;
    
    private enum GameStatus
    {
        PUSH,
        LOSE,
        WIN,
        BLACKJACK
    }
    
    /**
     * Creates new form BlackjackClientJFrame
     */
    public BlackjackClientJFrame() {
        initComponents();
        
        getContentPane().setBackground(new Color(0,180,0));
        
        try
        {
            blackjackService = new BlackjackService();
            blackjackProxy = blackjackService.getBlackjackPort();
            
            ( (BindingProvider) blackjackProxy ).getRequestContext().put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        cardBoxes = new ArrayList<JLabel>();
        
        cardBoxes.add(dealerCard1JLabel); 
        cardBoxes.add(dealerCard2JLabel); 
        cardBoxes.add(dealerCard3JLabel); 
        cardBoxes.add(dealerCard4JLabel); 
        cardBoxes.add(dealerCard5JLabel); 
        cardBoxes.add(dealerCard6JLabel); 
        cardBoxes.add(dealerCard7JLabel); 
        cardBoxes.add(dealerCard8JLabel); 
        cardBoxes.add(dealerCard9JLabel); 
        cardBoxes.add(dealerCard10JLabel); 
        cardBoxes.add(dealerCard11JLabel); 
        cardBoxes.add(playerCard1JLabel); 
        cardBoxes.add(playerCard2JLabel); 
        cardBoxes.add(playerCard3JLabel); 
        cardBoxes.add(playerCard4JLabel); 
        cardBoxes.add(playerCard5JLabel); 
        cardBoxes.add(playerCard6JLabel); 
        cardBoxes.add(playerCard7JLabel); 
        cardBoxes.add(playerCard8JLabel); 
        cardBoxes.add(playerCard9JLabel); 
        cardBoxes.add(playerCard10JLabel); 
        cardBoxes.add(playerCard11JLabel); 
        
        InitForm();
    }
    
    private void InitForm()
    {
        InitLabelsSatus(false,0, 0);
        InitCards();
        InitActionButtons();
    }
    
    private void InitLabelsSatus(Boolean status,int dealerScore,int playerScore)
    {
        gameResultsJLabel.setVisible(status);
        dealerJLabel.setVisible(status);
        dealerScoreJLabel.setText(String.valueOf(dealerScore));
        dealerScoreJLabel.setVisible(status);
        playerScoreJLabel.setText(String.valueOf(playerScore));
        playerScoreJLabel.setVisible(status);
        playerJLabel.setVisible(status);
    }
    
    private void InitCards()
    {
        for(int i=0;i<cardBoxes.size();i++)
        {
            cardBoxes.get(i).setIcon(null);
        }
    }
    
    private void InitActionButtons()
    {
        dealJButton.setEnabled(true);
        hitJButton.setEnabled(false);
        standJButton.setEnabled(false);
    }
    
    private void dealerPlay()
    {
        try
        {
            //White hand value is under 17 
            //Dealer must deal cards 
            String[] cards = dealerCards.split("\t");
            
            for(int i = 0;i<cards.length; i++)
            {
                displayCard(i,cards[i]);
            }
            
            while(blackjackProxy.getHandValue(dealerCards) < 17)
            {
                String newCard = blackjackProxy.dealCard(); //New card 
                dealerCards += "\t" + newCard;
                displayCard(currentDealerCard, newCard);
                ++currentDealerCard;
                JOptionPane.showMessageDialog(this, "Dealer takes a card", "Dealer's turn", JOptionPane.PLAIN_MESSAGE);
            }
            
            int dealersTotal = blackjackProxy.getHandValue(dealerCards);
            int playersTotal = blackjackProxy.getHandValue(playerCards);
            
            if(dealersTotal > 21)
            {
                gameOver( GameStatus.WIN );
                return;
            }
            
            if(dealersTotal > playersTotal)
            {
                gameOver(GameStatus.LOSE);
            }
            else if(dealersTotal < playersTotal)
            {
                gameOver(GameStatus.WIN);
            }
            else
            {
                gameOver(GameStatus.PUSH);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void displayCard(int card, String cardValue)
    {
        try
        {
            JLabel displayLabel = cardBoxes.get(card);
            
            //Show back
            if(cardValue.equals(""))
            {
                displayLabel.setIcon(new ImageIcon(getClass().getResource("/blackjackclient/images/back-blue.png")));
                return;
            }
            
            String face = cardValue.substring(0, cardValue.indexOf(" "));
            String suit = cardValue.substring(cardValue.indexOf(" ") + 1);
            
            char suitLetter;
            
            switch(Integer.parseInt(suit))
            {
                case 0:
                    suitLetter = 'h';
                    break;
                case 1:
                    suitLetter = 'd';
                    break;
                case 2:
                    suitLetter = 'c';
                    break;
                default:
                    suitLetter = 's';
                    break;
            }
            
            displayLabel.setIcon( new ImageIcon(getClass().getResource("/blackjackclient/images/" + face + suitLetter + ".png")));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void gameOver(GameStatus winner)
    {
        String[] cards = dealerCards.split("\t");
        
        for(int i=0;i<cards.length; i++)
        {
            displayCard(i,cards[i]);
        }
        
        
        if(winner == GameStatus.WIN)
        {
            gameResultsJLabel.setText("You win!");
            //TODO - add score to user
        }
        else if(winner == GameStatus.LOSE)
        {
            gameResultsJLabel.setText("You lose.");
            //TODO - remove score from user
        }
        else if(winner == GameStatus.PUSH)
        {
            gameResultsJLabel.setText("It's push.");
        }
        else // blackjack
        {
            gameResultsJLabel.setText("Blackjack!");
            //TODO - add score to user
        }
        
        int dealersTotal = blackjackProxy.getHandValue(dealerCards);
        int playersTotal = blackjackProxy.getHandValue(playerCards);
        InitLabelsSatus(true,dealersTotal,playersTotal);
        
        setActionButtonStatus(false,false,true);
    }

    private void setActionButtonStatus(Boolean standStatus,Boolean hitStatus, Boolean dealStatus)
    {
        standJButton.setEnabled(standStatus);
        hitJButton.setEnabled(hitStatus);
        dealJButton.setEnabled(dealStatus);
    }
            
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dealJButton = new javax.swing.JButton();
        hitJButton = new javax.swing.JButton();
        standJButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dealerCard1JLabel = new javax.swing.JLabel();
        dealerCard2JLabel = new javax.swing.JLabel();
        dealerCard3JLabel = new javax.swing.JLabel();
        dealerCard4JLabel = new javax.swing.JLabel();
        dealerCard11JLabel = new javax.swing.JLabel();
        dealerCard6JLabel = new javax.swing.JLabel();
        dealerCard7JLabel = new javax.swing.JLabel();
        dealerCard8JLabel = new javax.swing.JLabel();
        dealerCard9JLabel = new javax.swing.JLabel();
        dealerCard10JLabel = new javax.swing.JLabel();
        dealerCard5JLabel = new javax.swing.JLabel();
        playerCard1JLabel = new javax.swing.JLabel();
        playerCard2JLabel = new javax.swing.JLabel();
        playerCard3JLabel = new javax.swing.JLabel();
        playerCard4JLabel = new javax.swing.JLabel();
        playerCard5JLabel = new javax.swing.JLabel();
        playerCard6JLabel = new javax.swing.JLabel();
        playerCard8JLabel = new javax.swing.JLabel();
        playerCard7JLabel = new javax.swing.JLabel();
        playerCard9JLabel = new javax.swing.JLabel();
        playerCard10JLabel = new javax.swing.JLabel();
        playerCard11JLabel = new javax.swing.JLabel();
        gameResultsJLabel = new javax.swing.JLabel();
        dealerJLabel = new javax.swing.JLabel();
        dealerScoreJLabel = new javax.swing.JLabel();
        playerJLabel = new javax.swing.JLabel();
        playerScoreJLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        dealJButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        dealJButton.setText("Deal");
        dealJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dealJButtonActionPerformed(evt);
            }
        });

        hitJButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        hitJButton.setText("Hit");
        hitJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitJButtonActionPerformed(evt);
            }
        });

        standJButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        standJButton.setText("Stand");
        standJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                standJButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Dealer's hand:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Player's hand:");

        dealerCard1JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        dealerCard1JLabel.setToolTipText("");

        dealerCard2JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard3JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard4JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard11JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard6JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard7JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard8JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard9JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard10JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        dealerCard5JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N

        playerCard1JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard1JLabel.setToolTipText("");

        playerCard2JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard2JLabel.setToolTipText("");

        playerCard3JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard3JLabel.setToolTipText("");

        playerCard4JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard4JLabel.setToolTipText("");

        playerCard5JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard5JLabel.setToolTipText("");

        playerCard6JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard6JLabel.setToolTipText("");

        playerCard8JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard8JLabel.setToolTipText("");

        playerCard7JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard7JLabel.setToolTipText("");

        playerCard9JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard9JLabel.setToolTipText("");

        playerCard10JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard10JLabel.setToolTipText("");

        playerCard11JLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/blackjackclient/images/1c.png"))); // NOI18N
        playerCard11JLabel.setToolTipText("");

        gameResultsJLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gameResultsJLabel.setText("Blackjack!");

        dealerJLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        dealerJLabel.setText("Dealer:");

        dealerScoreJLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        dealerScoreJLabel.setText("17");

        playerJLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        playerJLabel.setText("Player:");

        playerScoreJLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        playerScoreJLabel.setText("21");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playerCard1JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard2JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard3JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard4JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard5JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard6JLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playerCard7JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard8JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard9JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard10JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerCard11JLabel)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dealerCard1JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard2JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard3JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard4JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard5JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard6JLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dealerCard7JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard8JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard9JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard10JLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerCard11JLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gameResultsJLabel)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(hitJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(dealJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(standJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dealerJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dealerScoreJLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(playerJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playerScoreJLabel)))
                        .addGap(28, 28, 28))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(dealerCard1JLabel)
                                        .addComponent(dealerCard2JLabel))
                                    .addComponent(dealerCard3JLabel)
                                    .addComponent(dealerCard4JLabel)
                                    .addComponent(dealerCard6JLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dealerCard8JLabel)
                                    .addComponent(dealerCard7JLabel)
                                    .addComponent(dealerCard9JLabel)
                                    .addComponent(dealerCard10JLabel)
                                    .addComponent(dealerCard11JLabel)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dealJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(hitJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(standJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(54, 54, 54)
                        .addComponent(gameResultsJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(dealerJLabel)
                            .addComponent(dealerScoreJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(playerJLabel)
                            .addComponent(playerScoreJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerCard1JLabel)
                            .addComponent(playerCard2JLabel)
                            .addComponent(playerCard3JLabel)
                            .addComponent(playerCard4JLabel)
                            .addComponent(playerCard5JLabel)
                            .addComponent(playerCard6JLabel))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerCard7JLabel)
                            .addComponent(playerCard8JLabel)
                            .addComponent(playerCard9JLabel)
                            .addComponent(playerCard10JLabel)
                            .addComponent(playerCard11JLabel))
                        .addGap(30, 30, 30))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dealerCard5JLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dealJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dealJButtonActionPerformed
        
        String card;
        
        for(int i =0; i<cardBoxes.size();i++)
        {
            cardBoxes.get(i).setIcon(null);
        }
        
        InitLabelsSatus(false,0, 0);
        
        //Shuffle the cards in server - Ανακατεύει την τράπουλα στον Server
        blackjackProxy.shuffle();
        
        //Deal two cards to player
        playerCards = blackjackProxy.dealCard(); //First card of player
        displayCard(11, playerCards); //Show first card
        card = blackjackProxy.dealCard();
        displayCard(12, card); //Show second card
        playerCards += "\t" + card; //Add the second card
        
        //Deal two cards to Dealer
        dealerCards = blackjackProxy.dealCard();
        displayCard(0,dealerCards);
        card = blackjackProxy.dealCard();
        displayCard(1,""); //Show back place of dealer second card
        dealerCards += "\t" + card;
        
        setActionButtonStatus(true,true,false);
        
        int dealersTotal = blackjackProxy.getHandValue(dealerCards);
        int playersTotal = blackjackProxy.getHandValue(playerCards);
        
        if(playersTotal == dealersTotal && playersTotal == 21)
        {
            gameOver(GameStatus.PUSH);
        }
        else if(dealersTotal == 21)
        {
            gameOver(GameStatus.LOSE);
        }
        else if(playersTotal == 21)
        {
            gameOver(GameStatus.BLACKJACK);
        }
        
        //Indexes of ArrayList<JLebel> cardBoxes
        currentDealerCard = 2; 
        currentPlayerCard = 13; 
    }//GEN-LAST:event_dealJButtonActionPerformed

    private void hitJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hitJButtonActionPerformed
        String card = blackjackProxy.dealCard();
        playerCards += "\t" + card;
        
        displayCard(currentPlayerCard, card);
        ++currentPlayerCard;
        
        int total = blackjackProxy.getHandValue(playerCards);
        
        if(total > 21) //player lose
        {
            gameOver(GameStatus.LOSE);
        }
        else if(total == 21)
        {
            hitJButton.setEnabled(false); //Player cannot take another card
            dealerPlay();
        }
    }//GEN-LAST:event_hitJButtonActionPerformed

    private void standJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_standJButtonActionPerformed
        setActionButtonStatus(false,false,true);
        dealerPlay();
    }//GEN-LAST:event_standJButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BlackjackClientJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BlackjackClientJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BlackjackClientJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BlackjackClientJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BlackjackClientJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dealJButton;
    private javax.swing.JLabel dealerCard10JLabel;
    private javax.swing.JLabel dealerCard11JLabel;
    private javax.swing.JLabel dealerCard1JLabel;
    private javax.swing.JLabel dealerCard2JLabel;
    private javax.swing.JLabel dealerCard3JLabel;
    private javax.swing.JLabel dealerCard4JLabel;
    private javax.swing.JLabel dealerCard5JLabel;
    private javax.swing.JLabel dealerCard6JLabel;
    private javax.swing.JLabel dealerCard7JLabel;
    private javax.swing.JLabel dealerCard8JLabel;
    private javax.swing.JLabel dealerCard9JLabel;
    private javax.swing.JLabel dealerJLabel;
    private javax.swing.JLabel dealerScoreJLabel;
    private javax.swing.JLabel gameResultsJLabel;
    private javax.swing.JButton hitJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel playerCard10JLabel;
    private javax.swing.JLabel playerCard11JLabel;
    private javax.swing.JLabel playerCard1JLabel;
    private javax.swing.JLabel playerCard2JLabel;
    private javax.swing.JLabel playerCard3JLabel;
    private javax.swing.JLabel playerCard4JLabel;
    private javax.swing.JLabel playerCard5JLabel;
    private javax.swing.JLabel playerCard6JLabel;
    private javax.swing.JLabel playerCard7JLabel;
    private javax.swing.JLabel playerCard8JLabel;
    private javax.swing.JLabel playerCard9JLabel;
    private javax.swing.JLabel playerJLabel;
    private javax.swing.JLabel playerScoreJLabel;
    private javax.swing.JButton standJButton;
    // End of variables declaration//GEN-END:variables
}
