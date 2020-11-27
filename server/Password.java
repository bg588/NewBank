package server;

public class Password {

    private String password;

    public Password() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean changePassword(String newPassword) {

        boolean containsACapital = false;
        boolean containsANumber = false;
        boolean atLeastEightCharacters = false;

        for (int i = 0; i < newPassword.length(); i++) {
            //go through each character in the password and check for success conditions
            char character = newPassword.charAt(i);
            if (Character.isUpperCase(character)) {
                containsACapital = true;
            } else if (Character.isDigit(character)) {
                containsANumber = true;
            }
        }
        if (newPassword.length() > 7) {
            atLeastEightCharacters = true;
        }

        if (containsACapital && containsANumber && atLeastEightCharacters) {
            setPassword(newPassword);
            return true;
        }
        return false;
    }
}
