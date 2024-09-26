package org.mrwood26.RDBPersistence;

public class RDBPersistence {
    private String dir;
    private String dbfilename;

    public RDBPersistence(String dir, String dbfilename) {
        this.dir = dir;
        this.dbfilename = dbfilename;
    }

    public String getDir() {
        return dir;
    }

    public String getDbfilename() {
        return dbfilename;
    }
    public String handleConfigGet(String parameter) {
        if ("dir".equals(parameter)) {
            return "*2\r\n$3\r\ndir\r\n$" + dir.length() + "\r\n" + dir + "\r\n";
        } else if ("dbfilename".equals(parameter)) {
            return "*2\r\n$9\r\ndbfilename\r\n$" + dbfilename.length() + "\r\n" + dbfilename + "\r\n";
        } else {
            return "-ERR unknown parameter\r\n";
        }
    }
    public void handleCommand(String command) {
        if (command.startsWith("CONFIG GET")) {
            String[] parts = command.split(" ");
            if (parts.length == 3) {
                String response = handleConfigGet(parts[2]);
                sendResponse(response);
            } else {
                sendResponse("-ERR wrong number of arguments for 'CONFIG GET' command\r\n");
            }
        } else {
            sendResponse("-ERR unknown command\r\n");
        }
    }
//    *2\r\n$3\r\ndir\r\n$16\r\n/tmp/redis-files\r\n
    private void sendResponse(String response) {
        // Logic to send the response to the client
    }



}
