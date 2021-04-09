package phuongnhi.tnpn.project;

public class Users {
    public String fullname, email, password, authority, image;
    public Users() {

    }

    public Users(String fullname, String email, String password, String authority, String image) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.image = image;
    }
}
