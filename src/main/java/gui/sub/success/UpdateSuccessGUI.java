package gui.sub.success;

import constants.UIConstants;
import gui.ProfileGUI;
import domain.User;
import lombok.extern.slf4j.Slf4j;


import javax.swing.*;
import java.awt.event.ActionEvent;


@Slf4j
public class UpdateSuccessGUI extends BaseSuccessGUI{

    public UpdateSuccessGUI(User user) {
        super(new JFrame(), UIConstants.CHANGE_INFO_GUI_IMAGE, "Update Successfully!!", "Return to Profile Page", (ActionEvent e) -> {
            new ProfileGUI(user).setVisible(true);
        });
    }
}
