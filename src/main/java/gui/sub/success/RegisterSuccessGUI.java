package gui.sub.success;

import constants.UIConstants;
import domain.User;
import gui.LoginGUI;
import util.FrameUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RegisterSuccessGUI extends BaseSuccessGUI {
    public RegisterSuccessGUI(JFrame frame) {
        super(frame, UIConstants.LOGIN_BACKGROUND_IMAGE, "Register Successfully!", "Return to Login Page", (ActionEvent e) -> {
            FrameUtil.disposeCurrentFrameAndCreateNewFrame("PDC Project Group 18", frame, new LoginGUI(new User()));
        });
    }
}
