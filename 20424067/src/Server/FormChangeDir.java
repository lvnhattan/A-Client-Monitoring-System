/*
 * Created by JFormDesigner on Tue Aug 16 12:51:10 ICT 2022
 */

package Server;

import java.awt.event.*;
import Config.User.AccountUser;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author unknown
 */
public class FormChangeDir extends JFrame {

    AccountUser user;
    String defaultPath="D:/Test";

    public FormChangeDir(AccountUser user) {
        initComponents();
        this.user=user;
        lbInfo.setText("UserName: "+user.getUsername()+"\n"+"IpClient: "+user.getIpclient()+"\n");
        txtPathdir.setText(defaultPath);
    }

    private void btnChange(ActionEvent e) {

    }

    private void btnExit(ActionEvent e) {
        // TODO add your code here
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        txtPathdir = new JTextField();
        lbPathdir = new JLabel();
        btnChange = new JButton();
        btnExit = new JButton();
        lbInfo = new JLabel();

        //======== this ========
        setTitle("ChangeDir");
        var contentPane = getContentPane();

        //---- lbPathdir ----
        lbPathdir.setText("PathDir");
        lbPathdir.setHorizontalAlignment(SwingConstants.CENTER);

        //---- btnChange ----
        btnChange.setText("Change");
        btnChange.addActionListener(e -> btnChange(e));

        //---- btnExit ----
        btnExit.setText("Exit");
        btnExit.addActionListener(e -> btnExit(e));

        //---- lbInfo ----
        lbInfo.setHorizontalAlignment(SwingConstants.LEFT);
        lbInfo.setText("text");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lbPathdir, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPathdir, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(85, 85, 85)
                    .addComponent(btnChange)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExit)
                    .addGap(74, 74, 74))
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbInfo, GroupLayout.PREFERRED_SIZE, 460, GroupLayout.PREFERRED_SIZE)
                    .addGap(44, 44, 44))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lbInfo, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lbPathdir, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPathdir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnChange)
                        .addComponent(btnExit))
                    .addGap(34, 34, 34))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JTextField txtPathdir;
    private JLabel lbPathdir;
    private JButton btnChange;
    private JButton btnExit;
    private JLabel lbInfo;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
