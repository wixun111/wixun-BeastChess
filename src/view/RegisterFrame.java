package view;


import model.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class RegisterFrame extends JFrame {
    private ArrayList<User> users;
    private JTextField Name;
    private JTextField Account;
    private Font pixel;
    private JPasswordField Password;

    public RegisterFrame(ChooseFrame chooseMode){
        setTitle("注册");
        setSize(380, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(null);
        this.users = chooseMode.users;
        this.pixel = chooseMode.pixel;
        JLabel name = new JLabel("名称");
        name.setFont(getPixel(Font.BOLD,20));
        name.setBounds(50, 30, 50, 40);
        add(name);
        Name = new JTextField();
        Name.setFont(getPixel(Font.BOLD,20));
        Name.setDoubleBuffered(true);;
        Name.setBounds(110,30 , 200, 40);
        add(Name);
        JLabel account = new JLabel("账户");
        account.setFont(getPixel(Font.BOLD,20));
        account.setBounds(50, 80, 50, 40);
        add(account);
        Account = new JTextField();
        Account.setFont(getPixel(Font.BOLD,20));
        Account.setBounds(110, 80, 200, 40);
        add(Account);
        JLabel password = new JLabel("密码");
        password.setFont(getPixel(Font.BOLD,20));
        password.setBounds(50, 130, 50, 40);
        add(password);
        Password = new JPasswordField();
        Password.setFont(new Font("",Font.BOLD,20));
        Password.setBounds(110, 130, 200, 40);
        add(Password);
        addRegisterButton();
        setVisible(true);
    }
    public Font getPixel(int style, int size){
        return pixel.deriveFont(style,size);
    }

    private void addRegisterButton(){
        JButton button = new JButton("注册");
        button.addActionListener((e) -> {
            String name = Name.getText();
            String account = Account.getText();
            char[] pwChar = Password.getPassword();
            String password = new String(pwChar);
            if (account.equals("")){
                JOptionPane.showMessageDialog(null, "请输入账户", "", JOptionPane.ERROR_MESSAGE);
            } else if (pwChar.length == 0){
                JOptionPane.showMessageDialog(null, "请输入密码", "", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean canCreate = true;
                for (int i = 0; users!=null && i < users.size(); i++) {
                    if (account.equals(users.get(i).getAccount())){
                        JOptionPane.showMessageDialog(null, "账户已存在", "", JOptionPane.ERROR_MESSAGE);
                        canCreate = false;
                        break;
                    }
                }
                if (canCreate){
                    JOptionPane.showMessageDialog(null, "注册成功！", "", JOptionPane.INFORMATION_MESSAGE);
                    users.add(new User(name,account, password, 0));
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("users.txt")));
                        oos.writeObject(users);
                        oos.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    setVisible(false);
                }
            }
        });
        button.setBounds(130, 200, 120, 40);
        button.setFont(new Font("Rockwell", Font.BOLD, 16));
        add(button);
    }
}
