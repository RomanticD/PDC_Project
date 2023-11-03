package dao;

import domain.Preference;

public interface PreferenceService {
    boolean updatePreference(Preference updatedPreference);

    boolean resetPreference(int userId);

    Preference getPreferenceByUserId(int userId);

    boolean setNewUserPreference(Preference newUserPreference);
}
