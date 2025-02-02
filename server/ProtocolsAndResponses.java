package server;

public class ProtocolsAndResponses {
        public static class Responses {
            public static final String SUCCESS = "SUCCESS";
            public static final String FAIL = "FAIL";
            public static final String EXIT = "DisconnectClient";
            public static final String LOGOUT = "Thank you, and hope to see you soon.";
            public static final String PWRULES = "Passwords must be at least 8 characters long and contain at least 1 capital letter and 1 number";
            public static final String SHOWPINFO = "Showing you your personal information registered with the bank:\n";
        }

        public static class Protocols {
            public static final String NEWACCOUNT = "NEWACCOUNT";
            public static final String DEPOSIT = "DEPOSIT";
            public static final String WITHDRAW = "WITHDRAW";
            public static final String SHOWMYACCOUNTS = "SHOWMYACCOUNTS";
            public static final String PAY = "PAY";
            public static final String MOVE = "MOVE";
            public static final String PLOAN = "PLOAN";
            public static final String SHOWPINFO = "SHOWPINFO";
            public static final String RENAMEACCOUNT = "RENAMEACCOUNT";
            public static final String CHANGEPW = "CHANGEPW";
            public static final String CLOSEACCOUNT = "CLOSEACCOUNT";
            public static final String EXIT = "EXIT";
            public static final String LOGOUT = "LOGOUT";
            public static final String MAINMENU = "MAINMENU";
        }
}
