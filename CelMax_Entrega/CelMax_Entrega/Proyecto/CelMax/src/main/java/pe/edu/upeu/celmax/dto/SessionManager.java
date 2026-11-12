package pe.edu.upeu.celmax.dto;

import lombok.Data;

@Data
public class SessionManager {
    static SessionManager instance;
    String userId;
    String userName;
    String userPerfil;

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
}
