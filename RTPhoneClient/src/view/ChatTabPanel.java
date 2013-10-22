/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import clientRemoteMethodInvocation.ClientRemoteMethodInvocation;
import database.Client;
import database.ClientMessage;
import java.awt.event.KeyEvent;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JH
 */
public class ChatTabPanel extends javax.swing.JPanel {

   private MainWindow mainWindow;
   private database.Client client;
   private ArrayList<ClientMessage> actualMessageList;

   /**
    * Creates new form ChatTabPanel
    */
   public ChatTabPanel(MainWindow mainWindow, database.Client client) {
      actualMessageList=new ArrayList<>();
      this.client = client;
      this.mainWindow = mainWindow;
      initComponents();
   }

   public Client getClient() {
      return client;
   }

   public MainWindow getMainWindow() {
      return mainWindow;
   }

   public void append(ClientMessage clientMessage) {
      if (clientMessage.getIndex() == -1) {
         jTextAreaChat.append(clientMessage.toString());
      }else{
         for (int index = 0; index < actualMessageList.size(); index++) {
            if(actualMessageList.get(index).getIndex()>clientMessage.getIndex()){
               actualMessageList.add(index, clientMessage);
            }
         }
         jTextAreaChat.setText("");
         for (int index = 0; index < actualMessageList.size(); index++) {
            jTextAreaChat.append(actualMessageList.get(index).toString());
         }
      }
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaSend = new javax.swing.JTextArea();
        jCheckBoxSend = new javax.swing.JCheckBox();
        jButtonSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaChat = new javax.swing.JTextArea();

        jTextAreaSend.setColumns(20);
        jTextAreaSend.setRows(2);
        jTextAreaSend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextAreaSendKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTextAreaSend);

        jCheckBoxSend.setText("Enter to send");

        jButtonSend.setText("Send");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });

        jTextAreaChat.setEditable(false);
        jTextAreaChat.setColumns(20);
        jTextAreaChat.setRows(5);
        jScrollPane1.setViewportView(jTextAreaChat);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxSend))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBoxSend)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSend))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

   private void sendMessage(){
       ClientMessage clientMessage = new ClientMessage(mainWindow.getMe(), client, jTextAreaSend.getText());
      if(client.getAddress()==null || client.getAddress().isEmpty()){
         mainWindow.getLoginWindow().getDefaultServerConfigurationsWindow().getDatabase().makeMessage(clientMessage);
      }else{
          try {
              ClientRemoteMethodInvocation rmi;
              Registry registry = LocateRegistry.getRegistry(client.getAddress(), 9000);
              rmi = (ClientRemoteMethodInvocation) registry.lookup("RTPhoneClient");
              rmi.sendMessage(clientMessage);
          } catch (RemoteException | NotBoundException exception) {
              Logger.getLogger(ChatTabPanel.class.getName()).log(Level.SEVERE, null, exception);
          }
      }
      jTextAreaSend.setText("");
   }
   
   private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
      sendMessage();
   }//GEN-LAST:event_jButtonSendActionPerformed

    private void jTextAreaSendKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextAreaSendKeyReleased
      if(evt.getKeyCode()== KeyEvent.VK_ENTER){
           if(jCheckBoxSend.isSelected()){
               sendMessage();
            }
      }
    }//GEN-LAST:event_jTextAreaSendKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSend;
    private javax.swing.JCheckBox jCheckBoxSend;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaChat;
    private javax.swing.JTextArea jTextAreaSend;
    // End of variables declaration//GEN-END:variables
}
