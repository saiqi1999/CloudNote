package edu.neu.khoury.madsea.saiqihe.todolistversion2.firebaseComponents;

public class FirebaseSyncFlag {
    private static final FirebaseSyncFlag instance = new FirebaseSyncFlag();
    boolean flag;

    private FirebaseSyncFlag() {
        flag = false;
    }

    public static FirebaseSyncFlag getInstance() {
        return instance;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
