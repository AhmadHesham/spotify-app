package auth;

import org.mindrot.jbcrypt.*;

public class Password {
    public final int logRounds = 10;

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
    }

    public boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

}
