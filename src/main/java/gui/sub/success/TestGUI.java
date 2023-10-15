package gui.sub.success;

        import constants.UIConstants;
        import domain.User;
        import gui.ProfileGUI;
        import gui.sub.success.BaseSuccessGUI;

        import java.awt.event.ActionEvent;

public class TestGUI extends BaseSuccessGUI {
    public TestGUI(User user) {
        super(UIConstants.CHANGE_INFO_GUI_IMAGE, "Update Successfully!!", "Return to Profile Page", (ActionEvent e) -> {
            new ProfileGUI(user).setVisible(true);
        });
    }
}
