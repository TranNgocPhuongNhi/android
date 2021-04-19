package phuongnhi.tnpn.project;

public class Lop {
    String ID_user, classID, lessonName;
    int count;

    public Lop() {
    }

    public Lop(String ID_user, String classID, String lessonName, int count) {
        this.ID_user = ID_user;
        this.classID = classID;
        this.lessonName = lessonName;
        this.count = count;
    }

    public String getID_user() {
        return ID_user;
    }

    public void setID_user(String ID_user) {
        this.ID_user = ID_user;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
