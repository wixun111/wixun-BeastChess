package view;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginFrame extends JFrame {
    private ChooseFrame chooseMode;
    private ArrayList<User> users;
    private JTextField Account;
    private JPasswordField Password;
    public LoginFrame(ChooseFrame chooseMode){
        setTitle("登录");
        setSize(380, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(null);
        setVisible(true);
        this.users = chooseMode.users;
        this.chooseMode = chooseMode;
        JLabel lbAccount = new JLabel("账户");
        lbAccount.setFont(new Font("", Font.BOLD, 20));
        lbAccount.setBounds(55, 30, 50, 40);
        add(lbAccount);

        Account = new JTextField();
        Account.setBounds(110, 35, 200, 30);
        add(Account);

        JLabel lbPassword = new JLabel("密码");
        lbPassword.setFont(new Font("", Font.BOLD, 20));
        lbPassword.setBounds(55, 80, 50, 40);
        add(lbPassword);

        Password = new JPasswordField();
        Password.setBounds(110, 85, 200, 30);
        add(Password);

        addLoginButton();
    }
    private void addLoginButton(){
        JButton button = new JButton("登录");
        button.addActionListener((e) -> {
            login();
        });
        button.setBounds(130, 140, 120, 40);
        button.setFont(new Font("Rockwell", Font.BOLD, 16));
        add(button);
    }

    private void login(){
        HashMap<String, String> password = new HashMap<>();
        HashMap<String, User> user = new HashMap<>();
        if (users.size() != 0){
            for (int i = 0; i < users.size(); i++){
                password.put(users.get(i).getAccount(), users.get(i).getPassword());
                user.put(users.get(i).getAccount(), users.get(i));
            }
        }
        String ac = Account.getText();
        char[] pwChars = Password.getPassword();
        String pw = new String(pwChars);
        if (ac.equals("")){
            JOptionPane.showMessageDialog(null, "请输入账户", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (pwChars.length == 0){
            JOptionPane.showMessageDialog(null, "请输入密码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.containsKey(ac)){
            if (pw.equals(password.get(ac))){
                setVisible(false);
                this.setVisible(false);
                chooseMode.isLogin = true;
                chooseMode.user = user.get(ac);
            } else {
                JOptionPane.showMessageDialog(null, "密码错误!", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "没有此用户", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}