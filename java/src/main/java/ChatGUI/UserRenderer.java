package ChatGUI;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import ChatCommons.*;

/**
 * Created by Guy on 23/12/2015.
 */
public class UserRenderer extends JLabel implements ListCellRenderer<User>
{
    public UserRenderer()
    {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User user, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        eUserStatus status = user.GetUserStatus();

        System.out.println(status);

        ImageIcon imageIcon = new ImageIcon("C:\\Users\\15z\\SecureChatP2P\\Photos\\" +  status);

        setIcon(imageIcon);
        setText(user.GetName());

        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
