package server;

public class ProtocolsAndResponses {
        public static class Responses
        {
            public static final String SUCCESS = "SUCCESS";
            public static final String FAIL = "FAIL";
        }

        public static class Protocols{
            public static final String NEWACCOUNT = "NEWACCOUNT";
            public static final String SHOWMYACCOUNTS = "SHOWMYACCOUNTS";
            public static final String PAY = "PAY";
            public static final String MOVE = "MOVE";
        }
}
