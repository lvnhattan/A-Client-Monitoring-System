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
    FormServer formServer = new FormServer();
    AccountUser user;
    String defaultPath="D:/Test";

    public FormChangeDir(AccountUser user) {
        initComponents();
        this.user=user;
        lbInfo.setText("UserName: "+user.getUsername()+"\n IpClient: "+user.getIpclient());
        txtPathdir.setText(defaultPath);
    }

    private void btnChange(ActionEvent e) {
       formServer.pathDir=txtPathdir.getText();
       System.out.println(txtPathdir.getText());
       this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void btnExit(ActionEvent e) {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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
        lbPathdir.setText("Path Directory");
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
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(lbInfo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addComponent(lbPathdir, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtPathdir, GroupLayout.PREFERRED_SIZE, 381, GroupLayout.PREFERRED_SIZE)
                                .addGroup(contentPaneLayout.createSequentialGroup()
                                    .addComponent(btnChange, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
                                    .addGap(12, 12, 12)))))
                    .addGap(49, 49, 49))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lbInfo, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lbPathdir, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPathdir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnChange, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                        .addComponent(btnExit, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                    .addGap(17, 17, 17))
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
